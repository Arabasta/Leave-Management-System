package com.team4.leaveprocessingsystem.util;

import com.team4.leaveprocessingsystem.model.Metadata.ColumnMetadata;
import com.team4.leaveprocessingsystem.service.Metadata.TableMetadataService;
import com.team4.leaveprocessingsystem.service.Metadata.ColumnMetadataService;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import java.util.List;

public class DataBuilderUtils {

    private final TableMetadataService tableMetadataService;
    private final ColumnMetadataService columnMetadataService;

    public DataBuilderUtils(TableMetadataService tableMetadataService,
                            ColumnMetadataService columnMetadataService) {
        this.tableMetadataService = tableMetadataService;
        this.columnMetadataService = columnMetadataService;
    }

    public JSONArray getTableMetadataAsJSON() {
        JSONArray jsonArray = new JSONArray();
        List<String> list = tableMetadataService.findAllEntityName();
        jsonArray.addAll(list);
        System.out.println(jsonArray);
        return jsonArray;
    }

    public JSONArray getColumnByTableMetadataIdAsJSON(int id) {
        JSONArray jsonArray = new JSONArray();
        List<ColumnMetadata> list = columnMetadataService.findByTableMetadataId(id);
        jsonArray.addAll(list);
        System.out.println(jsonArray);
        return jsonArray;
    }

    //TODO: figure out how to implement generalised method that allows other use cases to input relevant param to extract data from Metadata
}
