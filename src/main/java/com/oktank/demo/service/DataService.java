package com.oktank.demo.service;

import com.amazonaws.services.rdsdata.AWSRDSData;
import com.amazonaws.services.rdsdata.AWSRDSDataClient;
import com.amazonaws.services.rdsdata.model.ExecuteStatementRequest;
import com.amazonaws.services.rdsdata.model.ExecuteStatementResult;
import com.amazonaws.services.rdsdata.model.Field;

import java.util.List;

public class DataService {

    private static DataService single_instance = null;

    public static DataService getInstance()
    {
        if (single_instance == null)
            single_instance = new DataService();
        return single_instance;
    }

    public ExecuteStatementResult Query(String sql) {
        AWSRDSData rdsData = AWSRDSDataClient.builder().build();

        ExecuteStatementRequest request = new ExecuteStatementRequest()
                .withResourceArn(System.getenv("DB_CLUSTER_ARN"))
                .withSecretArn(System.getenv("SECRET_ARN"))
                .withDatabase("oktankdemo")
                .withSql(sql);

        return rdsData.executeStatement(request);
    }
}
