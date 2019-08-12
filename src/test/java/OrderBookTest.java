import org.junit.jupiter.api.*;
import trade.Order;
import trade.OrderBook;
import trade.OrderSide;
import trade.Order_Type;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Random;

import static trade.OrderProcessorHelper.randomPrice;
import static trade.OrderProcessorHelper.randomQuntity;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OrderBookTest {

    private OrderBook orderBook;
    private Random random;

    @BeforeAll
    void initialize() {
        orderBook = new OrderBook(1);
        random = new Random();
    }

    @BeforeEach
    void clearOrderBook() {
        orderBook.reset();
    }


    @Test
    void addNewOrderTestShouldReturnTrue() {
        Order randomBobAskLimitOrder = new Order(new Date().getTime(), Order_Type.LIMIT, randomQuntity(random), "Bob", OrderSide.ASK, randomPrice(random));
        orderBook.addOrder(randomBobAskLimitOrder, new Date().getTime());
        Assertions.assertEquals(randomBobAskLimitOrder, orderBook.getAsks().getOrderById(0));

        Order randomBilBidLimitOrder = new Order(new Date().getTime(), Order_Type.LIMIT, randomQuntity(random), "Bil", OrderSide.BID, randomPrice(random));
        orderBook.addOrder(randomBilBidLimitOrder, new Date().getTime());
        Assertions.assertEquals(randomBilBidLimitOrder, orderBook.getBids().getOrderById(1));
    }

    @Test
    void deleteOrderFromOrderBook() {
        // add order
        Order randomBobAskLimitOrder = new Order(new Date().getTime(), Order_Type.LIMIT, randomQuntity(random), "Bob", OrderSide.ASK, randomPrice(random));
        orderBook.addOrder(randomBobAskLimitOrder, new Date().getTime());
        Assertions.assertEquals(randomBobAskLimitOrder, orderBook.getAsks().getOrderById(0));

        //deleteOrder
        orderBook.deleteOrder(randomBobAskLimitOrder.getOrderSide(), randomBobAskLimitOrder.getOrderId(), new Date().getTime());
        Assertions.assertNull(orderBook.getAsks().getOrderById(0));
    }

    @Test
    void processOrder() {

        Order randomBobAskLimitOrder = new Order(new Date().getTime(), Order_Type.LIMIT, 20, "Bob", OrderSide.ASK, BigDecimal.TEN);
        orderBook.addOrder(randomBobAskLimitOrder, new Date().getTime());

        Order randomBilBidLimitOrder = new Order(new Date().getTime(), Order_Type.LIMIT, 20, "Bil", OrderSide.BID, BigDecimal.TEN);
        orderBook.addOrder(randomBilBidLimitOrder, new Date().getTime());

        orderBook.processOrder(randomBilBidLimitOrder);

        Assertions.assertNotNull(orderBook.getDeals().get(0));
    }

    @Test
    void updateOrder() {
        Order randomBobAskLimitOrder = new Order(new Date().getTime(), Order_Type.LIMIT, 20, "Bob", OrderSide.ASK, BigDecimal.TEN);
        orderBook.addOrder(randomBobAskLimitOrder, new Date().getTime());

        Order updatedOrder = new Order(new Date().getTime(), Order_Type.LIMIT, 30, "Bob", OrderSide.ASK, BigDecimal.TEN);
        updatedOrder.setOrderId(0);

        orderBook.updateOrder(updatedOrder, new Date().getTime());
        Assertions.assertEquals(30, orderBook.getAsks().getOrderById(0).getQuantity());

    }

    @Test
    void getBestBid() {
        orderBook.addOrder(new Order(new Date().getTime(), Order_Type.LIMIT, randomQuntity(random), "Bob", OrderSide.BID, BigDecimal.valueOf(8)), new Date().getTime());
        orderBook.addOrder(new Order(new Date().getTime(), Order_Type.LIMIT, randomQuntity(random), "Bil", OrderSide.BID, BigDecimal.valueOf(7)), new Date().getTime());
        orderBook.addOrder(new Order(new Date().getTime(), Order_Type.LIMIT, randomQuntity(random), "Art", OrderSide.BID, BigDecimal.valueOf(6)), new Date().getTime());
        Assertions.assertEquals(BigDecimal.valueOf(8), orderBook.getBestBid());
    }


    @Test
    void getBestOffer() {
        orderBook.addOrder(new Order(new Date().getTime(), Order_Type.LIMIT, randomQuntity(random), "Bob", OrderSide.ASK, BigDecimal.valueOf(1)), new Date().getTime());
        orderBook.addOrder(new Order(new Date().getTime(), Order_Type.LIMIT, randomQuntity(random), "Bil", OrderSide.ASK, BigDecimal.valueOf(2)), new Date().getTime());
        orderBook.addOrder(new Order(new Date().getTime(), Order_Type.LIMIT, randomQuntity(random), "Art", OrderSide.ASK, BigDecimal.valueOf(3)), new Date().getTime());
        Assertions.assertEquals(BigDecimal.valueOf(1), orderBook.getBestOffer());
    }
}
