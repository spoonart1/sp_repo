package javasign.com.dompetsehat.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import javasign.com.dompetsehat.VeryFund.GeneralHelper;
import javasign.com.dompetsehat.ui.activities.main.NewMainActivity;
import javasign.com.dompetsehat.ui.activities.transaction.AddTransactionActivity;
import javasign.com.dompetsehat.utils.DbHelper;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.utils.SessionManager;
import javasign.com.dompetsehat.utils.State;

/**
 * Created by Xenix on 2/29/2016.
 */
public class ActivitiesReminderService extends IntentService {

    private DbHelper db;
    private SessionManager session;
    private GeneralHelper helper = GeneralHelper.getInstance();
    private NotificationManager mNotificationManager;
    private int SERVICEACTIVITIESREMINDER = 2;

    public ActivitiesReminderService() {
        super("ActivitiesReminderService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        sendNotification();
        db = DbHelper.getInstance(getApplicationContext());
        session = new SessionManager(getApplicationContext());
        if(session.getIdUser() != null && !session.getIdUser().equals("") && !session.getIdUser().equals("null") && session.getIdUser() != "null") {
            int cashflow_count = db.getCountCashflowByDate(Integer.valueOf(session.getIdUser()), helper.getCurrentTime(helper.FORMAT_MONTH_MM));
            if(cashflow_count == 0)
            {
                sendNotification();
            }
        }
        stopSelf();
    }

    private void sendNotification() {
        String msgText = "Ada transaksi hari ini ? tambahkan di DompetSehat!";

        Intent notificationIntent = new Intent(this, NewMainActivity.class)
            .putExtra(State.GO_ACTIVITY,AddTransactionActivity.class.getCanonicalName())
            .putExtra(AddTransactionActivity.KEY_TYPE,AddTransactionActivity.TYPE_ADD)
            .putExtra(AddTransactionActivity.FROM,"service");
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent = PendingIntent.getActivity(this, 0,notificationIntent, 0);

        Helper.sendNotification(SERVICEACTIVITIESREMINDER,this,intent,"Notification",msgText);
    }
}
