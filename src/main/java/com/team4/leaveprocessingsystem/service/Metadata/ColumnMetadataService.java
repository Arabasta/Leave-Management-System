package com.team4.leaveprocessingsystem.service.Metadata;

import com.team4.leaveprocessingsystem.interfacemethods.Metadata.IColumnMetadata;
import com.team4.leaveprocessingsystem.model.Metadata.TableMetadata;
import com.team4.leaveprocessingsystem.model.Metadata.ColumnMetadata;
import com.team4.leaveprocessingsystem.repository.Metadata.ColumnMetadataRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ColumnMetadataService implements IColumnMetadata {

    private final ColumnMetadataRepository columnMetadataRepository;

    public ColumnMetadataService(ColumnMetadataRepository columnMetadataRepository) {
        this.columnMetadataRepository = columnMetadataRepository;
    }

    @Override
    @Transactional
    public ColumnMetadata save(ColumnMetadata columnMetadata) {
        return columnMetadataRepository.save(columnMetadata);
    }

    @Override
    @Transactional
    public ColumnMetadata saveColumn(String attributeName, String sqlColumnName, String sqlColumnType, TableMetadata tableMetadata) {
        ColumnMetadata columnMetadata = new ColumnMetadata();
        columnMetadata.setAttributeName(attributeName);
        columnMetadata.setSqlColumnName(sqlColumnName);
        columnMetadata.setSqlColumnType(sqlColumnType);
        columnMetadata.setTableMetadata(tableMetadata);
        return columnMetadataRepository.save(columnMetadata);
    }

    @Override
    @Transactional
    public List<ColumnMetadata> findAll() {
        return columnMetadataRepository.findAll();
    }

    @Override
    @Transactional
    public List<ColumnMetadata> findByTableMetadataId(int tableMetadataId) {
        return columnMetadataRepository.findByTableMetadataId(tableMetadataId);
    }

    @Override
    @Transactional
    public void deleteAllIfAny(){
        if (columnMetadataRepository.count() != 0) {
            columnMetadataRepository.deleteAll();
        }
    }
}
