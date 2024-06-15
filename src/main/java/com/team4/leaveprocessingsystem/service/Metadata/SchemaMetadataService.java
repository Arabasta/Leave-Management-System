package com.team4.leaveprocessingsystem.service.Metadata;

import com.team4.leaveprocessingsystem.interfacemethods.Metadata.ISchemaMetadata;
import com.team4.leaveprocessingsystem.model.Metadata.SchemaMetadata;
import com.team4.leaveprocessingsystem.repository.Metadata.SchemaMetadataRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SchemaMetadataService implements ISchemaMetadata {

    private final SchemaMetadataRepository schemaMetadataRepository;

    public SchemaMetadataService(SchemaMetadataRepository schemaMetadataRepository) {
        this.schemaMetadataRepository = schemaMetadataRepository;
    }

    @Override
    @Transactional
    public SchemaMetadata save(SchemaMetadata schemaMetadata) {
        return schemaMetadataRepository.save(schemaMetadata);
    }

    @Override
    @Transactional
    public SchemaMetadata saveTable(String entityName,
                                    String tableName) {
        SchemaMetadata schemaMetadata = new SchemaMetadata();
        schemaMetadata.setEntityName(entityName);
        schemaMetadata.setTableName(tableName);
        return schemaMetadataRepository.save(schemaMetadata);
    }

    @Override
    @Transactional
    public List<String> findAllEntityName() {
        return schemaMetadataRepository.findAllEntityName();
    }

    @Override
    @Transactional
    public List<Class<? extends Model>> getEntityList() {
        List<String> entityNames = schemaMetadataRepository.findAllEntityName();

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

}
