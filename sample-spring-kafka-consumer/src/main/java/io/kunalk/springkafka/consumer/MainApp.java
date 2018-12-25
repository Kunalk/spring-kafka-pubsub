package io.kunalk.springkafka.consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import io.kunalk.springkafka.consumer.config.KafkaConsumerProperties;

@SpringBootApplication
@EnableConfigurationProperties(KafkaConsumerProperties.class)
public class MainApp {
    public static void main(String[] args) {
        SpringApplication.run(MainApp.class, args);
    }
}
