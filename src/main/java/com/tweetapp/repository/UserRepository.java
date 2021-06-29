package com.tweetapp.repository;

import java.util.List;

import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

import com.tweetapp.domain.User;
//@Repository
@EnableScan
public interface UserRepository extends CrudRepository<User, String>{
	boolean existsByEmailId(String emailId);
	boolean existsByLoginId(String loginId);
	User findByLoginId(String loginId);
	List<User> findAll();
}
