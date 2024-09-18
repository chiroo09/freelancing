package com.laundryservice.maxcleaners.repository;

import com.laundryservice.maxcleaners.constant.enums.OrderStatus;
import com.laundryservice.maxcleaners.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

/**
 * Author Tejesh
 */
public interface OrderCustomRepository {
    Page<Order> findOrdersWithCriteria(LocalDate startDate, LocalDate endDate, String city, OrderStatus status, Pageable pageable);

}
