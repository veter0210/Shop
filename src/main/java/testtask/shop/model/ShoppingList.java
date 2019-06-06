package testtask.shop.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Dmitry Martynov on 29.05.2019
 */

@Entity
public class ShoppingList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @JsonIgnore
    private boolean isPeriodic;
    private long period;
    @JsonIgnore
    private Date nextTimeToBuy;
    private String resultOfLastTryToBuy;

    @OneToMany(mappedBy = "shoppingList", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<ProductRecord> list;

    public ShoppingList() {
        this.list = new ArrayList<>();
    }

    public ShoppingList(List<ProductRecord> list, boolean isPeriodic, long period) {
        this.isPeriodic = isPeriodic;
        this.period = period;
        this.list = list;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    @JsonProperty
    public boolean isPeriodic() {
        return isPeriodic;
    }

    @JsonIgnore
    public void setPeriodic(boolean periodic) {
        isPeriodic = periodic;
    }

    public long getPeriod() {
        return period;
    }

    public void setPeriod(long period) {
        this.period = period;
    }

    @JsonProperty
    public Date getNextTimeToBuy() {
        return nextTimeToBuy;
    }

    @JsonIgnore
    public void setNextTimeToBuy(Date nextTimeToBuy) {
        this.nextTimeToBuy = nextTimeToBuy;
    }

    public String getResultOfLastTryToBuy() {
        return resultOfLastTryToBuy;
    }

    public void setResultOfLastTryToBuy(String resultOfLastTryToBuy) {
        this.resultOfLastTryToBuy = resultOfLastTryToBuy;
    }

    public List<ProductRecord> getList() {
        return list;
    }

    public void setList(List<ProductRecord> list) {
        this.list = list;
    }

    public void addProductRecord(ProductRecord productRecord) {
        this.list.add(productRecord);
    }

    @Override
    public String toString() {
        return "ShoppingList{" +
                "id=" + id +
                ", isPeriodic=" + isPeriodic +
                ", period=" + period +
                ", list=" + list +
                '}';
    }
}
