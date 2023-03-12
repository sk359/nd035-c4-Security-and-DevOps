package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.services.Utils;

import org.slf4j.Logger;
import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;


// source: https://github.com/udacity/nd035-c4-Security-and-DevOps/tree/1.Auth

@RestController
@RequestMapping("/api/user")
public class UserController {
	
	// log4j directly:
	//private static Logger logger = LogManager.getLogger();
	
	// using facade pattern with slf4j:
	private Logger logger = LoggerFactory.getLogger(UserController.class);	
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@GetMapping("/id/{id}")
	public ResponseEntity<User> findById(@PathVariable Long id) {
		return ResponseEntity.of(userRepository.findById(id));
	}
	
	@GetMapping("/{username}")
	public ResponseEntity<User> findByUserName(@PathVariable String username) {
		User user = userRepository.findByUsername(username);
		return user == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(user);
	}
	
	@PostMapping("/create")
	public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) {
		User user = new User();
		user.setUsername(createUserRequest.getUsername());
		Cart cart = new Cart();
		cartRepository.save(cart);
		user.setCart(cart);
		if(createUserRequest.getPassword().length()<7 ||
				!createUserRequest.getPassword().equals(createUserRequest.getConfirmPassword())){
			//System.out.println("Error - Either length is less than 7 or pass and conf pass do not match. Unable to create ",
			//		createUserRequest.getUsername());
			String logMessage = String.format("Password for User %s not accepted", user.getUsername());
			String isoTime = Utils.getIsotimeNow();
			logger.error(logMessage, logMessage, isoTime, "ERROR", "UserController", "403");
			return ResponseEntity.badRequest().build();
		}
		user.setPassword(bCryptPasswordEncoder.encode(createUserRequest.getPassword()));
		String isoTime = Utils.getIsotimeNow();
		String logMessage = "";
		try {
		  userRepository.save(user);
		  logMessage = String.format("Created new User %s with ID %d", user.getUsername(), user.getId());
		  // use error to match the log level of the csv file
		  logger.error(logMessage, logMessage, isoTime, "INFO", "UserController", "200");
		  return ResponseEntity.ok(user);
		} catch (Exception e) {
			logMessage = String.format("Can not create new User %s: %s ", user.getUsername(), e);
			logger.error(logMessage, logMessage, isoTime, "ERROR", "UserController", "500");
			return ResponseEntity.of(null);
		}		
		
	}
	
}
