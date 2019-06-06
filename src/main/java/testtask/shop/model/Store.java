package testtask.shop.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;

/**
 * Created by Dmitry Martynov on 29.05.2019
 */
@Entity
public class Store {

    @Id
    private long id;
    private long stockBalance;
    @OneToOne
    @MapsId
    @JsonBackReference
    private Product product;

    public Store() {
    }

    public Store(Product product, long stockBalance) {
        this.product = product;
        this.stockBalance = stockBalance;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public long getStockBalance() {
        return stockBalance;
    }

    public void setStockBalance(long stockBalance) {
        this.stockBalance = stockBalance;
    }

    public void sell(long count) {
        setStockBalance(stockBalance - count);
    }

    @Override
    public String toString() {
        return "Store{" +
                "id=" + id +
                ", stockBalance=" + stockBalance +
                '}';
    }
}
