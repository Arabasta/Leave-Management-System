package com.team4.leaveprocessingsystem.seeder;

import com.team4.leaveprocessingsystem.integrators.MetadataExtractorIntegrator;
import com.team4.leaveprocessingsystem.model.Metadata.TableMetadata;
import com.team4.leaveprocessingsystem.repository.Metadata.TableMetadataRepository;
import com.team4.leaveprocessingsystem.service.Metadata.TableMetadataService;
import com.team4.leaveprocessingsystem.service.Metadata.ColumnMetadataService;
import org.hibernate.boot.Metadata;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Table;
import org.springframework.stereotype.Service;

@Service
public class MetadataSeeder {

    private final ColumnMetadataService columnMetadataService;
    private final TableMetadataService tableMetadataService;

    public MetadataSeeder(ColumnMetadataService columnMetadataService, TableMetadataService tableMetadataService, TableMetadataRepository tableMetadataRepository) {
        this.columnMetadataService = columnMetadataService;
        this.tableMetadataService = tableMetadataService;
    }

    public void seed() {
        contextLoads();
    }

    private void contextLoads() {
        tableMetadataService.deleteAllIfAny();
        columnMetadataService.deleteAllIfAny();

        System.out.println("-- START TABLE METADATA EXTRACTION --");
        Metadata metadata = MetadataExtractorIntegrator.INSTANCE.getMetadata();

        for (PersistentClass persistentClass : metadata.getEntityBindings()) {
            Table table = persistentClass.getTable();

            TableMetadata tableMetadata = tableMetadataService.saveTable(
                    persistentClass.getClassName(), //e.g., "com.team4.leaveprocessingsystem.model.Employee"
                    table.getName() //e.g., "employee"
            );

            for (Property property : persistentClass.getProperties()) {
                for (Column column : property.getColumns()) {
                    columnMetadataService.saveColumn(
                            property.getName(),
                            column.getName(),
                            column.getSqlType(),
                            tableMetadata
                    );
                }
            }
        }
        System.out.println("-- END TABLE METADATA EXTRACTION --");
    }
}
