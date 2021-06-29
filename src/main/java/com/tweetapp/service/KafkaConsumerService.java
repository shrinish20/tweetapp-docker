package com.tweetapp.service;

import org.apache.log4j.Logger;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {
	
	public static Logger log = Logger.getLogger(KafkaConsumerService.class);
	
	@KafkaListener(topics = "${kafka.topic.name}", groupId = "${kafka.group.id}", containerFactory = "concurrentKafkaListenerContainerFactory")
	public void consumeMessage(String msg) {
		log.info("Consuming Messages using Listener :::::: {} " + msg);		
	}

}
