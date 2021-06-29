package com.tweetapp.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tweetapp.domain.Tweet;
import com.tweetapp.domain.User;
import com.tweetapp.repository.TweetRepository;
import com.tweetapp.repository.UserRepository;

@Service
public class TweetService {

	private TweetRepository tweetRepo;

	public TweetService(TweetRepository tweetRepo) {
		this.tweetRepo = tweetRepo;
	}

	@Autowired
	private UserRepository userRepo;

	private static final Logger LOGGER = Logger.getLogger(TweetService.class);

	public List<Tweet> getAllTweets() {
		LOGGER.info("Entering getAllTweets() service :::: {}");
		List<Tweet> tweets = (List<Tweet>) tweetRepo.findAll();
		LOGGER.info("Exiting getAllTweets() service :::: {}");
		return tweets;
	}

	public String postTweet(Tweet tweet, String loginId) {
		LOGGER.info("Entering postTweet() service :::: {}");
		List<Tweet> tweetList = new ArrayList<Tweet>();
		User result = null;
		User userObj = userRepo.findByLoginId(loginId);
		if (userObj.getTweetList() != null) {
			tweetList = userObj.getTweetList();
		}
		tweet.setFirstName(userObj.getFirstName());
		tweet.setLastName(userObj.getLastName());
		Tweet resObj = tweetRepo.save(tweet);
		if (resObj != null) {
			tweetList.add(tweet);
			userObj.setTweetList(tweetList);
			result = userRepo.save(userObj);
		}
		LOGGER.info("Exiting postTweet() service :::: {}");
		return (resObj != null && result != null) ? "Success" : "Failed";
	}

	public String updateTweet(String id, Tweet tweet, String loginId) {
		LOGGER.info("Entering updateTweet() service :::: {}");
		User result = null;
		// List<Tweet> tweetList = userObj.getTweetList();
		Optional<Tweet> tweetLists = tweetRepo.findById(id);
		Tweet tweetObj = tweetLists.get();
		tweetObj.setTweetMessage(tweet.getTweetMessage());
		tweetObj.setPostedDate((new Timestamp(new Date().getTime())).toString());
		Tweet resObj = tweetRepo.save(tweetObj);
		if (resObj != null) {
			User userObj = userRepo.findByLoginId(loginId);
			if (userObj.getTweetList() != null) {
				for (Tweet obj : userObj.getTweetList()) {
					if (obj.getId().equals(id)) {
						obj.setTweetMessage(tweet.getTweetMessage());
						LOGGER.info("Exiting updateTweet() service :::: {}");
					}
				}
			}
			result = userRepo.save(userObj);
		}
		LOGGER.info("Exiting updateTweet() service :::: {}");
		return (resObj != null && result != null) ? "Success" : "Failed";
	}

	public void deleteTweet(String id, String loginId) {
		LOGGER.info("Entering deleteTweet() service :::: {}");
		int deleteIndex = 0;
		User result = null;
		User userObj = userRepo.findByLoginId(loginId);
		tweetRepo.deleteById(id);
		if (userObj.getTweetList() != null) {
			for (int i = 0; i < userObj.getTweetList().size(); i++) {
				if (userObj.getTweetList().get(i).getId().equals(id)) {
					deleteIndex = i;
					break;
				}
			}
			userObj.getTweetList().remove(deleteIndex);
			result = userRepo.save(userObj);
		}
	}

	public String likeTweet(String id, boolean isLiked) {
		LOGGER.info("Entering likeTweet() service :::: {}");
		Tweet resObj = null;
		User result = null;
		Optional<Tweet> tweetList = tweetRepo.findById(id);
		Tweet tweetObj = tweetList.get();
		int likes = 0;
		if (tweetList.isPresent()) {
			likes = tweetList.get().getLikes();
			likes = (isLiked && likes >= 0) ? (likes + 1) : ((likes > 0) ? (likes - 1) : 0);
			tweetObj.setLikes(likes);
			resObj = tweetRepo.save(tweetObj);
			if (resObj != null) {
				User userObj = userRepo.findByLoginId(tweetObj.getLoginId());
				if (userObj.getTweetList() != null) {
					for (Tweet obj : userObj.getTweetList()) {
						if (obj.getId().equals(id)) {
							obj.setLikes(likes);
							LOGGER.info("Exiting updateTweet() service :::: {}");
						}
					}
				}
				result = userRepo.save(userObj);
			}
		}
		LOGGER.info("Exiting likeTweet() service :::: {}");
		return (resObj != null && result != null) ? "Success" : "Failed";
	}

	public List<Tweet> getUserTweets(String userName) {
		LOGGER.info("Entering getUserTweets() service :::: {}");
		List<Tweet> tweetList = tweetRepo.findByLoginId(userName);
		LOGGER.info("Exiting getUserTweets() service :::: {}");
		return tweetList;
	}

}