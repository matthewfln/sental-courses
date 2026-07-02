import service.BookStore;
import ui.AppController;
import ui.ConsoleView;
import util.AppStateStorage;

public class Main {
    public static void main(String[] args) {
        BookStore store = AppStateStorage.load(); // Модель
        ConsoleView view = ConsoleView.getInstance(); // Представление
        AppController controller = new AppController(store, view); // Контроллер

        controller.start();
    }
}