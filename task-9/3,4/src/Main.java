import thread.*;

public class Main {
    public static void main(String[] args) { // Для задания 3
        int bufferCapacity = 5;

        // Создаем общий ресурс
        DataStorage sharedStorage = new SharedBuffer(bufferCapacity);

        // Создаем потоки и передаем им общий ресурс
        Thread producerThread = new Thread(new Producer(sharedStorage));
        Thread consumerThread = new Thread(new Consumer(sharedStorage));

        System.out.println("Запуск программы");

        producerThread.start();
        consumerThread.start();
    }

    
//    Для задания 4:

//    public static void main(String[] args) {
//        int intervalSeconds = 2;
//
//        Runnable timeTask = new SystemTimePrinter(intervalSeconds);
//
//        Thread daemonThread = new Thread(timeTask); // Создаем поток
//        daemonThread.setDaemon(true);
//
//        System.out.println("Главный поток начал работу.");
//
//        daemonThread.start(); // Запускаем служебный поток
//        try {
//            Thread.sleep(7000);
//        } catch (InterruptedException exception) {
//            Thread.currentThread().interrupt();
//        }
//
//        System.out.println("Главный поток завершил работу. Программа закрывается.");
//    }
}
