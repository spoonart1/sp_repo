package javasign.com.dompetsehat.services;

import android.app.IntentService;
import android.content.Intent;
import com.google.gson.Gson;
import javasign.com.dompetsehat.base.MyCustomApplication;
import javasign.com.dompetsehat.data.DataManager;
import javasign.com.dompetsehat.models.response.SignInResponse;
import javasign.com.dompetsehat.utils.SessionManager;
import javax.inject.Inject;

/**
 * Created by avesina on 2/13/17.
 */

public class InsertLoginDataService extends IntentService {
  private SessionManager session;
  @Inject DataManager dataManager;
  public InsertLoginDataService(){
    super("InsertLoginDataService");
    session = new SessionManager(getApplicationContext());
    ((MyCustomApplication) getApplication()).getApplicationComponent().inject(this);
  }
  @Override protected void onHandleIntent(Intent intent) {
    String data = session.getData();
    SignInResponse signInResponse = new Gson().fromJson(data,SignInResponse.class);
    //dataManager.saveUser(signInResponse.response.access_token, signInResponse.response.user);
    dataManager.saveAccount(signInResponse.response.user.id, signInResponse.response.accounts);
    dataManager.saveCategory(signInResponse.response.categories);
    dataManager.saveBudget(signInResponse.response.budgets);
    dataManager.saveAlarm(signInResponse.response.alarms);
    dataManager.savePlan(signInResponse.response.plans);
    dataManager.saveVendor(signInResponse.response.vendors);
    dataManager.saveUserCategory(signInResponse.response.user_categories);
  }
}
