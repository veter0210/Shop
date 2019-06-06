package testtask.shop;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import testtask.shop.model.Product;
import testtask.shop.model.Store;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Dmitry Martynov on 31.05.2019
 */

public class ProductControllerTest extends AbstractTest {

    public static final String URI_PRODUCTS = "/shopping/products/";

    @Override
    @Before
    public void setUp() {
        super.setUp();
    }

    @Test
    public void testGetProductStockBalance() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(URI_PRODUCTS)
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);

        String content = mvcResult.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        List<Object[]> list = objectMapper.readValue(content,new TypeReference<List<Object[]>>(){});
        assertTrue(list.size() > 0);
    }

    @Test
    public void testAddProductWithoutStore() throws Exception {
        Product product = new Product();
        product.setTitle("something");

        String inputJson = super.mapToJson(product);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(URI_PRODUCTS)
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(201, status);
        String content = mvcResult.getResponse().getContentAsString();
        Product productFromResponce = super.mapFromJson(content, Product.class);
        //Проверяем, что создался экземпляр хранилища.
        assertNotNull(productFromResponce.getStore());
        //Проверяем, что продукту присвоился Id.
        assertNotEquals(productFromResponce.getId(), 0L);
        //Проверяем, что Id продукта и хранилища совпадают.
        assertEquals(productFromResponce.getId(), productFromResponce.getStore().getId());
    }

    @Test
    public void testAddProductWithStore() throws Exception{
        Product product = new Product();
        product.setTitle("something");
        product.setStore(new Store(product, 100));

        String inputJson = super.mapToJson(product);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(URI_PRODUCTS)
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(201, status);
        String content = mvcResult.getResponse().getContentAsString();
        Product productFromResponce = super.mapFromJson(content, Product.class);
        //Проверяем, что продукту присвоился Id.
        assertNotEquals(productFromResponce.getId(), 0L);
        //Проверяем, что Id продукта и хранилища совпадают.
        assertEquals(productFromResponce.getId(), productFromResponce.getStore().getId());
    }

    @Test
    public void testAddProductWithNotEmptyId() throws Exception {
        Product product = new Product();
        product.setId(273);
        product.setTitle("something");

        String inputJson = super.mapToJson(product);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(URI_PRODUCTS)
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(400, status);
        Exception exception = mvcResult.getResolvedException();
        assertEquals(exception.getMessage(), "Product.Id field must be empty for create operation");
    }

    @Test
    public void testAddProductWithNullTitle() throws Exception {
        Product product = new Product();
        product.setTitle("");

        String inputJson = super.mapToJson(product);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(URI_PRODUCTS)
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(400, status);
        Exception exception = mvcResult.getResolvedException();
        assertEquals(exception.getMessage(), "Product.title field must not be empty");
    }

    @Test
    public void testAddProductWithNotEmptyStoreId() throws Exception {
        Product product = new Product();
        product.setTitle("something");
        product.setStore(new Store(product, 100));
        product.getStore().setId(28);

        String inputJson = super.mapToJson(product);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(URI_PRODUCTS)
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(400, status);
        Exception exception = mvcResult.getResolvedException();
        assertEquals(exception.getMessage(), "Product.store.Id field must be empty for create operation");
    }

    @Test
    public void testAddProductWithStockBalanceLessThanZero() throws Exception {
        Product product = new Product();
        product.setTitle("something");
        product.setStore(new Store(product, -573));

        String inputJson = super.mapToJson(product);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(URI_PRODUCTS)
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(400, status);
        Exception exception = mvcResult.getResolvedException();
        assertEquals(exception.getMessage(), "Product.store.stockBalance field must be >= 0");
    }

    @Test
    public void testDeleteProduct() throws Exception {
        Product product = new Product();
        product.setTitle("something");
        String inputJson = super.mapToJson(product);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(URI_PRODUCTS)
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();
        String content = mvcResult.getResponse().getContentAsString();
        Product productFromResponce = super.mapFromJson(content, Product.class);

        mvcResult = mockMvc.perform(MockMvcRequestBuilders
                .delete(URI_PRODUCTS + productFromResponce.getId())).andReturn();
        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        content = mvcResult.getResponse().getContentAsString();
        assertEquals(content, "Product is deleted successfully");
    }

    @Test
    public void testDeleteProductAssociatedWithProductRecords() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.delete(URI_PRODUCTS + 2)).andReturn();
        int status = mvcResult.getResponse().getStatus();
        assertEquals(400, status);
        Exception exception = mvcResult.getResolvedException();
        assertEquals(exception.getMessage(), "You must delete all ProductRecords pointing on this product from your ShoppingLists first");
    }

    @Test
    public void testDeleteNonExistingProduct() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.delete(URI_PRODUCTS + 792)).andReturn();
        int status = mvcResult.getResponse().getStatus();
        assertEquals(404, status);
        Exception exception = mvcResult.getResolvedException();
        assertEquals(exception.getMessage(), "Product with ID = 792 is not exists");
    }
}
