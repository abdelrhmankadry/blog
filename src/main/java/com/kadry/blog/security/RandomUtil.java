package com.kadry.blog.security;
import org.apache.commons.lang3.RandomStringUtils;

import java.security.SecureRandom;

public class RandomUtil {
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final int DEF_COUNT = 20;

    public static String generateRandomAlphanumericString(){
        return RandomStringUtils.random(DEF_COUNT,0,0,true,true, null, SECURE_RANDOM);
    }

    public static String generateActivationKey(){return generateRandomAlphanumericString();}
}
