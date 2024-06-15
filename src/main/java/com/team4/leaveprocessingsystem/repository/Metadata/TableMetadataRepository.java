package com.team4.leaveprocessingsystem.repository.Metadata;

import com.team4.leaveprocessingsystem.model.Metadata.TableMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TableMetadataRepository extends JpaRepository<TableMetadata, Integer> {

    @Query("SELECT t.attributeName FROM TableMetadata t " +
            "WHERE t.schemaMetadata.entityName =: entityName")
    List<String> findAllAttributeByEntityName(String entityName);

    @Query("SELECT t FROM TableMetadata t " +
            "WHERE t.schemaMetadata.entityName = :entityName")
    List<TableMetadata> findAllByEntityName(String entityName);

}
