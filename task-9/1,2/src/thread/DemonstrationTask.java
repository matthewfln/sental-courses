package thread;

class DemonstrationTask implements Runnable {

    private final Object lock;

    public DemonstrationTask(Object lock) {
        this.lock = lock;
    }

    @Override
    public void run() {
        try {
            // Переводим поток в состояние TIMED_WAITING
            Thread.sleep(1000);

            // Синхронизируемся по монитору и переводим поток в состояние WAITING
            synchronized (lock) {
                lock.wait();
            }
        } catch (InterruptedException e) {
            System.out.println("Поток был прерван во время ожидания.");
        }
    }
}