package testtask.shop;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import testtask.shop.model.Product;
import testtask.shop.model.ProductRecord;
import testtask.shop.model.ShoppingList;
import testtask.shop.service.ShoppingListService;

import static org.junit.Assert.*;

/**
 * Created by Dmitry Martynov on 31.05.2019
 */

public class ShoppingListControllerTest extends AbstractTest {

    public static final String URI = "/shopping/lists";
    @Autowired
    private ShoppingListService shoppingListService;

    @Override
    @Before
    public void setUp() {
        super.setUp();
    }

    @Test
    public void testGetAllShoppingLists() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(URI)
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        String content = mvcResult.getResponse().getContentAsString();
        ShoppingList[] shoppingLists = super.mapFromJson(content, ShoppingList[].class);
        assertTrue(shoppingLists.length > 0);
    }

    @Test
    public void testGetShoppingListById() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(URI+"/4")
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        String content = mvcResult.getResponse().getContentAsString();
        ShoppingList shoppingListFromResponse = super.mapFromJson(content, ShoppingList.class);
        assertEquals(4, shoppingListFromResponse.getId());

    }

    @Test
    public void testGetNotExistingShoppingListById() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(URI + "/491")
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(404, status);
        Exception exception = mvcResult.getResolvedException();
        assertEquals(exception.getMessage(), "ShoppingList with Id = 491 is not exists");
    }

    @Test
    public void testAddShoppingList() throws Exception {
        ShoppingList shoppingList = new ShoppingList();
        shoppingList.setPeriod(100);
        Product product = new Product();
        product.setId(2);
        shoppingList.addProductRecord(new ProductRecord(10,product,shoppingList));

        String inputJson = super.mapToJson(shoppingList);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(URI)
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(201, status);
        String content = mvcResult.getResponse().getContentAsString();
        ShoppingList shoppingListFromResponse = super.mapFromJson(content, ShoppingList.class);
        //Проверяем, что связь список-запись-продукт-хранилище работает.
        assertNotNull(shoppingListFromResponse.getList().get(0).getProduct().getStore());
    }

    @Test
    public void testAddShoppingListWithoutProductRecord() throws Exception {
        ShoppingList shoppingList = new ShoppingList();
        shoppingList.setPeriodic(false);
        shoppingList.setPeriod(0);

        String inputJson = super.mapToJson(shoppingList);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(URI)
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(201, status);
        String content = mvcResult.getResponse().getContentAsString();
        ShoppingList shoppingListFromResponse = super.mapFromJson(content, ShoppingList.class);
        assertEquals(0, shoppingListFromResponse.getList().size());
    }

    @Test
    public void testAddShoppingListWithNotEmptyId() throws Exception {
        ShoppingList shoppingList = new ShoppingList();
        shoppingList.setId(178);
        shoppingList.setPeriodic(false);
        shoppingList.setPeriod(100);
        Product product = new Product();
        product.setId(2);
        shoppingList.addProductRecord(new ProductRecord(10,product,shoppingList));

        String inputJson = super.mapToJson(shoppingList);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(URI)
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(400, status);
        Exception exception = mvcResult.getResolvedException();
        assertEquals(exception.getMessage(), "ShoppingList.Id field must be empty for create operation");
    }

    @Test
    public void testAddShoppingListWithNotEmptyProductRecordId() throws Exception {
        ShoppingList shoppingList = new ShoppingList();
        shoppingList.setPeriodic(false);
        shoppingList.setPeriod(100);
        Product product = new Product();
        product.setId(2);
        ProductRecord productRecord = new ProductRecord(10,product,shoppingList);
        productRecord.setId(23);
        shoppingList.addProductRecord(productRecord);

        String inputJson = super.mapToJson(shoppingList);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(URI)
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(400, status);
        Exception exception = mvcResult.getResolvedException();
        assertEquals(exception.getMessage(), "ShoppingList.ProductRecord.Id field must be empty for create operation");
    }

    @Test
    public void testAddShoppingListWithZeroInProductId() throws Exception {
        ShoppingList shoppingList = new ShoppingList();
        shoppingList.setPeriodic(false);
        shoppingList.setPeriod(0);
        Product product = new Product();
        product.setId(0);
        shoppingList.addProductRecord(new ProductRecord(10,product,shoppingList));

        String inputJson = super.mapToJson(shoppingList);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(URI)
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(400, status);
        Exception exception = mvcResult.getResolvedException();
        assertEquals(exception.getMessage(), "Product.Id field must be >0");
    }

    @Test
    public void testAddShoppingListWithNotExistingProduct() throws Exception {
        ShoppingList shoppingList = new ShoppingList();
        shoppingList.setPeriodic(false);
        shoppingList.setPeriod(0);
        Product product = new Product();
        product.setId(561);
        shoppingList.addProductRecord(new ProductRecord(10,product,shoppingList));

        String inputJson = super.mapToJson(shoppingList);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(URI)
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(404, status);
        Exception exception = mvcResult.getResolvedException();
        assertEquals(exception.getMessage(), "Product with ID = 561 is not exists");
    }

    @Test
    public void testAddShoppingListWithZeroCountInProductRecord() throws Exception {
        ShoppingList shoppingList = new ShoppingList();
        shoppingList.setPeriodic(false);
        shoppingList.setPeriod(0);
        Product product = new Product();
        product.setId(2);
        shoppingList.addProductRecord(new ProductRecord(0,product,shoppingList));

        String inputJson = super.mapToJson(shoppingList);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(URI)
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(400, status);
        Exception exception = mvcResult.getResolvedException();
        assertEquals(exception.getMessage(), "ProductRecord.count field must be >0");
    }

    @Test
    public void testEditShoppingList() throws Exception {
        ShoppingList shoppingList = new ShoppingList();
        shoppingList.setId(2);
        shoppingList.setPeriodic(false);
        shoppingList.setPeriod(0);
        Product product = new Product();
        product.setId(2);
        shoppingList.addProductRecord(new ProductRecord(10,product,shoppingList));

        String inputJson = super.mapToJson(shoppingList);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put(URI + "/2")
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        String content = mvcResult.getResponse().getContentAsString();
        ShoppingList shoppingListFromResponse = super.mapFromJson(content, ShoppingList.class);
        //Проверяем, что связь список-запись-продукт-хранилище работает.
        assertNotNull(shoppingListFromResponse.getList().get(0).getProduct().getStore());
    }

    @Test
    public void testEditShoppingListWithIdNotMatchRequestPath() throws Exception {
        ShoppingList shoppingList = new ShoppingList();
        shoppingList.setId(4);
        shoppingList.setPeriodic(false);
        shoppingList.setPeriod(0);
        Product product = new Product();
        product.setId(2);
        shoppingList.addProductRecord(new ProductRecord(10,product,shoppingList));

        String inputJson = super.mapToJson(shoppingList);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put(URI + "/2")
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(400, status);
        Exception exception = mvcResult.getResolvedException();
        assertEquals(exception.getMessage(), "ShoppingList.Id does not match the request path");
    }

    @Test
    public void testEditNotExistingShoppingList() throws Exception {
        ShoppingList shoppingList = new ShoppingList();
        shoppingList.setId(264);
        shoppingList.setPeriodic(false);
        shoppingList.setPeriod(0);
        Product product = new Product();
        product.setId(2);
        shoppingList.addProductRecord(new ProductRecord(10,product,shoppingList));

        String inputJson = super.mapToJson(shoppingList);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put(URI + "/264")
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(404, status);
        Exception exception = mvcResult.getResolvedException();
        assertEquals(exception.getMessage(), "ShoppingList with ID = 264 is not exists");
    }

    @Test
    public void testDeleteShoppingList() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.delete(URI + "/3")).andReturn();
        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        String content = mvcResult.getResponse().getContentAsString();
        assertEquals(content, "ShoppingList is deleted successfully");
    }

    @Test
    public void testDeleteNotExistingShoppingList() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.delete(URI + "/671")).andReturn();
        int status = mvcResult.getResponse().getStatus();
        assertEquals(404, status);
        Exception exception = mvcResult.getResolvedException();
        assertEquals(exception.getMessage(), "ShoppingList with ID = 671 is not exists");

    }

    @Test
    public void testBuyShoppingListOnceSuccess() throws Exception {
        ShoppingList shoppingList = new ShoppingList();
        Product product = new Product();
        product.setId(2);
        shoppingList.addProductRecord(new ProductRecord(10,product,shoppingList));
        shoppingList = shoppingListService.addShoppingList(shoppingList);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                .get(URI + "/buy/" + shoppingList.getId())).andReturn();
        int status = mvcResult.getResponse().getStatus();
        assertEquals(200,status);
        shoppingList = shoppingListService.getById(shoppingList.getId());
        assertEquals(shoppingList.getResultOfLastTryToBuy(), "OK");
    }

    @Test
    public void testBuyShoppingListOnceNotSuccess() throws Exception {
        ShoppingList shoppingList = new ShoppingList();
        Product product = new Product();
        product.setId(2);
        shoppingList.addProductRecord(new ProductRecord(20000,product,shoppingList));
        shoppingList = shoppingListService.addShoppingList(shoppingList);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                .get(URI + "/buy/" + shoppingList.getId())).andReturn();
        int status = mvcResult.getResponse().getStatus();
        assertEquals(500,status);
        Exception exception = mvcResult.getResolvedException();
        assertEquals(exception.getMessage(), "ShoppingList was not purchased: not enough product with id = 2 in stock");
    }

    @Test
    public void testPeriodicBuyShoppingListSuccess() throws Exception {
        ShoppingList shoppingList = new ShoppingList();
        shoppingList.setPeriod(6000);
        Product product = new Product();
        product.setId(2);
        shoppingList.addProductRecord(new ProductRecord(10,product,shoppingList));
        shoppingList = shoppingListService.addShoppingList(shoppingList);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                .get(URI + "/buy/periodic/" + shoppingList.getId())).andReturn();
        int status = mvcResult.getResponse().getStatus();
        assertEquals(200,status);
        Thread.sleep(5000);
        shoppingList = shoppingListService.getById(shoppingList.getId());
        assertEquals(shoppingList.getResultOfLastTryToBuy(), "OK");
    }

    @Test
    public void testPeriodicBuyShoppingListNotSuccess() throws Exception {
        ShoppingList shoppingList = new ShoppingList();
        shoppingList.setPeriod(6000);
        Product product = new Product();
        product.setId(2);
        shoppingList.addProductRecord(new ProductRecord(20000,product,shoppingList));
        shoppingList = shoppingListService.addShoppingList(shoppingList);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                .get(URI + "/buy/periodic/" + shoppingList.getId())).andReturn();
        int status = mvcResult.getResponse().getStatus();
        assertEquals(200,status);
        Thread.sleep(5000);
        shoppingList = shoppingListService.getById(shoppingList.getId());
        assertEquals(shoppingList.getResultOfLastTryToBuy(), "ShoppingList was not purchased: not enough product with id = 2 in stock");
    }

}
