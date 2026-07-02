package thread;

public class ThreadStateDemonstrator {

    public static void main(String[] args) throws InterruptedException {
        Object lock = new Object();

        DemonstrationTask task = new DemonstrationTask(lock);

        Thread thread = new Thread(task);
        Thread thread2 = new Thread(task);
        thread.setName("Поток-1");
        thread2.setName("Поток-2");

        // Задание 1
        System.out.println("1. Состояние после создания: " + thread.getState()); // состояние NEW - поток создан

        thread.start();
        System.out.println("2. Состояние после вызова start(): " + thread.getState()); // состояние RUNNABLE - поток готов к выполнению

        Thread.sleep(200);
        System.out.println("3. Состояние во время Thread.sleep(): " + thread.getState()); // состояние TIMED_WAITING - поток спит

        Thread.sleep(1500);
        System.out.println("4. Состояние во время Object.wait(): " + thread.getState()); // состояние WAITING - поток бесконечно ждет notify()

        synchronized (lock) {
            lock.notify();
            Thread.sleep(200);

            System.out.println("5. Состояние при ожидании монитора: " + thread.getState()); // состояние BLOCKED - поток ожидает освобождения монитора
        }

        thread.join();
        thread2.join();

        System.out.println("6. Состояние после завершения работы: " + thread.getState()); // состояние TERMINATED

        // Задание 2
        System.out.println(thread.getName());
        System.out.println(thread2.getName());
    }
}