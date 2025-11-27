package com.snapshot.chonect.domain.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Entity(name = "connection")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConnectionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String fromElementId;
    private String toElementId;
    private String actionType;
    @Column(name = "order_num")
    private Integer orderNum;
    private Integer delay;
    @Column(name = "is_parallel")
    private Boolean isParallel;
}
