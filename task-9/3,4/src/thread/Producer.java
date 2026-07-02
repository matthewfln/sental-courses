package thread;

public class Producer implements Runnable {
    private static final int MAX_NUMBER = 100;
    private final DataStorage storage;

    public Producer(DataStorage storage) {
        this.storage = storage;
    }

    @Override
    public void run() {
        try {
            while (true) {
                int randomValue = (new java.util.Random()).nextInt(MAX_NUMBER);
                storage.addData(randomValue);

                // Имитация времени на производство
                Thread.sleep(300);
            }
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            System.out.println("Поток производителя был прерван.");
        }
    }
}