package com.example.demo.controllers;

import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

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
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;

@RunWith(SpringRunner.class)
public class CartControllerTest {
	
	@Mock
	private UserRepository userRepository;
	
	@Mock
	private CartRepository cartRepository;
	
	@Mock
	private ItemRepository itemRepository;
	
	@InjectMocks
	private CartController cartController;
	
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
	public void addToCartFailureUnknownUser() {
		final String username = "Smith";
		final Long itemId = 10L;
		final int itemQuantity = 2;
		ModifyCartRequest cartRequest = new ModifyCartRequest();
		cartRequest.setItemId(itemId);
		cartRequest.setQuantity(itemQuantity);
		cartRequest.setUsername(username);		
		when(userRepository.findByUsername(username)).thenReturn(null);
		
		ResponseEntity<Cart> response = cartController.addTocart(cartRequest);
		
		Assert.assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCodeValue());
	}
	
	@Test
	public void addToCartSuccess() {
		final String username = "Smith";
		final Long itemId = 10L;
		final int itemQuantity = 2;
		ModifyCartRequest cartRequest = new ModifyCartRequest();
		cartRequest.setItemId(itemId);
		cartRequest.setQuantity(itemQuantity);
		cartRequest.setUsername(username);		
		User user = createUser(username, "Sanchez137");
		when(userRepository.findByUsername(username)).thenReturn(user);
		Item item = new Item();
		item.setId(itemId);
		item.setPrice(BigDecimal.valueOf(9.99));
		Cart cart = new Cart();
		cart.setItems(new ArrayList<Item>());
		final int cartTotal = 0;
		cart.setTotal(new BigDecimal(cartTotal));
		user.setCart(cart);
		cart.setUser(user);
		
		Optional<Item> itemOpt = Optional.of(item);
		when(itemRepository.findById(itemId)).thenReturn(itemOpt);
		
		ResponseEntity<Cart> response = cartController.addTocart(cartRequest);
		
		Assert.assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
		Assert.assertEquals(itemQuantity, response.getBody().getItems().size());
		Assert.assertEquals(BigDecimal.valueOf(19.98), response.getBody().getTotal());
	}
	
	@Test
	public void removeNonexistingItem() {
		final String username = "Smith";
		final Long itemId = 10L;
		final int itemQuantity = 2;
		ModifyCartRequest cartRequest = new ModifyCartRequest();
		cartRequest.setItemId(itemId);
		cartRequest.setQuantity(itemQuantity);
		cartRequest.setUsername(username);		
		User user = createUser(username, "Sanchez137");
		when(userRepository.findByUsername(username)).thenReturn(user);
		Item item = new Item();
		item.setId(itemId);
		item.setPrice(BigDecimal.valueOf(9.99));
		Cart cart = new Cart();
		cart.setItems(new ArrayList<Item>());
		final int cartTotal = 0;
		cart.setTotal(new BigDecimal(cartTotal));
		user.setCart(cart);
		cart.setUser(user);
		
		Optional<Item> itemOpt = Optional.empty();
		when(itemRepository.findById(itemId)).thenReturn(itemOpt);
		
		ResponseEntity<Cart> response = cartController.removeFromcart(cartRequest);
		
		Assert.assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCodeValue());
		
	}
	

}
