package com.tweetapp.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Service
public class KafkaProducerService {
	
	public static Logger log = Logger.getLogger(KafkaProducerService.class);
	
	@Value(value = "${kafka.topic.name}")
	private String topicName;
	
	private KafkaTemplate<String, String> kafkaTemplate;

	public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
	}

	public void publishMessage(String tweet, String loginId) {		
		log.info("Publishing Message {} :::::: {}");		
		ListenableFuture<SendResult<String, String>> futureResult = this.kafkaTemplate.send(topicName, tweet);
		futureResult.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {

			@Override
			public void onSuccess(SendResult<String, String> result) {				
				log.info("Success!! Message Published - " + tweet + " by User - " + loginId);				
			}

			@Override
			public void onFailure(Throwable ex) {
				log.error("Error!! Unable to Publish message - " + ex.getMessage());				
			}
		});	
	}
	
	

}
