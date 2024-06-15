package com.team4.leaveprocessingsystem.interfacemethods.Metadata;

import com.team4.leaveprocessingsystem.model.Metadata.SchemaMetadata;
import org.springframework.ui.Model;

import java.util.List;

public interface ISchemaMetadata {

    SchemaMetadata save(SchemaMetadata schemaMetadata);

    SchemaMetadata saveTable(String entityName, String tableName);

    List<Class<? extends Model>> getEntityList();
}
