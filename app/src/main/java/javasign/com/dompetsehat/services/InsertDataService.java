package javasign.com.dompetsehat.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import javasign.com.dompetsehat.ui.activities.main.NewMainActivity;
import org.json.JSONArray;
import org.json.JSONObject;

import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.VeryFund.GeneralHelper;
import javasign.com.dompetsehat.utils.DbHelper;
import javasign.com.dompetsehat.utils.SessionManager;
import javasign.com.dompetsehat.utils.State;
import timber.log.Timber;

/**
 * Created by Xenix on 2/19/2016.
 */
public class InsertDataService extends IntentService {

    private DbHelper db;
    private SessionManager session;
    private NotificationManager mNotificationManager;
    private String response_api;
    private String from;
    private GeneralHelper helper = GeneralHelper.getInstance();
    private Intent mIntent;

    public InsertDataService() {
        super("InsertDataService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        session = new SessionManager(getApplicationContext());
    }

    private void request(){
        db = DbHelper.getInstance(getApplicationContext());
        session.setLastSync(SessionManager.LAST_SYNC);
        if (mIntent != null && mIntent.getExtras() != null) {
            response_api = session.getData();
            from = mIntent.getExtras().getString(helper.FLAG);
        }

        try {
            JSONObject json = new JSONObject(response_api);
            if (json.getString("status").equals("success")) {
                String array_kosong = json.getString("response");
                if (array_kosong.equalsIgnoreCase("[]")) {
                    sendNotification("You dont have new transaction :(");
                } else {
                    JSONObject responses = json.getJSONObject("response");
                    JSONArray account2 = responses.getJSONArray("accounts");
                    Timber.d("user_id == "+session.getIdUser());
                    for (int i = 0; i < account2.length(); i++) {
                        JSONObject account = account2.getJSONObject(i);
                        Timber.d("account"+account);
                        JSONObject accountObject = db.updateAccount(account, responses, session.getIdUser());
                        Timber.d("accountObject"+accountObject);
                        if (accountObject == null)
                            continue;

                        if(!accountObject.has("products"))
                            continue;

                        JSONArray product2 = accountObject.getJSONArray("products");
                        for (int z = 0; z < product2.length(); z++) {
                            JSONObject product = product2.getJSONObject(z);
                            Timber.d("proudct first "+product);
                            //Timber.d(from+" = "+helper.SENDJSONREQUESTREFRESH3);
                            //if (from.equalsIgnoreCase(helper.SENDJSONREQUESTREFRESH3))
                            //    if (db.getCashflowCountByProduct(Integer.valueOf(session.getIdUser()), product.getInt("id")) != 0)
                            //        continue;

                            JSONObject productObject = null;
                            try {
                                productObject = db.updateProduct(product, account, session.getIdUser());
                                Timber.d("productObject"+productObject.toString());
                            }catch (Exception e){
                                Timber.d("product Object null");
                            }

                            if (productObject == null)
                                continue;

                            if(!productObject.has("cashflow"))
                                continue;

                            JSONArray cashflow2 = productObject.getJSONArray("cashflow");
                            for (int x = 0; x < cashflow2.length(); x++) {
                                JSONObject cashflow = cashflow2.getJSONObject(x);
                                JSONObject cashflowObject = db.updateCashflow(cashflow, product, session.getIdUser());
                                if (cashflowObject == null)
                                    continue;
                                if (from.equalsIgnoreCase(helper.SENDJSONREQUESTSYNCACCOUNTLASTCONNECT))
                                    db.scraping_atm(cashflowObject, session.getIdUser());

                                db.getLastUpdatedCash(account.getInt("id"));
                            }
                        }
                        session.setLastSync(session.LAST_SYNC_ACCOUNT);
                    }

                    try {
                        Thread.sleep(1500);
                    } catch(Exception ex) {
                        Thread.currentThread().interrupt();
                    }
                    db.badgesCheck(session.getIdUser());
                    State.Broadcast(getApplicationContext(), State.EVENT_CONTENT_NEED_UPDATE);
                    State.Broadcast(getApplicationContext(), State.EVENT_DRAWER_LIST_NEED_UPDATE);

                    if (from.equalsIgnoreCase(helper.SENDJSONREQUESTREFRESH3)) {
                        sendNotification("Data cashflow updated");
                    } else if(from.equalsIgnoreCase(helper.SENDJSONREQUESTSYNCACCOUNTLASTCONNECT)) {
                        sendNotification("DompetSehat: Success parsing your cashflow");
                    }
                }
            } else {
                if(json.has("msg"))
                    sendNotification(json.getString("msg"));
                else
                    sendNotification(json.getString("message"));
            }
        } catch (NullPointerException e){
            Timber.e("Exception "+e);
        }catch (Exception ex) {
            Timber.e("Exception "+ex);
            sendNotification(ex.toString());
        }
        if (from.equalsIgnoreCase(helper.SENDJSONREQUESTREFRESH3))
            NewMainActivity.FLAG_RETROFIT_REFRESH3 = false;

        session.clearData();
        stopSelf();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        mIntent = intent;
        request();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void sendNotification(String msg) {
        NotificationManager mNotificationManager;
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle("DompetSehat Notification");
        mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(msg));
        mBuilder.setContentText(msg);
        //long[] pattern = {0, 600, 200, 400, 200};
        //mBuilder.setVibrate(pattern);
        mBuilder.setSmallIcon(R.drawable.icon_dompet);
        mBuilder.setAutoCancel(true);
        mBuilder.setVibrate(new long[] { -1 });
        mBuilder.build().flags |= Notification.FLAG_AUTO_CANCEL;
        //mNotificationManager.notify(NewMainActivity.SERVICEREMINDER, mBuilder.build());
    }
}
