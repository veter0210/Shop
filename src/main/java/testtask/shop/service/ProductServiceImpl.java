package testtask.shop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import testtask.shop.exceptions.BadRequestException;
import testtask.shop.exceptions.NotFoundException;
import testtask.shop.model.Product;
import testtask.shop.model.Store;
import testtask.shop.repository.ProductRecordRepository;
import testtask.shop.repository.ProductRepository;

import java.util.List;

/**
 * Created by Dmitry Martynov on 30.05.2019
 */

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductRecordRepository productRecordRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public Product getById(long id) {
        return productRepository.findById(id).get();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Product addProduct(Product product) {
        if (product.getId() != 0L) {
            throw new BadRequestException("Product.Id field must be empty for create operation");
        }
        checkProductTitle(product);
        if (product.getStore() == null) {
            Store store = new Store(product, 0L);
            product.setStore(store);
        } else {
            if (product.getStore().getId() != 0L) {
                throw new BadRequestException("Product.store.Id field must be empty for create operation");
            }
            if (product.getStore().getStockBalance() < 0) {
                throw new BadRequestException("Product.store.stockBalance field must be >= 0");
            }
            product.getStore().setProduct(product);
        }
        return productRepository.saveAndFlush(product);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteById(long id) {
        if (!productRepository.findById(id).isPresent()) {
            throw new NotFoundException("Product with ID = " + id + " is not exists");
        }
        if (productRecordRepository.existsByProduct(productRepository.findById(id).get())) {
            throw new BadRequestException("You must delete all ProductRecords pointing on this product from your ShoppingLists first");
        }
        productRepository.deleteById(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Product editProduct(Product product) {
        if (product.getId() <= 0L) {
            throw new BadRequestException("Product.Id field must be >0 for update operation");
        }
        checkProductTitle(product);
        if (!productRepository.findById(product.getId()).isPresent()) {
            throw new NotFoundException("Product with ID = " + product.getId() + " is not exists");
        }
        if (product.getId() != product.getStore().getId()) {
            throw new BadRequestException("Product.Id must be equal to Store.Id for update operation");
        }
        if (product.getStore().getStockBalance() < 0) {
            throw new BadRequestException("Store.stockBalance field must be >=0");
        }
        return productRepository.saveAndFlush(product);
    }

    private void checkProductTitle(Product product) {
        if (product.getTitle() == null || product.getTitle().equals("")) {
            throw new BadRequestException("Product.title field must not be empty");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Product> getAll() {
        return productRepository.findAll();
    }
}
