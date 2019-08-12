package trade;

import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static trade.OrderProcessorHelper.processLimitOrder;
import static trade.OrderProcessorHelper.processMarketOrder;

// TODO for precision, change prices from double to java.math.BigDecimal

public class OrderBook {
    private List<Deal> deals = new ArrayList<>();
    private OrderTree bids = new OrderTree();
    private OrderTree asks = new OrderTree();
    private double tickSize; // todo A tick size is the minimum price movement of a trading instrument. wiki :)
    private long lastUpdateTime;
    private int nextOrderId;

    private final static Logger log = Logger.getLogger(OrderBook.class);

    public OrderBook(double tickSize) {
        this.tickSize = tickSize;
        this.reset();
    }

    public void reset() {
        deals.clear();
        bids.reset();
        asks.reset();
        lastUpdateTime = 0;
        nextOrderId = 0;

    }

    /**
     * Clips price according to tickSize
     *
     * @param price
     * @return
     */
    private BigDecimal clipPrice(BigDecimal price) {
        int numDecPlaces = (int) Math.log10(1 / this.tickSize);
        BigDecimal bd = price;
        BigDecimal rounded = bd.setScale(numDecPlaces, BigDecimal.ROUND_HALF_UP);
        return rounded;
    }

    public void addOrder(Order order, long time) {
        order.setOrderId(nextOrderId);
        this.lastUpdateTime = time;
        switch (order.getOrderSide()) {
            case BID:
                if (!this.bids.orderExists(order.getOrderId())) {
                    log.info("Adding order to orderBook: " + order + " time: " + time);
                    bids.addOrder(order);
                    nextOrderId++;

                }
                break;
            case ASK:
                if (!this.asks.orderExists(order.getOrderId())) {
                    log.info("Adding order to orderBook: " + order + " time: " + time);
                    asks.addOrder(order);
                    nextOrderId++;
                }
                break;
            default:
                throw new IllegalArgumentException("No order with this side type");
        }
    }

    public void updateOrder(Order orderToUpdate, long time) {
        this.lastUpdateTime = time;
        log.info("About to update order: " + orderToUpdate);
        switch (orderToUpdate.getOrderSide()) {
            case ASK:
                if (this.asks.orderExists(orderToUpdate.getOrderId())) {
                    this.asks.updateOrder(orderToUpdate);
                } else {
                    throw new IllegalArgumentException("Order doesnt exist");
                }
                break;

            case BID:
                if (this.bids.orderExists(orderToUpdate.getOrderId())) {
                    this.bids.updateOrder(orderToUpdate);
                } else {
                    throw new IllegalArgumentException("Order doesnt exist");
                }
                break;
        }
    }


    public void deleteOrder(OrderSide side, long orderToRemoveId, long time) {
        this.lastUpdateTime = time;
        switch (side) {
            case BID:
                if (bids.orderExists(orderToRemoveId)) {
                    bids.removeOrderById(orderToRemoveId);
                    log.info("Order with  id " + orderToRemoveId + " " + side + " was removed from book ");
                }
                break;
            case ASK:
                if (asks.orderExists(orderToRemoveId)) {
                    asks.removeOrderById(orderToRemoveId);
                    log.info("Order with  id " + orderToRemoveId + " " + side + " was removed from book ");
                }
                break;
            default:
                throw new IllegalArgumentException("deleteOrder() given neither 'bid' nor 'offer'");
        }
    }

    public void processOrder(Order order) {
        Order_Type orderType = order.getOrderType();
        this.lastUpdateTime = order.getOrderCreationTime();
        if (order.getQuantity() <= 0) {
            throw new IllegalArgumentException("processOrder() given qty <= 0");
        }
        switch (orderType) {
            case LIMIT:
                BigDecimal clippedPrice = clipPrice(order.getPrice());
                order.setPrice(clippedPrice);
                log.info("About to process LIMIT order  " + order + " at clippedPrice " + clippedPrice);
                processLimitOrder(this, order);
                break;
            case MARKET:
                log.info("About to process MARKET order " + order);
                processMarketOrder(this, order);
                break;
        }
    }


    public BigDecimal getBestBid() {
        return bids.getMaxPrice();
    }

    public BigDecimal getWorstBid() {
        return bids.getMinPrice();
    }

    public BigDecimal getBestOffer() {
        return asks.getMinPrice();
    }

    public BigDecimal getWorstOffer() {
        return asks.getMaxPrice();
    }

    public double getTickSize() {
        return tickSize;
    }


    public List<Deal> getDeals() {
        return deals;
    }

    public OrderTree getBids() {
        return bids;
    }

    public OrderTree getAsks() {
        return asks;
    }

    public int getNextOrderId() {
        return nextOrderId;
    }

    public void setNextOrderId(int nextOrderId) {
        this.nextOrderId = nextOrderId;
    }
}
