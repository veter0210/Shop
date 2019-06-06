package testtask.shop.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import javax.validation.constraints.Min;

/**
 * Created by Dmitry Martynov on 29.05.2019
 */
@Entity
public class ProductRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Min(1L)
    private long count;
    @ManyToOne(optional = false)
    private Product product;
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JsonBackReference
    private ShoppingList shoppingList;

    public ProductRecord() {
    }


    @PreRemove
    public void remove() {
        this.setProduct(null);
        this.setShoppingList(null);
    }
    public ProductRecord(long count, Product product, ShoppingList shoppingList) {
        this.count = count;
        this.product = product;
        this.shoppingList = shoppingList;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public ShoppingList getShoppingList() {
        return shoppingList;
    }

    public void setShoppingList(ShoppingList shoppingList) {
        this.shoppingList = shoppingList;
    }

    @Override
    public String toString() {
        return "ProductRecord{" +
                "id=" + id +
                ", count=" + count +
                ", product=" + product +
                '}';
    }
}
