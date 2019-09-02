package bean;

import java.util.Date;

/**
 * Created by Dell on 8/13/2019.
 */

public class Orders {
    private Integer orderId;
    private String orderDate;
    private String orderCompletedDate;
    private Double orderAmount;
    private String orderStatus;
    private String orderPaymentStatus;
    private Consumer consumer;

    private Integer orderCarwasherId;
    private Integer orderVehicleId;
    private Integer orderAddressId;
    private String orderAddressCity;
    private String orderAddressState;

    private Carwasher carwasher;
    private Vehicle orderVehicle;
    private ConsumerAddress orderAddress;

    public Carwasher getCarwasher() {
        return carwasher;
    }

    public void setCarwasher(Carwasher carwasher) {
        this.carwasher = carwasher;
    }

    public Vehicle getOrderVehicle() {
        return orderVehicle;
    }

    public void setOrderVehicle(Vehicle orderVehicle) {
        this.orderVehicle = orderVehicle;
    }

    public ConsumerAddress getOrderAddress() {
        return orderAddress;
    }

    public void setOrderAddress(ConsumerAddress orderAddress) {
        this.orderAddress = orderAddress;
    }

    public String getOrderAddressCity() {
        return orderAddressCity;
    }

    public void setOrderAddressCity(String orderAddressCity) {
        this.orderAddressCity = orderAddressCity;
    }

    public String getOrderAddressState() {
        return orderAddressState;
    }

    public void setOrderAddressState(String orderAddressState) {
        this.orderAddressState = orderAddressState;
    }

    public Integer getOrderCarwasherId() {
        return orderCarwasherId;
    }

    public void setOrderCarwasherId(Integer orderCarwasherId) {
        this.orderCarwasherId = orderCarwasherId;
    }

    public Integer getOrderVehicleId() {
        return orderVehicleId;
    }

    public void setOrderVehicleId(Integer orderVehicleId) {
        this.orderVehicleId = orderVehicleId;
    }

    public Integer getOrderAddressId() {
        return orderAddressId;
    }

    public void setOrderAddressId(Integer orderAddressId) {
        this.orderAddressId = orderAddressId;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getOrderCompletedDate() {
        return orderCompletedDate;
    }

    public void setOrderCompletedDate(String orderCompletedDate) {
        this.orderCompletedDate = orderCompletedDate;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Double getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(Double orderAmount) {
        this.orderAmount = orderAmount;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderPaymentStatus() {
        return orderPaymentStatus;
    }

    public void setOrderPaymentStatus(String orderPaymentStatus) {
        this.orderPaymentStatus = orderPaymentStatus;
    }

    public Consumer getConsumer() {
        return consumer;
    }

    public void setConsumer(Consumer consumer) {
        this.consumer = consumer;
    }
}
