package thread;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class SystemTimePrinter implements Runnable {
    private final int intervalSeconds;

    public SystemTimePrinter(int intervalSeconds) {
        this.intervalSeconds = intervalSeconds;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                String currentTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                System.out.println("Системное время: " + currentTime);

                Thread.sleep(intervalSeconds * 1000L);
            }
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            System.out.println("Служебный поток вывода времени был прерван.");
        }
    }
}