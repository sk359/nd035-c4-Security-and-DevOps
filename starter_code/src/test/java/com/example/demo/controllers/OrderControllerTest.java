package com.example.demo.controllers;

import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;

@RunWith(SpringRunner.class)
public class OrderControllerTest {
	
	@Mock
	private UserRepository userRepository;
	
	@Mock
	private OrderRepository orderRepository;
			
	@InjectMocks
	private OrderController orderController;
	
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
	public void testSubmitOrderForUnknownUser() {
		final String username = "Morty";
		when(userRepository.findByUsername(username)).thenReturn(null);
		
		ResponseEntity<UserOrder> userOrderResponse = orderController.submit(username);
		
		Assert.assertEquals(HttpStatus.NOT_FOUND.value(), userOrderResponse.getStatusCodeValue());		
	}
	
	@Test
	public void testSubmitOrderForKnownUser() {
		final String username = "Morty";
		User user = createUser(username, "Jessica123");
		Cart cart = new Cart();
		cart.setItems(new ArrayList<Item>());
		final int cartTotal = 112;
		cart.setTotal(new BigDecimal(cartTotal));
		user.setCart(cart);
		cart.setUser(user);
		when(userRepository.findByUsername(username)).thenReturn(user);
		
		ResponseEntity<UserOrder> userOrderResponse = orderController.submit(username);
		
		Assert.assertEquals(HttpStatus.OK.value(), userOrderResponse.getStatusCodeValue());
		Assert.assertEquals(username, userOrderResponse.getBody().getUser().getUsername());		
	}
	
	@Test
	public void testGetOrdersForUnknownUser() {
		final String username = "Morty";
		when(userRepository.findByUsername(username)).thenReturn(null);
		
		ResponseEntity<List<UserOrder>> userOrderResponse = orderController.getOrdersForUser(username);
		
		Assert.assertEquals(HttpStatus.NOT_FOUND.value(), userOrderResponse.getStatusCodeValue());		
	}
	
	@Test
	public void testGetOrdersForKnownUser() {
		final String username = "Morty";
		User user = createUser(username, "Jessica123");
		Cart cart = new Cart();
		List<Item> items = new ArrayList<Item>();
		Item item = new Item();
		item.setPrice(new BigDecimal(300));
		item.setDescription("Item Description");
		item.setId(11L);
		item.setName("ItemName");
		items.add(item);
		cart.setItems(items);
		final int cartTotal = 112;
		cart.setTotal(new BigDecimal(cartTotal));
		user.setCart(cart);
		cart.setUser(user);
		List<UserOrder> orders = new ArrayList<UserOrder>();
		UserOrder newOrder = new UserOrder();
		newOrder.setUser(user);
		newOrder.setTotal(new BigDecimal(300));
		newOrder.setItems(items);
		orders.add(newOrder);
		
		when(userRepository.findByUsername(username)).thenReturn(user);
		when(orderRepository.findByUser(user)).thenReturn(orders);
		
		ResponseEntity<List<UserOrder>> userOrderResponse = orderController.getOrdersForUser(username);
		
		Assert.assertEquals(HttpStatus.OK.value(), userOrderResponse.getStatusCodeValue());		
		Assert.assertEquals(1, userOrderResponse.getBody().size());
	}

}
