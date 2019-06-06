package testtask.shop.service;

import testtask.shop.model.Product;

import java.util.List;

/**
 * Created by Dmitry Martynov on 29.05.2019
 */

public interface ProductService {

    /**
     * Запрос продукта по Id
     * @param id    Id продукта
     * @return      Продукт
     */
    Product getById(long id);

    /**
     * Добавление продукта в базу данных
     * @param product   Продукт
     * @return          Добавленный продукт с проставленным Id и Id хранилища
     */
    Product addProduct(Product product);

    /**
     * Удаление продукта по Id
     * @param id    Id Продукта
     */
    void deleteById(long id);

    /**
     * Внесение изменений в продукт
     * @param product   Продукт
     * @return          Продукт с изменениями
     */
    Product editProduct(Product product);

    /**
     * Запрос списка всех продуктов
     * @return      Список всех продуктов
     */
    List<Product> getAll();

    List<Object[]> getBalances();

}
