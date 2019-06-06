package testtask.shop;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import testtask.shop.model.Product;
import testtask.shop.model.ProductRecord;
import testtask.shop.model.ShoppingList;
import testtask.shop.model.Store;
import testtask.shop.service.ProductService;
import testtask.shop.service.ShoppingListService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dmitry Martynov on 31.05.2019
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ShoppingApplication.class)
@WebAppConfiguration
public abstract class AbstractTest {

    protected MockMvc mockMvc;

    @Autowired
    WebApplicationContext webApplicationContext;

    @Autowired
    private ProductService productService;

    @Autowired
    private ShoppingListService shoppingListService;

    protected void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        //создание продуктов
        List<Product> products = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Product pr = new Product();
            pr.setTitle("product #"+(i+1));
            Store store = new Store(pr, Math.round(Math.random()*20)+20);
            pr.setStore(store);
            products.add(productService.addProduct(pr));
        }

        int counter = 0;
        for (int i = 0; i < 5; i++) {
            ShoppingList sl = new ShoppingList();
            for (int j = 0; j < 2; j++) {
                ProductRecord productRecord = new ProductRecord();
                productRecord.setProduct(products.get(counter));
                counter++;
                productRecord.setCount(Math.round(Math.random()*10)+1);
                productRecord.setShoppingList(sl);
                sl.addProductRecord(productRecord);
            }
            shoppingListService.addShoppingList(sl);
        }
    }

    protected String mapToJson(Object obj) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(obj);
    }

    protected <T> T mapFromJson(String json, Class<T> clazz)
            throws JsonParseException, JsonMappingException, IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, clazz);
    }
}
