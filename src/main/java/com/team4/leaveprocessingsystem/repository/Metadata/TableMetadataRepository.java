package com.team4.leaveprocessingsystem.repository.Metadata;

import com.team4.leaveprocessingsystem.model.Metadata.TableMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TableMetadataRepository extends JpaRepository<TableMetadata, Integer> {

    @Query("SELECT t.entityName FROM TableMetadata t")
    List<String> findAllEntityName();

    @Query("SELECT t.tableName FROM TableMetadata t")
    List<String> findAllTableName();

}
