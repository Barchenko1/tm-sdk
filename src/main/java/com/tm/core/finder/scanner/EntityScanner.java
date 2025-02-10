package com.tm.core.finder.scanner;

import com.tm.core.finder.manager.IEntityMappingManager;
import com.tm.core.finder.table.EntityTable;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EntityScanner implements IEntityScanner {

    private final IEntityMappingManager entityMappingManager;
    private final String entityPackage;

    public EntityScanner(IEntityMappingManager entityMappingManager,
                         String entityPackage) {
        this.entityMappingManager = entityMappingManager;
        this.entityPackage = entityPackage;
        getEntityTables();
    }

    private void getEntityTables() {
        Map<Class<?>, EntityTable> entityInfo = new HashMap<>();
        Set<Class<?>> entityClasses = getClassesInPackage(entityPackage);

        for (Class<?> entityClass : entityClasses) {
            if (entityClass.isAnnotationPresent(Entity.class)) {
                Table table = entityClass.getAnnotation(Table.class);
                String tableName = (table != null) ? table.name() : entityClass.getSimpleName();

                Map<String, Class<?>> fields = new HashMap<>();
                for (Field field : entityClass.getDeclaredFields()) {
                    fields.put(field.getName(), field.getType());
                }
                EntityTable entityTable = new EntityTable(
                        entityClass,
                        tableName);
                entityInfo.put(entityClass, entityTable);
                entityMappingManager.addEntityTable(entityTable);
            }
        }
    }

    private static Set<Class<?>> getClassesInPackage(String packageName) {
        String path = packageName.replace('.', '/');
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        try (Stream<Path> paths = Files.walk(Paths.get(Objects.requireNonNull(classLoader.getResource(path)).toURI()))) {
            return paths
                    .filter(Files::isRegularFile)
                    .filter(file -> file.getFileName().toString().endsWith(".class"))
                    .map(file -> {
                        String relativePath = file.toString().replace(File.separatorChar, '.');
                        int startIndex = relativePath.indexOf(packageName);
                        String className = relativePath.substring(startIndex, relativePath.length() - 6); // Remove ".class"
                        try {
                            if (!className.contains("$")) {
                                return Class.forName(className);
                            }
                            return null;
                        } catch (ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
        } catch (Exception e) {
            throw new RuntimeException("Failed to get classes in package: " + packageName, e);
        }
    }

}
