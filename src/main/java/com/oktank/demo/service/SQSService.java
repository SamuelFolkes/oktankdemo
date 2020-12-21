package com.oktank.demo.service;

public class SQSService {
    private static SQSService single_instance = null;

    public static SQSService getInstance()
    {
        if (single_instance == null)
            single_instance = new SQSService();
        return single_instance;
    }

    public void SendMessage(String imgData, String keyName)
    {
       /*AmazonSQS sqs = AmazonSQSClientBuilder.standard()
               .withRegion(Regions.EU_WEST_1)
               .build();*/
    }
}
