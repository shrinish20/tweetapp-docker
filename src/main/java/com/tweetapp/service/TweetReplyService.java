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
import com.tweetapp.domain.TweetReply;
import com.tweetapp.domain.User;
import com.tweetapp.repository.TweetReplyRepository;
import com.tweetapp.repository.TweetRepository;
import com.tweetapp.repository.UserRepository;

@Service
public class TweetReplyService {

	private TweetReplyRepository tweetReplyRepo;

	public TweetReplyService(TweetReplyRepository tweetReplyRepo) {
		this.tweetReplyRepo = tweetReplyRepo;
	}

	@Autowired
	private TweetRepository tweetRepo;

	@Autowired
	private UserRepository userRepo;

	private static final Logger LOGGER = Logger.getLogger(TweetReplyService.class);

	public String replyTweet(String id, String loginId, TweetReply tweetReply) {
		LOGGER.info("Entering replyTweet() service :::: {}");
		Tweet tweetObj = new Tweet();
		Tweet result = null;
		TweetReply resObj = null;
		User finalResult = null;
		List<TweetReply> userReplyList = new ArrayList<TweetReply>();
		User userObj = userRepo.findByLoginId(loginId);
		tweetReply.setFirstName(userObj.getFirstName());
		tweetReply.setLastName(userObj.getLastName());
		tweetReply.setTweetId(id);
		tweetReply.setLoginId(loginId);
		tweetReply.setPostedDate(new Timestamp(new Date().getTime()).toString());
		resObj = tweetReplyRepo.save(tweetReply);
		Optional<Tweet> tweetList = tweetRepo.findById(id);
		if (tweetList.isPresent()) {
			tweetObj = tweetList.get();
			if (tweetObj.getReply() != null && resObj != null) {
				List<TweetReply> replyLists = tweetObj.getReply();
				replyLists.add(tweetReply);
				tweetObj.setReply(replyLists);
				result = tweetRepo.save(tweetObj);
				if (userObj.getTweetList() != null && result != null) {
					for (Tweet obj : userObj.getTweetList()) {
						if (obj.getId().equals(id)) {
							userReplyList = obj.getReply();
							userReplyList.add(tweetReply);
							obj.setReply(userReplyList);
							LOGGER.info("Exiting updateTweet() service :::: {}");
						}
					}
				}
				finalResult = userRepo.save(userObj);
			}
		}
		LOGGER.info("Exiting replyTweet() service :::: {}");
		return (finalResult != null) ? "Success" : "Failed";
	}
}
