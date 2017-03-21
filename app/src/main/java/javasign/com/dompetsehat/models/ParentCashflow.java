package javasign.com.dompetsehat.models;

import java.util.ArrayList;

/**
 * Created by Xenix on 12/15/2015.
 */
public class ParentCashflow {
    private String date_transaction;
    private Float total;
    private ArrayList<Cash> cashChild;

    public String getDate_transaction() {
        return date_transaction;
    }

    public void setDate_transaction(String date_transaction) {
        this.date_transaction = date_transaction;
    }

    public Float getTotal() {
        return total;
    }

    public void setTotal(Float total) {
        this.total = total;
    }

    public ArrayList<Cash> getCashChild() {
        return cashChild;
    }

    public void setCashChild(ArrayList<Cash> cashChild) {
        this.cashChild = cashChild;
    }
}