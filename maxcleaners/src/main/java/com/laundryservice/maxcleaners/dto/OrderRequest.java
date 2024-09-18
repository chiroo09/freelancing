package com.laundryservice.maxcleaners.dto;

import com.laundryservice.maxcleaners.model.Item;
import com.laundryservice.maxcleaners.model.Order;

import java.util.List;

/**
 * Author Tejesh
 */
public class OrderRequest {

    private Order order;
    private List<Item> items;

    // Getters and Setters

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }
}
