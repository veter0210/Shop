package testtask.shop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import testtask.shop.model.Store;
import testtask.shop.repository.StoreRepository;

import java.util.List;

/**
 * Created by Dmitry Martynov on 30.05.2019
 */

@Service
public class StoreServiceImpl implements StoreService {

    @Autowired
    private StoreRepository storeRepository;

    @Override
    public Store getById(long id) {
        return storeRepository.findById(id).get();
    }

    @Override
    public Store addStore(Store store) {
        return storeRepository.saveAndFlush(store);
    }

    @Override
    public void deleteById(long id) {
        storeRepository.deleteById(id);
    }

    @Override
    public Store editStore(Store store) {
        return storeRepository.saveAndFlush(store);
    }

    @Override
    public List<Store> getAll() {
        return storeRepository.findAll();
    }
}
