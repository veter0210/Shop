package testtask.shop.model;

import javax.persistence.*;

/**
 * Created by Dmitry Martynov on 29.05.2019
 */

@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String title;

    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL)
    private Store store;

    public Product() {
    }

    public Product(String title) {
        this.title = title;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", store=" + store +
                '}';
    }
}
