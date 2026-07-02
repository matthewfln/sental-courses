package thread;

public interface DataStorage {
    void addData(int data) throws InterruptedException;
    int extractData() throws InterruptedException;
}