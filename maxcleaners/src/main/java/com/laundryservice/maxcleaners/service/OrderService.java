package com.laundryservice.maxcleaners.service;

import com.laundryservice.maxcleaners.constant.enums.OrderStatus;
import com.laundryservice.maxcleaners.constant.enums.PickupStatus;
import com.laundryservice.maxcleaners.dto.OrderUpdateRequest;
import com.laundryservice.maxcleaners.exception.InvalidRequestException;
import com.laundryservice.maxcleaners.exception.OrderAlreadyUpdatedException;
import com.laundryservice.maxcleaners.model.Customer;
import com.laundryservice.maxcleaners.model.Item;
import com.laundryservice.maxcleaners.model.Order;
import com.laundryservice.maxcleaners.repository.ItemRepository;
import com.laundryservice.maxcleaners.repository.OrderRepository;
import com.laundryservice.maxcleaners.util.MaxCleanearsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Author Tejesh
 */
@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private MaxCleanearsUtil maxCleanearsUtil;


    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);


   /* public Order createOrder(Order order, List<Item> items, Long customerId) {
        // Set the customer ID on the order
        order.setCustomerId(customerId);

        // Calculate order price based on items
        BigDecimal totalPrice = items.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Add extra charge for express order
        if (order.getOrderType() == OrderType.EXPRESS) {
            totalPrice = totalPrice.add(BigDecimal.ONE); // Add $1 for express
        }

        // Set the calculated price on the order
        order.setOrderPrice(totalPrice);

        // Save the order in the repository
        Order savedOrder = orderRepository.save(order);

        // Save each item associated with the order
        for (Item item : items) {
            item.setOrderId(savedOrder.getId());
            itemRepository.save(item);
        }

        return savedOrder;
    }*/

   /* public Order createOrder(Order order, List<Item> items, Long customerId) {
        // Set the customer ID on the order
        order.setCustomerId(customerId);

        // Set initial order price as null or a default value
        order.setOrderPrice(BigDecimal.ZERO); // Or BigDecimal.ZERO if you prefer
        order.setStatus(OrderStatus.IN_PROGRESS);

        // Save the order in the repository
        Order savedOrder;
        try {
            savedOrder = orderRepository.save(order);
        } catch (Exception e) {
            throw new InvalidRequestException("Error saving the order: " + e.getMessage());
        }



        return savedOrder;
    }*/

    public Order createOrder(Order order, Customer customer) {
        // Set the customer ID on the order
        order.setCustomerId(customer.getId());

        // Set the customer phonenumber on the order
        order.setPhoneNumber(customer.getPhoneNumber());
        order.setAddress(customer.getAddress());
        order.setCity(extractCityFromAddress(customer.getAddress()));


        // Initialize order price to zero
        order.setOrderPrice(BigDecimal.ZERO);

        // Set the initial status
        order.setStatus(OrderStatus.PENDING);


        //set the initial pickup status
        order.setPickupStatus(PickupStatus.SCHEDULED);

        // Set creation and update timestamps
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        // Save the order in the repository
        Order savedOrder;
        try {
            savedOrder = orderRepository.save(order);
        } catch (Exception e) {
            throw new InvalidRequestException("Error saving the order: " + e.getMessage());
        }

        // Return the saved order
        return savedOrder;
    }



    public Order updateOrder(Long orderId, OrderUpdateRequest updateRequest) {
        logger.info("Received request to update order with ID: {}", orderId);

        Order existingOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    logger.error("Order with ID {} not found", orderId);
                    return new InvalidRequestException("Order not found");
                });

        // Log existing order details before updating
        logger.debug("Existing order details: {}", existingOrder);

        // Calculate total price and log the calculation
        BigDecimal totalPrice = maxCleanearsUtil.calculateTotalPrice(updateRequest.getItems(), existingOrder.getOrderType());
        logger.info("Calculated total price for order ID {}: {}", orderId, totalPrice);

        // Check if the status is delivered or not
        boolean isUpdateRequired = !existingOrder.getStatus().equals(updateRequest.getStatus());


        if (!isUpdateRequired) {
            logger.info("Order ID {} is already updated", orderId);
            throw new OrderAlreadyUpdatedException("Order is already updated and delivered to customer");
        }

        // Update the order price, status, pickup status, and pickup issue
        existingOrder.setOrderPrice(totalPrice);
        existingOrder.setStatus(updateRequest.getStatus());
        logger.info("Updated order status for order ID {}: {}", orderId, updateRequest.getStatus());

        existingOrder.setPickupStatus(updateRequest.getPickupStatus());
        logger.info("Updated pickup status for order ID {}: {}", orderId, updateRequest.getPickupStatus());

        existingOrder.setPickupIssue(updateRequest.getPickupIssue());
        logger.info("Updated pickup issue for order ID {}: {}", orderId, updateRequest.getPickupIssue());

        // Update items if provided and log each item update
        if (updateRequest.getItems() != null) {
            logger.info("Updating items for order ID {}: {}", orderId, updateRequest.getItems().size());
            for (Item item : updateRequest.getItems()) {
                item.setOrderId(existingOrder.getId());
                itemRepository.save(item);
                logger.info("Saved item for order ID {}: Item ID: {}, Name: {}, Quantity: {}", orderId, item.getId(), item.getName(), item.getQuantity());
            }
        }

        // Save and return the updated order, with logging
        Order updatedOrder = orderRepository.save(existingOrder);
        logger.info("Successfully updated order with ID: {}", orderId);

        return updatedOrder;
    }




    public Order getOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    public Page<Order> retriveOrders(LocalDate startDate, LocalDate endDate, String city,OrderStatus status, Pageable pageable) {
        logger.info("Fetching orders with filters - Start Date: {}, End Date: {}, City: {}", startDate, endDate, city);
        return orderRepository.findOrdersWithCriteria(startDate, endDate, city, status, pageable);
    }

    private String extractCityFromAddress(String address) {
        if (address != null && !address.isEmpty()) {
            String[] addressParts = address.split(",");
            if (addressParts.length >= 2) {
                return addressParts[1].trim();
            }
        }
        return "";
    }
}


