package testtask.shop.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import testtask.shop.model.Product;
import testtask.shop.model.Store;
import testtask.shop.service.ProductService;
import testtask.shop.service.StoreService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dmitry Martynov on 30.05.2019
 */

@RestController
@RequestMapping("/shopping/products")
@Api(value = "Управление продуктами", description = "Операции для управления списком продуктов в наличии")
public class ProductController  {

    @Autowired
    private ProductService productService;

    @GetMapping
    @ResponseStatus(code = HttpStatus.OK)
    @ApiOperation(value = "Запрос остатков всех продуктов", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Object[]> getProductStockBalance() {
        return productService.getBalances();
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    @ApiOperation(value = "Добавление продукта в базу данных", notes = "Поле id должно быть пустым, поле title обязательно, " +
            "может быть добавлено хранилище (Store) с пустым id и начальным количеством продукта. " +
            "Возвращает добавленный продукт.",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Product addProduct(@ApiParam (value = "Объект Product", required = true) @RequestBody Product product) {
        return productService.addProduct(product);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    @ApiOperation(value = "Удаление продукта из БД", notes = "Удаляет продукт с id = {id} из базы данных, " +
            "если он существует и не связан ни с одной записью в списках покупок")
    public String deleteProductById(@ApiParam(value = "Id продукта", required = true) @PathVariable long id) {
        productService.deleteById(id);
        return "Product is deleted successfully";
    }
}
