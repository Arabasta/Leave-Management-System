package com.team4.leaveprocessingsystem.interfacemethods.Metadata;

import com.team4.leaveprocessingsystem.model.Metadata.TableMetadata;
import com.team4.leaveprocessingsystem.model.Metadata.ColumnMetadata;
import jakarta.transaction.Transactional;

import java.util.List;

public interface IColumnMetadata {

    ColumnMetadata save(ColumnMetadata columnMetadata);

    ColumnMetadata saveColumn(String columnProperty, String columnName,
                              String columnSqlType, TableMetadata tableMetadata);

    List<ColumnMetadata> findAll();

    List<ColumnMetadata> findByTableMetadataId(int tableMetadataId);

    @Transactional
    void deleteAllIfAny();
}
