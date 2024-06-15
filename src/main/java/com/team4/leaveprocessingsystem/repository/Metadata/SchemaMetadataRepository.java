package com.team4.leaveprocessingsystem.repository.Metadata;

import com.team4.leaveprocessingsystem.model.Metadata.SchemaMetadata;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SchemaMetadataRepository extends JpaRepository<SchemaMetadata, Integer> {

    List<String> findAllEntityName();

    List<String> findAllTableName();

}
