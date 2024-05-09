package com.tm.core.mapper;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class DtoEntityMapper implements IDtoEntityMapper {

    private final IDtoEntityBind dtoEntityBind;

    public DtoEntityMapper(IDtoEntityBind dtoEntityBind) {
        this.dtoEntityBind = dtoEntityBind;
    }

    @Override
    public <E, R> void mapDtoToEntity(E dtoObject, R entityObject, String bindKey) {
        Field[] dtoFields = dtoObject.getClass().getDeclaredFields();
        Field[] classFields = entityObject.getClass().getDeclaredFields();
        dtoEntityBind.setKey(bindKey);
        List<FieldPayload> fieldPayloadList = getFieldPayloadList(dtoObject, dtoFields);

        for (Field classField: classFields) {
            if ("id".equals(classField.getName())) {
                continue;
            }
            List<FieldPayload> innerFieldPayloadList = new ArrayList<>();
            fieldPayloadList.forEach(fieldPayload -> {
                if (fieldPayload.getName().equals(classField.getName())
                        && fieldPayload.getType() == classField.getType()) {
                    mapDtoToObject(entityObject, fieldPayload);
                }
                if (isClassBind(classField, fieldPayload.getName())) {
                    mapFieldValue(classField, fieldPayload, innerFieldPayloadList);
                }
            });
            if (!innerFieldPayloadList.isEmpty()) {
                fillInnerClassFields(entityObject, classField, innerFieldPayloadList);
            }
        }
    }

    private <E> List<FieldPayload> getFieldPayloadList(E dtoObject, Field[] dtoFields) {
        return Arrays.stream(dtoFields).map(dtoField -> {
            String name = dtoEntityBind.get(dtoField.getName())
                    .orElseThrow(() -> new RuntimeException("not found map name"));
            dtoField.setAccessible(true);
            Object value;
            try {
                value = dtoField.get(dtoObject);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            return new FieldPayload(name, dtoField.getType(), value);
        }).toList();
    }

    private void mapFieldValue(Field classField, FieldPayload fieldPayload, List<FieldPayload> tempFildValueMap) {
        String classFieldName = getBindFieldName(fieldPayload.getName());
        Object object = getInnerObject(classField);
        Field[] innerObjectFieldArray = object.getClass().getDeclaredFields();
        Arrays.stream(innerObjectFieldArray)
                .filter(field -> field.getName().equals(classFieldName))
                .findFirst()
                .ifPresent(field -> {
                    tempFildValueMap.add(fieldPayload);
                });
    }

    private <R> void fillInnerClassFields(R entityObject, Field classField, List<FieldPayload> fieldPayloadList) {
        Object innerObject = getInnerObject(classField);
        fieldPayloadList.forEach((fieldPayload) -> {
            setField(innerObject, fieldPayload);
        });
        setObject(entityObject, classField, innerObject);
    }

    private <E> void mapDtoToObject(E entityObject, FieldPayload fieldPayload) {
        String setterMethodName = getSetMethodName(fieldPayload.getName()
                .substring(fieldPayload.getName().lastIndexOf(".") + 1));
        try {
            Method setterMethod = entityObject.getClass().getMethod(setterMethodName, fieldPayload.getType());
            setterMethod.invoke(entityObject, fieldPayload.getValue());
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private String getBindSetterName(String dtoFieldName) {
        return Optional.of(dtoFieldName)
                .map(name -> getSetMethodName(name.substring(name.lastIndexOf(".") + 1)))
                .orElseThrow(() -> new RuntimeException("wrong json bind mapping"));
    }

    private boolean isClassBind(Field classField, String dtoFieldName) {
        return Optional.of(dtoFieldName)
                .filter(name -> name.contains("."))
                .map(name -> name.substring(0, name.lastIndexOf(".")))
                .filter(name-> name.equalsIgnoreCase(classField.getName()))
                .isPresent();
    }

    private String getBindFieldName(String dtoFieldName) {
        return Optional.of(dtoFieldName)
                .map(name -> name.substring(name.lastIndexOf(".") + 1))
                .orElseThrow(() -> new RuntimeException("wrong json bind mapping"));
    }

    private String getSetMethodName(String input) {
        return "set" + input.substring(0, 1).toUpperCase() + input.substring(1);
    }

    private Object getInnerObject(Field field) {
        try {
            field.setAccessible(true);
            return field.getType().getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private void setField(Object entityObject, FieldPayload fieldPayload) {
        String setterMethodName = getBindSetterName(fieldPayload.getName());
        try {
            Method setterMethod = entityObject.getClass().getMethod(setterMethodName, fieldPayload.getType());
            setterMethod.invoke(entityObject, fieldPayload.getValue());
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private <R> void setObject(R entityObject, Field classField, Object object) {
        String setterMethodName = getSetMethodName(classField.getName());
        try {
            Method setterMethod = entityObject.getClass().getMethod(setterMethodName, classField.getType());
            classField.setAccessible(true);
            setterMethod.invoke(entityObject, object);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

}
