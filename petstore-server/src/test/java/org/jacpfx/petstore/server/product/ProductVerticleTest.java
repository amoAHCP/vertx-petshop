package org.jacpfx.petstore.server.product;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jacpfx.petstore.dto.ProductListDTO;
import org.jacpfx.petstore.model.Product;
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
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * Created by amo on 25.02.14.
 */
public class ProductVerticleTest {

    private ExecutorService executor = Executors.newCachedThreadPool();
    static final PlatformManager pm = PlatformLocator.factory.createPlatformManager();
    private Gson parser = new Gson();
    @BeforeClass
    public static void onStart() throws MalformedURLException, InterruptedException {
        // if server was started manually, uncomment this:
        connect(10);
    }



    private static PlatformManager connect(int instances) throws MalformedURLException, InterruptedException {

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
    public void getAllProductsOnConnect() throws InterruptedException, IOException {
        CountDownLatch latch = new CountDownLatch(1);
        CountDownLatch latch2 = new CountDownLatch(1);
        final WebSocket[] wsTemp = new WebSocket[1];
        HttpClient client = getClient((ws) -> {
            latch.countDown();
            wsTemp[0] = ws;
            ws.dataHandler((data) -> {
                System.out.println("client data handler 1:" + data);
                assertNotNull(data.getString(0, data.length()));
                latch2.countDown();
            });
        }, "/all");

        latch.await();

        assertNotNull(wsTemp[0]);

        //  wsTemp[0].writeTextFrame("");
        latch2.await();

         client.close();
    }

    @Test
    public void getAllProducts() throws InterruptedException, IOException {


        CountDownLatch latch = new CountDownLatch(1);
        CountDownLatch latch2 = new CountDownLatch(2);
        final WebSocket[] wsTemp = new WebSocket[1];
        HttpClient client = getClient((ws) -> {
            // Connected!^                                         ^
            latch.countDown();
            wsTemp[0] = ws;
            ws.dataHandler((data) -> {


                System.out.println("client data handler 2:" + data);
                assertNotNull(data.getString(0, data.length()));
                latch2.countDown();
            });
        }, "/all");

        latch.await();

        assertNotNull(wsTemp[0]);
        // connect and receive products
        wsTemp[0].writeTextFrame("");
        latch2.await();
        client.close();
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
                     outer.countDown();
                     wsTemp[0] = ws;
                     ws.dataHandler((data) -> {


                         System.out.println("client data handler 3:" + data);
                         assertNotNull(data.getString(0, data.length()));
                         inner.countDown();
                     });
                 }, "/all");

                 assertNotNull(wsTemp[0]);
                 client.close();
             });

            amount--;
        }
        outer.await();
        inner.await();
    }


   @Test
    /**
     * returns initial product list, add new product check... add a second product and check again
     */
    public void testGetAllAddSingleAndGetAll() throws IOException, InterruptedException {


        CountDownLatch outer = new CountDownLatch(1);
        CountDownLatch outerUpdate = new CountDownLatch(1);
        CountDownLatch[] inner = {new CountDownLatch(1)};
        final WebSocket[] wsTemp = new WebSocket[1];
        List<Product> productList = new ArrayList<>();
        HttpClient client = getClient((ws) -> {
            // Connected!^                                         ^
            outer.countDown();
            wsTemp[0] = ws;
            ws.dataHandler((data) -> {


                System.out.println("client data handler 4:" + data);
                Type collectionType = new TypeToken<List<Product>>(){}.getType();
                String json =  data.getString(0, data.length());
                assertNotNull(json);

                productList.addAll(parser.fromJson(json, ProductListDTO.class).getProducts());
                inner[0].countDown();
            });
        }, "/all");

        outer.await();
        inner[0].await();
        assertNotNull(wsTemp[0]);
        assertFalse(productList.isEmpty());
        inner[0] = new CountDownLatch(1);
        // store initial product list size
        int size = productList.size();

        ///// add a product
        final WebSocket[] wsSendTemp = new WebSocket[1];
        HttpClient clientAdd = getClient((ws) -> {
            // Connected!^                                         ^
            System.out.println("connected to update");

            wsSendTemp[0] = ws;
            outerUpdate.countDown();

        }, "/update");
        outerUpdate.await();
        Product p1 = new Product(100L,"KatzeGrau","",10,2,"ziemlich alt");
        // send product
        wsSendTemp[0].write(new Buffer(Serializer.serialize(p1)));
        inner[0].await();
        assertFalse(productList.isEmpty());
        assertTrue(size+1==productList.size());

        inner[0] = new CountDownLatch(1);
        Product p2 = new Product(101L,"Hund","",10,5,"weisser Hund");
        // send product
        wsSendTemp[0].write(new Buffer(Serializer.serialize(p2)));

        inner[0].await();
        assertFalse(productList.isEmpty());
        assertTrue(size+2==productList.size());
       client.close();

    }

    @Test
    /**
     * returns initial product list, add a list of products check... add a new list of products and check again
     */
    public void testGetAllAddListAndGetAll() throws IOException, InterruptedException {


        CountDownLatch outer = new CountDownLatch(1);
        CountDownLatch outerUpdate = new CountDownLatch(1);
        CountDownLatch[] inner = {new CountDownLatch(1)};
        final WebSocket[] wsTemp = new WebSocket[1];
        List<Product> productList = new ArrayList<>();
        HttpClient client = getClient((ws) -> {
            // Connected!^                                         ^
            outer.countDown();
            wsTemp[0] = ws;
            ws.dataHandler((data) -> {


                System.out.println("client data handler 5:" + data);
                Type collectionType = new TypeToken<List<Product>>(){}.getType();
                String json =  data.getString(0, data.length());
                assertNotNull(json);

                productList.addAll(parser.fromJson(json, ProductListDTO.class).getProducts());
                inner[0].countDown();
            });
        }, "/all");

        outer.await();
        inner[0].await();
        assertNotNull(wsTemp[0]);
        assertFalse(productList.isEmpty());
        inner[0] = new CountDownLatch(1);


        ///// add a product
        final WebSocket[] wsSendTemp = new WebSocket[1];
        HttpClient clientAdd = getClient((ws) -> {
            // Connected!^                                         ^
            System.out.println("connected to update");

            wsSendTemp[0] = ws;
            outerUpdate.countDown();

        }, "/updateAll");
        outerUpdate.await();
        // send product
        wsSendTemp[0].write(new Buffer(Serializer.serialize(new ProductListDTO(ProductListDTO.State.UPDATE,Arrays.asList(new Product(100L,"KatzeGrau","box1.png",10,1,"kaum Fell"),new Product(101L,"HundWeiss","box2.png",10,1,"Sonderling"))))));
        inner[0].await();
        assertFalse(productList.isEmpty());
        assertTrue(9==productList.size());

        inner[0] = new CountDownLatch(1);
        // send product
        wsSendTemp[0].write(new Buffer(Serializer.serialize(new ProductListDTO(ProductListDTO.State.UPDATE,Arrays.asList(new Product(103L,"Irgendeintier","box1.png",10,2,"noch nie gesehen"),new Product(100L,"KatzeGrau","box2.png",10,10,"frisst viel"),new Product(101L,"HundWeiss","box3.png",10,3,"schon wieder"))))));

        inner[0].await();
        assertFalse(productList.isEmpty());
        assertTrue(9==productList.size());
        client.close();
    }
}
