package config;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Configurator {

    private final Map<String, Properties> propertiesCache = new HashMap<>();

    public void configure(Object obj) {
        Class<?> clazz = obj.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(ConfigProperty.class)) {
                ConfigProperty annotation = field.getAnnotation(ConfigProperty.class);
                String fileName = annotation.configFileName();
                String propName = annotation.propertyName();

                if (propName.isEmpty()) {
                    propName = clazz.getSimpleName() + "." + field.getName();
                }

                try {
                    Properties props = getProperties(fileName);
                    String value = props.getProperty(propName);

                    if (value != null) {
                        setFieldValue(obj, field, value, annotation.type());
                    }
                } catch (IOException e) {
                    System.out.println("Не удалось загрузить конфигурацию из файла: " + fileName);
                } catch (IllegalAccessException e) {
                    System.out.println("Нет доступа к полю: " + field.getName());
                }
            }
        }
    }

    private Properties getProperties(String fileName) throws IOException {
        // Если файла еще нет в кэше, загружаем его с диска
        if (!propertiesCache.containsKey(fileName)) {
            Properties props = new Properties();
            try (FileInputStream fis = new FileInputStream(fileName)) {
                props.load(fis);
            }
            propertiesCache.put(fileName, props);
        }
        return propertiesCache.get(fileName);
    }

    private void setFieldValue(Object obj, Field field, String value, PropertyType type) throws IllegalAccessException {
        field.setAccessible(true);
        Class<?> fieldType = field.getType();

        if (type == PropertyType.INTEGER || fieldType == int.class || fieldType == Integer.class) {
            field.set(obj, Integer.parseInt(value));
        } else if (type == PropertyType.BOOLEAN || fieldType == boolean.class || fieldType == Boolean.class) {
            field.set(obj, Boolean.parseBoolean(value));
        } else if (type == PropertyType.ARRAY_STRING || fieldType == String[].class) {
            field.set(obj, value.split(","));
        } else {
            field.set(obj, value); // по умолчанию - String
        }
    }
}
