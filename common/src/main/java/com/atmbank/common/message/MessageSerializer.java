package com.atmbank.common.message;

import com.atmbank.common.utils.ConversionUtils;
import org.apache.commons.codec.DecoderException;

import java.io.*;

public class MessageSerializer {
    public static byte[] serialize(Message message) throws IOException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteOut);
        out.writeObject(message);
        return byteOut.toByteArray();
    }

    public static Message deserialize(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteIn = new ByteArrayInputStream(data);
        ObjectInputStream in = new ObjectInputStream(byteIn);
        return (Message) in.readObject();
    }

    public static Message deserialize(String data) throws IOException, ClassNotFoundException {
        byte[] dataBytes = ConversionUtils.toBytes(data);
        return deserialize(dataBytes);
    }

    public static Message deserialize(String data, boolean isHex) throws IOException, ClassNotFoundException, DecoderException {
        byte[] dataBytes = ConversionUtils.toBytes(data, isHex);
        return deserialize(dataBytes);
    }
}
