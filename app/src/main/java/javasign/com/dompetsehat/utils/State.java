package javasign.com.dompetsehat.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import java.util.Locale;
import javasign.com.dompetsehat.BuildConfig;

/**
 * Created by Spoonart on 12/7/2015.
 */
public class State {

  public final static int ERROR_SERVER = -111;
  public final static int ERROR_SCRIPT = -112;


  public final static int UNDEFINED = -1;

  public final static String FLAG_DATA_HAS_UPDATED = "DATA.HAS.UPDATE";
  public final static String EVENT_CONTENT_NEED_UPDATE = "VIEW.NEED.UPDATE";
  public final static String EVENT_DRAWER_LIST_NEED_UPDATE = "DRAWER.NEED.UPDATE";
  public final static String EVENT_DRAWER_PICT_NEED_UPDATE = "DRAWER.PICT.UPDATE";
  public final static String EVENT_FROM_SETTING_TO_MANAGE_ACCOUNT =
      "FROM.SETTING.TO.MANAGE.ACCOUNT";
  public final static String EVENT_IF_DETAIL_DONT_HAS_NEW_TRANSACTION =
      "IF.DETAIL.DONT.HAS.NEW.TRANSACTION";

  public final static String DEFAULT_KEY_STR = "UPDATE.STR";
  public final static String DEFAULT_KEY_INT = "UPDATE.INT";
  public final static String DEFAULT_VALUE_STR = "VALUE.STR";

  public final static int FLAG_DATA_NEED_UPDATE = 999;

  public final static int HOME = 0;
  public final static int TRANSACTION = 1;
  public final static int BUDGET = 2;
  public final static int REMINDER = 3;
  public final static int GOAL = 4;
  public final static int EMPTY = 5;
  public final static int FEEDBACK = 6;
  public final static int SETTING = 7;
  public final static int MANAGE = 8;

  public final static String GO_ACTIVITY = "go_activity";

  public final static int[] States =
      new int[] { HOME, TRANSACTION, BUDGET, REMINDER, GOAL, EMPTY, FEEDBACK, SETTING, MANAGE };

  public static final String FLAG_TRANSACTION_ACTIVITY = "transaksi2";
  public static final String FLAG_FRAGMENT_FINPLAN = "rencana";
  public static final String FLAG_FRAGMENT_TIMELINE = "transaksi";
  public static final String FLAG_FRAGMENT_OVERVIEW = "laporan";
  public static final String FLAG_FRAGMENT_BUDGET = "anggaran";
  public static final String FLAG_FRAGMENT_MORE = "lainnya";

  public static final String FLAG_FRAGMENT_REFERRAL = "kode referal";
  public static final String FLAG_FRAGMENT_COMISSION = "komisi saya";
  public static final String FLAG_FRAGMENT_PORTOFOLIO = "portofolio";

  public static final int OVERVIEW_NETT_INCOME = 0;
  public static final int OVERVIEW_DETAIL_INCOME = 2;
  public static final int OVERVIEW_DETAIL_EXPENSE = 1;


  public static final int FILTER_BY_NETT = 0;
  public static final int FILTER_BY_TRANSACTION = 1;
  public static final int FILTER_BY_CATEGORY = 2;

  public static final int FLAG_ACTIVITY_WILL_FINISH_LATER = -1;
  public static final int FLAG_ACTIVITY_WILL_FINISH_AFTER = 0;
  public static final int FLAG_ACTIVITY_WILL_FINISH_IMMIDIATE = 5;

  public static final int FROM_DETAIL_ACCOUNT = 1;
  public static final int FROM_TIMELINE = 2;
  public static final int FROM_OVERVIEW_NETT_INCOME = 3;
  public static final int FROM_OVERVIEW_INCOME_TRANSACTION = 4;
  public static final int FROM_OVERVIEW_EXPENSE_TRANSACTION = 5;
  public static final int FROM_OVERVIEW_INCOME_CATEGORY = 6;
  public static final int FROM_OVERVIEW_EXPENSE_CATEGORY = 7;
  public static final int FROM_NOTIFICATION = 8;

  //String URL_with_encrypt = "http://111.221.107.97/mmincwsuatrefsvc2/NewProfile/UpdateProfile?RequestId="+ID;
  //String URL = "https://111.221.107.97/mmincwsuatdev/NewProfile/UpdateProfile?RequestId="+ID;
  public static final String URL_MAIN_MAMI = BuildConfig.URL_MAMI;
  public static final String URL_MAMI_INCWSUATWEB = "incwsuatweb";
  public static final String URL_MAMI_REKSADANA = "Reksadana";

  public static final String IDS_TRANSACTION = "ids_transaction";
  public static final String ID_ACCOUNT = "id_account";
  public static final String STATUS = "status";
  public static final String YEAR = "tahun";
  public static final String MONTH = "bulan";

  private static Locale locale;

  public static Locale getLocale(){
    if(locale == null)
      locale = new Locale("in","ID");
    
    return locale;
  }

  public static void Broadcast(Context context, String eventName) {
    Intent intent = new Intent(FLAG_DATA_HAS_UPDATED);
    intent.putExtra(DEFAULT_KEY_STR, eventName);
    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
  }

  public static void Broadcast(Context context, String eventName, String value) {
    Intent intent = new Intent(FLAG_DATA_HAS_UPDATED);
    intent.putExtra(DEFAULT_KEY_STR, eventName);
    intent.putExtra(DEFAULT_VALUE_STR, value);
    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
  }

  public static void unRegisterBroadCast(Context context, BroadcastReceiver broadcastReceiver) {
    LocalBroadcastManager.getInstance(context).unregisterReceiver(broadcastReceiver);
  }

  public static void registerBroadCast(Context context, BroadcastReceiver broadcastReceiver) {
    LocalBroadcastManager.getInstance(context)
        .registerReceiver(broadcastReceiver, new IntentFilter(FLAG_DATA_HAS_UPDATED));
  }
}
