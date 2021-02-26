package com.zjq.chat.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public interface Json {

    /***
     * 将传回前端消息转换为json格式
     * @param map
     * @return
     * @throws JsonProcessingException
     */
    String toJson(Map<String, ?> map) throws JsonProcessingException;

}
