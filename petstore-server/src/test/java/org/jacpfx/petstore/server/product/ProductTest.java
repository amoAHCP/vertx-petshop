package org.jacpfx.petstore.server.product;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.vertx.java.core.http.HttpClient;
import org.vertx.java.core.http.WebSocket;
import org.vertx.testtools.JavaClassRunner;
import org.vertx.testtools.TestVerticle;
import org.vertx.testtools.VertxAssert;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertNotNull;

/**
 * Created by amo on 13.03.14.
 */
@RunWith(JavaClassRunner.class)
public class ProductTest extends TestVerticle {

    @Override
    public void start()  {
        initialize();
        container.deployVerticle("org.jacpfx.petstore.server.product.ProductVerticle",1,(event)->{
            System.out.println(event+"   GGGG");
        });
        startTests();
        System.out.println("BEFORE");

    }

    @Before
    public void onStart() throws MalformedURLException, InterruptedException {
        System.out.println("BEFORE");
    }

    @Test
    public void getAllProductsInitialConnect() throws InterruptedException, IOException {
        final CountDownLatch waitForDeploy = new CountDownLatch(1);

        /*try {
            waitForDeploy.await(1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        CountDownLatch latch = new CountDownLatch(1);
        CountDownLatch latch2 = new CountDownLatch(1);
        final WebSocket[] wsTemp = new WebSocket[1];
        vertx.
                createHttpClient().
                setHost("localhost").
                setPort(8080).
                connectWebsocket("/all", (ws) -> {
            System.out.println("connected");
            latch.countDown();
            wsTemp[0] = ws;
            ws.dataHandler((data) -> {
                System.out.println("client data handler:" + data);
                assertNotNull(data.getString(0, data.length()));
                latch2.countDown();
            });
        });

     //   latch.await();

      //  assertNotNull(wsTemp[0]);

        //  wsTemp[0].writeTextFrame("");
        latch2.await();

        VertxAssert.testComplete();
    }
}
