package javasign.com.dompetsehat.models;

import java.util.ArrayList;

/**
 * Created by Xenix on 12/18/2015.
 */
public class ParentBudget {
    private String date_start;
    private String date_end;
    private ArrayList<Budget> budgetChild;

    public ArrayList<Budget> getBudgetChild() {
        return budgetChild;
    }

    public void setBudgetChild(ArrayList<Budget> budgetChild) {
        this.budgetChild = budgetChild;
    }

    public String getDate_start() {
        return date_start;
    }

    public void setDate_start(String date_start) {
        this.date_start = date_start;
    }

    public String getDate_end() {
        return date_end;
    }

    public void setDate_end(String date_end) {
        this.date_end = date_end;
    }
}
