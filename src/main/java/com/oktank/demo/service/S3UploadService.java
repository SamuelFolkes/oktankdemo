package com.oktank.demo.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;

public class S3UploadService {
    private static S3UploadService single_instance = null;

    public static S3UploadService getInstance()
    {
        if (single_instance == null)
            single_instance = new S3UploadService();
        return single_instance;
    }

    public void UploadBase64(String imgData, String keyName) {
        System.out.println(imgData);
        String bucketName = System.getenv("BUCKET_NAME");
        byte[] bI = Base64.getDecoder().decode((imgData.substring(imgData.indexOf(",")+1)).getBytes());
        InputStream fIs = new ByteArrayInputStream(bI);

        try {
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withRegion("us-east-1")
                    .build();

            ObjectMetadata metadata = new ObjectMetadata();

            metadata.setContentLength(bI.length);
            metadata.setContentType("image/jpeg");
            metadata.setCacheControl("public, max-age=31536000");

            PutObjectRequest request = new PutObjectRequest(bucketName, keyName, fIs, metadata);
            request.withCannedAcl(CannedAccessControlList.PublicRead);
            s3Client.putObject(request);

        } catch (AmazonServiceException e) {
            e.printStackTrace();
        } catch (SdkClientException e) {
            e.printStackTrace();
        }
    }
}
