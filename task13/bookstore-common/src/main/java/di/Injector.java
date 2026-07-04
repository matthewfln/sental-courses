package di;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class Injector {
    private final Map<Class<?>, Object> container = new HashMap<>();

    public void register(Object instance) {
        container.put(instance.getClass(), instance);
    }

    // Главный метод
    public void inject(Object obj) {
        Class<?> clazz = obj.getClass();

        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Inject.class)) {
                field.setAccessible(true); // доступ к private полям
                Class<?> fieldType = field.getType();

                Object dependency = getOrCreate(fieldType);

                try {
                    field.set(obj, dependency);
                } catch (IllegalAccessException e) {
                    System.out.println("Ошибка DI: не удалось внедрить зависимость " + fieldType.getName());
                }
            }
        }
    }

    // Вспомогательный метод: берет из контейнера или создает новый через Reflection
    private Object getOrCreate(Class<?> clazz) {
        if (container.containsKey(clazz)) {
            return container.get(clazz);
        }

        try {
            Constructor<?> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true); // если он private
            Object newInstance = constructor.newInstance();

            container.put(clazz, newInstance);
            inject(newInstance);

            return newInstance;
        } catch (Exception e) {
            System.out.println("Ошибка DI: не удалось создать экземпляр " + clazz.getName());
            return null;
        }
    }
}
