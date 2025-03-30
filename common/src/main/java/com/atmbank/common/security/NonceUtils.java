package com.atmbank.common.security;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

public class NonceUtils {
    public static byte[] generateNonce(int length) {
        byte[] nonce = ByteArrayGenerator.generate(length);
        return nonce;
    }

    public static String generateNonceHex(int length) {
        byte[] nonce = generateNonce(length);
        return Hex.encodeHexString(nonce);
    }

    public static byte[] incrementNonce(byte[] nonce) {
        byte[] incrementedNonce = nonce.clone();
        for (byte b : incrementedNonce) {
            if (b < Byte.MAX_VALUE) {
                b++;
                break;
            } else {
                b = 0;
            }
        }
        return incrementedNonce;
    }

    public static String incrementNonceHex(String nonceHex) throws DecoderException {
        byte[] nonce = Hex.decodeHex(nonceHex);
        byte[] incrementedNonce = incrementNonce(nonce);
        return Hex.encodeHexString(incrementedNonce);
    }
}
