package testtask.shop.service;

import testtask.shop.model.ProductRecord;

import java.util.List;

/**
 * Created by Dmitry Martynov on 29.05.2019
 * Методы не описаны, так как просты на данный момент и имеют реализацию в
 * виде перенаправления запроса в слой DAO в имплементации
 */

public interface ProductRecordService {

    ProductRecord getById(long id);

    ProductRecord addProductRecord(ProductRecord productRecord);

    void deleteById(long id);

    ProductRecord editProductRecord(ProductRecord productRecord);

    List<ProductRecord> getAll();

}
