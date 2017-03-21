package javasign.com.dompetsehat.services;

import android.app.Activity;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import java.util.ArrayList;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.models.Alarm;
import javasign.com.dompetsehat.ui.activities.main.NewMainActivity;
import javasign.com.dompetsehat.ui.activities.reminder.ReminderActivity;
import javasign.com.dompetsehat.utils.DbHelper;
import javasign.com.dompetsehat.utils.RupiahCurrencyFormat;
import javasign.com.dompetsehat.utils.SessionManager;
import timber.log.Timber;

/**
 * Created by Xenix on 8/7/2015.
 */
public class AlarmService extends IntentService {
    private NotificationManager mNotificationManager;
    private int result = Activity.RESULT_CANCELED;
    private DbHelper db;
    private String ret = "no";
    private int jumlah = 0 ;
    private ArrayList<Alarm> listAlarm;
    private String TODAY = "Today";
    private String TOMORROW = "Tomorrow";
    private String YESTERDAY = "Yesterday";
    private SessionManager session;
    private RupiahCurrencyFormat rp = new RupiahCurrencyFormat();

    public AlarmService() {
        super("UpdatingService");
    }

    @Override public void onCreate() {
        super.onCreate();
        Timber.e("avesina ");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        db = DbHelper.getInstance(getApplicationContext());
        session = new SessionManager(getApplicationContext());
        if(session.getIdUser() != null && !session.getIdUser().equals("") && !session.getIdUser().equals("null") && session.getIdUser() != "null") {
            db.checkAllAlarm(Integer.valueOf(session.getIdUser()));

            //CEK ALARM HARI KEMAREN
            listAlarm = db.getAlarmYesterday(Integer.valueOf(session.getIdUser()), "UpdatingService");
            if (listAlarm.size() > 0)
                sendNotification(listAlarm, YESTERDAY);

            //CEK ALARM HARI INI
            listAlarm = db.getAlarmToday(Integer.valueOf(session.getIdUser()), "UpdatingService");
            if (listAlarm.size() > 0)
                sendNotification(listAlarm, TODAY);

            //CEK ALARM HARI ESOK
            listAlarm = db.getAlarmTomorrow(Integer.valueOf(session.getIdUser()), "UpdatingService");
            if (listAlarm.size() > 0)
                sendNotification(listAlarm, TOMORROW);
        }
    }

    @Override public void onDestroy() {
        super.onDestroy();
        Timber.d("AlarmService destroy");
    }

    private void sendNotification(ArrayList<Alarm> listAlarm, String msg) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notificationIntent = new Intent(this, ReminderActivity.class);
        notificationIntent.putExtra("From", "UpdatingService");

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        String completeMsg = msg;

        if(listAlarm.size() == 1)
            completeMsg = msg + " " + listAlarm.get(0).getDeskripsi_alarm() + " : " + rp.toRupiahFormatSimple(listAlarm.get(0).getJumlah_alarm());
        else if(listAlarm.size() > 1)
            completeMsg = msg + " ("+listAlarm.size()+")";

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle("Bills Reminder !");
        mBuilder.setContentText(completeMsg);
        long[] pattern = {0, 600, 200, 400, 200};
        mBuilder.setVibrate(pattern);

        mBuilder.setSmallIcon(R.drawable.icon_dompet);
        //mBuilder.setSound(Uri.parse("android.resource://javasign.com.dompetsehat/" + R.raw.baymax));

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle("Bills reminder details:");

        for (int z = 0; z < listAlarm.size(); z++)
            inboxStyle.addLine(msg + " " + listAlarm.get(z).getDeskripsi_alarm() + " : " + rp.toRupiahFormatSimple(listAlarm.get(z).getJumlah_alarm()));

        mBuilder.setStyle(inboxStyle);
        mBuilder.setVibrate(new long[] { -1 });

        mBuilder.setContentIntent(intent);
        mBuilder.setAutoCancel(true);
        mNotificationManager.notify(0, mBuilder.build());

        jumlah = 0;
    }
}