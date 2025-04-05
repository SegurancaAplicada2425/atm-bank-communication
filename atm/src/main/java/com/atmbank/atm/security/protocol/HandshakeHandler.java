package com.atmbank.atm.security.protocol;

import com.atmbank.atm.security.SecurityContext;
import com.atmbank.common.message.MessageSerializer;
import com.atmbank.common.message.MessageType;
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

    public String getHandshakeRequest() throws ProtocolException {
        try {
            SecureStringMessageBuilder secureBuilder = new SecureStringMessageBuilder(securityContext.getBankPublicKey(), KeyType.PUBLIC, securityContext.getAtmKey(), KeyType.SYMMETRIC);
            secureBuilder.addField("k", securityContext.getAtmKey());
            secureBuilder.addField("t", timestampManager.getCurrentTimestamp());
            return secureBuilder.build();
        } catch (Exception e) {
            throw new ProtocolException("Failed to build handshake request: " + e.getMessage(), e);
        }
    }

    public void processServerExchangeMessage(String serverExchangeMessage) throws ProtocolException {
        try {
            SecureStringMessageBuilder builder = SecureStringMessageBuilder.from(securityContext.getAtmKey(), serverExchangeMessage);

            String dhKeyHex = builder.getField("k");
            String deqNumberHex = builder.getField("n");
            String timestampHex = builder.getField("t");

            byte[] dhKey = validateDHKey(dhKeyHex);
            validateSeqNumber(deqNumberHex, true);
            validateTimestamp(timestampHex);

            sessionManager.generateSessionKey(dhKey);
        } catch (Exception e) {
            throw new ProtocolException("Failed to process server exchange message: " + e.getMessage(), e);
        } finally {
            seqNumberManager.increment();
        }
    }

    public String getClientExchangeMessage() throws ProtocolException {
        try {
            SecureStringMessageBuilder secureBuilder = new SecureStringMessageBuilder(securityContext.getAtmKey());
            secureBuilder.addField("k", sessionManager.getPublicKey());
            secureBuilder.addField("n", seqNumberManager.getCurrent());
            secureBuilder.addField("t", timestampManager.getCurrentTimestamp());
            return secureBuilder.build();
        } catch (Exception e) {
            throw new ProtocolException("Failed to build client exchange message: " + e.getMessage(), e);
        } finally {
            seqNumberManager.increment();
        }
    }

    public void processHandshakeResponse(String handshakeResponse) throws ProtocolException {
        try {
            SecureStringMessageBuilder builder = SecureStringMessageBuilder.from(securityContext.getAtmKey(), handshakeResponse);

            String objHex = builder.getField("o");
            if (objHex == null || objHex.isEmpty()) {
                throw new ProtocolException("Failed to process handshake response: o is null or empty");
            }

            HandshakeResponse response = (HandshakeResponse) MessageSerializer.deserialize(objHex, true);
            if (response == null || response.getType() != MessageType.HANDSHAKE || response.getStatus() != ResponseStatus.SUCCESS) {
                throw new ProtocolException("Failed to process handshake response: invalid message type or status");
            }

            String seqNumberHex = builder.getField("n");
            String timestampHex = builder.getField("t");

            validateSeqNumber(seqNumberHex);
            validateTimestamp(timestampHex);
        } catch (Exception e) {
            throw new ProtocolException("Failed to process handshake response: " + e.getMessage(), e);
        } finally {
            seqNumberManager.increment();
        }
    }

    private byte[] validateDHKey(String dhString) throws ProtocolException {
        byte[] dhKey;
        try {
            dhKey = dhString != null && !dhString.isEmpty() ? ConversionUtils.toBytes(dhString, true) : null;
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

    private int validateSeqNumber(String seqNumberString, boolean first) throws ProtocolException {
        Integer seqNumber = seqNumberString != null && !seqNumberString.isEmpty() ? ConversionUtils.toInt(seqNumberString, true) : null;
        if (seqNumber == null || seqNumber == Integer.MIN_VALUE || !first && seqNumber != seqNumberManager.getCurrent()) {
            throw new ProtocolException("Failed to validate sequence number: sequence number is null or does not match");
        }
        if (first) {
            seqNumberManager.setCurrent(seqNumber);
        }
        return seqNumber;
    }

    private int validateSeqNumber(String seqNumberString) throws ProtocolException {
        return validateSeqNumber(seqNumberString, false);
    }

    private long validateTimestamp(String timestampString) throws ProtocolException {
        Long timestamp = timestampString != null && !timestampString.isEmpty() ? ConversionUtils.toLong(timestampString, true) : null;
        if (timestamp == null || timestamp == Long.MIN_VALUE || !timestampManager.isValidTimestamp(timestamp)) {
            throw new ProtocolException("Failed to validate timestamp: timestamp is null or does not match");
        }
        return timestamp;
    }
}
