package testtask.shop.service;

import testtask.shop.ScheduledTasks;
import testtask.shop.model.ShoppingList;

import java.util.List;

/**
 * Created by Dmitry Martynov on 29.05.2019
 */

public interface ShoppingListService {

    /**
     * Запрос списка покупок по Id
     * @param id    Id списка покупок
     * @return      Списпок покупок
     */
    ShoppingList getById(long id);

    /**
     * Добавление списка покупок в базу данных
     * @param shoppingList  Список покупок
     * @return              Список покупок с проставленными Id
     */
    ShoppingList addShoppingList(ShoppingList shoppingList);

    /**
     * Удаление списка покупок по Id
     * @param id    Id списка покупок
     */
    void deleteById(long id);

    /**
     * Внесение изменений в список покупок
     * @param shoppingList  Список покупок
     * @return              Список покупок с изменениями
     */
    ShoppingList editShoppingList(ShoppingList shoppingList);

    /**
     * Запрос всех списков покупок
     * @return  Список всех списков покупок
     */
    List<ShoppingList> getAll();

    /**
     * Запрос всех периодических списков покупок
     * @return  Список списков покупок с прсотавленным флагом периодичности
     */
    List<ShoppingList> getAllByIsPeriodicTrue();

    /**
     * Выкуп по списку покупок с соответствующим Id один раз, если это возможно.
     * Результат попытки сохраняется в поле resultOfLastTryToBuy списка покупок
     * @param id    Id списка покупок
     */
    void buyShoppingListOnce(long id);

    /**
     * Устанавливает у списка покупок с соответствующим Id флаг периодичности isPeriodic
     * и время следующего выкупа nextTimeToBuy, если он существует.
     * Таким образом, при следующем запросе периодических списков
     * методом {@link ScheduledTasks#getAllPeriodicShoppingListsAndBuyIfNeed()} он будет обработан
     * @param id    Id списка покупок
     */
    void startPeriodicBuying(long id);

    /**
     * Снимает у списка покупок с соответствующим Id флаг периодичности isPeriodic
     * и обнуляет время следующего выкупа nextTimeToBuy, если он существует.
     * @param id    Id списка покупок
     */
    void stopPeriodicBuying(long id);
}
