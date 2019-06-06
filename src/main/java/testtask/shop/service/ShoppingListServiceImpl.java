package testtask.shop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import testtask.shop.exceptions.BadRequestException;
import testtask.shop.exceptions.InternalErrorException;
import testtask.shop.exceptions.NotFoundException;
import testtask.shop.model.ProductRecord;
import testtask.shop.model.ShoppingList;
import testtask.shop.model.Store;
import testtask.shop.repository.ProductRepository;
import testtask.shop.repository.ShoppingListRepository;
import testtask.shop.repository.StoreRepository;

import java.util.Date;
import java.util.List;


/**
 * Created by Dmitry Martynov on 30.05.2019
 */

@Service
public class ShoppingListServiceImpl implements ShoppingListService {

    private static final String SHOPPING_LIST_ID_MUST_BE_EMPTY_FOR_CREATE = "ShoppingList.Id field must be empty for create operation";
    private static final String PRODUCT_RECORD_ID_MUST_BE_EMPTY_FOR_CREATE = "ShoppingList.ProductRecord.Id field must be empty for create operation";
    private static final String SHOPPING_LIST_IS_PERIODIC_ALREADY = "This ShoppingList is periodic already";
    private static final String SHOPPING_LIST_PERIOD_MUST_BE_POSITIVE_FOR_PERIODIC_BUY = "ShoppingList.period field must be >0 for periodic buy operation";
    private static final String SHOPPING_LIST_IS_NOT_PERIODIC_ALREADY = "This ShoppingList is not periodic already";
    private static final String PRODUCT_ID_MUST_BE_POSITIVE = "Product.Id field must be >0";
    private static final String PRODUCT_RECORD_COUNT_MUST_BE_POSITIVE = "ProductRecord.count field must be >0";

    @Autowired
    private ShoppingListRepository shoppingListRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StoreRepository storeRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public ShoppingList getById(long id) {
        if (!shoppingListRepository.findById(id).isPresent()) {
            throw new NotFoundException("ShoppingList with Id = " + id + " is not exists");
        }
        return shoppingListRepository.findById(id).get();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ShoppingList addShoppingList(ShoppingList shoppingList) {
        if (shoppingList.getId() != 0L) {
            throw new BadRequestException(SHOPPING_LIST_ID_MUST_BE_EMPTY_FOR_CREATE);
        }
        for (ProductRecord productRecord : shoppingList.getList()) {
            if (productRecord.getId() != 0L) {
                throw new BadRequestException(PRODUCT_RECORD_ID_MUST_BE_EMPTY_FOR_CREATE);
            }
        }
        checkShoppingListData(shoppingList);
        return shoppingListRepository.saveAndFlush(shoppingList);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteById(long id) {
        if (!shoppingListRepository.findById(id).isPresent()) {
            throw new NotFoundException("ShoppingList with ID = " + id + " is not exists");
        }
        shoppingListRepository.deleteById(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ShoppingList editShoppingList(ShoppingList shoppingList) {
        if (!shoppingListRepository.findById(shoppingList.getId()).isPresent()) {
            throw new NotFoundException("ShoppingList with ID = " + shoppingList.getId() + " is not exists");
        }
        checkShoppingListData(shoppingList);
        return shoppingListRepository.saveAndFlush(shoppingList);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void buyShoppingListOnce(long id) {
        ShoppingList shoppingList = getById(id);
        for (ProductRecord productRecord : shoppingList.getList()) {
            if (productRecord.getProduct().getStore().getStockBalance() < productRecord.getCount()) {
                String message = String.format("ShoppingList was not purchased: not enough product with id = %d in stock",
                        productRecord.getProduct().getId());
                shoppingList.setResultOfLastTryToBuy(message);
                throw new InternalErrorException(message);
            }
        }
        for (ProductRecord productRecord : shoppingList.getList()) {
            Store store = productRecord.getProduct().getStore();
            store.sell(productRecord.getCount());
            storeRepository.saveAndFlush(store);
        }
        shoppingList.setResultOfLastTryToBuy("OK");
        editShoppingList(shoppingList);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startPeriodicBuying(long id) {
        ShoppingList shoppingList = getById(id);
        if (shoppingList.isPeriodic()) {
            throw new BadRequestException(SHOPPING_LIST_IS_PERIODIC_ALREADY);
        }
        if (shoppingList.getPeriod() <= 0L) {
            throw new BadRequestException(SHOPPING_LIST_PERIOD_MUST_BE_POSITIVE_FOR_PERIODIC_BUY);
        }
        shoppingList.setPeriodic(true);
        Date nextTimeToBuy = new Date();
        nextTimeToBuy.setTime(new Date().getTime() + shoppingList.getPeriod());
        shoppingList.setNextTimeToBuy(nextTimeToBuy);
        editShoppingList(shoppingList);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stopPeriodicBuying(long id) {
        ShoppingList shoppingList = getById(id);
        if (!shoppingList.isPeriodic()) {
            throw new BadRequestException(SHOPPING_LIST_IS_NOT_PERIODIC_ALREADY);
        }
        shoppingList.setPeriodic(false);
        shoppingList.setNextTimeToBuy(null);
        editShoppingList(shoppingList);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ShoppingList> getAll() {
        return shoppingListRepository.findAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ShoppingList> getAllByIsPeriodicTrue() {
        return shoppingListRepository.getAllByIsPeriodicTrue();
    }

    /**
     * Проверка данных списка покупок на корректность
     * @param shoppingList  Список покупок
     */
    private void checkShoppingListData(ShoppingList shoppingList) {
        for (ProductRecord productRecord : shoppingList.getList()) {
            Long productId = productRecord.getProduct().getId();
            if (productId <= 0L) {
                throw new BadRequestException(PRODUCT_ID_MUST_BE_POSITIVE);
            }
            if (!productRepository.findById(productId).isPresent()) {
                throw new NotFoundException("Product with ID = " + productId + " is not exists");
            }
            if (productRecord.getCount() <= 0L) {
                throw new BadRequestException(PRODUCT_RECORD_COUNT_MUST_BE_POSITIVE);
            }
            productRecord.setProduct(productRepository.findById(productId).get());
        }
    }
}
