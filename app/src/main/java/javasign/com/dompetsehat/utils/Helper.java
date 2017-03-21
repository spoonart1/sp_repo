package javasign.com.dompetsehat.utils;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.TransformationMethod;
import android.util.Base64;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import au.com.bytecode.opencsv.CSVWriter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.kelvinapps.rxfirebase.RxFirebaseAuth;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;
import java.util.TimeZone;
import javasign.com.dompetsehat.BuildConfig;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.base.MyCustomApplication;
import javasign.com.dompetsehat.models.Budget;
import javasign.com.dompetsehat.models.Cash;
import javasign.com.dompetsehat.presenter.sync.SyncPresenter;
import javasign.com.dompetsehat.ui.activities.budget.BudgetSetupActivity;
import javasign.com.dompetsehat.ui.activities.main.NewMainActivity;
import javasign.com.dompetsehat.ui.dialogs.AdvancedDialog;
import javasign.com.dompetsehat.ui.dialogs.DisclaimerDialog;
import javasign.com.dompetsehat.ui.dialogs.InformationDialog;
import javasign.com.dompetsehat.ui.dialogs.MenuDialog;
import javasign.com.dompetsehat.ui.dialogs.SimpleInformationDialog;
import javasign.com.dompetsehat.view.BlankView;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import org.json.JSONArray;
import org.json.JSONObject;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by Spoonart on 12/2/2015.
 */
public class Helper {

  public static final String DEVELOPER_DEVICE_ID = "869055028901738#866376022575549";

  public static final int GREEN_DOMPET_COLOR = Color.parseColor("#28b89b");
  public static final String DATE_START = "date_start";
  public static final String DATE_END = "date_end";

  public final static String THIS_WEEK = "minggu_ini";
  public final static String THIS_MONTH = "bulan_ini";
  public final static String QUATER_MONTH = "catur_wulan";
  public final static String NEXT_MONTH = "bulan_depan";
  public final static String A_MONTH_AGO = "bulan_lalu";
  public final static String THREE_MONTHS_AGO = "tiga bulan_lalu";
  public final static String THREE_MONTHS_LAST_AGO = "tiga bulan_terakhir";
  public final static String SIX_MONTHS_AGO = "enam_bulan_lalu";
  public final static String SIX_MONTHS_LAST_AGO = "enam_bulan_terakhir";
  public final static String WEEKLY = "mingguan";
  public final static String BIWEEKLY = "dua_mingguan";
  public final static String MONTHLY = "bulanan";
  public final static String YEARLY = "tahunan";
  public final static String A_YEAR_AGO = "setahun_lalu";
  public final static String CUSTOME = "custome";

  private static String FILE_CHART = "mnt/sdcard/chart.pdf";
  private static String FILE_CSV = "mnt/sdcard/data.csv";

  public static String[] MONTHS;
  public static String[] MONTHS_SHORT;

  static {
    MONTHS = new String[] {
        "Januari", "Februari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus", "September",
        "Oktober", "November", "Desember"
    };
    MONTHS_SHORT = new String[] {
        "Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Agt", "Sep", "Okt", "Nov", "Des"
    };
  }

  public static Bitmap resizeImageBaseOnScreenSize(Context context, Bitmap image) {
    WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    Display display = wm.getDefaultDisplay();

    int screenHeight = display.getHeight();
    int screenWidht = display.getWidth();
    image = Bitmap.createScaledBitmap(image, screenWidht, screenHeight, true);

    return image;
  }

  public static void showKeyboard(EditText target, Context context, boolean selectAll) {
    target.setEnabled(true);
    target.setFocusable(true);
    if (selectAll) target.selectAll();
    target.requestFocus();
    InputMethodManager imm =
        (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.showSoftInput(target, InputMethodManager.SHOW_IMPLICIT);
  }

  public static void hideKeyboard(View v, Context context) {
    if (v == null) return;
    InputMethodManager imm =
        (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.hideSoftInputFromInputMethod(v.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
  }

  public static void hideKeyboard(Activity activity) {
    View v = activity.getCurrentFocus();
    if (v == null) return;
    InputMethodManager imm =
        (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.hideSoftInputFromInputMethod(v.getWindowToken(), 0);
  }

  public static void showCustomSnackBar(View parent, LayoutInflater inflater, CharSequence text) {
    if (parent == null) return;
    Snackbar snackbar = Snackbar.make(parent, text, Snackbar.LENGTH_LONG)
        .setAction(parent.getResources().getString(R.string.close).toUpperCase(),
            new View.OnClickListener() {
              @Override public void onClick(View view) {

              }
            });

    snackbar.setActionTextColor(Color.WHITE);
    View sbView = snackbar.getView();
    sbView.setBackgroundColor(
        parent.getContext().getResources().getColor(R.color.green_dompet_ori));
    TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
    textView.setTextColor(Color.WHITE);
    snackbar.show();
  }

  public static void hideKeyboard(View focussedText, final View button) {
    if (focussedText instanceof EditText) {
      EditText editText = (EditText) focussedText;
      InputMethodManager imm =
          (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
      imm.hideSoftInputFromWindow(editText.getWindowToken(),
          InputMethodManager.RESULT_UNCHANGED_SHOWN);
      button.requestFocus();
    }
  }

  public static void sync(@NonNull Context context, @NonNull SyncPresenter presenter) {
    if (NetworkUtil.getConnectivityStatus(context) > 0) {
      presenter.syncAll();
    }
  }

  public static String populateStringToDate(Context context, Long timestamp) {
    Calendar cd = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
    Date date = new Date(timestamp);
    cd.setTime(date);
    String minute = context.getString(R.string.minute);
    String second = context.getString(R.string.second);
    String hour = context.getString(R.string.hour);
    String response = "";
    Timber.e(date + " avesina waktu " + cd + " date " + date);
    if (cd.get(Calendar.HOUR) > 0) {
      response += cd.get(Calendar.HOUR) + " " + hour + " ";
    }
    if (cd.get(Calendar.MINUTE) > 0) {
      response += cd.get(Calendar.MINUTE) + " " + minute + " ";
    }
    response += cd.get(Calendar.SECOND) + " " + second + " ";
    return response;
  }

  public static Spanned fromHtml(String html) {
    Spanned result;
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
      result = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
    } else {
      result = Html.fromHtml(html);
    }
    return result;
  }

  public static void showCustomSnackBar(View parent, LayoutInflater inflater, CharSequence text,
      int color, int gravity) {
    if (parent == null) return;
    Snackbar snackbar = Snackbar.make(parent, text, Snackbar.LENGTH_LONG)
        .setAction(parent.getResources().getString(R.string.close).toUpperCase(),
            new View.OnClickListener() {
              @Override public void onClick(View view) {

              }
            });

    snackbar.setActionTextColor(Color.WHITE);
    View sbView = snackbar.getView();
    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) sbView.getLayoutParams();
    params.gravity = gravity;
    sbView.setLayoutParams(params);
    sbView.setBackgroundColor(ContextCompat.getColor(parent.getContext(), color));
    TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
    textView.setTextColor(Color.WHITE);
    snackbar.show();
  }

  public static boolean haveNumber(final String str) {

    if (str == null || str.isEmpty()) return false;

    StringBuilder sb = new StringBuilder();
    boolean found = false;
    for (char c : str.toCharArray()) {
      if (Character.isDigit(c)) {
        sb.append(c);
        found = true;
      } else if (found) {
        // If we already found a digit before and this char is not a digit, stop looping
        break;
      }
    }

    return sb.length() >= 1;
  }

  public static void showCustomSnackBar(View parent, LayoutInflater inflater, CharSequence text,
      boolean Indeterminate, int colorAccent) {
    showCustomSnackBar(parent, inflater, text, Indeterminate, colorAccent, Gravity.BOTTOM);
  }

  public static void showCustomSnackBar(View parent, LayoutInflater inflater, CharSequence text,
      boolean Indeterminate, int colorAccent, int gravity) {
    if (parent == null) return;
    Snackbar snackbar = Snackbar.make(parent, text, Snackbar.LENGTH_LONG)
        .setAction(parent.getResources().getString(R.string.close).toUpperCase(), view -> {

        });

    if (Indeterminate) {
      snackbar.setDuration(Snackbar.LENGTH_INDEFINITE);
    }
    snackbar.setActionTextColor(Color.WHITE);
    View sbView = snackbar.getView();
    if (sbView.getLayoutParams() instanceof FrameLayout.LayoutParams) {
      FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) sbView.getLayoutParams();
      params.gravity = gravity;
      sbView.setLayoutParams(params);
    } else if (sbView.getLayoutParams() instanceof CoordinatorLayout.LayoutParams) {
      CoordinatorLayout.LayoutParams params =
          (CoordinatorLayout.LayoutParams) sbView.getLayoutParams();
      params.gravity = gravity;
      sbView.setLayoutParams(params);
    }
    sbView.setBackgroundColor(colorAccent);
    TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
    textView.setTextColor(Color.WHITE);
    textView.setMaxLines(20);
    snackbar.show();
  }

  public static void DshowCustomSnackBar(View parent, LayoutInflater inflater, CharSequence text,
      boolean Indeterminate, int colorAccent, int gravity) {
    if (parent == null) return;
    Snackbar snackbar = Snackbar.make(parent, text, Snackbar.LENGTH_LONG)
        .setAction(parent.getResources().getString(R.string.close).toUpperCase(), view -> {

        });

    if (Indeterminate) {
      snackbar.setDuration(Snackbar.LENGTH_INDEFINITE);
    }
    snackbar.setActionTextColor(Color.WHITE);
    View sbView = snackbar.getView();
    if (sbView.getLayoutParams() instanceof FrameLayout.LayoutParams) {
      FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) sbView.getLayoutParams();
      params.gravity = gravity;
      sbView.setLayoutParams(params);
    } else if (sbView.getLayoutParams() instanceof CoordinatorLayout.LayoutParams) {
      CoordinatorLayout.LayoutParams params =
          (CoordinatorLayout.LayoutParams) sbView.getLayoutParams();
      params.gravity = gravity;
      sbView.setLayoutParams(params);
    }
    sbView.setBackgroundColor(colorAccent);
    TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
    textView.setTextColor(Color.WHITE);
    textView.setMaxLines(20);
    snackbar.show();
  }

  public static void disposeSwiper(SwipeRefreshLayout refreshLayout) {
    if (refreshLayout == null) return;

    refreshLayout.setRefreshing(false);
    refreshLayout.destroyDrawingCache();
    refreshLayout.clearAnimation();
  }

  public static String generateCommaString(Object[] datas) {
    String populate = "";
    int index = 0;
    for (Object ob : datas) {
      if (index == 0) {
        populate += String.valueOf(ob);
      } else {
        populate += "," + String.valueOf(ob);
      }
      index++;
    }
    return populate;
  }

  public static void setbackgroundColor(View target, int color) {
    target.setBackgroundColor(color);
  }

  public static void goTo(Context context, Class<?> cls) {
    context.startActivity(new Intent(context, cls));
  }

  public static void goTo(Context context, Class<?> cls, Intent intent) {
    context.startActivity(intent.setClass(context, cls));
  }

  public static void finishAllPreviousActivityWithNextTarget(Context context, Class<?> cls) {
    context.startActivity(new Intent(context, cls).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
        | Intent.FLAG_ACTIVITY_CLEAR_TASK
        | Intent.FLAG_ACTIVITY_CLEAR_TOP));
  }

  public static void finishAllPreviousActivityWithNextTarget(Context context, Class<?> cls,
      Intent i) {
    context.startActivity(i.setClass(context, cls)
        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
            | Intent.FLAG_ACTIVITY_CLEAR_TASK
            | Intent.FLAG_ACTIVITY_CLEAR_TOP));
  }

  public static AlertDialog createDialog(Context context, String title, String message,
      String label_positif, String label_negatif, DialogInterface.OnClickListener positif,
      DialogInterface.OnClickListener negatif) {
    final AlertDialog.Builder builder = new AlertDialog.Builder(context);
    builder.setTitle(title);
    builder.setMessage(message);
    builder.setPositiveButton(label_positif, positif);
    builder.setNegativeButton(label_negatif, negatif);
    return builder.create();
  }

  public static void finishAllActivityWithDelay(Context context, Class<?> cls) {
    final Handler handler = new Handler();
    handler.postDelayed(new Runnable() {
      @Override public void run() {
        Helper.finishAllPreviousActivityWithNextTarget(context, cls);
      }
    }, 300);
  }

  public static void animateChangeColor(final View target, int colorFrom, int colorTo) {
    ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
    colorAnimation.setDuration(300); // milliseconds
    colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

      @Override public void onAnimationUpdate(ValueAnimator animator) {
        target.setBackgroundColor((int) animator.getAnimatedValue());
      }
    });
    colorAnimation.start();
  }

  public static void animateFade(final View target, final boolean fadeIn, Context context) {
    target.requestFocus();
    int animResId = fadeIn ? R.anim.fade_in : R.anim.fade_out;
    target.setClickable(fadeIn);
    Animation anim = AnimationUtils.loadAnimation(context, animResId);
    anim.setAnimationListener(new Animation.AnimationListener() {
      @Override public void onAnimationStart(Animation animation) {
        target.setVisibility(View.VISIBLE);
      }

      @Override public void onAnimationEnd(Animation animation) {
        if (!fadeIn) {
          target.setVisibility(View.GONE);
        }
      }

      @Override public void onAnimationRepeat(Animation animation) {

      }
    });
    target.startAnimation(anim);
  }

  public static void animateSlidingEnter(final View target, final boolean slideDown,
      Context context) {
    target.requestFocus();
    int animResId = slideDown ? R.anim.slide_in_from_top : R.anim.slide_out_to_top;
    target.setClickable(slideDown);
    Animation anim = AnimationUtils.loadAnimation(context, animResId);
    anim.setAnimationListener(new Animation.AnimationListener() {
      @Override public void onAnimationStart(Animation animation) {
        System.out.println("Helper.onAnimationStart");
        target.setVisibility(View.VISIBLE);
      }

      @Override public void onAnimationEnd(Animation animation) {
        System.out.println("Helper.onAnimationEnd: " + slideDown);
        if (!slideDown) {
          target.setVisibility(View.GONE);
        }
      }

      @Override public void onAnimationRepeat(Animation animation) {
        System.out.println("Helper.onAnimationRepeat");
      }
    });
    target.startAnimation(anim);
  }

  public static void checkIfBlank(BlankView bv, boolean isEmpty) {
    if (isEmpty) {
      bv.show(true);
    } else {
      bv.dispose();
    }
  }

  public static void checkIfBlank(BlankView bv, boolean isEmpty,
      View.OnClickListener onClickListener) {
    if (isEmpty) {
      bv.show(true);
      bv.showFloatingButton(onClickListener);
    } else {
      bv.dispose();
    }
  }

  public static void checkIfZero(View view, boolean isEmpty, View.OnClickListener action) {
    view.setOnClickListener(action);
    if (isEmpty) {
      view.setVisibility(View.VISIBLE);
    } else {
      view.setVisibility(View.GONE);
    }
  }

  public static void animateGrowing(View target) {
    ViewCompat.animate(target).scaleX(1.11f).scaleY(1.11f).setDuration(300).start();
  }

  public static void animateShrinking(View target) {
    ViewCompat.animate(target).scaleX(1).scaleY(1).setDuration(300).start();
  }

  public static void openInfoDialog(FragmentManager fragmentManager) {
    InformationDialog dialog = new InformationDialog();
    dialog.show(fragmentManager, "info-dialog");
  }

  public static AdvancedDialog showAdvancedDialog(FragmentManager manager,
      AdvancedDialog.ABuilder builder) {
    AdvancedDialog dialog = AdvancedDialog.newInstance(builder, null);
    dialog.show(manager, "adv-dialog");
    return dialog;
  }

  public static void showAdvancedDialog(FragmentManager manager, AdvancedDialog.ABuilder builder,
      Intent target) {
    AdvancedDialog dialog = AdvancedDialog.newInstance(builder, target);
    dialog.show(manager, "adv-dialog");
  }

  public static void showSimpleInformationDialog(FragmentManager manager, String title,
      String message) {
    SimpleInformationDialog dialog = SimpleInformationDialog.newInstance(title, message);
    dialog.show(manager, "simple-dialog");
  }

  public static void showDisclaimerDialogWithTitle(FragmentManager manager, String title,
      String topMessage) {
    DisclaimerDialog dialog = DisclaimerDialog.newInstance(topMessage, null);
    dialog.setTitle(title);
    dialog.show(manager, "disclaimer-dialog");
  }

  public static void showDisclaimerDialog(FragmentManager manager, String topMessage,
      String footerMessage) {
    DisclaimerDialog dialog = DisclaimerDialog.newInstance(topMessage, footerMessage);
    dialog.show(manager, "disclaimer-dialog");
  }

  public static void showDisclaimerDialog(FragmentManager manager, String topMessage,
      String footerMessage, String link) {
    DisclaimerDialog dialog = DisclaimerDialog.newInstance(topMessage, footerMessage, link);
    dialog.show(manager, "disclaimer-dialog");
  }

  public static void setTextSizeToFit(boolean fixSize, TextView textView) {
    if (fixSize) {
      correctWidth(textView, textView.getWidth() - 2);
    } else {
      textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textView.getTextSize());
    }
  }

  private static void correctWidth(TextView textView, int desiredWidth) {
    Paint paint = new Paint();
    Rect bounds = new Rect();

    paint.setTypeface(textView.getTypeface());
    float textSize = textView.getTextSize();
    paint.setTextSize(textSize);
    String text = textView.getText().toString();
    paint.getTextBounds(text, 0, text.length(), bounds);

    while (bounds.width() > desiredWidth) {
      Timber.e("bound width " + bounds.width() + " > " + desiredWidth);
      textSize--;
      paint.setTextSize(textSize);
      paint.getTextBounds(text, 0, text.length(), bounds);
    }

    textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
  }

  public static String cleanPhoneNumber(String phone) {
    return phone.replaceAll("[\\s\\-()]", "");
  }

  public static HashMap<String, String> populatePeriodicString(String every) {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    Calendar calendar = Calendar.getInstance();
    int this_week_of_year = calendar.get(Calendar.WEEK_OF_YEAR);
    HashMap<String, String> map = new HashMap<>();
    switch (every) {
      case THIS_WEEK:
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        map.put(DATE_START, simpleDateFormat.format(calendar.getTime()));
        calendar.add(Calendar.DAY_OF_YEAR, 6);
        map.put(DATE_END, simpleDateFormat.format(calendar.getTime()));
        break;
      case THIS_MONTH:
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        map.put(DATE_START, simpleDateFormat.format(calendar.getTime()));
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        map.put(DATE_END, simpleDateFormat.format(calendar.getTime()));
        break;
      case QUATER_MONTH:
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        map.put(DATE_START, simpleDateFormat.format(calendar.getTime()));
        calendar.add(Calendar.MONTH, 3);
        calendar.set(calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        map.put(DATE_END, simpleDateFormat.format(calendar.getTime()));
        break;
      case NEXT_MONTH:
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        map.put(DATE_START, simpleDateFormat.format(calendar.getTime()));
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        map.put(DATE_END, simpleDateFormat.format(calendar.getTime()));
        break;
      case WEEKLY:
        calendar.set(Calendar.WEEK_OF_YEAR, this_week_of_year);
        calendar.set(Calendar.DAY_OF_WEEK, 1);
        map.put(DATE_START, simpleDateFormat.format(calendar.getTime()));
        calendar.add(Calendar.DAY_OF_YEAR, 6);
        map.put(DATE_END, simpleDateFormat.format(calendar.getTime()));
        break;
      case BIWEEKLY:
        calendar.set(Calendar.WEEK_OF_YEAR, this_week_of_year);
        calendar.set(Calendar.DAY_OF_WEEK, 1);
        map.put(DATE_START, simpleDateFormat.format(calendar.getTime()));
        calendar.add(Calendar.WEEK_OF_YEAR, 2);
        map.put(DATE_END, simpleDateFormat.format(calendar.getTime()));
        break;
      case MONTHLY:
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        map.put(DATE_START, simpleDateFormat.format(calendar.getTime()));
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        map.put(DATE_END, simpleDateFormat.format(calendar.getTime()));
        break;
      case YEARLY:
        calendar.set(Calendar.DAY_OF_YEAR, 1);
        map.put(DATE_START, simpleDateFormat.format(calendar.getTime()));
        calendar.set(Calendar.DAY_OF_YEAR, calendar.getActualMaximum(Calendar.DAY_OF_YEAR));
        map.put(DATE_END, simpleDateFormat.format(calendar.getTime()));
        break;
      case A_YEAR_AGO:
        calendar.add(Calendar.YEAR, -1);
        calendar.set(Calendar.DAY_OF_YEAR, 1);
        map.put(DATE_START, simpleDateFormat.format(calendar.getTime()));
        calendar.set(Calendar.DAY_OF_YEAR, calendar.getActualMaximum(Calendar.DAY_OF_YEAR));
        map.put(DATE_END, simpleDateFormat.format(calendar.getTime()));
        break;
      case A_MONTH_AGO:
        calendar.add(Calendar.MONTH, -1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        map.put(DATE_START, simpleDateFormat.format(calendar.getTime()));
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        map.put(DATE_END, simpleDateFormat.format(calendar.getTime()));
        break;
      case THREE_MONTHS_AGO:
        calendar.add(Calendar.MONTH, -3);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        map.put(DATE_START, simpleDateFormat.format(calendar.getTime()));
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        map.put(DATE_END, simpleDateFormat.format(calendar.getTime()));
        break;
      case THREE_MONTHS_LAST_AGO:
        calendar.add(Calendar.MONTH, -1);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        map.put(DATE_END, simpleDateFormat.format(calendar.getTime()));
        calendar.add(Calendar.MONTH, -3);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        map.put(DATE_START, simpleDateFormat.format(calendar.getTime()));
        break;
      case SIX_MONTHS_AGO:
        calendar.add(Calendar.MONTH, -6);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        map.put(DATE_START, simpleDateFormat.format(calendar.getTime()));
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        map.put(DATE_END, simpleDateFormat.format(calendar.getTime()));
        break;
      case SIX_MONTHS_LAST_AGO:
        calendar.add(Calendar.MONTH, -1);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        map.put(DATE_END, simpleDateFormat.format(calendar.getTime()));
        calendar.add(Calendar.MONTH, -6);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        map.put(DATE_START, simpleDateFormat.format(calendar.getTime()));
        break;
    }
    return map;
  }

  public static HashMap<String, String> populatePeriodicCustome(Date date_start, Date date_end,
      boolean is_repeat) {
    HashMap<String, String> map = new HashMap<>();
    if (is_repeat) {
      Calendar calendar = Calendar.getInstance();
      if (date_end.getTime() < calendar.getTime().getTime()) {
        calendar.setTime(date_start);
        calendar.add(Calendar.YEAR, 1);
        map.put(DATE_START, setSimpleDateFormat(calendar.getTime(), "yyyy-MM-dd"));
        calendar.setTime(date_end);
        calendar.add(Calendar.YEAR, 1);
        map.put(DATE_END, setSimpleDateFormat(calendar.getTime(), "yyyy-MM-dd"));
        return map;
      }
    }
    map.put(DATE_START, setSimpleDateFormat(date_start, "yyyy-MM-dd"));
    map.put(DATE_END, setSimpleDateFormat(date_end, "yyyy-MM-dd"));
    return map;
  }

  public static String getDateNow() {
    Calendar calendar = Calendar.getInstance();
    return setSimpleDateFormat(calendar.getTime(), "yyyy-MM-dd HH:mm:ss");
  }

  public static String getMonthName(int month, boolean shortMonth) {
    if (!shortMonth) {
      return MONTHS[month];
    } else {
      return MONTHS_SHORT[month];
    }
  }

  public static int accentColor(Context context, double value) {
    if (value == 0) {
      return Color.BLACK;
    }
    return value >= 0 ? context.getResources().getColor(R.color.green_dompet_ori)
        : context.getResources().getColor(R.color.red_500);
  }

  public static int accentColor(Context context, double valueMin, double valueMax) {
    return valueMin < valueMax ? context.getResources().getColor(R.color.green_dompet_ori)
        : context.getResources().getColor(R.color.red_500);
  }

  public static void openPDFFiles(Context context) {
    AssetManager assetManager = context.getAssets();

    InputStream in = null;
    OutputStream out = null;
    File file = new File(
        FILE_CHART);//here schedule1.pdf is the pdf file name which is keep in assets folder.
    try {
      in = assetManager.open(FILE_CHART);
      out = context.openFileOutput(file.getName(), Context.MODE_WORLD_READABLE);
      copyFile(in, out);
      in.close();
      in = null;
      out.flush();
      out.close();
      out = null;
    } catch (Exception e) {
      Timber.e("tag" + e.getMessage());
    }

    Intent intent = new Intent(Intent.ACTION_VIEW);
    intent.setDataAndType(Uri.parse("file://" + FILE_CHART), "application/pdf");

    context.startActivity(intent);
  }

  public static String combinePlural(String[] words) {
    int length = words.length;
    int index = 1;
    String combine = "";
    for (String word : words) {
      if (index > 1) {
        if (index == length) {
          combine += " & " + word;
        } else {
          combine += ", " + word;
        }
      } else {
        combine += word;
      }
      index++;
    }
    return combine;
  }

  public static void openFileCSV(Context context) {
    File file = new File(FILE_CSV);
    Uri path = Uri.fromFile(file);
    Intent pdfOpenintent = new Intent(Intent.ACTION_VIEW);
    pdfOpenintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    pdfOpenintent.setDataAndType(path, "application/text");
    try {
      context.startActivity(pdfOpenintent);
    } catch (ActivityNotFoundException e) {

    }
  }

  private static void copyFile(InputStream in, OutputStream out) throws IOException

  {
    byte[] buffer = new byte[1024];
    int read;
    while ((read = in.read(buffer)) != -1)

    {
      out.write(buffer, 0, read);
    }
  }

  public static Bitmap getBitmapFromView(View view, int h, int w) {
    //Define a bitmap with the same size as the view
    //Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),Bitmap.Config.ARGB_8888);
    Bitmap returnedBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
    //Bind a canvas to it
    Canvas canvas = new Canvas(returnedBitmap);
    //Get the view's background
    Drawable bgDrawable = view.getBackground();
    if (bgDrawable != null)
    //has background drawable, then draw it on the canvas
    {
      bgDrawable.draw(canvas);
    } else
    //does not have background drawable, then draw white background on the canvas
    {
      canvas.drawColor(Color.WHITE);
    }
    // draw the view on the canvas
    view.draw(canvas);
    //return the bitmap
    return returnedBitmap;
  }

  private static void addImage(Document document, byte[] byteArray) {
    Image image = null;
    try {
      image = Image.getInstance(byteArray);
    } catch (BadElementException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (MalformedURLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    //image.scaleAbsolute(document.getPageSize().getHeight(),document.getPageSize().getHeight());
    try {
      document.add(image);
    } catch (DocumentException e) {
      // TODO Auto-generated catch block
    }
  }

  public static String[] merge(String[] a, String[] b) {
    Set<Object> set = new HashSet<>(Arrays.asList(a));
    set.addAll(Arrays.asList(b)); // skips duplicate as per Set implementation
    set.removeAll(Arrays.asList("", null));
    return set.toArray(new String[0]);
  }

  public static void deleteDB(Context context) {
    boolean result = context.deleteDatabase(DbHelper.DATABASE_NAME);
    if (result == true) {
      Toast.makeText(context, "DB Deleted!", Toast.LENGTH_LONG).show();
    }
  }

  public static void exportDB(Context context) {
    File sd = Environment.getExternalStorageDirectory();
    File data = Environment.getDataDirectory();
    FileChannel source = null;
    FileChannel destination = null;
    String currentDBPath =
        "/data/" + context.getPackageName() + "/databases/" + DbHelper.DATABASE_NAME;
    String backupDBPath = DbHelper.DATABASE_NAME;
    File currentDB = new File(data, currentDBPath);
    File backupDB = new File(sd, backupDBPath);
    try {
      source = new FileInputStream(currentDB).getChannel();
      destination = new FileOutputStream(backupDB).getChannel();
      destination.transferFrom(source, 0, source.size());
      source.close();
      destination.close();
      Toast.makeText(context, "DB Exported!", Toast.LENGTH_LONG).show();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void writeToFile(String data) {
    final File file = new File(FILE_CSV);

    // Save your stream, don't forget to flush() it before closing it.

    try {
      file.createNewFile();
      FileOutputStream fOut = new FileOutputStream(file);
      OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
      myOutWriter.append(data);

      myOutWriter.close();

      fOut.flush();
      fOut.close();
    } catch (IOException e) {
      Timber.e("Exception " + "File write failed: " + e.toString());
    }
  }

  public static String ArrayToCSV(ArrayList<Cash> cashes) {
    String data = "";
    for (Cash cash : cashes) {
      data += cash.getId()
          + ","
          + cash.getCash_id()
          + ","
          + cash.getCash_date()
          + ","
          + cash.getNote()
          + ","
          + cash.getCashflow_rename()
          + ","
          + cash.getDescription()
          + ","
          + cash.getType()
          + ","
          + cash.getAmount()
          + ","
          + cash.getCash_tag()
          + ","
          + cash.getCreated_at()
          + ","
          + cash.getUpdated_at()
          + ","
          + cash.getDeleted_at()
          + ","
          + cash.getCategory_id();
      data += "\n";
    }
    return data;
  }

  public static String exportCSV(Cursor curCSV, String filename) {
    String path = null;
    File exportDir = new File(Environment.getExternalStorageDirectory(), "DompetSehat");
    if (!exportDir.exists()) {
      exportDir.mkdirs();
    }
    File file = new File(exportDir, filename + ".csv");
    try {
      file.createNewFile();
      CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
      csvWrite.writeNext(curCSV.getColumnNames());
      if (curCSV.moveToFirst()) {
        do {
          //Which column you want to exprort
          String arrStr[] = new String[curCSV.getColumnNames().length];
          int i = 0;
          for (String name : curCSV.getColumnNames()) {
            String data = curCSV.getString(curCSV.getColumnIndex(name));
            arrStr[i] = data;
            i++;
          }
          Timber.e(" " + arrStr);
          csvWrite.writeNext(arrStr);
        } while (curCSV.moveToNext());
      }
      path = file.getPath();
      csvWrite.close();
      curCSV.close();
    } catch (Exception e) {
      Timber.e("Helper " + e);
    }
    return path;
  }

  public static String exportToExcel(Cursor cursor, String filename) {
    String path = null;
    File exportDir = new File(Environment.getExternalStorageDirectory(), "DompetSehat");
    if (!exportDir.exists()) {
      exportDir.mkdirs();
    }
    File file = new File(exportDir, filename + ".xls");

    WorkbookSettings wbSettings = new WorkbookSettings();
    wbSettings.setLocale(new Locale("en", "EN"));
    WritableWorkbook workbook;

    try {
      workbook = Workbook.createWorkbook(file, wbSettings);
      //Excel sheet name. 0 represents first sheet
      WritableSheet sheet = workbook.createSheet("DompetSehat", 0);
      try {
        int i = 0;
        for (String name : cursor.getColumnNames()) {
          sheet.addCell(new Label(i, 0, name));
          i++;
        }
        int a = 0;
        if (cursor.moveToFirst()) {
          do {
            int j = 1;
            for (String name : cursor.getColumnNames()) {
              String data = cursor.getString(cursor.getColumnIndex(name));
              sheet.addCell(new Label(j, a, data));
              j++;
            }
            a++;
          } while ((cursor.moveToNext()));
        }
        path = file.getPath();
        cursor.close();
      } catch (RowsExceededException e) {
        e.printStackTrace();
      } catch (WriteException e) {
        e.printStackTrace();
      }
      workbook.write();
      try {
        workbook.close();
      } catch (WriteException e) {
        e.printStackTrace();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return path;
  }

  public static String exportPDF(Cursor curCSV, String filename) {
    String path = null;
    File exportDir = new File(Environment.getExternalStorageDirectory(), "DompetSehat");
    if (!exportDir.exists()) {
      exportDir.mkdirs();
    }
    try {
      File file = new File(exportDir, filename + ".pdf");
      Document document = new Document(new Rectangle(842, 595), 20, 20, 20, 20);
      PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(file));
      document.open();
      document.addTitle(filename);
      String table = "<html><body><table><tr>";
      for (String name : curCSV.getColumnNames()) {
        table += "<th>" + name + "</th>";
      }
      table += "</tr>";
      if (curCSV.moveToFirst()) {
        while (curCSV.moveToNext()) {
          Timber.e(" table " + table);
          table += "<tr>";
          for (String name : curCSV.getColumnNames()) {
            String data = curCSV.getString(curCSV.getColumnIndex(name));
            table += "<td>" + data + "</td>";
          }
          table += "</tr>";
        }
      }
      table += "</table></body></html>";
      InputStream is = new ByteArrayInputStream(table.getBytes());
      XMLWorkerHelper.getInstance().parseXHtml(writer, document, is);
      path = file.getPath();
      document.close();
    } catch (Exception e) {
      Timber.e("Helper " + e);
    }
    return path;
  }

  public static void exportPDF(Bitmap screen, int h, int w) {

    try {
      Document document = new Document();

      PdfWriter.getInstance(document, new FileOutputStream(FILE_CHART));
      ByteArrayOutputStream stream = new ByteArrayOutputStream();
      screen.compress(Bitmap.CompressFormat.PNG, 100, stream);
      byte[] byteArray = stream.toByteArray();
      Timber.e("H:" + h + " W:" + w);
      Rectangle size = new Rectangle(w, h);
      document.setPageSize(size);
      document.open();
      addImage(document, byteArray);
      document.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static String formatFloatToStringNonDecimal(float value) {
    return String.format("%.0f", value);
  }

  public static String imploded(Object[] objects, String delim) {
    String out = "";
    for (int i = 0; i < objects.length; i++) {
      if (i != 0) {
        out += delim;
      }
      out += objects[i].toString();
    }
    return out;
  }

  public static String formatFloatToStringNonDecimal(double value) {
    DecimalFormat format = new DecimalFormat();
    format.setDecimalSeparatorAlwaysShown(false);
    return format.format(value);
  }

  public static String ListCashFlowToString(ArrayList<Cash> listcash, String last_sync) {
    String encodedBytes = null;
    JSONArray jsonArray = new JSONArray();
    for (int i = 0; i < listcash.size(); i++) {
      try {
        JSONObject cashflow = new JSONObject();
        cashflow.put(DbHelper.ID, listcash.get(i).getId());
        cashflow.put(DbHelper.CASHFLOW_ID, listcash.get(i).getCash_id());
        cashflow.put(DbHelper.PRODUCT_ID, listcash.get(i).getProduct_id());
        cashflow.put(DbHelper.CASHFLOW_NAME, listcash.get(i).getDescription());
        cashflow.put(DbHelper.CASHFLOW_RENAME, listcash.get(i).getCashflow_rename());
        cashflow.put(DbHelper.CASHFLOW_NOTE, listcash.get(i).getNote());
        cashflow.put(DbHelper.CASHFLOW_DATE, listcash.get(i).getCash_date());
        cashflow.put(DbHelper.CASHFLOW_TAG, listcash.get(i).getCash_tag());
        cashflow.put(DbHelper.CATEGORY_ID, listcash.get(i).getCategory_id());
        cashflow.put(DbHelper.HARI, listcash.get(i).getHari());
        cashflow.put(DbHelper.TANGGAL, listcash.get(i).getTanggal());
        cashflow.put(DbHelper.BULAN, listcash.get(i).getBulan());
        cashflow.put(DbHelper.TAHUN, listcash.get(i).getTahun());
        cashflow.put(DbHelper.AMOUNT, listcash.get(i).getAmount());
        cashflow.put(DbHelper.JENIS_MUTASI, listcash.get(i).getType());
        cashflow.put(DbHelper.CREATED_AT, listcash.get(i).getCreated_at());
        cashflow.put(DbHelper.UPDATED_AT, listcash.get(i).getUpdated_at());
        cashflow.put(DbHelper.DELETED_AT, listcash.get(i).getDeleted_at());
        cashflow.put(DbHelper.STATUS, listcash.get(i).getStatus());
        cashflow.put(DbHelper.LAST_SYNC_CASH, converterEpoc(last_sync));
        jsonArray.put(i, cashflow);
      } catch (Exception e) {

      }
    }

    JSONObject cashflowObj = new JSONObject();
    try {
      cashflowObj.put("cashflow", jsonArray);
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      encodedBytes =
          Base64.encodeToString(cashflowObj.toString().getBytes("UTF-8"), Base64.DEFAULT);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return encodedBytes;
  }

  public static String converterEpochToDate(Long epoch) {
        /*return new SimpleDateFormat("yyyy-MM-dd kk:mm:ss")
                .format(new java.util.Date (epoch*1000));*/
    Date date = new Date(epoch * 1000L); // *1000 is to convert seconds to milliseconds
    SimpleDateFormat sdf =
        new SimpleDateFormat("yyyy-MM-dd kk:mm:ss", State.getLocale()); // the format of your date
    sdf.setTimeZone(TimeZone.getTimeZone(
        "GMT")); // give a timezone reference for formating (see comment at the bottom
    return sdf.format(date);
  }

  public static Long converterEpoc(String lc) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss zzz", State.getLocale());
    Date date = null;
    try {
      date = sdf.parse(lc + " GMT");
    } catch (ParseException e) {
      e.printStackTrace();
    }
    long timeInMillisSinceEpoch = date != null ? date.getTime() : 0;

    return timeInMillisSinceEpoch / 1000;
  }

  public static String setSimpleDateFormat(Date date, String form) {
    SimpleDateFormat sdf = new SimpleDateFormat(form, State.getLocale());
    return sdf.format(date);
  }

  public static Date setInputFormatter(String form, String date) {
    DateFormat inputFormatter = new SimpleDateFormat(form, State.getLocale());
    try {
      return inputFormatter.parse(date);
    } catch (ParseException e) {

    }
    return null;
  }

  public static <T> boolean isEmptyObject(T anyObject) {
    return anyObject == null;
  }

  public static void showError(Context context, String[] messages) {
    for (String msg : messages) {
      Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }
  }

  public static void shareWA(Context context, String kode_referral) {
    Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
    whatsappIntent.setType("text/plain");
    whatsappIntent.setPackage("com.whatsapp");
    whatsappIntent.putExtra(Intent.EXTRA_TEXT, kode_referral);
    try {
      context.startActivity(whatsappIntent);
    } catch (android.content.ActivityNotFoundException ex) {
      Toast.makeText(context, "Applikasi Whatsapp belum terinstall", Toast.LENGTH_LONG).show();
    }
  }

  public static void shareText(Context context, String kode_referral) {
    String title =
        "Yuk, ikutan Reksa Dana Manulife melalui DompetSehat – Kode Referensi " + kode_referral;
    String text = "Hai!\n";
    text += "\n";
    text += "Sudah kenal Reksa Dana Manulife dan DompetSehat?\n";
    text += "\n";
    text +=
        "DompetSehat adalah aplikasi pengelolaan pribadi, yang bekerja sama dengan Reksa Dana Manulife sebagai penyedia solusi investasi. Di Reksa Dana Manulife, kita bisa berinvestasi sekaligus mendapatkan komisi referral. \n";
    text += "\n";
    text +=
        "Sebagai investor, kita bisa menikmati potensi pertumbuhan kekayaan melalui investasi di pasar modal. Sedangkan dengan mengajak teman-teman lain ikut serta, kita bisa menikmati Komisi Referral. Seru kan?\n";
    text += "\n";
    text += "Caranya mudah :\n";
    text +=
        "1. \t\t\tDownload aplikasi DompetSehat di Play Store, lalu daftar jadi pengguna DompetSehat.\n";
    text +=
        "2. \t\t\tMasuk ke menu My Referral dan Register jadi investor Reksa Dana Manulife (klikMAMI). Jangan lupa masukkan Kode Referral saya yaitu "
            + kode_referral
            + "\n";
    text +=
        "3. \t\t\tDapatkan dan segera bagikan Kode Referral Anda ke sebanyak mungkin teman dan kenalan Anda\n";
    text += "\n\nIngin tahu lebih jauh?";
    //text += "\n\nklik "+"http://play.google.com/store/apps/details?id="+context.getPackageName();
    text += "\n\nklik " + "https://dompetsehat.com/referral/" + kode_referral;
    text += "\n\nSalam investasi!";

    Intent shareIntent = new Intent(Intent.ACTION_SEND);
    shareIntent.setType("text/plain");
    shareIntent.putExtra(Intent.EXTRA_SUBJECT, title);
    shareIntent.putExtra(Intent.EXTRA_TEXT, text);
    Helper.trackThis(context, "user klik tombol bagikan Kode Referral");
    context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.share_via)));
  }

  public static boolean isLegalPassword(String pass) {
    Timber.e("password " + pass);
    //(?=.*[0-9]) a digit must occur at least once
    //(?=.*[a-z]) a lower case letter must occur at least once
    //(?=.*[A-Z]) an upper case letter must occur at least once
    //(?=.*[@#$%^&+=]) a special character must occur at least once
    //(?=\\S+$) no whitespace allowed in the entire string
    //.{8,} at least 8 characters
    String pattern = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,}";
    if (pass.length() < 8 || pass.length() > 12) {
      return false;
    }
    if (!pass.matches(pattern)) {
      return false;
    }
    return true;
  }

  public static String getDeviceId(Context context) {
    if (!BuildConfig.DEBUG_MODE) {
      return "0";
    }
    TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    String device_id = tm.getDeviceId();
    System.out.println("Helper.getDeviceId " + device_id);
    return device_id;
  }

  public static void trackThis(Context activity, String screenName) {
    MyCustomApplication.sendEvent(activity, "general", screenName);
  }

  public static String DecimalToString(double nilai, Integer n) {
    if (n != null) {
      return String.format("%." + n + "f", nilai);
    } else {
      return String.valueOf(nilai);
    }
  }

  public static boolean isNull(String ob) {
    if (ob == null) {
      return true;
    } else if (ob.equalsIgnoreCase("")) {
      return true;
    } else if (ob.equalsIgnoreCase("null")) {
      return true;
    } else {
      return false;
    }
  }

  public static String[] UCWord(String[] words) {
    String[] ns = new String[words.length];
    int index = 0;
    for (String word : words) {
      String n = UCWord(word);
      ns[index] = n;
      index++;
    }
    return ns;
  }

  public static String UCWord(String word) {
    if (word.length() >= 2) {
      return Character.toUpperCase(word.charAt(0)) + word.substring(1);
    } else if (word.length() == 1) {
      return Character.toUpperCase(word.charAt(0)) + "";
    } else {
      return word;
    }
  }

  public static String randomString(int MAX_LENGTH) {
    String ALLOWED_CHARACTERS = "0123456789qwertyuiopasdfghjklzxcvbnm";
    Random random = new Random();
    StringBuilder sb = new StringBuilder(MAX_LENGTH);
    for (int i = 0; i < MAX_LENGTH; ++i) {
      sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
    }
    return sb.toString();
  }

  public static int randomInt(int MAX_LENGTH) {
    String s_max = "9";
    String s_min = "1";
    for (int i = 1; i < MAX_LENGTH; i++) {
      s_max += "9";
      s_min += "1";
    }
    Timber.e(s_max + "-" + s_min);
    int max = Integer.valueOf(s_max);
    int min = Integer.valueOf(s_min);
    Random r = new Random();
    int i1 = r.nextInt(max - min) + min;
    return i1;
  }

  public static void sendNotification(Context context, String message, String title,
      Boolean autocancel) {
    if (TextUtils.isEmpty(title)) {
      title = "DompetSehat Notification";
    }
    if (autocancel = null) {
      autocancel = true;
    }
    NotificationManager mNotificationManager;
    mNotificationManager =
        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
    mBuilder.setContentTitle(title);
    mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
    mBuilder.setContentText(message);
    long[] pattern = { 0, 600, 200, 400, 200 };
    mBuilder.setVibrate(pattern);
    mBuilder.setSmallIcon(R.drawable.icon_dompet);
    mBuilder.setAutoCancel(autocancel);
    mBuilder.build().flags |= Notification.FLAG_AUTO_CANCEL;
    mNotificationManager.notify(NewMainActivity.SERVICEREMINDER, mBuilder.build());
  }

  public static String BitmapToBase64String(Bitmap bitmap) {
    int MAX_BYTES_IMAGE = 1194304;
    ByteArrayOutputStream out;
    int quality = 80;
    try {
      do {
        out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out);
        quality -= 5;
        Timber.e("size " + out.size());
      } while (out.size() > MAX_BYTES_IMAGE);
      out.close();
      byte[] byteArray = out.toByteArray();
      return Base64.encodeToString(byteArray, Base64.DEFAULT);
    } catch (Exception e) {
      Timber.e("ERRROR " + e);
    }
    return null;
  }

  public static Bitmap getBitmapFromURL(String src) {
    try {
      URL url = new URL(src);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setDoInput(true);
      connection.connect();
      InputStream input = connection.getInputStream();
      Bitmap myBitmap = BitmapFactory.decodeStream(input);
      return myBitmap;
    } catch (IOException e) {
      // Log exception
      return null;
    }
  }

  public static int getAccentColor(Context context, int state) {
    if (state == State.OVERVIEW_DETAIL_EXPENSE) {
      return ContextCompat.getColor(context, R.color.red_500);
    }
    return ContextCompat.getColor(context, R.color.green_dompet_ori);
  }

  public static String getDifferenceInDays(Date endDate, Date startDate) {
    long different = endDate.getTime() - startDate.getTime();

    System.out.println("startDate : " + startDate);
    System.out.println("endDate : " + endDate);
    System.out.println("different : " + different);

    long secondsInMilli = 1000;
    long minutesInMilli = secondsInMilli * 60;
    long hoursInMilli = minutesInMilli * 60;
    long daysInMilli = hoursInMilli * 24;

    long elapsedDays = different / daysInMilli;
    different = different % daysInMilli;

    long elapsedHours = different / hoursInMilli;
    different = different % hoursInMilli;

    long elapsedMinutes = different / minutesInMilli;
    different = different % minutesInMilli;

    long elapsedSeconds = different / secondsInMilli;

    System.out.printf("%d days, %d hours, %d minutes, %d seconds%n", elapsedDays, elapsedHours,
        elapsedMinutes, elapsedSeconds);

    return elapsedDays + "hari";
  }

  public static void openDialogPreAddBudget(FragmentActivity fragmentActivity, Class c) {

    final MenuDialog menuDialog = MenuDialog.newInstance("Pilihan", new String[] {
        "Tambah Anggaran Per Kategori", "Tambah Anggaran Sekaligus"
    });

    menuDialog.show(fragmentActivity.getSupportFragmentManager(), "menu_dialog");
    menuDialog.simpleModeOn();
    menuDialog.setOnMenuDialogClick(new MenuDialog.OnMenuDialogClick() {
      @Override public void onOkClick(String item, int pos) {
        //keep empty
      }

      @Override public void onMenuClick(String label, int pos, Dialog dialog) {
        switch (pos) {
          case 0:
            Intent in = new Intent(fragmentActivity, c).putExtra(Words.FLAG, State.FROM_TIMELINE)
                .putExtra(Budget.MODE, Budget.MODE_ADD);
            fragmentActivity.startActivityForResult(in, State.FROM_TIMELINE);
            break;
          case 1:
            Intent in2 =
                new Intent(fragmentActivity, BudgetSetupActivity.class).putExtra(Words.FLAG,
                    State.FROM_TIMELINE).putExtra(Budget.MODE, Budget.MODE_ADD);
            fragmentActivity.startActivityForResult(in2, State.FROM_TIMELINE);
            break;
        }
        menuDialog.dismissAllowingStateLoss();
      }
    });
  }

  public static void sendNotification(int id, Context context, PendingIntent intent, String title,
      String message) {
    NotificationManager mNotificationManager =
        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
    mBuilder.setContentTitle(title);
    mBuilder.setContentText(message);
    long[] pattern = { 0, 600, 200, 400, 200 };
    mBuilder.setVibrate(pattern);

    mBuilder.setSmallIcon(R.drawable.icon_dompet);
    NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
    bigTextStyle.setBigContentTitle("Notification");
    bigTextStyle.bigText(message);

    mBuilder.setStyle(bigTextStyle);
    mBuilder.setVibrate(new long[] { -1 });

    mBuilder.setContentIntent(intent);
    mBuilder.setAutoCancel(true);
    mNotificationManager.notify(id, mBuilder.build());
  }

  //public static boolean isConnected(Context context) {
  //  ConnectivityManager cm =
  //      (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
  //  NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
  //  return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
  //}

  public static boolean checkList(List<?> datas) {
    if (datas != null) {
      if (datas.size() > 0) {
        return true;
      }
    }
    return false;
  }

  public static void deleteWithConfirmation(View anchorview, Activity activity,
      View.OnClickListener onClickListener,
      BaseTransientBottomBar.BaseCallback<Snackbar> callback) {
    Snackbar snackbar =
        Snackbar.make(anchorview, activity.getString(R.string.has_delete), Snackbar.LENGTH_LONG)
            .setAction(activity.getString(R.string.undo), onClickListener);
    snackbar.setActionTextColor(Color.WHITE);
    View sbView = snackbar.getView();
    if (sbView.getLayoutParams() instanceof CoordinatorLayout.LayoutParams) {
      CoordinatorLayout.LayoutParams params =
          (CoordinatorLayout.LayoutParams) sbView.getLayoutParams();
      sbView.setLayoutParams(params);
    }
    if (sbView.getLayoutParams() instanceof FrameLayout.LayoutParams) {
      FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) sbView.getLayoutParams();
      sbView.setLayoutParams(params);
    }
    sbView.setBackgroundColor(ContextCompat.getColor(activity, R.color.red_300));
    TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
    textView.setTextColor(Color.WHITE);
    snackbar.addCallback(callback);
    snackbar.show();
  }

  public static void deleteWithConfirmationMessage(View anchorview, Activity activity,
      String message, View.OnClickListener onClickListener,
      BaseTransientBottomBar.BaseCallback<Snackbar> callback) {
    Snackbar snackbar = Snackbar.make(anchorview, message, Snackbar.LENGTH_LONG)
        .setAction(activity.getString(R.string.undo), onClickListener);
    snackbar.setActionTextColor(Color.WHITE);
    View sbView = snackbar.getView();
    if (sbView.getLayoutParams() instanceof CoordinatorLayout.LayoutParams) {
      CoordinatorLayout.LayoutParams params =
          (CoordinatorLayout.LayoutParams) sbView.getLayoutParams();
      sbView.setLayoutParams(params);
    }
    if (sbView.getLayoutParams() instanceof FrameLayout.LayoutParams) {
      FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) sbView.getLayoutParams();
      sbView.setLayoutParams(params);
    }
    sbView.setBackgroundColor(ContextCompat.getColor(activity, R.color.red_300));
    TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
    textView.setTextColor(Color.WHITE);
    snackbar.addCallback(callback);
    snackbar.show();
  }

  public static void moveViewUpIfKeyboardAppear(final View activityRootView,
      final ScrollView scrollView, final View scrolledContent) {
    scrollView.setSmoothScrollingEnabled(true);
    activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
      int heightView = activityRootView.getHeight();
      int widthView = activityRootView.getWidth();
      double value = 1.0 * widthView / heightView;

      boolean isHiding = value < 1.0;
      //boolean isScrollable = scrollView.getHeight()
      //    < scrolledContent.getHeight()
      //    + scrollView.getPaddingTop()
      //    + scrollView.getPaddingBottom();

      int viewHeight = scrollView.getMeasuredHeight();
      int contentHeight = scrollView.getChildAt(0).getHeight();
      boolean isScrollable = viewHeight - contentHeight < 0;

      if (!isHiding && isScrollable) {
        ObjectAnimator.ofInt(scrollView, "scrollY", scrolledContent.getHeight() / 2)
            .setDuration(500)
            .start();
      }
    });
  }

  public static void setDefaultFontPasswordHint(TransformationMethod method, TextView... fields) {
    for (TextView field : fields) {
      field.setTypeface(Typeface.DEFAULT);
      field.setTransformationMethod(method);
    }
  }

  public static int randInt(int min, int max) {
    Random rand = new Random();
    int randomNum = rand.nextInt((max - min) + 1) + min;
    return randomNum;
  }

  public static void signInFirebase(SessionManager session, FirebaseAuth mAuth, String email) {
    if (session.isLoggedIn()) {
      Timber.e("signInFirebase user " + mAuth.getCurrentUser());
      if (mAuth.getCurrentUser() == null) {
        if (!TextUtils.isEmpty(email)) {
          RxFirebaseAuth.signInWithEmailAndPassword(mAuth, email, session.getIdUser()+email)
              .observeOn(AndroidSchedulers.mainThread())
              .subscribeOn(Schedulers.io())
              .subscribe(authResult -> {
                Timber.e("signInFirebase signInWithEmailAndPassword authResult " + authResult);
              }, throwable -> {
                if (throwable instanceof FirebaseAuthInvalidUserException) {
                  FirebaseAuthInvalidUserException error =
                      (FirebaseAuthInvalidUserException) throwable;
                  if (error.getErrorCode().equalsIgnoreCase("ERROR_USER_NOT_FOUND")) {
                    RxFirebaseAuth.createUserWithEmailAndPassword(mAuth, email, session.getIdUser()+email)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(authResult -> {
                          Timber.e("signInFirebase createUserWithEmailAndPassword authResult " + authResult);
                        },throwable1 -> {
                          Timber.e("signInFirebase createUserWithEmailAndPassword ERROR " + throwable1);
                        },()->{});
                  }
                } Timber.e("signInFirebase signInWithEmailAndPassword ERROR " + throwable);
              }, () -> {
              });
        } else {
          RxFirebaseAuth.signInAnonymously(mAuth)
              .observeOn(AndroidSchedulers.mainThread())
              .subscribeOn(Schedulers.io())
              .subscribe(authResult -> {
                Timber.e("signInFirebase signInAnonymously authResult " + authResult);
              }, throwable -> {
                Timber.e("signInFirebase signInAnonymously ERROR " + throwable);
              }, () -> {
              });
        }
      }
    }
  }

  /*public static boolean deleteTransaction(Cash cashflow, DbHelper db){
    boolean set = false;
    int product_id = cashflow.getProduct_id();
    int amount = cashflow.getProduct_id();
    String type_mutasi = cashflow.getType();
    if (cashflow.getCash_id() == -1) {
      set = db.deleteCashByIDCashflowLocal(cashflow.getId());
    } else {
      set = db.softDeleteCashflow(cashflow.getId());
    }
    if (set) {
      db.updateSaldoAfterEditTransaction(product_id, Float.valueOf(amount), type_mutasi);
    }
    return set;
  }*/
}
