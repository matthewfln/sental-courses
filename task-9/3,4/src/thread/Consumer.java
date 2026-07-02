package thread;

public class Consumer implements Runnable {
    private final DataStorage storage;

    public Consumer(DataStorage storage) {
        this.storage = storage;
    }

    @Override
    public void run() {
        try {
            while (true) {
                storage.extractData();

                // Имитация времени на обработку
                Thread.sleep(800);
            }
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            System.out.println("Поток потребителя был прерван.");
        }
    }
}