package com.test.dog.dogbreeder;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class BeanConfiguration {

    @Autowired
    private Environment env;

    @Bean
    public AmazonS3 amazonS3(AWSCredentials credentials) {
        AmazonS3Client s3client = new AmazonS3Client(credentials);
        return s3client;
    }

    @Bean
    public AWSCredentials awsCredentials() {
        String accessKey = env.getProperty("cloud.aws.credentials.accessKey");
        String secretKey = env.getProperty("cloud.aws.credentials.secretKey");
        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        return credentials;
    }
}
