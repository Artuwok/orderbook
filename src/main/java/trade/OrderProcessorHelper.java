package trade;

import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Random;

public class OrderProcessorHelper {

    private static Logger log = Logger.getLogger(OrderProcessorHelper.class);

    public static int processOrderList(OrderBook orderBook, ArrayList<Deal> deals, OrderList orders,
                                       int remainingQuantity, Order order) {
        OrderSide side = order.getOrderSide();
        String buyer, seller;
        String takerId = order.getVenue();
        long time = order.getOrderCreationTime();
        while ((orders.getLength() > 0) && (remainingQuantity > 0)) {
            int treadedQuantity;
            Order headOrder = orders.getHeadOrder();
            if (remainingQuantity < headOrder.getQuantity()) {
                treadedQuantity = remainingQuantity;
                switch (side) {
                    case ASK:
                        orderBook.getBids().updateOrderQuantity(headOrder.getQuantity() - remainingQuantity,
                                headOrder.getOrderId());
                        break;
                    case BID:
                        orderBook.getAsks().updateOrderQuantity(headOrder.getQuantity() - remainingQuantity,
                                headOrder.getOrderId());
                        break;
                }

                remainingQuantity = 0;
            } else {
                treadedQuantity = headOrder.getQuantity();
                switch (side) {
                    case ASK:
                        orderBook.getBids().removeOrderById(headOrder.getOrderId());
                        break;
                    case BID:
                        orderBook.getAsks().removeOrderById(headOrder.getOrderId());
                        break;
                }

                remainingQuantity = remainingQuantity - treadedQuantity;
            }
            if (side == OrderSide.ASK) {
                buyer = headOrder.getVenue();
                seller = takerId;
            } else {
                buyer = takerId;
                seller = headOrder.getVenue();
            }
            Deal deal =
                    new Deal(time,
                            headOrder.getPrice(),
                            treadedQuantity,
                            buyer,
                            seller,
                            headOrder.getOrderId()
                    );
            log.info("Order was processed");
            deals.add(deal);
            orderBook.getDeals().add(deal);
        }
        return remainingQuantity;
    }

    public static void processMarketOrder(OrderBook orderBook, Order order) {
        ArrayList<Deal> deals = new ArrayList<>();
        int quantityRemaining = order.getQuantity();

        switch (order.getOrderSide()) {
            case BID:
                while ((quantityRemaining > 0) && (orderBook.getAsks().getCountOfOrders() > 0)) {
                    OrderList ordersAtBest = orderBook.getAsks().getMinPriceList();
                    quantityRemaining = processOrderList(orderBook, deals, ordersAtBest, quantityRemaining,
                            order);
                }
                break;
            case ASK:
                while ((quantityRemaining > 0) && (orderBook.getBids().getCountOfOrders() > 0)) {
                    OrderList ordersAtBest = orderBook.getBids().getMaxPriceList();
                    quantityRemaining = processOrderList(orderBook, deals, ordersAtBest, quantityRemaining,
                            order);
                }
                break;
            default:
                throw new IllegalArgumentException("order neither market nor limit: " +
                        order.getOrderSide());
        }
    }

    public static void processLimitOrder(OrderBook orderBook, Order order) {

        ArrayList<Deal> trades = new ArrayList<>();
        OrderSide side = order.getOrderSide();
        int quantityRemaining = order.getQuantity();
        BigDecimal price = order.getPrice();
        if (side == OrderSide.BID) {

            while ((orderBook.getAsks().getCountOfOrders() > 0) &&
                    (quantityRemaining > 0) &&
                    (price.compareTo(orderBook.getAsks().getMinPrice())) >= 0) {
                OrderList ordersAtBest = orderBook.getAsks().getMinPriceList();
                quantityRemaining = processOrderList(orderBook, trades, ordersAtBest, quantityRemaining,
                        order);
            }
            // If volume remains, add order to book
            if (quantityRemaining > 0) {
                order.setOrderId(orderBook.getNextOrderId());
                order.setQuantity(quantityRemaining);
                orderBook.getBids().addOrder(order);
                orderBook.setNextOrderId(orderBook.getNextOrderId() + 1);
            }
        } else if (side == OrderSide.ASK) {

            while ((orderBook.getBids().getCountOfOrders() > 0) &&
                    (quantityRemaining > 0) && (price.compareTo(orderBook.getBids().getMaxPrice()) <= 0)) {
                OrderList ordersAtBest = orderBook.getBids().getMaxPriceList();
                quantityRemaining = processOrderList(orderBook, trades, ordersAtBest, quantityRemaining,
                        order);
            }
            // If volume remains, add to book
            if (quantityRemaining > 0) {
                order.setOrderId(orderBook.getNextOrderId());
                order.setQuantity(quantityRemaining);
                orderBook.getAsks().addOrder(order);
                orderBook.setNextOrderId(orderBook.getNextOrderId() + 1);
            }
        } else {
            throw new IllegalArgumentException("order neither market nor limit: " +
                    side);
        }
    }

    public static BigDecimal randomPrice(Random random) {
        return BigDecimal.valueOf(random.nextDouble() * 10);
    }

    public static int randomQuntity(Random random) {
        return random.nextInt(10) * 10;
    }
}
