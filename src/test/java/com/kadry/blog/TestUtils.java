package com.kadry.blog;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestUtils {

    public static String asJsonString(final Object obj){
        try{
            return new ObjectMapper().writeValueAsString(obj);
        }catch (JsonProcessingException e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
