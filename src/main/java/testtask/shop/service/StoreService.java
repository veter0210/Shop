package testtask.shop.service;

import testtask.shop.model.Store;

import java.util.List;

/**
 * Created by Dmitry Martynov on 29.05.2019
 * Методы не описаны, так как просты на данный момент и имеют реализацию в
 * виде перенаправления запроса в слой DAO в имплементации
 */

public interface StoreService {

    Store getById(long id);

    Store addStore(Store store);

    void deleteById(long id);

    Store editStore(Store store);

    List<Store> getAll();
}
