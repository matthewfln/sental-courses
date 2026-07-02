package util;

import service.BookStore;
import java.io.*;

public class AppStateStorage {
    private static final String FILE_NAME = "app_state.dat";

    public static void save(BookStore store) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(store);
            System.out.println("[Система] Состояние программы успешно сохранено в " + FILE_NAME);
        } catch (IOException e) {
            System.out.println("[Система] Ошибка при сохранении состояния: " + e.getMessage());
        }
    }

    public static BookStore load() {
        File file = new File(FILE_NAME);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                BookStore store = (BookStore) ois.readObject();
                System.out.println("[Система] Состояние программы успешно восстановлено.");
                return store;
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("[Система] Ошибка при восстановлении состояния: " + e.getMessage());
            }
        }
        System.out.println("[Система] Файл сохранения не найден. Создан новый пустой склад.");
        return new BookStore();
    }
}