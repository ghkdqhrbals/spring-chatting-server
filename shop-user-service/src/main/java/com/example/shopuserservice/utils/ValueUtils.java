package com.example.shopuserservice.utils;

import org.apache.commons.lang.RandomStringUtils;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Random;

public class ValueUtils {

    // 랜덤 유틸
    public static String randomCombinedString(int length){
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    public static String randomSecuredIntegerString(int length){
        int min = 0;
        int max = 9;
        String randomString ="";
        SecureRandom secureRandom = new SecureRandom();

        for (int i=0;i<length;i++){
            randomString += secureRandom.nextInt(max - min) + min;
        }

        return randomString;
    }
    public static String randomAlphabeticString(int length){
        return RandomStringUtils.randomAlphabetic(length);
    }

    // 시간 유틸
    // 시간이 지났나요? true:지남, false:아직
    public static Boolean isAlreadyTimePassed(LocalDateTime time){
        return LocalDateTime.now().isAfter(time);
    }
}
