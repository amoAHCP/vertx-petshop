package org.jacpfx.petstore.server.product;

import org.jacpfx.petstore.util.MessageUtil;
import org.junit.Before;
import org.junit.Test;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.VertxFactory;
import org.vertx.java.core.http.HttpClient;
import org.vertx.java.core.http.WebSocket;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.assertNotNull;

/**
 * Created by amo on 25.02.14.
 */
public class ProductVerticleTest {
    Vertx vertx =null;

    @Before
    public void onStart() {
        vertx = VertxFactory.newVertx();
    }

    @Test
    public void getAllProducts() throws InterruptedException, IOException {
        CountDownLatch latch = new CountDownLatch(1);
        CountDownLatch latch2 = new CountDownLatch(1);
        final WebSocket[] wsTemp=new WebSocket[1];
        HttpClient client = vertx.
                createHttpClient().
                setHost("localhost").
                setPort(8080).
                connectWebsocket("/all", new Handler<WebSocket>() {
            public void handle(WebSocket ws) {
                // Connected!^                                         ^
                System.out.println("connected");
                latch.countDown();
                wsTemp[0]= ws;
                ws.dataHandler((data)->{


                    System.out.println("client data handler:" + data);
                    assertNotNull(data.getString(0,data.length()));
                    latch2.countDown();
                });

            }
        });
        latch.await();

        assertNotNull(wsTemp[0]);

        wsTemp[0].writeTextFrame("");
        latch2.await();

    }
}
