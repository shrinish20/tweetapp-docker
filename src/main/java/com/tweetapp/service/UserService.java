package com.tweetapp.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.tweetapp.domain.User;
import com.tweetapp.exception.EntityNotFoundException;
import com.tweetapp.repository.UserRepository;

@Service
public class UserService implements UserDetailsService{

	@Autowired
	private UserRepository userRepo;
	
	private static final Logger LOGGER = Logger.getLogger(UserService.class);
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User userObj = userRepo.findByLoginId(username);
		if(userObj == null) {
			throw new EntityNotFoundException(username + " - Username Not Found");
		}
		return new org.springframework.security.core.userdetails.User(userObj.getLoginId(), userObj.getUserPassword(), new ArrayList<>());
	}

	public List<User> getUserDetails() {
		LOGGER.info("Entering getUserDetails() service :::: {}");
		List<User> userList = userRepo.findAll();
		LOGGER.info("Exiting getUserDetails() service :::: {}");
		return userList;
	}

	public String registerUser(User user) {
		LOGGER.info("Entering registerUser() service :::: {}");
		String result = "";
		boolean isLoginId = userRepo.existsByLoginId(user.getLoginId());
		boolean isEmailId = userRepo.existsByEmailId(user.getEmailId()); 		
		if(!(isLoginId || isEmailId)) {
			User resObj = userRepo.save(user);
			result = (resObj != null) ? "Success" : "Failed";
		} else {
			result = "User Already Exist for the ID - " + (isLoginId) != null?user.getLoginId():user.getEmailId();
		}
		LOGGER.info("Exiting registerUser() service :::: {}");
		return result;
	}

	public User searchByUsername(String loginId) {
		LOGGER.info("Entering searchByUsername() service :::: {}");
		User userObj = userRepo.findByLoginId(loginId);
		LOGGER.info("Exiting searchByUsername() service :::: {}");
		return userObj;
	}

	public String resetPassword(User user) {
		LOGGER.info("Entering resetPassword() service :::: {}");
		boolean isLoginId = userRepo.existsByLoginId(user.getLoginId());
		User resObj = null;
		if(isLoginId) {
			User userObj = userRepo.findByLoginId(user.getLoginId());
			userObj.setUserPassword(user.getUserPassword());
			resObj = userRepo.save(userObj);			
		}		
		String result = (resObj != null) ? "Success" : "Failed";
		LOGGER.info("Exiting resetPassword() service :::: {}");
		return result;
	}

}
