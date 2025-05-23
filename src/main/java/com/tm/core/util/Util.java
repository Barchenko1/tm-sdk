package com.tm.core.util;

import com.tm.core.configuration.ConfigDbType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Util {
    private static final Logger LOGGER = LoggerFactory.getLogger(Util.class);

    public static ConfigDbType getFileType(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            throw new RuntimeException();
        }

        if (fileName.endsWith(".xml")) {
            return ConfigDbType.XML;
        } else if (fileName.endsWith(".properties")) {
            return ConfigDbType.PROPERTIES;
        }
        LOGGER.warn("no configuration selected");
        throw new IllegalArgumentException("no configuration selected");
    }
}
