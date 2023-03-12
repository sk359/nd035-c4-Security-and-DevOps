package com.example.demo.controllers;

import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;

import org.junit.Assert;


@RunWith(SpringRunner.class)
//@SpringBootTest
public class ItemControllerTest {
	
	@Mock
	private ItemRepository itemRepository;
	
	@InjectMocks
	private ItemController itemController;
	
	@Before
    public void setUp() {
        MockitoAnnotations.initMocks(this); // this is needed for inititalizytion of mocks, if you use @Mock 
        //itemController = new ItemController(itemRepository); // only when not Autowired       
    }
	
	private Item createItem(String name, int price, String description) {
		Item item = new Item();
		item.setName(name);
		item.setPrice(new BigDecimal(price));
		item.setDescription(description);
		return item;
	}
	
	@Test
	public void testEmptyList() {
		when(itemRepository.findAll()).thenReturn(new ArrayList<Item>());
		
		ResponseEntity<List<Item>> itemList = itemController.getItems();
		
		Assert.assertEquals(0, itemList.getBody().size());		
	}
	
	@Test
	public void testNonEmptyList() {
		Item item = createItem("TestItem", 200, "Item description");		
		List<Item> responseList = new ArrayList<Item>();
		responseList.add(item);
		when(itemRepository.findAll()).thenReturn(responseList);
		
		ResponseEntity<List<Item>> itemList = itemController.getItems();
		
		Assert.assertEquals(1, itemList.getBody().size());
		Assert.assertEquals("TestItem", itemList.getBody().get(0).getName());
		Assert.assertEquals("Item description", itemList.getBody().get(0).getDescription());
	}
	
	@Test
	public void testFindNoneById() {
		Optional<Item> emptyOptional = Optional.ofNullable(null);
		when(itemRepository.findById(2L)).thenReturn(emptyOptional);
		
		ResponseEntity<Item> item = itemController.getItemById(2L);
		
		Assert.assertEquals(false, item.hasBody());		
	}
	
	@Test
	public void testFindById() {
		Item item = createItem("TestItem", 200, "Item description");
		Optional<Item> emptyOptional = Optional.ofNullable(item);
		when(itemRepository.findById(12L)).thenReturn(emptyOptional);
		
		ResponseEntity<Item> itemResponse = itemController.getItemById(12L);
		
		Assert.assertEquals(true, itemResponse.hasBody());	
		Assert.assertEquals("TestItem", itemResponse.getBody().getName());	
	}
	
	@Test
	public void testFindNoneByName() {		
		when(itemRepository.findByName("Car")).thenReturn(new ArrayList<Item>());
		
		ResponseEntity<List<Item>> items = itemController.getItemsByName("Car");
		
		Assert.assertEquals(404, items.getStatusCodeValue());		
	}
	
	@Test
	public void testFindOneByName() {		
		Item item = createItem("TestItem", 200, "Item description");
		List<Item> responseList = new ArrayList<Item>();
		responseList.add(item);
		when(itemRepository.findByName("Car")).thenReturn(responseList);
		
		ResponseEntity<List<Item>> items = itemController.getItemsByName("Car");
		
		Assert.assertEquals(200, items.getStatusCodeValue());
		Assert.assertEquals(1, items.getBody().size());
	}
	
	
	

}
