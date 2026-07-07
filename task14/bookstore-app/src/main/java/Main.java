import config.AppConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ui.AppController;

public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        AppController controller = context.getBean(AppController.class);
        controller.start();

        context.close();
    }
}
