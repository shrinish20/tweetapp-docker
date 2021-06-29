package com.tweetapp.repository;

import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

import com.tweetapp.domain.TweetReply;


//@Repository
@EnableScan
public interface TweetReplyRepository extends CrudRepository<TweetReply, String> {
	
}
