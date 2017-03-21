package javasign.com.dompetsehat.VeryFund;

import android.app.ProgressDialog;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Base64;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.mikepenz.iconics.view.IconicsTextView;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.models.Cash;
import javasign.com.dompetsehat.utils.RupiahCurrencyFormat;
import retrofit.client.Response;

/**
 * Created by Xenix on 1/4/2016.
 */
public class GeneralHelper {

  private static GeneralHelper generalHelper;
  /*BEGIN DATE AND TIME STRING*/
  public static final String FORMAT_D = "d";
  public static final String FORMAT_M = "M";

  public static final String FORMAT_HH = "HH";
  public static final String FORMAT_DD = "dd";
  public static final String FORMAT_MM = "MM";
  public static final String FORMAT_YYYY = "yyyy";
  public static final String FORMAT_EEEE = "EEEE";

  public static final String FORMAT_MMM_YYYY = "MMM yyyy";
  public static final String FORMAT_MMMM_YYYY = "MMMM yyyy";

  public static final String FORMAT_MONTH_MM = "yyyy-MM-dd";
  public static final String FORMAT_DD_MMM_YYYY = "dd-MMM-yyyy";
  public static final String FORMAT_BIRTHDAY= "dd MMMM yyyy";
  public static final String FORMAT_DD_MM_YYYY_SLICED = "dd/MM/yyyy";
  public static final String FORMAT_DD_MM_YY_SLICED = "dd/MM/yy";
  public static final String FORMAT_YYYY_MM_DD = "yyyy-MM-dd";
  public static final String FORMAT_LENGKAP = "yyyy-MM-dd HH:mm:ss";

  public static final String MMM_YYYY = "MMMM, yyyy";
  public static final String FORMAT_WITH_DAY = "EEEE, dd-MM-yyyy";
  public static final String FORMAT_WITH_DAY_SHORT_AND_MONTH_SHORT = "EE, dd-MMM-yyyy";
  public static final String FORMAT_WITH_DAY_SHORT = "EE, dd-MM-yyyy";
    /*END DATE AND TIME STRING*/

  public static final String skeySpec = "kVBkcfp3ivy3GqzSn62Dn2sW27X408VN";
  public static final String cipher = "AES/CBC/PKCS7Padding";
  public static final String AES = "AES";
  public static final String UTF8 = "UTF-8";
  public static final String RESPONSE_API = "response_api";
  public static final String FLAG = "flag";
  public static final String SENDJSONREQUESTREFRESH3 = "sendJSONRequestRefresh3";
  public static final String SENDJSONREQUESTSYNCACCOUNTLASTCONNECT =
      "sendJSONRequestSyncAccountLastConnect";

  public static final String YES = "yes";

  public static synchronized GeneralHelper getInstance() {
    if (generalHelper == null) {
      generalHelper = new GeneralHelper();
    }
    return generalHelper;
  }

  public void formatEditText(EditText target) {
    RupiahCurrencyFormat rcf = new RupiahCurrencyFormat();
    rcf.formatEditText(target);
  }

  public void formatEditText(EditText target, IconicsTextView btn, Resources resources) {
    RupiahCurrencyFormat rcf = new RupiahCurrencyFormat();
    rcf.formatEditText(target);
    rcf.setButtonToListen(resources, btn);
  }

  @Deprecated public void formatEditText(EditText target, Button btn, Resources resources) {
    //no longer used
  }

  public void formatEditText(EditText target, TextView text) {
    RupiahCurrencyFormat rcf = new RupiahCurrencyFormat();
    rcf.formatEditText(target);
  }

  public void dismissProgressDialog(ProgressDialog pDialog) {
    if (pDialog != null && pDialog.isShowing()) {
      pDialog.dismiss();
    }
  }

  public String responseRetrofitToString(Response response) {
    //Try to get response body
    BufferedReader reader = null;
    StringBuilder sb = new StringBuilder();
    try {
      reader = new BufferedReader(new InputStreamReader(response.getBody().in()));
      String line;
      try {
        while ((line = reader.readLine()) != null) {
          sb.append(line);
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return sb.toString();
  }

  public String setTransName(Cash cash) {
    if (cash.getCashflow_rename() != null
        && !cash.getCashflow_rename().isEmpty()
        && !cash.getCashflow_rename().equalsIgnoreCase("null")
        && !cash.getCashflow_rename().equalsIgnoreCase("")
        && cash.getCashflow_rename().length() != 0) {
      return cash.getCashflow_rename();
    } else {
      return cash.getDescription();
    }
  }

  public String setNoteName(Cash cash) {
    if (cash.getNote() != null
        && !cash.getNote().isEmpty()
        && !cash.getNote()
        .equalsIgnoreCase("null")
        && !cash.getNote().equalsIgnoreCase("")
        && cash.getNote().length() != 0) {
      return cash.getNote();
    } else {
      return "";
    }
  }

  public String getCurrentTime(String msg) {
    Calendar c = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_MONTH_MM);
    if (msg.equalsIgnoreCase("UpdatingService")) {
      sdf = new SimpleDateFormat(FORMAT_DD);
    } else if (msg.equalsIgnoreCase(FORMAT_MONTH_MM)) {
      sdf = new SimpleDateFormat(FORMAT_MONTH_MM);
    } else if (msg.equalsIgnoreCase(FORMAT_LENGKAP)) sdf = new SimpleDateFormat(FORMAT_LENGKAP);
    return sdf.format(c.getTime());
  }

  public int getCurrentMonth() {
    Calendar c = Calendar.getInstance();
    SimpleDateFormat sdf;
    sdf = new SimpleDateFormat(FORMAT_M);
    return Integer.valueOf(sdf.format(c.getTime()));
  }

  public int getCurrentYear() {
    Calendar c = Calendar.getInstance();
    SimpleDateFormat sdf;
    sdf = new SimpleDateFormat(FORMAT_YYYY);
    return Integer.valueOf(sdf.format(c.getTime()));
  }

  public String getYesterdayTime(String msg) {
    Calendar c = Calendar.getInstance();
    c.add(Calendar.DATE, -1);
    SimpleDateFormat sdf;
    if (msg.equalsIgnoreCase("UpdatingService")) {
      sdf = new SimpleDateFormat(FORMAT_DD);
    } else {
      sdf = new SimpleDateFormat(FORMAT_MONTH_MM);
    }
    return sdf.format(c.getTime());
  }

  public long getInterval(String currentTime, String lastConnect) {
    SimpleDateFormat formatter = new SimpleDateFormat(FORMAT_MONTH_MM);
    Long test = Long.parseLong("0");
    try {
      Date date1 = formatter.parse(currentTime);
      Date date2 = formatter.parse(lastConnect);
      test = date1.getTime() - date2.getTime();
    } catch (Exception e) {
    }
    return TimeUnit.MILLISECONDS.toDays(test);
  }

  public Long converterEpoc(String lc) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss zzz");
    Date date = null;
    try {
      date = sdf.parse(lc + " GMT");
    } catch (ParseException e) {
      e.printStackTrace();
    }
    long timeInMillisSinceEpoch = date.getTime();

    return timeInMillisSinceEpoch / 1000;
  }

  public String converterEpochToDate(Long epoch) {
        /*return new SimpleDateFormat("yyyy-MM-dd kk:mm:ss")
                .format(new java.util.Date (epoch*1000));*/
    Date date = new Date(epoch * 1000L); // *1000 is to convert seconds to milliseconds
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss"); // the format of your date
    sdf.setTimeZone(TimeZone.getTimeZone(
        "GMT")); // give a timezone reference for formating (see comment at the bottom
    return sdf.format(date);
  }

  public static void greenStatusBar(Window window, Resources resources) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
      window.setStatusBarColor(resources.getColor(R.color.green_dompet_ori));
    }
  }


  public Bitmap decodeBase64(String input) {
    byte[] decodedByte = Base64.decode(input, 0);
    return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
  }

  public static void statusBarColor(Window window, int color) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
      window.setStatusBarColor(color);
    }
  }

  public int getAge(Calendar calendar){
    int y, m, d;
    y = calendar.get(Calendar.YEAR);
    m = calendar.get(Calendar.MONTH);
    d = calendar.get(Calendar.DAY_OF_MONTH);
    return getAge(y,m,d);
  }

  public int getAge (int _year, int _month, int _day) {

    GregorianCalendar cal = new GregorianCalendar();
    int y, m, d, a;

    y = cal.get(Calendar.YEAR);
    m = cal.get(Calendar.MONTH);
    d = cal.get(Calendar.DAY_OF_MONTH);
    cal.set(_year, _month, _day);
    a = y - cal.get(Calendar.YEAR);
    if ((m < cal.get(Calendar.MONTH))
            || ((m == cal.get(Calendar.MONTH)) && (d < cal
            .get(Calendar.DAY_OF_MONTH)))) {
      --a;
    }
    if(a < 0)
      throw new IllegalArgumentException("Age < 0");
    return a;
  }

}
