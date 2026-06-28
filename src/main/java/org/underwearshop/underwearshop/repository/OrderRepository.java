package org.underwearshop.underwearshop.repository;

import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.underwearshop.underwearshop.entity.Order;
import org.underwearshop.underwearshop.entity.OrderStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByStatusEqualsOrderByIdDesc(OrderStatus status, Pageable pageable);

    Page<Order> findAllByStatusIs(OrderStatus status, Pageable pageable);

    @EntityGraph(attributePaths = {"orderItems", "orderItems.product"})
    List<Order> findAllByStatusIsOrderByIdDesc(OrderStatus status);

    @EntityGraph(attributePaths = {"orderItems", "orderItems.product"})
    Optional<Order> findById(Long aLong);
}
