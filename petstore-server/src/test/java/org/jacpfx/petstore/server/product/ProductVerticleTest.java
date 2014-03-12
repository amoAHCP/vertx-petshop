package org.jacpfx.petstore.server.product;

import org.junit.Before;
import org.junit.Test;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.VertxFactory;
import org.vertx.java.core.http.HttpClient;
import org.vertx.java.core.http.WebSocket;
import org.vertx.java.platform.PlatformLocator;
import org.vertx.java.platform.PlatformManager;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.*;

import static org.junit.Assert.assertNotNull;

/**
 * Created by amo on 25.02.14.
 */
public class ProductVerticleTest {

    private ExecutorService executor = Executors.newCachedThreadPool();
    PlatformManager pm;
    @Before
    public void onStart() throws MalformedURLException, InterruptedException {
        pm = connect(100);
    }

    private PlatformManager connect(int instances) throws MalformedURLException, InterruptedException {
        final PlatformManager pm = PlatformLocator.factory.createPlatformManager();
        final CountDownLatch waitForDeploy = new CountDownLatch(1);
        pm.deployVerticle("org.jacpfx.petstore.server.product.ProductVerticle",
                null,
                new URL[]{new File(".").toURI().toURL()},
                instances,
                null,
                (event) -> {
                    if (event.succeeded()) waitForDeploy.countDown();
                });
        waitForDeploy.await(1000, TimeUnit.MILLISECONDS);
        return pm;

    }

    private HttpClient getClient(final Handler<WebSocket> handler, final String path) {

        Vertx vertx = VertxFactory.newVertx();
        HttpClient client = vertx.
                createHttpClient().
                setHost("localhost").
                setPort(8080).
                connectWebsocket(path, handler);

        return client;
    }

    @Test
    public void getAllProductsInitialConnect() throws InterruptedException, IOException {
        CountDownLatch stopLatch = new CountDownLatch(1);
        CountDownLatch latch = new CountDownLatch(1);
        CountDownLatch latch2 = new CountDownLatch(1);
        final WebSocket[] wsTemp = new WebSocket[1];
        HttpClient client = getClient((ws) -> {
            System.out.println("connected");
            latch.countDown();
            wsTemp[0] = ws;
            ws.dataHandler((data) -> {
                System.out.println("client data handler:" + data);
                assertNotNull(data.getString(0, data.length()));
                latch2.countDown();
            });
        }, "/all");

        latch.await();

        assertNotNull(wsTemp[0]);

        //  wsTemp[0].writeTextFrame("");
        latch2.await();


    }

    @Test
    public void getAllProducts() throws InterruptedException, IOException {


        CountDownLatch latch = new CountDownLatch(1);
        CountDownLatch latch2 = new CountDownLatch(2);
        final WebSocket[] wsTemp = new WebSocket[1];
        HttpClient client = getClient((ws) -> {
            // Connected!^                                         ^
            System.out.println("connected");
            latch.countDown();
            wsTemp[0] = ws;
            ws.dataHandler((data) -> {


                System.out.println("client data handler:" + data);
                assertNotNull(data.getString(0, data.length()));
                latch2.countDown();
            });
        }, "/all");

        latch.await();

        assertNotNull(wsTemp[0]);
        // second connect
        wsTemp[0].writeTextFrame("");
        latch2.await();
    }

    @Test
    public void testMultithreadedProductGet() throws MalformedURLException, InterruptedException {
        int amount =100;
        CountDownLatch outer = new CountDownLatch(100);
        CountDownLatch inner = new CountDownLatch(100);

        while(amount!=0) {
             executor.submit(() ->{

                 final WebSocket[] wsTemp = new WebSocket[1];
                 HttpClient client = getClient((ws) -> {
                     // Connected!^                                         ^
                     System.out.println("connected");
                     outer.countDown();
                     wsTemp[0] = ws;
                     ws.dataHandler((data) -> {


                         System.out.println("client data handler:" + data);
                         assertNotNull(data.getString(0, data.length()));
                         inner.countDown();
                     });
                 }, "/all");

                 assertNotNull(wsTemp[0]);
             });

            amount--;
        }
        outer.await();
        inner.await();
    }

    public void testGetAllAddSingleAndGetAll() throws MalformedURLException, InterruptedException {
        PlatformManager pm = connect(1);


        CountDownLatch outer = new CountDownLatch(1);
        CountDownLatch inner = new CountDownLatch(2);
        final WebSocket[] wsTemp = new WebSocket[1];
        HttpClient client = getClient((ws) -> {
            // Connected!^                                         ^
            System.out.println("connected");
            outer.countDown();
            wsTemp[0] = ws;
            ws.dataHandler((data) -> {


                System.out.println("client data handler:" + data);
                assertNotNull(data.getString(0, data.length()));
                inner.countDown();
            });
        }, "/all");

        outer.await();

        assertNotNull(wsTemp[0]);

        ///// add a product
        final WebSocket[] wsSendTemp = new WebSocket[1];
        HttpClient clientAdd = getClient((ws) -> {
            // Connected!^                                         ^
            System.out.println("connected");
            outer.countDown();
            wsSendTemp[0] = ws;

        }, "/update");


        // second connect
        wsTemp[0].writeTextFrame("");
        inner.await();
        pm.stop();
    }
}
