package org.underwearshop.underwearshop.repository;

import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.underwearshop.underwearshop.entity.Order;
import org.underwearshop.underwearshop.entity.OrderStatus;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByStatusEqualsOrderByIdDesc(OrderStatus status, Pageable pageable);

    Page<Order> findAllByStatusIs(OrderStatus status, Pageable pageable);
}
