package com.tweetapp.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

@Configuration
public class KafkaTopicConfig {

	@Value(value = "${kafka.topic.name}")
	private String topicName;

	@Bean
	public KafkaAdmin kafkaAdmin() {
		Map<String, Object> adminConfigs = new HashMap<String, Object>();
		adminConfigs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		return new KafkaAdmin(adminConfigs);
	}

//	@Bean
//	public NewTopic tweetTopic() {
//		return TopicBuilder.name(topicName).partitions(1).replicas(1).build();
//	}

}
