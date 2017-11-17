package javasign.com.dompetsehat.receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import java.util.Calendar;
import java.util.GregorianCalendar;
import javasign.com.dompetsehat.services.ActivitiesReminderService;
import javasign.com.dompetsehat.services.AlarmService;
import javasign.com.dompetsehat.services.ReminderService;
import javasign.com.dompetsehat.services.SyncIntentService;
import javasign.com.dompetsehat.ui.activities.main.NewMainActivity;
import javasign.com.dompetsehat.utils.SessionManager;
import timber.log.Timber;

/**
 * Created by Xenix on 12/16/2015.
 */
public class AllWakefulBroadcastReceiver extends WakefulBroadcastReceiver {

  AlarmManager alarmManager;
  PendingIntent pendingIntent, alarmIntent, reminderActivitiesIntent, syncActivitiesIntent;
  GregorianCalendar calendar;
  SessionManager session;

  @Override public void onReceive(Context context, Intent intent) {
    session = new SessionManager(context);
    Timber.e("avesina " + session.getIdUser());
    if (session.getIdUser() != null && !session.getIdUser().equals("") && !session.getIdUser()
        .equals("null") && session.getIdUser() != "null") {
      Intent myIntent = new Intent(context, ReminderService.class);
      pendingIntent = PendingIntent.getService(context, NewMainActivity.SERVICEREMINDER, myIntent,
          PendingIntent.FLAG_UPDATE_CURRENT);

      Intent myIntent2 = new Intent(context, AlarmService.class);
      alarmIntent = PendingIntent.getService(context, NewMainActivity.SERVICEALARM, myIntent2,
          PendingIntent.FLAG_UPDATE_CURRENT);

      Intent myIntent3 = new Intent(context, ActivitiesReminderService.class);
      reminderActivitiesIntent =
          PendingIntent.getService(context, NewMainActivity.SERVICEACTVITIESREMINDER, myIntent3,
              PendingIntent.FLAG_UPDATE_CURRENT);
      alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);

      Intent myIntent4 = new Intent(context, SyncIntentService.class);
      syncActivitiesIntent = PendingIntent.getService(context, NewMainActivity.SERVICESYNC, myIntent4,
          PendingIntent.FLAG_UPDATE_CURRENT);

      //FINPLAN SERVICE
      calendar = (GregorianCalendar) Calendar.getInstance();
      calendar.set(Calendar.HOUR_OF_DAY, 21);
      calendar.set(Calendar.MINUTE, 0);
      calendar.set(Calendar.SECOND, 0);
      calendar.set(Calendar.MILLISECOND, 0);
      if (Calendar.getInstance().after(calendar)) {
        calendar.add(Calendar.DATE, 1);
      }
      alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
          AlarmManager.INTERVAL_DAY, pendingIntent);

      //ALARM SERVICE
      calendar = (GregorianCalendar) Calendar.getInstance();
      calendar.set(Calendar.HOUR_OF_DAY, 10);
      calendar.set(Calendar.MINUTE, 0);
      calendar.set(Calendar.SECOND, 0);
      calendar.set(Calendar.MILLISECOND, 0);
      if (Calendar.getInstance().after(calendar)) {
        calendar.add(Calendar.DATE, 1);
      }
      alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
          AlarmManager.INTERVAL_DAY, alarmIntent);

      //ACTIVITIES FINPLAN SERVICE
      calendar = (GregorianCalendar) Calendar.getInstance();
      calendar.set(Calendar.HOUR_OF_DAY, session.getHoursActivitiesReminder());
      calendar.set(Calendar.MINUTE, session.getMinutesActivitiesReminder());
      calendar.set(Calendar.SECOND, 0);
      calendar.set(Calendar.MILLISECOND, 0);
      if (Calendar.getInstance().after(calendar)) {
        calendar.add(Calendar.DATE, 1);
      }

      if(session.getActivitiesReminder()) {
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, reminderActivitiesIntent);
      }

      calendar = (GregorianCalendar) Calendar.getInstance();
      calendar.add(Calendar.SECOND, 30);
      Long INTERVAL = AlarmManager.INTERVAL_DAY;
      calendar = (GregorianCalendar) Calendar.getInstance();
      calendar.set(Calendar.HOUR_OF_DAY, session.getHoursSync());
      calendar.set(Calendar.MINUTE, session.getMinutesSync());
      calendar.set(Calendar.SECOND, 0);
      calendar.set(Calendar.MILLISECOND, 0);
      if (Calendar.getInstance().after(calendar)) {
        calendar.add(Calendar.DATE, 1);
      }

      alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), INTERVAL,
          syncActivitiesIntent);
    }
  }
}
