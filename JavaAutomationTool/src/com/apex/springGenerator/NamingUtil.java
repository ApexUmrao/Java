package com.apex.springGenerator;

public class NamingUtil {

    public static String toClassName(String name) {
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }

    public static String toEntityName(String name) {
        return toClassName(name) + "Entity";
    }
}