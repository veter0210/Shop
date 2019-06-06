package testtask.shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import testtask.shop.model.Product;
import testtask.shop.model.ProductRecord;

/**
 * Created by Dmitry Martynov on 29.05.2019
 */

@Repository
public interface ProductRecordRepository extends JpaRepository<ProductRecord, Long> {

    /**
     * Проверка, существуют ли записи с соответствующим продуктом
     * @param product   Объект Продукт
     * @return          true or false
     */
    boolean existsByProduct(Product product);
}
