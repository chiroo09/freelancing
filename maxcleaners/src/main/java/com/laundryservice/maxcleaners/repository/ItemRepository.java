package com.laundryservice.maxcleaners.repository;

import com.laundryservice.maxcleaners.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Author Tejesh
 */
public interface ItemRepository extends JpaRepository<Item, Long> {
}
