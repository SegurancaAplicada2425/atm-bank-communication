package com.atmbank.common.utils;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class ConversionUtils {
    public static byte[] toBytes(int value) {
        return ByteBuffer.allocate(4).putInt(value).array();
    }

    public static byte[] toBytes(long value) {
        return ByteBuffer.allocate(8).putLong(value).array();
    }

    public static byte[] toBytes(String value) {
        return value.getBytes(StandardCharsets.UTF_8);
    }

    public static byte[] toBytes(String value, boolean isHex) throws DecoderException {
        if (isHex) {
            return Hex.decodeHex(value);
        } else {
            return toBytes(value);
        }
    }

    public static int toInt(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getInt();
    }

    public static int toInt(String value, boolean isHex) {
        try {
            return Integer.parseInt(value, isHex ? 16 : 10);
        } catch (NumberFormatException e) {
            return Integer.MIN_VALUE;
        }
    }

    public static int toInt(String value) {
        return toInt(value, false);
    }

    public static long toLong(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getLong();
    }

    public static long toLong(String value, boolean isHex) {
        try {
            return Long.parseLong(value, isHex ? 16 : 10);
        } catch (NumberFormatException e) {
            return Long.MIN_VALUE;
        }
    }

    public static long toLong(String value) {
        return toLong(value, false);
    }

    public static String toString(byte[] bytes) {
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public static String toString(int value) {
        return Integer.toString(value);
    }

    public static String toString(long value) {
        return Long.toString(value);
    }

    public static String toHexString(byte[] bytes) {
        return Hex.encodeHexString(bytes);
    }

    public static String toHexString(int value) {
        return Integer.toHexString(value);
    }

    public static String toHexString(long value) {
        return Long.toHexString(value);
    }

    public static String toHexString(String value) {
        return toHexString(toBytes(value));
    }
}
