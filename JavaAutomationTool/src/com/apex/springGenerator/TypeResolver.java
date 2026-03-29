package com.apex.springGenerator;

import com.fasterxml.jackson.databind.JsonNode;

public class TypeResolver {

    public static Class<?> resolve(JsonNode node) {
        if (node.isTextual()) return String.class;
        if (node.isInt()) return Integer.class;
        if (node.isLong()) return Long.class;
        if (node.isDouble() || node.isFloat()) return Double.class;
        if (node.isBoolean()) return Boolean.class;
        return Object.class;
    }
}
