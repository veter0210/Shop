package testtask.shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import testtask.shop.model.Product;

/**
 * Created by Dmitry Martynov on 29.05.2019
 */

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>{
}
