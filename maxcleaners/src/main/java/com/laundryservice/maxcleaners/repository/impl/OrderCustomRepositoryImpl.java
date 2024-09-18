package com.laundryservice.maxcleaners.repository.impl;

import com.laundryservice.maxcleaners.constant.enums.OrderStatus;
import com.laundryservice.maxcleaners.exception.InvalidRequestException;
import com.laundryservice.maxcleaners.model.Order;
import com.laundryservice.maxcleaners.repository.OrderCustomRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Author Tejesh
 */
@Repository
public class OrderCustomRepositoryImpl implements OrderCustomRepository {

    private static final Logger logger = LoggerFactory.getLogger(OrderCustomRepositoryImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<Order> findOrdersWithCriteria(LocalDate startDate, LocalDate endDate, String city, OrderStatus status, Pageable pageable) {
        logger.info("Searching for orders with startDate: {}, endDate: {}, city: {}, pageable: {}", startDate, endDate, city, pageable);

        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Order> query = cb.createQuery(Order.class);
            Root<Order> order = query.from(Order.class);

            Predicate predicate = cb.conjunction();


            if (startDate != null) {
                LocalDateTime startDateTime = startDate.atStartOfDay();

                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(order.get("pickupDate"), startDateTime));
                logger.debug("Added start date filter: {}", startDate);
            }

            if (endDate != null) {
                LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(order.get("pickupDate"), endDateTime));
                logger.debug("Added end date filter: {}", endDate);
            }

            if (city != null && !city.isEmpty()) {
                predicate = cb.and(predicate, cb.equal(order.get("city"), city));
                logger.debug("Added city filter: {}", city);
            }

            if (status != null) {
                predicate = cb.and(predicate, cb.equal(order.get("status"), status));
                logger.debug("Added status filter: {}", status);
            }

            query.where(predicate);

            CriteriaQuery<Order> selectQuery = query.select(order);
            javax.persistence.Query jpaQuery = entityManager.createQuery(selectQuery);
            jpaQuery.setFirstResult((int) pageable.getOffset());
            jpaQuery.setMaxResults(pageable.getPageSize());

            // Using a total count query for pagination
            CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
            Root<Order> countRoot = countQuery.from(Order.class);
            countQuery.select(cb.count(countRoot)).where(predicate);
            Long total = entityManager.createQuery(countQuery).getSingleResult();

            return new PageImpl<>(jpaQuery.getResultList(), pageable, total);

        } catch (Exception e) {
            logger.error("Error occurred while searching orders: {}", e.getMessage());
            throw new InvalidRequestException("Failed to retrieve orders: " + e.getMessage());
        }
    }
}
