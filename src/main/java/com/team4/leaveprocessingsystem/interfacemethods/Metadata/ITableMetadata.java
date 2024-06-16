package com.team4.leaveprocessingsystem.interfacemethods.Metadata;

import com.team4.leaveprocessingsystem.model.Metadata.TableMetadata;
import jakarta.transaction.Transactional;
import org.springframework.ui.Model;

import java.util.List;

public interface ITableMetadata {

    TableMetadata save(TableMetadata tableMetadata);

    TableMetadata saveTable(String entityName, String tableName);

    @Transactional
    List<String> findAllEntityName();

    List<Class<? extends Model>> getEntityList();

    @Transactional
    void deleteAllIfAny();
}
