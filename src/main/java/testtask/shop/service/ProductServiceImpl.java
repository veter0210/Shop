package testtask.shop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import testtask.shop.exceptions.BadRequestException;
import testtask.shop.exceptions.NotFoundException;
import testtask.shop.model.Product;
import testtask.shop.model.Store;
import testtask.shop.repository.ProductRecordRepository;
import testtask.shop.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dmitry Martynov on 30.05.2019
 */

@Service
public class ProductServiceImpl implements ProductService {

    private static final String PRODUCT_ID_MUST_BE_POSITIVE_FOR_UPDATE = "Product.Id field must be >0 for update operation";
    private static final String PRODUCT_ID_MUST_BE_EMPTY_FOR_CREATE_OPERATION = "Product.Id field must be empty for create operation";
    private static final String PRODUCT_STORE_ID_MUST_BE_EMPTY_FOR_CREATE = "Product.store.Id field must be empty for create operation";
    private static final String STOCK_BALANCE_FIELD_MUST_BE_POSITIVE = "Product.store.stockBalance field must be >= 0";
    private static final String DELETE_ALL_PRODUCT_RECORDS_POINTING_ON_THIS_PRODUCT_FIRST = "You must delete all ProductRecords pointing on this product from your ShoppingLists first";
    private static final String PRODUCT_ID_MUST_BE_EQUAL_TO_STORE_ID_FOR_UPDATE = "Product.Id must be equal to Store.Id for update operation";
    private static final String STOCK_BALANCE_MUST_BE_POSITIVE = "Store.stockBalance field must be >=0";
    private static final String PRODUCT_TITLE_MUST_NOT_BE_EMPTY = "Product.title field must not be empty";

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
            throw new BadRequestException(PRODUCT_ID_MUST_BE_EMPTY_FOR_CREATE_OPERATION);
        }
        checkProductTitle(product);
        if (product.getStore() == null) {
            Store store = new Store(product, 0L);
            product.setStore(store);
        } else {
            if (product.getStore().getId() != 0L) {
                throw new BadRequestException(PRODUCT_STORE_ID_MUST_BE_EMPTY_FOR_CREATE);
            }
            if (product.getStore().getStockBalance() < 0) {
                throw new BadRequestException(STOCK_BALANCE_FIELD_MUST_BE_POSITIVE);
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
            throw new BadRequestException(DELETE_ALL_PRODUCT_RECORDS_POINTING_ON_THIS_PRODUCT_FIRST);
        }
        productRepository.deleteById(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Product editProduct(Product product) {
        if (product.getId() <= 0L) {
            throw new BadRequestException(PRODUCT_ID_MUST_BE_POSITIVE_FOR_UPDATE);
        }
        checkProductTitle(product);
        if (!productRepository.findById(product.getId()).isPresent()) {
            throw new NotFoundException("Product with ID = " + product.getId() + " is not exists");
        }
        if (product.getId() != product.getStore().getId()) {
            throw new BadRequestException(PRODUCT_ID_MUST_BE_EQUAL_TO_STORE_ID_FOR_UPDATE);
        }
        if (product.getStore().getStockBalance() < 0) {
            throw new BadRequestException(STOCK_BALANCE_MUST_BE_POSITIVE);
        }
        return productRepository.saveAndFlush(product);
    }

    private void checkProductTitle(Product product) {
        if (product.getTitle() == null || product.getTitle().equals("")) {
            throw new BadRequestException(PRODUCT_TITLE_MUST_NOT_BE_EMPTY);
        }
    }

    @Override
    public List<Object[]> getBalances() {
        List<Object[]> resultSet = new ArrayList<>();
        List<Product> products = productRepository.findAll();
        for (Product product : products) {
            Object[] set = new Object[3];
            set[0] = product.getId();
            set[1] = product.getTitle();
            set[2] = product.getStore().getStockBalance();
            resultSet.add(set);
        }
        return resultSet;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Product> getAll() {
        return productRepository.findAll();
    }
}
