package com.tm.core.mapper;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class DtoEntityBind implements IDtoEntityBind {
    private final String folderPath;
    private final Map<String, JsonObject> jsonObjectMap;
    private String key;

    public DtoEntityBind(String folderPath) {
        this.folderPath = folderPath;
        this.jsonObjectMap = initJsonMap();
    }

    private Map<String, JsonObject> initJsonMap() {
        URL url = Thread.currentThread().getContextClassLoader().getResource(this.folderPath);
        File folder;
        try {
            folder = new File(url.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        File[] files = folder.listFiles();
        Map<String, JsonObject> map = new HashMap<>();
        if (files != null) {
            Gson gson = new Gson();
            BufferedReader reader;
            for (File file : files) {
                if (file.isFile()) {
                    try (InputStream in =
                                 Thread.currentThread().getContextClassLoader().getResourceAsStream(
                                         this.folderPath + "/" + file.getName())) {
                        reader = new BufferedReader(new InputStreamReader(in));
                        map.put(file.getName().substring(0, file.getName().lastIndexOf(".")),
                                gson.fromJson(reader, JsonObject.class));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return map;
    }

    @Override
    public Optional<String> get(String field) {
        JsonObject jsonObject = jsonObjectMap.get(key);
        return jsonObject.has(field) ? Optional.of(jsonObject.get(field).getAsString()) : Optional.empty();
    }

    @Override
    public void setKey(String key) {
        this.key = key;
    }

}
