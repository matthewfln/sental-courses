import config.Configurator;
import util.Config;
import di.Injector;
import service.BookStore;
import ui.AppController;
import ui.ConsoleView;
import util.AppStateStorage;

public class Main {
    public static void main(String[] args) {

        Configurator configurator = new Configurator();
        configurator.configure(Config.getInstance());

        BookStore store = AppStateStorage.load(); // Модель
        ConsoleView view = ConsoleView.getInstance(); // Представление
        AppController controller = new AppController(); // Контроллер

        Injector injector = new Injector();
        injector.register(store);
        injector.register(view);
        injector.inject(controller);

        controller.start();
    }
}