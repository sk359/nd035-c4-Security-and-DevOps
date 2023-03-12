package com.example.demo.model.persistence;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;
import org.springframework.test.context.junit4.SpringRunner;



@RunWith(SpringRunner.class) 
@SuiteClasses({})
public class AllTests {
	
	@Test
	public void testUser() {
		User user = new User();
		user.setPassword("pw1");
		user.setUsername("testuser");
		Assert.assertEquals("pw1", user.getPassword());
		Assert.assertEquals("testuser", user.getUsername());
	}
	
	@Test
	public void testCart() {
		Cart cart = new Cart();
		cart.setTotal(new BigDecimal(5000));
		Assert.assertEquals(5000, cart.getTotal().doubleValue(), 0.001);		
	}

}
