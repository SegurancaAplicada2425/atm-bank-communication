package com.atmbank.common.config;

public class Constants {
    public static final boolean DEBUG_MODE = true;
    public static final int PIN_SIZE = 128;
    public static final int MIN_ACCOUNT_INITIAL_BALANCE = 10;
    public static final int CONNECTION_TIMEOUT = 10000; // 10 seconds
    public static final int MAX_CONNECTIONS = 10;

    // Error codes
    public static final int SUCCESS_CODE = 0;
    public static final int PROTOCOL_ERROR_CODE = 63;
    public static final int GENERAL_ERROR_CODE = 255;

    // RSA encryption
    public static final String RSA_KEY_ALGORITHM = "RSA";
    public static final int RSA_KEY_SIZE = 2048;
    public static final String RSA_ALGORITHM = "RSA";
    public static final String RSA_SIGNATURE_ALGORITHM = "SHA256withRSA";

    // AES encryption
    public static final String AES_KEY_ALGORITHM = "AES";
    public static final int AES_KEY_SIZE = 128;
    public static final String AES_ALGORITHM = "AES/CTR/NoPadding";

    // DH key exchange
    public static final String DH_KEY_ALGORITHM = "DH";
    public static final int DH_KEY_SIZE = 2048;
    public static final String DH_ALGORITHM = "DH";

    // Session key
    public static final int SESSION_KEY_SIZE = AES_KEY_SIZE;

    // HMAC
    public static final String HMAC_ALGORITHM = "HmacSHA256";

    // Secret reducer
    public static final String SECRET_REDUCER_ALGORITHM = "SHA-256";

    // Timestamp
    public static final long TIMESTAMP_VALIDITY = 10000; // 10 seconds
}
