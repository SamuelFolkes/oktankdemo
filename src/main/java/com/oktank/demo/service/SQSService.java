package com.oktank.demo.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oktank.demo.model.Employee;
import com.oktank.demo.model.VerifyMessage;

public class SQSService {
    private static SQSService single_instance = null;

    public static SQSService getInstance()
    {
        if (single_instance == null)
            single_instance = new SQSService();
        return single_instance;
    }

    public void SendMessage(Employee employee)
    {
        try {
            AmazonSQS sqs = AmazonSQSClientBuilder.standard()
                    .withRegion("us-east-1")
                    .build();

            String queueUrl = System.getenv("NOTIFY_QUEUE_URL");

            VerifyMessage msg = new VerifyMessage();
            msg.setId(employee.getId());
            msg.setEmail(employee.getEmail());
            msg.setVerified(employee.getVerified());
            msg.setName(employee.getName());
            msg.setMessageType("request");

            ObjectMapper Obj = new ObjectMapper();
            String jsonStr = Obj.writeValueAsString(msg);

            System.out.println(jsonStr);

            SendMessageRequest send_msg_request = new SendMessageRequest()
                    .withQueueUrl(queueUrl)
                    .withMessageBody(jsonStr)
                    .withDelaySeconds(5);
            sqs.sendMessage(send_msg_request);

        } catch (AmazonServiceException e) {
            e.printStackTrace();
        } catch (SdkClientException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (Exception e ) {
            e.printStackTrace();
        }
    }
}
