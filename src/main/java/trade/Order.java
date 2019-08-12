package trade;

import java.math.BigDecimal;

public class Order {
    private long orderId;
    private long orderCreationTime;
    private Order_Type orderType;
    private int quantity;
    private OrderSide orderSide;
    private BigDecimal price;
    private String venue;
    private Order nextOrder;
    private Order prevOrder;
    private OrderList listOfOrders;


    public Order(long orderCreationTime,
                 Order_Type orderType,
                 int quantity,
                 String venue,
                 OrderSide orderSide,
                 BigDecimal price) {

        this.orderCreationTime = orderCreationTime;
        this.orderType = orderType;
        this.orderSide = orderSide;
        this.quantity = quantity;
        if (price != null) {
            this.price = price;
        }
        this.venue = venue;
    }

    public void updateQuantity(int quantity, long timestamp) {
        if ((quantity > this.quantity) && (this.listOfOrders.getTailOrder() != this)) {
            // Move order to the end of the list. i.e. loses time priority
            this.listOfOrders.moveTail(this);
            this.orderCreationTime = timestamp;
        }
        listOfOrders.setVolume(listOfOrders.getVolume() - (this.quantity - quantity));
        this.quantity = quantity;
    }


    public long getOrderCreationTime() {
        return orderCreationTime;
    }

    public void setOrderCreationTime(long orderCreationTime) {
        this.orderCreationTime = orderCreationTime;
    }

    public Order_Type getOrderType() {
        return orderType;
    }

    public void setOrderType(Order_Type orderType) {
        this.orderType = orderType;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public OrderSide getOrderSide() {
        return orderSide;
    }

    public void setOrderSide(OrderSide orderSide) {
        this.orderSide = orderSide;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public Order getNextOrder() {
        return nextOrder;
    }

    public void setNextOrder(Order nextOrder) {
        this.nextOrder = nextOrder;
    }

    public Order getPrevOrder() {
        return prevOrder;
    }

    public void setPrevOrder(Order prevOrder) {
        this.prevOrder = prevOrder;
    }

    public OrderList getListOfOrders() {
        return listOfOrders;
    }

    public void setListOfOrders(OrderList listOfOrders) {
        this.listOfOrders = listOfOrders;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", orderCreationTime=" + orderCreationTime +
                ", orderType=" + orderType +
                ", quantity=" + quantity +
                ", orderSide=" + orderSide +
                ", price=" + price +
                ", venue='" + venue + '\'' +
                ", listOfOrders=" + listOfOrders +
                '}';
    }
}
