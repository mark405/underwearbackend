package org.underwearshop.underwearshop.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.PersistenceCreator;

import java.util.List;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE, onConstructor = @__(@PersistenceCreator))
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    private String username;

    private String telephone;

    private String deliveryType;

    private String deliveryAddress;

    private OrderStatus status;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems;
}
