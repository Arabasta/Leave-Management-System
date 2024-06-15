package com.team4.leaveprocessingsystem.interfacemethods.Metadata;

import com.team4.leaveprocessingsystem.model.Metadata.SchemaMetadata;
import com.team4.leaveprocessingsystem.model.Metadata.TableMetadata;

import java.util.List;

public interface ITableMetadata {

    TableMetadata save(TableMetadata tableMetadata);

    TableMetadata saveColumn(String columnProperty, String columnName,
                             String columnSqlType, SchemaMetadata schemaMetadata);

    List<TableMetadata> findAll();
}
