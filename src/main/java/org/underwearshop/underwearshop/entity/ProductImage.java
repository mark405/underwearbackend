package org.underwearshop.underwearshop.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.PersistenceCreator;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE, onConstructor = @__(@PersistenceCreator))
@Entity
public class ProductImage {

    @Id
    @GeneratedValue
    private Long id;

    private String image;

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private Product product;
}