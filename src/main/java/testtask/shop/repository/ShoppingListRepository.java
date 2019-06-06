package testtask.shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import testtask.shop.model.ShoppingList;

import java.util.List;

/**
 * Created by Dmitry Martynov on 29.05.2019
 */

@Repository
public interface ShoppingListRepository extends JpaRepository<ShoppingList, Long> {

    /**
     * Запрос всех периодических списков покупок
     * @return      Список всех периодических списков покупок
     */
    List<ShoppingList> getAllByIsPeriodicTrue();
}
