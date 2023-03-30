package ru.clevertec.multi;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ClientFuture implements Callable<CompletableFuture<Void>> {
    private List<Integer> data;
    private AtomicLong accumulator = new AtomicLong();
    private Server server;
    private Lock lock = new ReentrantLock();

    public ClientFuture(int n, Server server) {
        data = new ArrayList<>(n);
        for (int i = 1; i <= n; i++) {
            data.add(i);
        }
        this.server = server;
    }

    public List<Integer> getData() {
        return data;
    }

    public AtomicLong getAccumulator() {
        return accumulator;
    }

    public void go() {
        int size = data.size();
        ExecutorService executor = Executors.newCachedThreadPool();
        List<Future<CompletableFuture<Void>>> futures = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            futures.add(executor.submit(this::call));
        }
        CompletableFuture[] futureArr = futures.stream().map(f-> {
            try {
                return f.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }).toArray(CompletableFuture[]::new);
        CompletableFuture.allOf(futureArr).join();
        executor.shutdown();
    }

    @Override
    public CompletableFuture<Void> call() {
        int val = 0;
        try {
            lock.lock();
            int size = data.size();
            int pos = (int) Math.floor(Math.random() * size);
            val = data.remove(pos);
        } finally {
            lock.unlock();
        }
        Exchanger exchanger = new Exchanger(val);
        return CompletableFuture.supplyAsync(() -> server.handle(exchanger))
                .thenAccept(answer -> accumulator.addAndGet(answer.getVal()));
    }
}
