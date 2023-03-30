package ru.clevertec.multi;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Server {
    private static final Random random = new Random();
    private List<Integer> store = new ArrayList<>();
    private final Lock lock = new ReentrantLock();

    public List<Integer> getStore() {
        return store;
    }

    public Exchanger handle(Exchanger data) {
        randomDelay();
        int val = data.getVal();
        int size = 0;
        try {
            lock.lock();
            store.add(val);
            size = store.size();
        } finally {
            lock.unlock();
        }
        return new Exchanger(size);
    }

    public static void randomDelay() {
        int delay = 100 + random.nextInt(900);
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
