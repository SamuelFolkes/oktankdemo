package com.oktank.demo.service;

import com.amazonaws.services.rdsdata.AWSRDSData;
import com.amazonaws.services.rdsdata.AWSRDSDataClient;
import com.amazonaws.services.rdsdata.model.ExecuteStatementRequest;
import com.amazonaws.services.rdsdata.model.ExecuteStatementResult;
import com.amazonaws.services.rdsdata.model.Field;

import java.util.List;

public class DataService {
    //public static final String RESOURCE_ARN = "arn:aws:rds:us-east-1:123456789012:cluster:mydbcluster";
    //public static final String SECRET_ARN = "arn:aws:secretsmanager:us-east-1:123456789012:secret:mysecret";

    // static variable single_instance of type Singleton
    private static DataService single_instance = null;

    public static DataService getInstance()
    {
        if (single_instance == null)
            single_instance = new DataService();
        return single_instance;
    }

    public ExecuteStatementResult Query(String sql, String dbClusterArn, String dbName, String dbSecretArn) {
        AWSRDSData rdsData = AWSRDSDataClient.builder().build();

        ExecuteStatementRequest request = new ExecuteStatementRequest()
                .withResourceArn(dbClusterArn)
                .withSecretArn(dbSecretArn)
                .withDatabase(dbName)
                .withSql(sql);

        return rdsData.executeStatement(request);
    }

    /*
    public static void Init(String clusterArn, String secretArn) {
        AWSRDSData rdsData = AWSRDSDataClient.builder().build();

        ExecuteStatementRequest request = new ExecuteStatementRequest()
                .withResourceArn(clusterArn)
                .withSecretArn(secretArn)
                .withDatabase("oktankdemo")
                .withSql("select * from mytable");

        ExecuteStatementResult result = rdsData.executeStatement(request);

        for (List<Field> fields: result.getRecords()) {
            String stringValue = fields.get(0).getStringValue();
            long numberValue = fields.get(1).getLongValue();

            System.out.println(String.format("Fetched row: string = %s, number = %d", stringValue, numberValue));
        }
    }
    */
}
