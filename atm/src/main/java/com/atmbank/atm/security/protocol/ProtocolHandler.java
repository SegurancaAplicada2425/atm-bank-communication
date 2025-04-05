package com.atmbank.atm.security.protocol;

import com.atmbank.atm.security.SecurityContext;
import com.atmbank.common.message.MessageSerializer;
import com.atmbank.common.message.request.Request;
import com.atmbank.common.message.response.Response;
import com.atmbank.common.security.message.SecureStringMessageBuilder;
import com.atmbank.common.security.protocol.ProtocolException;
import com.atmbank.common.security.protocol.SeqNumberManager;
import com.atmbank.common.security.protocol.SessionManager;
import com.atmbank.common.security.protocol.TimestampManager;
import com.atmbank.common.utils.ConversionUtils;

public class ProtocolHandler {
    private final SessionManager sessionManager;
    private final SeqNumberManager seqNumberManager;
    private final TimestampManager timestampManager;
    private final HandshakeHandler handshakeHandler;

    public ProtocolHandler(SecurityContext securityContext) throws Exception {
        this.sessionManager = new SessionManager();
        this.seqNumberManager = new SeqNumberManager();
        this.timestampManager = new TimestampManager();
        this.handshakeHandler = new HandshakeHandler(securityContext, sessionManager, seqNumberManager, timestampManager);
    }

    public String getHandshakeRequest() throws ProtocolException {
        return handshakeHandler.getHandshakeRequest();
    }

    public void processServerExchangeMessage(String serverExchangeMessage) throws ProtocolException {
        handshakeHandler.processServerExchangeMessage(serverExchangeMessage);
    }

    public String getClientExchangeMessage() throws ProtocolException {
        return handshakeHandler.getClientExchangeMessage();
    }

    public void processHandshakeResponse(String handshakeResponse) throws ProtocolException {
        handshakeHandler.processHandshakeResponse(handshakeResponse);
    }

    public String getSessionRequest(Request request) throws ProtocolException {
        try {
            SecureStringMessageBuilder secureBuilder = new SecureStringMessageBuilder(sessionManager.getSessionKey());
            secureBuilder.addField("o", MessageSerializer.serialize(request));
            secureBuilder.addField("n", seqNumberManager.getCurrent());
            secureBuilder.addField("t", timestampManager.getCurrentTimestamp());
            return secureBuilder.build();
        } catch (Exception e) {
            throw new ProtocolException("Failed to build session request: " + e.getMessage(), e);
        } finally {
            seqNumberManager.increment();
        }
    }

    public Response processSessionResponse(String responseStr) throws ProtocolException {
        try {
            SecureStringMessageBuilder builder = SecureStringMessageBuilder.from(sessionManager.getSessionKey(), responseStr);

            String objHex = builder.getField("o");
            if (objHex == null || objHex.isEmpty()) {
                throw new ProtocolException("o is null or empty");
            }

            Response response = (Response) MessageSerializer.deserialize(objHex, true);
            if (response == null) {
                throw new ProtocolException("response is null");
            }

            String seqNumberHex = builder.getField("n");
            String timestampHex = builder.getField("t");

            validateSeqNumber(seqNumberHex);
            validateTimestamp(timestampHex);

            return response;
        } catch (Exception e) {
            throw new ProtocolException("Failed to process session response: " + e.getMessage(), e);
        } finally {
            seqNumberManager.increment();
        }
    }

    private int validateSeqNumber(String seqNumberString) throws SecurityException {
        Integer seqNumber = seqNumberString != null && !seqNumberString.isEmpty() ? ConversionUtils.toInt(seqNumberString, true) : null;
        if (seqNumber == null || seqNumber == Integer.MIN_VALUE || seqNumberManager.getCurrent() != seqNumber) {
            throw new SecurityException("Failed to validate sequence number: sequence number is null or does not match");
        }
        return seqNumber;
    }

    private long validateTimestamp(String timestampString) throws SecurityException {
        Long timestamp = timestampString != null && !timestampString.isEmpty() ? ConversionUtils.toLong(timestampString, true) : null;
        if (timestamp == null || timestamp == Long.MIN_VALUE || !timestampManager.isValidTimestamp(timestamp)) {
            throw new SecurityException("Failed to validate timestamp: timestamp is null or does not match");
        }
        return timestamp;
    }
}
