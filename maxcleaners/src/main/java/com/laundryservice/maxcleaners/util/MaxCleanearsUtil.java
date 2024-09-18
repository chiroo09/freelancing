package com.laundryservice.maxcleaners.util;

import com.laundryservice.maxcleaners.constant.enums.OrderType;
import com.laundryservice.maxcleaners.model.Item;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * Author Tejesh
 */
@Component
public class MaxCleanearsUtil {
    public BigDecimal calculateTotalPrice(List<Item> items, OrderType orderType) {
        // Calculate the total price based on item quantity and price
        BigDecimal totalPrice = items.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calculate total quantity of items (sum of item quantities)
        long totalQuantity = items.stream()
                .mapToLong(Item::getQuantity)  // Get the quantity of each item
                .sum();

        // Add $1 per item if the order is Express
        if (orderType == OrderType.EXPRESS) {
            totalPrice = totalPrice.add(BigDecimal.valueOf(totalQuantity));
        }

        return totalPrice;
    }


}
