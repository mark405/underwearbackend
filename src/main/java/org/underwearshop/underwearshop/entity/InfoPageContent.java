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
public class InfoPageContent {
    @Id
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
}
