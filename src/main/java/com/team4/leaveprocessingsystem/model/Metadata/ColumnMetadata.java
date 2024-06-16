package com.team4.leaveprocessingsystem.model.Metadata;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
public class ColumnMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private String attributeName;

    @Column
    private String sqlColumnName;

    @Column
    private String sqlColumnType;

    @ManyToOne
    @JoinColumn(name = "table_metadata_id")
    private TableMetadata tableMetadata;
}
