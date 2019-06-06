package testtask.shop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import testtask.shop.model.ProductRecord;
import testtask.shop.repository.ProductRecordRepository;

import java.util.List;

/**
 * Created by Dmitry Martynov on 30.05.2019
 */

@Service
public class ProductRecordServiceImpl implements ProductRecordService {

    @Autowired
    private ProductRecordRepository productRecordRepository;

    @Override
    public ProductRecord getById(long id) {
        return productRecordRepository.findById(id).get();
    }

    @Override
    public ProductRecord addProductRecord(ProductRecord productRecord) {
        return productRecordRepository.saveAndFlush(productRecord);
    }

    @Override
    public void deleteById(long id) {
        productRecordRepository.deleteById(id);
    }

    @Override
    public ProductRecord editProductRecord(ProductRecord productRecord) {
        return productRecordRepository.saveAndFlush(productRecord);
    }

    @Override
    public List<ProductRecord> getAll() {
        return productRecordRepository.findAll();
    }
}
