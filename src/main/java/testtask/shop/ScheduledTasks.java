package testtask.shop;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import testtask.shop.model.ProductRecord;
import testtask.shop.model.ShoppingList;
import testtask.shop.model.Store;
import testtask.shop.service.ShoppingListService;
import testtask.shop.service.StoreService;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Данный компонент выполняет периодичские задачи:
 * 1) выкуп по периодическим спискам покупок
 * 2) добавление продуктов на склад
 * Created by Dmitry Martynov on 03.06.2019
 */
@Component
@EnableScheduling
public class ScheduledTasks {

    private static final ExecutorService executor = Executors.newFixedThreadPool(10);

    @Autowired
    private ShoppingListService shoppingListService;

    @Autowired
    private StoreService storeService;

    // Раз в 5 секунд проверяет имеющиеся периодические списки покупок на предмет того, что подошло время очередной закупки
    // Совершает выкуп по списку, если всех продуктов достаточно на складе.
    // Результат попытки выкупа сохраняется в поле resultOfLastTryToBuy объекта ShoppingList
    @Scheduled(fixedRate = 5000)
    public void getAllPeriodicShoppingListsAndBuyIfNeed() {
        Date now = new Date();
        List<ShoppingList> periodicLists = shoppingListService.getAllByIsPeriodicTrue();
        for (ShoppingList shoppingList : periodicLists) {
            if (Math.abs(now.getTime() - shoppingList.getNextTimeToBuy().getTime()) < 5000) {
                executor.submit(() -> {
                    boolean canBuy = true;
                    for (ProductRecord productRecord : shoppingList.getList()) {
                        if (productRecord.getProduct().getStore().getStockBalance() < productRecord.getCount()) {
                            canBuy = false;
                            shoppingList.setResultOfLastTryToBuy(String.format("ShoppingList was not purchased: not enough product with id = %d in stock",
                                    productRecord.getProduct().getId()));
                            break;
                        }
                    }
                    if (canBuy) {
                        for (ProductRecord productRecord : shoppingList.getList()) {
                            Store store = productRecord.getProduct().getStore();
                            store.sell(productRecord.getCount());
                            storeService.editStore(store);
                        }
                        shoppingList.setResultOfLastTryToBuy("OK");
                    }
                    Date nextTimeToBuy = new Date();
                    nextTimeToBuy.setTime(now.getTime()+shoppingList.getPeriod());
                    shoppingList.setNextTimeToBuy(nextTimeToBuy);
                    shoppingListService.editShoppingList(shoppingList);
                });
            }
        }
    }

    // Раз в 5 минут добавляет отстаки продуктов
    @Scheduled(fixedRate = 300000)
    public void addStocks() {
        for (Store store : storeService.getAll()) {
            store.setStockBalance(store.getStockBalance() + 20);
            storeService.editStore(store);
        }
    }
}
