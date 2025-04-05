package com.atmbank.common.message;

import com.atmbank.common.utils.ConversionUtils;

import java.util.LinkedHashMap;
import java.util.Map;

public class StringMessageBuilder {
    protected static final String FIELD_SEPARATOR = "|";
    protected static final String FIELD_SEPARATOR_REGEX = "\\|";
    protected static final String KEY_VALUE_SEPARATOR = ":";

    private final Map<String, String> fields;

    public StringMessageBuilder() {
        this.fields = new LinkedHashMap<>();
    }

    public StringMessageBuilder addField(String key, String value) {
        this.fields.put(key, value);
        return this;
    }

    public StringMessageBuilder addField(String key, String value, boolean isHex) {
        return addField(key, !isHex ? ConversionUtils.toHexString(value) : value);
    }

    public StringMessageBuilder addField(String key, byte[] value) {
        return addField(key, ConversionUtils.toHexString(value));
    }

    public StringMessageBuilder addField(String key, int value) {
        return addField(key, ConversionUtils.toHexString(value));
    }

    public StringMessageBuilder addField(String key, long value) {
        return addField(key, ConversionUtils.toHexString(value));
    }

    public StringMessageBuilder addField(String key, Object value) {
        return addField(key, value.toString());
    }

    public String build() throws Exception {
        if (fields.isEmpty()) {
            throw new MessageFormatException("No fields to build the message");
        }

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : fields.entrySet()) {
            if (!sb.isEmpty()) {
                sb.append(FIELD_SEPARATOR);
            }
            sb.append(entry.getKey()).append(KEY_VALUE_SEPARATOR).append(entry.getValue());
        }
        return sb.toString();
    }

    public static StringMessageBuilder from(String message) {
        StringMessageBuilder builder = new StringMessageBuilder();
        String[] fieldPairs = message.split(FIELD_SEPARATOR_REGEX);
        for (String pair : fieldPairs) {
            String[] keyValue = pair.split(KEY_VALUE_SEPARATOR, 2);
            if (keyValue.length == 2) {
                builder.addField(keyValue[0], keyValue[1]);
            }
        }
        return builder;
    }

    public String getField(String key) {
        return fields.get(key);
    }
}
