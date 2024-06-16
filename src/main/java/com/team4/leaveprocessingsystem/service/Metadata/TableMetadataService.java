package com.team4.leaveprocessingsystem.service.Metadata;

import com.team4.leaveprocessingsystem.interfacemethods.Metadata.ITableMetadata;
import com.team4.leaveprocessingsystem.model.Metadata.TableMetadata;
import com.team4.leaveprocessingsystem.repository.Metadata.TableMetadataRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TableMetadataService implements ITableMetadata {

    private final TableMetadataRepository tableMetadataRepository;

    public TableMetadataService(TableMetadataRepository tableMetadataRepository) {
        this.tableMetadataRepository = tableMetadataRepository;
    }

    @Override
    @Transactional
    public TableMetadata save(TableMetadata tableMetadata) {
        return tableMetadataRepository.save(tableMetadata);
    }

    @Override
    @Transactional
    public TableMetadata saveTable(String entityName,
                                   String tableName) {
        TableMetadata tableMetadata = new TableMetadata();
        tableMetadata.setEntityName(entityName);
        tableMetadata.setTableName(tableName);
        return tableMetadataRepository.save(tableMetadata);
    }

    @Override
    @Transactional
    public List<String> findAllEntityName() {
        return tableMetadataRepository.findAllEntityName();
    }

    @Override
    @Transactional
    public List<Class<? extends Model>> getEntityList() {
        List<String> entityNames = tableMetadataRepository.findAllEntityName();

        return entityNames.stream()
                .map(this::getEntityByName)
                .collect(Collectors.toList());
    }

    private Class<? extends Model> getEntityByName(String entityName) {
        try {
            return (Class<? extends Model>) Class.forName(entityName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Class not found: " + entityName, e);
        }
    }

    @Override
    @Transactional
    public void deleteAllIfAny() {
        if (tableMetadataRepository.count() != 0) {
            tableMetadataRepository.deleteAll();
        }
    }
}
