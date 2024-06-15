package com.team4.leaveprocessingsystem.service.Metadata;

import com.team4.leaveprocessingsystem.interfacemethods.Metadata.ITableMetadata;
import com.team4.leaveprocessingsystem.model.Metadata.SchemaMetadata;
import com.team4.leaveprocessingsystem.model.Metadata.TableMetadata;
import com.team4.leaveprocessingsystem.repository.Metadata.TableMetadataRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public TableMetadata saveColumn(String attributeName, String sqlColumnName, String sqlColumnType, SchemaMetadata schemaMetadata) {
        TableMetadata tableMetadata = new TableMetadata();
        tableMetadata.setAttributeName(attributeName);
        tableMetadata.setSqlColumnName(sqlColumnName);
        tableMetadata.setSqlColumnType(sqlColumnType);
        tableMetadata.setSchemaMetadata(schemaMetadata);
        return tableMetadataRepository.save(tableMetadata);
    }

    @Override
    @Transactional
    public List<TableMetadata> findAll() {
        return tableMetadataRepository.findAll();
    }

}
