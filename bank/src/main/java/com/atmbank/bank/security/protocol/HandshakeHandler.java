package com.atmbank.bank.security.protocol;

import com.atmbank.bank.security.SecurityContext;
import com.atmbank.common.config.Constants;
import com.atmbank.common.message.MessageSerializer;
import com.atmbank.common.message.response.HandshakeResponse;
import com.atmbank.common.message.response.ResponseStatus;
import com.atmbank.common.security.KeyType;
import com.atmbank.common.security.message.SecureStringMessageBuilder;
import com.atmbank.common.security.protocol.ProtocolException;
import com.atmbank.common.security.protocol.SeqNumberManager;
import com.atmbank.common.security.protocol.SessionManager;
import com.atmbank.common.security.protocol.TimestampManager;
import com.atmbank.common.security.utils.DHUtils;
import com.atmbank.common.utils.ConversionUtils;
import org.apache.commons.codec.DecoderException;

public class HandshakeHandler {
    private final SecurityContext securityContext;
    private final SessionManager sessionManager;
    private final SeqNumberManager seqNumberManager;
    private final TimestampManager timestampManager;

    public HandshakeHandler(SecurityContext securityContext, SessionManager sessionManager, SeqNumberManager seqNumberManager, TimestampManager timestampManager) {
        this.securityContext = securityContext;
        this.sessionManager = sessionManager;
        this.seqNumberManager = seqNumberManager;
        this.timestampManager = timestampManager;
    }

    public void processHandshakeRequest(String handshakeRequest) throws ProtocolException {
        try {
            SecureStringMessageBuilder builder = SecureStringMessageBuilder.from(securityContext.getBankPrivateKey(), KeyType.PRIVATE, "k", KeyType.SYMMETRIC, handshakeRequest);

            String keyHex = builder.getField("k");
            String timestampHex = builder.getField("t");

            byte[] key = validateKey(keyHex);
            validateTimestamp(timestampHex);

            securityContext.setAtmKey(key);
        } catch (Exception e) {
            throw new ProtocolException("Failed to process handshake request: " + e.getMessage(), e);
        } finally {
            seqNumberManager.increment();
        }
    }

    public String getServerExchangeMessage() throws ProtocolException {
        try {
            SecureStringMessageBuilder builder = new SecureStringMessageBuilder(securityContext.getAtmKey());
            builder.addField("k", sessionManager.getPublicKey());
            builder.addField("n", seqNumberManager.getCurrent());
            builder.addField("t", timestampManager.getCurrentTimestamp());
            return builder.build();
        } catch (Exception e) {
            throw new ProtocolException("Failed to build server exchange message: " + e.getMessage(), e);
        } finally {
            seqNumberManager.increment();
        }
    }

    public void processClientExchangeMessage(String clientExchangeMessage) throws ProtocolException {
        try {
            SecureStringMessageBuilder builder = SecureStringMessageBuilder.from(securityContext.getAtmKey(), clientExchangeMessage);

            String dhKeyHex = builder.getField("k");
            String seqNumberHex = builder.getField("n");
            String timestampHex = builder.getField("t");

            byte[] dhKey = validateDHKey(dhKeyHex);
            validateSeqNumber(seqNumberHex);
            validateTimestamp(timestampHex);

            sessionManager.generateSessionKey(dhKey);
        } catch (Exception e) {
            throw new ProtocolException("Failed to process client exchange message: " + e.getMessage(), e);
        } finally {
            seqNumberManager.increment();
        }
    }

    public String getHandshakeResponse() throws ProtocolException {
        try {
            SecureStringMessageBuilder builder = new SecureStringMessageBuilder(securityContext.getAtmKey(), KeyType.SYMMETRIC);
            builder.addField("o", MessageSerializer.serialize(new HandshakeResponse(ResponseStatus.SUCCESS)));
            builder.addField("n", seqNumberManager.getCurrent());
            builder.addField("t", timestampManager.getCurrentTimestamp());
            return builder.build();
        } catch (Exception e) {
            throw new ProtocolException("Failed to build handshake response: " + e.getMessage(), e);
        } finally {
            seqNumberManager.increment();
        }
    }

    private byte[] validateKey(String keyHex) throws ProtocolException {
        byte[] key;
        try {
            key = keyHex != null && !keyHex.isEmpty() ? ConversionUtils.toBytes(keyHex, true) : null;
        } catch (DecoderException e) {
            throw new ProtocolException("Failed to validate key: " + e.getMessage(), e);
        }
        if (key == null || key.length != Constants.AES_KEY_SIZE / 8) {
            throw new ProtocolException("Failed to validate key: key is null or invalid length");
        }
        return key;
    }

    private byte[] validateDHKey(String dhKeyHex) throws ProtocolException {
        byte[] dhKey;
        try {
            dhKey = dhKeyHex != null && !dhKeyHex.isEmpty() ? ConversionUtils.toBytes(dhKeyHex, true) : null;
        } catch (DecoderException e) {
            throw new ProtocolException("Failed to validate DH key: " + e.getMessage(), e);
        }
        if (dhKey == null) {
            throw new ProtocolException("Failed to validate DH key: DH key is null");
        }
        try {
            DHUtils.getPublicKeyFromBytes(dhKey);
        } catch (Exception e) {
            throw new ProtocolException("Failed to validate DH key: " + e.getMessage(), e);
        }
        return dhKey;
    }

    private int validateSeqNumber(String seqNumberHex) throws ProtocolException {
        Integer seqNumber = seqNumberHex != null && !seqNumberHex.isEmpty() ? ConversionUtils.toInt(seqNumberHex, true) : null;
        if (seqNumber == null || seqNumber == Integer.MIN_VALUE || seqNumber != seqNumberManager.getCurrent()) {
            throw new ProtocolException("Failed to validate sequence number: sequence number is null or does not match");
        }
        return seqNumber;
    }

    private long validateTimestamp(String timestampHex) throws ProtocolException {
        Long timestamp = timestampHex != null && !timestampHex.isEmpty() ? ConversionUtils.toLong(timestampHex, true) : null;
        if (timestamp == null || timestamp == Long.MIN_VALUE || !timestampManager.isValidTimestamp(timestamp)) {
            throw new ProtocolException("Failed to validate timestamp: timestamp is null or does not match");
        }
        return timestamp;
    }
}
