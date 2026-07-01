import enums.BookStatus;
import enums.OrderStatus;
import model.Book;
import model.Order;
import service.BookStore;
import ui.AppController;
import ui.ConsoleView;


public class Main {
    public static void main(String[] args) {
        BookStore store = new BookStore(); // Модель
        ConsoleView view = ConsoleView.getInstance(); // Представление
        AppController controller = new AppController(store, view); // Контроллер

        controller.start();
    }
}