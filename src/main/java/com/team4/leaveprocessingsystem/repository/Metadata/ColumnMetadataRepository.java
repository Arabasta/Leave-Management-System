package com.team4.leaveprocessingsystem.repository.Metadata;

import com.team4.leaveprocessingsystem.model.Metadata.ColumnMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ColumnMetadataRepository extends JpaRepository<ColumnMetadata, Integer> {

    @Query("SELECT c.attributeName FROM ColumnMetadata c " +
            "WHERE c.tableMetadata.entityName =: entityName")
    List<String> findAllAttributeByEntityName(String entityName);

    @Query("SELECT c FROM ColumnMetadata c " +
            "WHERE c.tableMetadata.entityName = :entityName")
    List<ColumnMetadata> findAllByEntityName(String entityName);

    List<ColumnMetadata> findByTableMetadataId(int tableMetadataId);

}
