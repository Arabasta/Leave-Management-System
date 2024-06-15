package com.team4.leaveprocessingsystem.seeder;

import com.team4.leaveprocessingsystem.LeaveProcessingSystemApplication;
import com.team4.leaveprocessingsystem.integrators.MetadataExtractorIntegrator;
import com.team4.leaveprocessingsystem.model.Metadata.SchemaMetadata;
import com.team4.leaveprocessingsystem.service.Metadata.SchemaMetadataService;
import com.team4.leaveprocessingsystem.service.Metadata.TableMetadataService;
import org.hibernate.boot.Metadata;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MetadataSeeder {

    private static final Logger LOGGER = LoggerFactory.getLogger(LeaveProcessingSystemApplication.class);
    private final TableMetadataService tableMetadataService;
    private final SchemaMetadataService schemaMetadataService;

    public MetadataSeeder(TableMetadataService tableMetadataService, SchemaMetadataService schemaMetadataService) {
        this.tableMetadataService = tableMetadataService;
        this.schemaMetadataService = schemaMetadataService;
    }

    public void seed() {
        contextLoads();
    }

    private void contextLoads() {
        System.out.println("-- START TABLE METADATA EXTRACTION --");
        Metadata metadata = MetadataExtractorIntegrator.INSTANCE.getMetadata();

        for (PersistentClass persistentClass : metadata.getEntityBindings()) {
            Table table = persistentClass.getTable();

            SchemaMetadata schema = schemaMetadataService.saveTable(
                    persistentClass.getClassName(),
                    table.getName());

            for (Property property : persistentClass.getProperties()) {
                for (Column column : property.getColumns()) {
                    tableMetadataService.saveColumn(
                            property.getName(),
                            column.getName(),
                            column.getSqlType(),
                            schema);
                }
            }
        }
        System.out.println("-- END TABLE METADATA EXTRACTION --");
    }
}
