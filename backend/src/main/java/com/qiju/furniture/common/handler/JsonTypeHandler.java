package com.qiju.furniture.common.handler;

import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Custom JSON Type Handler for MyBatis-Plus
 * Handles conversion between Java objects and JSON database columns
 *
 * @author Qiju Team
 */
public class JsonTypeHandler extends JacksonTypeHandler {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public JsonTypeHandler(Class<?> type) {
        super(type);
    }
}
