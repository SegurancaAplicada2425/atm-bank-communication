package com.atmbank.common.security.protocol;

import com.atmbank.common.config.Constants;
import com.atmbank.common.security.utils.DHUtils;

import java.security.KeyPair;

public class SessionManager {
    private final byte[] privateKey;
    private final byte[] publicKey;
    private byte[] sessionKey;

    public SessionManager() throws Exception {
        KeyPair keyPair = DHUtils.generateKeyPair();
        this.privateKey = DHUtils.getPrivateKeyBytes(keyPair);
        this.publicKey = DHUtils.getPublicKeyBytes(keyPair);
    }

    public byte[] generateSessionKey(byte[] otherPartyPublicKey) throws Exception {
        this.sessionKey = DHUtils.generateSharedSecret(privateKey, otherPartyPublicKey, Constants.SESSION_KEY_SIZE);
        return sessionKey;
    }

    public byte[] getPublicKey() {
        return publicKey;
    }

    public byte[] getSessionKey() {
        return sessionKey;
    }
}
