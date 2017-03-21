package javasign.com.dompetsehat.models;

import java.util.ArrayList;

/**
 * Created by Xenix on 1/7/2016.
 */
public class ParentAccount {
    private Account account;
    private ArrayList<Product> productsChild;

    public ArrayList<Product> getProductsChild() {
        return productsChild;
    }

    public void setProductsChild(ArrayList<Product> productsChild) {
        this.productsChild = productsChild;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }
}
