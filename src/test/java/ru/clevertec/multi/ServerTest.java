package ru.clevertec.multi;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;


class ServerTest {

    @Test
    void handleTest() {
        Server server = new Server();
        server.handle(new Exchanger(1));
        server.handle(new Exchanger(2));
        Assertions.assertThat(server.getStore()).hasSize(2);
    }
}