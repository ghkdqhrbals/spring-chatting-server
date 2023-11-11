package com.example.commondto.token;

import java.util.HashMap;

public class TokenConst {
    public static final HashMap<String,String> prefixValue = new HashMap<>();
    public static final String keyName = "accessToken";
    public static final String tokenPrefix = "Bearer ";
    public static final String userSessionPrefix = "UserSession:";

    public TokenConst() {
        prefixValue.put("UserSession", userSessionPrefix);
    }

    public static String getConstantValueByClassName(String className) {


        if (className.equals("UserSession")) {
            return "keyName";
        }
        return null;

    }
}
