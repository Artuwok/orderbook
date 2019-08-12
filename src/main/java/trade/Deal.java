package trade;

import java.math.BigDecimal;

public class Deal {
    private long timestamp;
    private BigDecimal price;
    private int quantity;
    private String buyer;
    private String seller;
    private long orderHit;

    public Deal(long time, BigDecimal price, int quantity,
                String buyer, String seller, long orderHit) {
        this.timestamp = time;
        this.price = price;
        this.quantity = quantity;
        this.buyer = buyer;
        this.seller = seller;
        this.orderHit = orderHit; // the orderId of the order that was in the book
    }


    public long getTimestamp() {
        return timestamp;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getBuyer() {
        return buyer;
    }

    public String getSeller() {
        return seller;
    }

    public long getOrderHit() {
        return orderHit;
    }

    @Override
    public String toString() {
        return "Deal{" +
                "timestamp=" + timestamp +
                ", price=" + price +
                ", quantity=" + quantity +
                ", buyer='" + buyer + '\'' +
                ", seller='" + seller + '\'' +
                ", orderHit=" + orderHit +
                '}';
    }
}
