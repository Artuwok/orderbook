package trade;

import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.TreeMap;

public class OrderTree {
    private int depth;
    private int volume;
    private int countOfOrders;

    private TreeMap<BigDecimal, OrderList> priceTree = new TreeMap<>();
    private HashMap<BigDecimal, OrderList> priceMap = new HashMap<>();
    private HashMap<Long, Order> orderMap = new HashMap<>();

    private static Logger log = Logger.getLogger(OrderTree.class);

    public OrderTree() {
        reset();
    }

    public void reset() {
        priceTree.clear();
        priceMap.clear();
        orderMap.clear();
        volume = 0;
        countOfOrders = 0;
        depth = 0;
    }


    /*
     * Returns the OrderList object associated with 'price'
     */
    private OrderList getOrderListForPriceLevel(BigDecimal price) {
        return priceMap.get(price);
    }

    /*
     * Returns the order given the order id
     */
    public Order getOrderById(long orderId) {
        if (orderExists(orderId)) {
            return orderMap.get(orderId);
        } else return null;
    }

    public void createPrice(BigDecimal price) {
        depth = depth + 1;
        OrderList newList = new OrderList();
        priceTree.put(price, newList);
        priceMap.put(price, newList);
    }

    public void removePrice(BigDecimal price) {
        depth -= 1;
        priceTree.remove(price);
        priceMap.remove(price);
    }

    public boolean priceExists(BigDecimal price) {
        return priceMap.containsKey(price);
    }

    public boolean orderExists(long orderId) {
        return orderMap.containsKey(orderId);
    }

    public void addOrder(Order order) {
        long orderId = order.getOrderId();
        BigDecimal orderPrice = order.getPrice();
        if (orderExists(orderId)) {
            removeOrderById(orderId);
        }
        countOfOrders = countOfOrders + 1;
        if (!priceExists(orderPrice)) {
            createPrice(orderPrice);
        }
        order.setListOfOrders(priceMap.get(orderPrice));
        priceMap.get(orderPrice).appendOrder(order);
        orderMap.put(orderId, order);
        volume += order.getQuantity();
    }

    public void updateOrderQuantity(int newQuantity, long orderId) {
        Order order = this.orderMap.get(orderId);
        int originalVol = order.getQuantity();
        order.updateQuantity(newQuantity, order.getOrderCreationTime());
        this.volume += (order.getQuantity() - originalVol);
    }

    public void updateOrder(Order updatedOrder) {
        long orderId = updatedOrder.getOrderId();
        BigDecimal newPrice = updatedOrder.getPrice();
        Order order = this.orderMap.get(orderId);

        log.info("Old order: " + order);

        int oldQuantity = order.getQuantity();
        if (!newPrice.equals(order.getPrice())) {
            // Price has been updated
            OrderList tempOrderList = this.priceMap.get(order.getPrice());
            tempOrderList.removeOrder(order);
            if (tempOrderList.getLength() == 0) {
                removePrice(order.getPrice());
            }
            addOrder(updatedOrder);
        } else {
            // The quantity has changed
            order.updateQuantity(updatedOrder.getQuantity(), updatedOrder.getOrderCreationTime());
        }
        this.volume += (order.getQuantity() - oldQuantity);
    }

    public void removeOrderById(long orderId) {
        this.countOfOrders = countOfOrders - 1;
        Order order = orderMap.get(orderId);
        this.volume -= order.getQuantity();
        order.getListOfOrders().removeOrder(order);
        if (order.getListOfOrders().getLength() == 0) {
            this.removePrice(order.getPrice());
        }
        this.orderMap.remove(orderId);
    }

    public BigDecimal getMaxPrice() {
        if (this.depth > 0) {
            return this.priceTree.lastKey();
        } else {
            return null;
        }
    }

    public BigDecimal getMinPrice() {
        if (this.depth > 0) {
            return this.priceTree.firstKey();
        } else {
            return null;
        }
    }

    public OrderList getMaxPriceList() {
        if (this.depth > 0) {
            return this.getOrderListForPriceLevel(getMaxPrice());
        } else {
            return null;
        }
    }

    public OrderList getMinPriceList() {
        if (this.depth > 0) {
            return this.getOrderListForPriceLevel(getMinPrice());
        } else {
            return null;
        }
    }


    public Integer getVolume() {
        return volume;
    }

    public Integer getCountOfOrders() {
        return countOfOrders;
    }

    public Integer getDepth() {
        return depth;
    }

}

