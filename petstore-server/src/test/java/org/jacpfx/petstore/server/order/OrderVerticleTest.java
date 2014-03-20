package org.jacpfx.petstore.server.order;

import com.google.gson.Gson;
import org.jacpfx.petstore.dto.OrderListDTO;
import org.jacpfx.petstore.model.*;
import org.jacpfx.petstore.util.MessageUtil;
import org.jacpfx.petstore.util.Serializer;
import org.junit.BeforeClass;
import org.junit.Test;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.VertxFactory;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.HttpClient;
import org.vertx.java.core.http.WebSocket;
import org.vertx.java.platform.PlatformLocator;
import org.vertx.java.platform.PlatformManager;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * Created by amo on 17.03.14.
 */
public class OrderVerticleTest {
    static final PlatformManager pm = PlatformLocator.factory.createPlatformManager();
    private Gson parser = new Gson();

    @BeforeClass
    public static void onStart() throws MalformedURLException, InterruptedException {
        // if server was started manually, uncomment this:
        connect(1);
    }


    private static PlatformManager connect(int instances) throws MalformedURLException, InterruptedException {

        final CountDownLatch waitForDeploy = new CountDownLatch(1);
        pm.deployVerticle("org.jacpfx.petstore.server.order.OrderVerticle",
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
                setPort(9090).
                connectWebsocket(path, handler);

        return client;
    }

    @Test
    public void testAndUpdate() throws InterruptedException, IOException {
        CountDownLatch outer = new CountDownLatch(2);
        CountDownLatch inner = new CountDownLatch(5);
        final WebSocket[] wsTemp = new WebSocket[1];
        Set<Order> allOrders = new HashSet<>();
        HttpClient client = getClient((ws) -> {
            // Connected!^                                         ^
            outer.countDown();
            wsTemp[0] = ws;
            ws.dataHandler((data) -> {
                assertNotNull(data);
                OrderListDTO orders = null;
                try {
                    orders = MessageUtil.getMessage(data.getBytes(), OrderListDTO.class);
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
                assertNotNull(orders);
                allOrders.clear();
                allOrders.addAll(orders.getOrders());
                System.out.println("client data handler 2:" + orders.getState());

                inner.countDown();
            });
        }, "/getAllOrders");
        final WebSocket[] wsUpdateTemp = new WebSocket[1];
        HttpClient updateClient = getClient((ws) -> {
            // Connected!^                                         ^
            outer.countDown();
            wsUpdateTemp[0] = ws;

        }, "/updateOrder");


        outer.await();

        wsUpdateTemp[0].writeBinaryFrame(new Buffer(Serializer.serialize(new Order(1, new Customer("a", "b", "aa@gmx.ch", new Address("1", "2", "3", "CH")), new Basket(Arrays.asList(new BasketItem(new Product(4L, "pet", "", 5D, 2, "s"), 3)))))));
        wsUpdateTemp[0].writeBinaryFrame(new Buffer(Serializer.serialize(new Order(2, new Customer("a", "b", "aa@gmx.ch", new Address("1", "2", "3", "CH")), new Basket(Arrays.asList(new BasketItem(new Product(4L, "pet", "", 5D, 2, "qs"), 1)))))));
        wsUpdateTemp[0].writeBinaryFrame(new Buffer(Serializer.serialize(new Order(3, new Customer("a", "b", "aa@gmx.ch", new Address("1", "2", "3", "CH")), new Basket(Arrays.asList(new BasketItem(new Product(4L, "pet", "", 5D, 2, "r"), 4)))))));
        wsUpdateTemp[0].writeBinaryFrame(new Buffer(Serializer.serialize(new Order(4, new Customer("a", "b", "aa@gmx.ch", new Address("1", "2", "3", "CH")), new Basket(Arrays.asList(new BasketItem(new Product(4L, "pet", "", 5D, 2, "asdf"), 2)))))));

        inner.await();
        assertFalse(allOrders.isEmpty());
        assertTrue(allOrders.size() == 1);
    }

    @Test
    public void orderTest() throws InterruptedException {
        CountDownLatch outer = new CountDownLatch(1);
        CountDownLatch inner = new CountDownLatch(1);
        final WebSocket[] wsUpdateTemp = new WebSocket[1];
        HttpClient updateClient = getClient((ws) -> {
            // Connected!^                                         ^
            outer.countDown();
            wsUpdateTemp[0] = ws;
            ws.dataHandler((data) -> {
                assertNotNull(data);

                System.out.println("client Order:" + data);

                inner.countDown();
            });

        }, "/placeOrder");
        outer.await();
        Order order = new Order(1, new Customer("a", "b", "aa@gmx.ch", new Address("1", "2", "3", "CH")),new Basket(Arrays.asList(new BasketItem(new Product(4L, "pet", "", 5D, 2, "asdf"), 2))));

        String orderJson = parser.toJson(order);
        wsUpdateTemp[0].writeTextFrame(orderJson);
        inner.await();
    }


}
