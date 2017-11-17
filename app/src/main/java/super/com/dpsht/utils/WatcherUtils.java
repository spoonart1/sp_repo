package javasign.com.dompetsehat.utils;

import android.app.Notification;
import android.content.Context;
import android.text.TextUtils;
import br.com.goncalves.pugnotification.notification.PugNotification;
import java.util.ArrayList;
import java.util.HashMap;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.models.Account;
import javasign.com.dompetsehat.models.Budget;
import javasign.com.dompetsehat.models.Category;
import javasign.com.dompetsehat.models.Product;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by avesina on 2/28/17.
 */

public class WatcherUtils {
  public static int KEY_OVERSPENDING_NOTIF = 1;
  CompositeSubscription compositeSubscription = new CompositeSubscription();
  SessionManager sessionManager = null;
  Context context;
  DbHelper db;
  MCryptNew mCryptNew = new MCryptNew();
  RupiahCurrencyFormat format = new RupiahCurrencyFormat();

  public WatcherUtils(Context context, DbHelper db) {
    this.context = context;
    this.db = db;
  }

  public WatcherUtils setSessionManager(SessionManager sessionManager) {
    this.sessionManager = sessionManager;
    return this;
  }

  public void triggerAll() {
    if (sessionManager != null) {

    } else {
      throw new NullPointerException();
    }
  }

  public void triggerOverSpending() {
    if (sessionManager == null) {
      throw new NullPointerException();
    }
    ArrayList<Product> products = db.getAllProductBySaldo(sessionManager.getIdUser(), "<", 0);
    compositeSubscription.add(Observable.from(products)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(product -> {
          String name = mCryptNew.decrypt(product.getName());
          showNotif(product.getId_product(), "Overspending",
              "Overspending " + name + " your saldo only have " + format.toRupiahFormatSimple(
                  product.getBalance()), "Rekening " + name + " memiliki saldo kurang dari 0");
        }, throwable -> {
        }, () -> {
        }));
  }

  public void triggerOverBudget() {
    ArrayList<Budget> budgets = db.getAllBudget(sessionManager.getIdUser());
    compositeSubscription.add(Observable.from(budgets)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(budget -> {
          if (budget.getRepeat() == Budget.BUDGET_REPEAT_TRUE) {
            HashMap<String, String> map = new HashMap<>();
            if (budget.getEvery().equalsIgnoreCase(Budget.CUSTOM.toLowerCase())) {
              map = Helper.populatePeriodicCustome(
                  Helper.setInputFormatter("yyyy-MM-dd", budget.getDate_start()),
                  Helper.setInputFormatter("yyyy-MM-dd", budget.getDate_end()), true);
            } else {
              map = Helper.populatePeriodicString(budget.getEvery().toLowerCase());
            }
            budget.setDate_start(map.get(Helper.DATE_START));
            budget.setDate_end(map.get(Helper.DATE_END));
          }
          if (budget.getDate_start() != null && budget.getDate_end() != null) {
            float saldo_cash = db.getTotalAmountCashByCategoryAndDate(String.valueOf(budget.getCategory_budget()),
                    "All", sessionManager.getIdUser(), budget.getDate_start(), budget.getDate_end());
            if(saldo_cash > budget.getAmount_budget()){
              Category category = db.getCategoryByID(budget.getCategory_budget());
              double lebih = saldo_cash-budget.getAmount_budget();
              showNotif(budget.getId(),"OverBudget","Budget "+category.getName()+" lebih "+format.toRupiahFormatSimple(lebih),null);
            }
          }
        }, throwable -> {
        }, () -> {
        }));
  }

  private void showNotif(int identifier, String title, String message, String bigtext) {
    if(TextUtils.isEmpty(bigtext)){
      bigtext = message;
    }
    PugNotification.with(context)
        .load()
        .identifier(identifier)
        .title(title)
        .message(message)
        .bigTextStyle(bigtext)
        .smallIcon(R.drawable.icon_dompet)
        .largeIcon(R.drawable.icon_dompet)
        .flags(Notification.DEFAULT_ALL)
        .simple()
        .build();
  }
}
