package com.example.demo.security;

import static org.mockito.BDDMockito.given;

import org.junit.Test;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.UserRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserDetailsServiceTest {
	
	@MockBean
    private UserRepository userRepository;
	
	@Autowired
	private UserDetailsService userDetailsService;
	
	
	
	@Test(expected = UsernameNotFoundException.class)
	public void testThrowsExceptionWhenUserNotFound() {
		given(userRepository.findByUsername("testname")).willReturn(null);
		UserDetails details = userDetailsService.loadUserByUsername("testname");
	}
	
	@Test()
	public void testUserExists() {
		User user = new User();
		user.setUsername("testname");
		user.setPassword("secretPassword");
		given(userRepository.findByUsername("testname")).willReturn(user);
		UserDetails details = userDetailsService.loadUserByUsername("testname");
		Assert.assertEquals("testname", details.getUsername());
	}

}
