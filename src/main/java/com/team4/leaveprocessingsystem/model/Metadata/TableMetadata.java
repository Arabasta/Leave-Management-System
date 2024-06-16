package com.team4.leaveprocessingsystem.model.Metadata;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
public class TableMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String entityName;

    @Column(nullable = false)
    private String tableName;
}
