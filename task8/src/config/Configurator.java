package config;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Properties;

public class Configurator {

    public void configure(Object obj) {
        Class<?> clazz = obj.getClass();

        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(ConfigProperty.class)) {
                ConfigProperty annotation = field.getAnnotation(ConfigProperty.class);
                String fileName = annotation.configFileName();
                String propName = annotation.propertyName();

                // Если propertyName не указано, формируем как ИМЯ_КЛАССА.ИМЯ_ПОЛЯ
                if (propName.isEmpty()) {
                    propName = clazz.getSimpleName() + "." + field.getName();
                }

                Properties props = new Properties();
                try (FileInputStream fis = new FileInputStream(fileName)) {
                    props.load(fis);
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

    private void setFieldValue(Object obj, Field field, String value, PropertyType type) throws IllegalAccessException {
        field.setAccessible(true);
        Class<?> fieldType = field.getType();

        // Преобразование значения в текущий тип поля (согласно типу или явному указанию в enum)
        if (type == PropertyType.INTEGER || fieldType == int.class || fieldType == Integer.class) {
            field.set(obj, Integer.parseInt(value));
        } else if (type == PropertyType.BOOLEAN || fieldType == boolean.class || fieldType == Boolean.class) {
            field.set(obj, Boolean.parseBoolean(value));
        } else if (type == PropertyType.ARRAY_STRING || fieldType == String[].class) {
            field.set(obj, value.split(","));
        } else {
            field.set(obj, value); // По умолчанию - String
        }
    }
}