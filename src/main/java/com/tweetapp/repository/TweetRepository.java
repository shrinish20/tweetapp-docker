package com.tweetapp.repository;

import java.util.List;

import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

import com.tweetapp.domain.Tweet;

//@Repository
@EnableScan
public interface TweetRepository extends CrudRepository<Tweet, String>{
	List<Tweet> findByLoginId(String loginId);
}
