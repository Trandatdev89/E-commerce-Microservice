package com.project01.productservice.ship;

import com.project01.productservice.entity.Ship;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ShipRepository extends JpaRepository<Ship, Long> {
}
