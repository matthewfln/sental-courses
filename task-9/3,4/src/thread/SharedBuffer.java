package thread;

import java.util.LinkedList;
import java.util.Queue;

public class SharedBuffer implements DataStorage {
    private final Queue<Integer> queue;
    private final int maxCapacity;

    public SharedBuffer(int maxCapacity) {
        this.queue = new LinkedList<>();
        this.maxCapacity = maxCapacity;
    }

    @Override
    public synchronized void addData(int data) throws InterruptedException {
        // Если буфер заполнен - производитель должен ждать
        while (queue.size() == maxCapacity) {
            wait();
        }

        queue.add(data);
        System.out.println("Производитель создал: " + data + " (Размер буфера: " + queue.size() + ")");

        // Оповещаем другие потоки
        notifyAll();
    }

    @Override
    public synchronized int extractData() throws InterruptedException {
        // Если буфер пуст - потребитель должен ждать
        while (queue.isEmpty()) {
            wait();
        }

        int data = queue.poll();
        System.out.println("Потребитель забрал: " + data + " (Размер буфера: " + queue.size() + ")");

        // Оповещаем другие потоки
        notifyAll();

        return data;
    }
}