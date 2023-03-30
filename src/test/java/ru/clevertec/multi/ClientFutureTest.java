package ru.clevertec.multi;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


class ClientFutureTest {
    private static Server server;
    private static ClientFuture client;
    private static int N;
    @BeforeAll
    static void init(){
        N = 100;
        server = new Server();
        client = new ClientFuture(N, server);
        client.go();
    }

    @Test
    void accumulatorTest() {
        Assertions.assertThat(client.getAccumulator()).hasValue((1+N) * (N/2));
    }
    @Test
    void dataListTest(){
        Assertions.assertThat(client.getData()).hasSize(0);
    }
    @Test
    void serverListSizeTest(){
        Assertions.assertThat(server.getStore()).hasSize(N);
    }
    @Test
    void serverListContainTest(){
        boolean allContain = true;
        for (int i = 1; i <= N; i++) {
            allContain &= server.getStore().contains(Integer.valueOf(i));
        }
        Assertions.assertThat(allContain).isTrue();
    }
}