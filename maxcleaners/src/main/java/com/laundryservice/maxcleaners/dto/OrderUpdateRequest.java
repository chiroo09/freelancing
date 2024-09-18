package com.laundryservice.maxcleaners.dto;

import com.laundryservice.maxcleaners.constant.enums.OrderStatus;
import com.laundryservice.maxcleaners.constant.enums.PickupStatus;
import com.laundryservice.maxcleaners.model.Item;

import java.math.BigDecimal;
import java.util.List;

/**
 * Author Tejesh
 */
public class OrderUpdateRequest {

    private BigDecimal orderPrice; // Updated order price
    private OrderStatus status;    // Updated order status
    private List<Item> items;      // Updated list of items

    private PickupStatus pickupStatus; // New field for pickup status
    private String pickupIssue;

    // Getters and Setters

    public BigDecimal getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(BigDecimal orderPrice) {
        this.orderPrice = orderPrice;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public PickupStatus getPickupStatus() {
        return pickupStatus;
    }

    public void setPickupStatus(PickupStatus pickupStatus) {
        this.pickupStatus = pickupStatus;
    }

    public String getPickupIssue() {
        return pickupIssue;
    }

    public void setPickupIssue(String pickupIssue) {
        this.pickupIssue = pickupIssue;
    }
}
