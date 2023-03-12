package com.example.demo.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import static org.mockito.Mockito.mock;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.demo.model.persistence.User;

@RunWith(SpringRunner.class)
//@SpringBootTest
//@ContextConfiguration()
@AutoConfigureJsonTesters
public class JWTAuthenticationFilterTest {
		
	
	//@Autowired do not autowire because not annotated with Service or Component
	private JWTAuthenticationFilter authFilter;
	
	
	@Before
	public void setup() {
		AuthenticationManager am = mock(AuthenticationManager.class);
		authFilter = new JWTAuthenticationFilter(am);
	}
	
	
	@Test(expected = RuntimeException.class)
	public void testThrowsExceptionWhenUserNotFound() {
		MockHttpServletRequest request = new MockHttpServletRequest();		
		Authentication details = authFilter.attemptAuthentication(request, null);
	}
	
	@Test
	public void testUserExists() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setContentType("application/json");
		request.setCharacterEncoding("utf8");
		JSONObject json = new JSONObject();
		try {
			json.put("username", "tesuser");
			json.put("password", "password");
			request.setContent(json.toString().getBytes());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//request.setContent("{\"username\": \"testuser\"}".getBytes());
		//request.setParameter("username", "testuser");
		//request.setParameter("password", "secretPassword");
		Authentication auth = authFilter.attemptAuthentication(request, null);
		Assert.assertEquals("testuser", auth.getName());
	}

}
