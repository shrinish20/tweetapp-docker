package com.tweetapp.webservices;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tweetapp.domain.CustomResponse;
import com.tweetapp.domain.Tweet;
import com.tweetapp.domain.TweetReply;
import com.tweetapp.exception.NoResultsFoundException;
import com.tweetapp.service.KafkaProducerService;
import com.tweetapp.service.TweetReplyService;
import com.tweetapp.service.TweetService;
import com.tweetapp.util.TweetConstant;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * Class TweetController
 *
 */
@Api(tags = "TweetController")
@RestController
@EnableAutoConfiguration
@RequestMapping(value = "/api/v1.0/tweets")
public class TweetController {

	private TweetService tweetService;

	public TweetController(TweetService tweetService) {
		this.tweetService = tweetService;
	}

	@Autowired
	private KafkaProducerService kafkaProducerService;

	@Autowired
	private TweetReplyService tweetReplyService;

	private static final Logger LOGGER = Logger.getLogger(TweetController.class);

	@ApiOperation(value = "Retrieves List of tweets")
	@RequestMapping(value = "/all", method = RequestMethod.GET, produces = TweetConstant.APPLICATION_JSON)
	public ResponseEntity<CustomResponse> getAllTweets() {
		LOGGER.info("Entering getAllTweets() API :::: {}");
		CustomResponse response;
		List<Tweet> tweetList = tweetService.getAllTweets();
		if (tweetList.isEmpty()) {
			LOGGER.error("No Tweets Found");
			throw new NoResultsFoundException("No Tweets Found");
		} else {
			response = new CustomResponse(HttpStatus.OK, TweetConstant.SUCCESS_RETRIEVED, LocalDateTime.now(),
					tweetList);
		}
		LOGGER.info("Exiting getAllTweets() API :::: {}");
		return new ResponseEntity<CustomResponse>(response, response.getStatus());
	}

	@ApiOperation(value = "Retrieve List of tweets by Username")
	@RequestMapping(value = "/{loginId}", method = RequestMethod.GET, produces = TweetConstant.APPLICATION_JSON)
	public ResponseEntity<CustomResponse> getUserTweets(
			@ApiParam(value = "username", required = true) @PathVariable(name = "loginId", required = true) String loginId) {
		LOGGER.info("Entering getUserTweets() API :::: {}");
		CustomResponse response;
		List<Tweet> tweetList = tweetService.getUserTweets(loginId);
		if (tweetList.isEmpty()) {
			LOGGER.error("No Tweets Found for Id " + loginId);
			throw new NoResultsFoundException("No Tweets Found for Id " + loginId);
		} else {
			response = new CustomResponse(HttpStatus.OK, TweetConstant.SUCCESS_RETRIEVED, LocalDateTime.now(),
					tweetList);
		}
		LOGGER.info("Exiting getUserTweets() API :::: {}");
		return new ResponseEntity<CustomResponse>(response, response.getStatus());
	}

	@ApiOperation(value = "To post a tweet")
	@RequestMapping(value = "/{loginId}/add", method = RequestMethod.POST, consumes = TweetConstant.APPLICATION_JSON)
	public ResponseEntity<CustomResponse> postTweet(
			@ApiParam(value = "username", required = true) @PathVariable(name = "loginId", required = true) String loginId,
			@RequestBody Tweet tweet) {
		LOGGER.info("Entering postTweet() API :::: {}");
		CustomResponse response;
		tweet.setLoginId(loginId);
		tweet.setLikes(0);
		tweet.setReply(Collections.emptyList());
		tweet.setPostedDate((new Timestamp(new Date().getTime())).toString());
		String result = tweetService.postTweet(tweet, loginId);
		kafkaProducerService.publishMessage(tweet.getTweetMessage(), loginId);
		if ("Success".equalsIgnoreCase(result)) {
			response = new CustomResponse(HttpStatus.CREATED, TweetConstant.TWEET_SUCCESS, LocalDateTime.now());
		} else {
			response = new CustomResponse(HttpStatus.UNPROCESSABLE_ENTITY, result, LocalDateTime.now());
		}
		LOGGER.info("Exiting postTweet() API :::: {}");
		return new ResponseEntity<CustomResponse>(response, response.getStatus());
	}

	@ApiOperation(value = "Updates a posted tweet")
	@RequestMapping(value = "/{loginId}/update/{id}", method = RequestMethod.PUT, consumes = TweetConstant.APPLICATION_JSON)
	public ResponseEntity<CustomResponse> updateTweet(
			@ApiParam(value = "username", required = true) @PathVariable(name = "loginId", required = true) String loginId,
			@ApiParam(value = "tweetId", required = true) @PathVariable(name = "id", required = true) String id,
			@RequestBody Tweet tweet) {
		LOGGER.info("Entering updateTweet() API :::: {}");
		CustomResponse response;
		String result = tweetService.updateTweet(id, tweet, loginId);
		if ("Success".equalsIgnoreCase(result)) {
			response = new CustomResponse(HttpStatus.OK, TweetConstant.TWEET_UPDATED, LocalDateTime.now());
		} else {
			response = new CustomResponse(HttpStatus.UNPROCESSABLE_ENTITY, result, LocalDateTime.now());
		}
		LOGGER.info("Exiting updateTweet() API :::: {}");
		return new ResponseEntity<CustomResponse>(response, response.getStatus());
	}

	@ApiOperation(value = "Deletes a tweet")
	@RequestMapping(value = "/{loginId}/delete/{id}", method = RequestMethod.DELETE, produces = TweetConstant.APPLICATION_JSON)
	public ResponseEntity<CustomResponse> deleteTweet(
			@ApiParam(value = "username", required = true) @PathVariable(name = "loginId", required = true) String loginId,
			@ApiParam(value = "tweetId", required = true) @PathVariable(name = "id", required = true) String id) {
		LOGGER.info("Entering deleteTweet() API :::: {}");
		CustomResponse response;
		tweetService.deleteTweet(id, loginId);
		response = new CustomResponse(HttpStatus.NO_CONTENT, TweetConstant.TWEET_DELETED, LocalDateTime.now());
		LOGGER.info("Exiting deleteTweet() API :::: {}");
		return new ResponseEntity<CustomResponse>(response, response.getStatus());
	}

	@ApiOperation(value = "To like a tweet")
	@RequestMapping(value = "/{loginId}/like/{id}", method = RequestMethod.PUT)
	public ResponseEntity<CustomResponse> likeTweet(
			@ApiParam(value = "username", required = true) @PathVariable(name = "loginId", required = true) String loginId,
			@ApiParam(value = "tweetId", required = true) @PathVariable(name = "id", required = true) String id,
			@RequestParam(required = true) boolean isLiked) {
		LOGGER.info("Entering likeTweet() API :::: {}");
		CustomResponse response;
		String result = tweetService.likeTweet(id, isLiked);
		if ("Success".equalsIgnoreCase(result)) {
			response = new CustomResponse(HttpStatus.ACCEPTED,
					((isLiked) ? TweetConstant.TWEET_LIKED : TweetConstant.TWEET_DISLIKED), LocalDateTime.now());
		} else {
			response = new CustomResponse(HttpStatus.EXPECTATION_FAILED, result, LocalDateTime.now());
		}
		LOGGER.info("Exiting likeTweet() API :::: {}");
		return new ResponseEntity<CustomResponse>(response, response.getStatus());
	}

	@ApiOperation(value = "Reply to other's tweet")
	@RequestMapping(value = "/{loginId}/reply/{id}", method = RequestMethod.POST, consumes = TweetConstant.APPLICATION_JSON)
	public ResponseEntity<CustomResponse> replyTweet(
			@ApiParam(value = "username", required = true) @PathVariable(name = "loginId", required = true) String loginId,
			@ApiParam(value = "tweetId", required = true) @PathVariable(name = "id", required = true) String id,
			@RequestBody TweetReply reply) {
		LOGGER.info("Entering replyTweet() API :::: {}");
		CustomResponse response;
		String result = tweetReplyService.replyTweet(id, loginId, reply);
		if ("Success".equalsIgnoreCase(result)) {
			response = new CustomResponse(HttpStatus.ACCEPTED, TweetConstant.TWEET_REPLIED, LocalDateTime.now());
		} else {
			response = new CustomResponse(HttpStatus.EXPECTATION_FAILED, result, LocalDateTime.now());
		}
		LOGGER.info("Exiting replyTweet() API :::: {}");
		return new ResponseEntity<CustomResponse>(response, response.getStatus());
	}

}
