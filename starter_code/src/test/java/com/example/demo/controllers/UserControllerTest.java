package com.example.demo.controllers;

import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;

import org.junit.Assert;

@RunWith(SpringRunner.class)  // instructs the spring-test module that it should create an ApplicationContext
public class UserControllerTest {
	
	@Mock
	private UserRepository userRepository;
	
	@Mock
	private CartRepository cartRepository;
	
	@Mock
	private BCryptPasswordEncoder passwordEncoder;
	
	@InjectMocks
	private UserController userController;
	
	@Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);            
    }
	
	private User createUser(String name, String password) {
		User user = new User();
		user.setUsername(name);
		user.setPassword(password);
		return user;
	}
	
	@Test
	public void testFindNoneByName() {		
		when(userRepository.findByUsername("Rick")).thenReturn(null);
		
		ResponseEntity<User> userResponseEntity = userController.findByUserName("Rick");
		
		Assert.assertEquals(404, userResponseEntity.getStatusCodeValue());		
	}
	
	@Test
	public void testFindOneByName() {	
		User user = createUser("Rick", "Sanchez137");
		Cart cart = new Cart();
		final int cartTotal = 112;
		cart.setTotal(new BigDecimal(cartTotal));
		user.setCart(cart);
		when(userRepository.findByUsername("Rick")).thenReturn(user);
		
		ResponseEntity<User> userResponseEntity = userController.findByUserName("Rick");
		
		Assert.assertEquals(200, userResponseEntity.getStatusCodeValue());
		Assert.assertEquals("Rick", userResponseEntity.getBody().getUsername());
		Assert.assertEquals(cartTotal, userResponseEntity.getBody().getCart().getTotal().intValue());
	}
	
	@Test
	public void testCreateUserPasswordTooShort() {
		CreateUserRequest createRequest = new CreateUserRequest();
		final String shortPassword = "abc";
		final String userName = "Rick";
		createRequest.setPassword(shortPassword);
		createRequest.setConfirmPassword(shortPassword);
		createRequest.setUsername(userName);
		User user = createUser(userName, shortPassword);
		Cart cart = new Cart();
		final int cartTotal = 112;
		cart.setTotal(new BigDecimal(cartTotal));
		when(cartRepository.save(cart)).thenReturn(cart);
		when(userRepository.save(user)).thenReturn(user);
		
		ResponseEntity<User> userResponseEntity = userController.createUser(createRequest);
		
		Assert.assertEquals(HttpStatus.BAD_REQUEST.value(), userResponseEntity.getStatusCodeValue());		
	}
	
	@Test
	public void testCreateUserPasswordUnequalConfirm() {
		CreateUserRequest createRequest = new CreateUserRequest();
		final String okPassword = "Sanchez137";
		final String userName = "Rick";
		createRequest.setPassword(okPassword);
		createRequest.setConfirmPassword("SanchezC137");
		createRequest.setUsername(userName);
		User user = createUser(userName, okPassword);
		Cart cart = new Cart();
		final int cartTotal = 112;
		cart.setTotal(new BigDecimal(cartTotal));
		when(cartRepository.save(cart)).thenReturn(cart);
		when(userRepository.save(user)).thenReturn(user);
		
		ResponseEntity<User> userResponseEntity = userController.createUser(createRequest);
		
		Assert.assertEquals(HttpStatus.BAD_REQUEST.value(), userResponseEntity.getStatusCodeValue());		
	}
	
	@Test
	public void testCreateUserPasswordOk() {
		CreateUserRequest createRequest = new CreateUserRequest();
		final String okPassword = "Sanchez137";
		final String userName = "Rick";
		createRequest.setPassword(okPassword);
		createRequest.setConfirmPassword(okPassword);
		createRequest.setUsername(userName);
		User user = createUser(userName, okPassword);
		Cart cart = new Cart();
		final int cartTotal = 112;
		cart.setTotal(new BigDecimal(cartTotal));
		when(cartRepository.save(cart)).thenReturn(cart);
		when(userRepository.save(user)).thenReturn(user);
		when(passwordEncoder.encode(okPassword)).thenCallRealMethod();
		
		ResponseEntity<User> userResponseEntity = userController.createUser(createRequest);
		
		Assert.assertEquals(HttpStatus.OK.value(), userResponseEntity.getStatusCodeValue());
		Assert.assertEquals(userName, userResponseEntity.getBody().getUsername());
	}
	
	
    
    /*
    @Test
    @WithMockUser("customUsername")
    public void getMessageWithMockUserCustomUsername() {
    	ResponseEntity<User> response = userController.findByUserName("customUsername");
    	Assert.assertEquals(response.getBody().getUsername(), "customUsername");    
    }
    */
    
    /*
        
    @Test
    public void getByUsernameWithoutJWT() throws URISyntaxException, Exception {
    // works!!
    	given(userRepository.findByUsername("testname")).willReturn(null);
    	
    	mvc.perform(MockMvcRequestBuilders.get((new URI("/api/user/testname"))))
    			.andExpect(MockMvcResultMatchers.status().is(403));
    		    //.andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(1));
    }
    
    
    @Test
    public void getByUsernameWithJWTNotFound() throws URISyntaxException, Exception {
    	given(userRepository.findByUsername("testname")).willReturn(null);
    	//given(jwtVerification.getAuthentication(any())).willReturn(null);
    	    	    	
    	mvc.perform(MockMvcRequestBuilders.get((new URI("/api/user/testname")))
    			.header("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJZb2tvIiwiZXhwIjoxNjc4Mzk0OTY1fQ.Fg9uKgkZvBHRom64FLpH9I1gQjDHsiPBNomvhLpYMsNWj_yKcK3jKg9Y2UxnS0zsh3exdzRoMFeJu4tX2PW7-Q"))
    			//.with(jwtDecoder().authorities(new SimpleGrantedAuthority("booking:WRITE"))))
    			.andExpect(MockMvcResultMatchers.status().is(404));
    		    //.andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(1));
    }
    
    
    @Test
    public void getByUsernameWithJWTForExistingUser() throws URISyntaxException, Exception {
    	// does not work with old provided version of spring boot
    	User user = new User();
    	user.setId(1);
    	user.setUsername("testname");
    	
    	//when(userRepository.findByUsername("testname")).thenReturn(user);    
    	doReturn(user).when(userRepository).findByUsername("testname");
    	
    	mvc.perform(MockMvcRequestBuilders.get((new URI("/api/user/testname")))
    			.header("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJZb2tvIiwiZXhwIjoxNjc4Mzk0OTY1fQ.Fg9uKgkZvBHRom64FLpH9I1gQjDHsiPBNomvhLpYMsNWj_yKcK3jKg9Y2UxnS0zsh3exdzRoMFeJu4tX2PW7-Q"))
    			//.with(jwtDecoder().authorities(new SimpleGrantedAuthority("booking:WRITE"))))
    			.andExpect(MockMvcResultMatchers.status().isOk())
    		    .andExpect(MockMvcResultMatchers.jsonPath("$.getBody().getUsername()").value("testname"));
    }
    */
    

}
