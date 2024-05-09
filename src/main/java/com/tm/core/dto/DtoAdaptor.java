package com.tm.core.dto;

import org.hibernate.type.BasicTypeReference;
import org.hibernate.type.StandardBasicTypes;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class DtoAdaptor implements IDtoAdaptor {

    @Override
    public Map<String, BasicTypeReference<?>> getMetadata(Class<?> clazz) {
        Map<String, BasicTypeReference<?>> basicTypeMappings = getBasicTypeMappings();
        Map<String, BasicTypeReference<?>> fieldMap = new HashMap<>();

        for (Field field : clazz.getDeclaredFields()) {
            String fieldName = field.getName();
            String fieldTypeSimpleName = field.getType().getSimpleName();
            BasicTypeReference<?> basicType = basicTypeMappings.get(fieldTypeSimpleName);

            if (basicType != null) {
                fieldMap.put(fieldName, basicType);
            }
        }

        return fieldMap;

    }

    private Map<String, BasicTypeReference<?>> getBasicTypeMappings() {
        return new HashMap<>() {{
            put("String", StandardBasicTypes.STRING);
            put("Integer", StandardBasicTypes.INTEGER);
            put("Long", StandardBasicTypes.LONG);
            put("Boolean", StandardBasicTypes.BOOLEAN);
            put("Object", StandardBasicTypes.OBJECT_TYPE);
            put("Blob", StandardBasicTypes.BLOB);
            put("Date", StandardBasicTypes.DATE);
        }};
    }

    private void put(String string, BasicTypeReference<String> string1) {

    }
}
