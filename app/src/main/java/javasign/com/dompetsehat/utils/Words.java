package javasign.com.dompetsehat.utils;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;
import java.util.HashMap;
import timber.log.Timber;

/**
 * Created by bastianbentra on 8/5/16.
 */
public class Words {

  public static HashMap<String, String[]> FullMonthName = new HashMap<>();

  static {
    FullMonthName.put("Jan", new String[]{"Januari", "0"});
    FullMonthName.put("Feb", new String[]{"Februari", "1"});
    FullMonthName.put("Mar", new String[]{"Maret", "2"});
    FullMonthName.put("Apr", new String[]{"April", "3"});
    FullMonthName.put("Mei", new String[]{"Mei", "4"});
    FullMonthName.put("Jun", new String[]{"Juni", "5"});
    FullMonthName.put("Jul", new String[]{"Juli", "6"});
    FullMonthName.put("Agt", new String[]{"Agustus", "7"});
    FullMonthName.put("Sept", new String[]{"September", "8"});
    FullMonthName.put("Okt", new String[]{"Oktober", "9"});
    FullMonthName.put("Nov", new String[]{"November", "10"});
    FullMonthName.put("Des", new String[]{"Desember", "11"});
  }

  public static long daily = 86400000;
  public static long hourly = 3600000;
  public static long fifteenminute = 900000;

  public static String pdf = "http://dompets.id/downloads/manualbook_DS.pdf";
  public static String full_link_pdf =  "http://drive.google.com/viewerng/viewer?embedded=true&url="+pdf;

  public final static String[] days = {"Minggu","Senin","Selasa","Rabu","Kamis","Jumat", "Sabtu"};


  public static final String MESSAGE = "message";
  public static final String FLAG = "flag";
  public static final String TITLE = "flag";
  public static final String TYPE = "type";
  public static final String REQUEST = "request";
  public static final String ID = "id";
  public static final String NONE = "";
  public static final String DOMPET = "dompet";
  public static final String BANK = "bank";
  public static final String INVEST = "invest";

  public static final String Account = "Rekening";
  public static final String Budget = "Anggaran";
  public static final String Reminder = "Pengingat";
  public static final String Referral = "Kode Referal";
  public static final String Comission = "Komisi";
  public static final String SwitchMode = "Setelan Mode";
  public static final String Setting = "Pengaturan";
  public static final String Help = "Bantuan";
  public static final String Feedback = "Ulasan";

  public static String MONTH = "month";
  public static String YEAR = "year";
  public static String DAY = "day";

  public static final String ID_VENDOR = "id_vendor";
  public static final String NAMA_VENDOR = "name_vendor";
  public static final String ACCOUNT_ID = "account_id";


  public static String toSentenceCase(String source) {
    if(TextUtils.isEmpty(source)) return "";
    return source.substring(0, 1).toUpperCase() + source.substring(1).toLowerCase();
  }

  public static String toTitleCase(String s) {
    final String ACTIONABLE_DELIMITERS = " '-/"; // these cause the character following
    s = s.replace("_"," ");
    // to be capitalized

    StringBuilder sb = new StringBuilder();
    boolean capNext = true;

    for (char c : s.toCharArray()) {
      c = (capNext)
          ? Character.toUpperCase(c)
          : Character.toLowerCase(c);
      sb.append(c);
      capNext = (ACTIONABLE_DELIMITERS.indexOf((int) c) >= 0); // explicit cast not needed
    }
    return sb.toString();
  }

  public static void setButtonToListen(View button, final TextView... texts) {
    final View btn = button;

    TextWatcher listener = new TextWatcher() {
      @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override public void onTextChanged(CharSequence s, int start, int before, int count) {

      }

      @Override public void afterTextChanged(Editable s) {
        boolean enable = false;
        for (TextView e : texts) {
          enable = (e.getText() != null && (e.getText().length() > 0));
          if (!enable) break;
        }
        btn.setEnabled(enable);
      }
    };

    for (TextView e : texts) {
      e.addTextChangedListener(listener);
    }
  }

  public static void setViewToListenVisibility(View view, final TextView... texts) {
    final View v = view;

    TextWatcher listener = new TextWatcher() {
      @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override public void onTextChanged(CharSequence s, int start, int before, int count) {

      }

      @Override public void afterTextChanged(Editable s) {
        boolean enable = false;
        for (TextView e : texts) {
          enable = (e.getText() != null && (e.getText().length() > 0));
          if (!enable) break;
        }
        if(!enable) {
          v.setVisibility(View.VISIBLE);
        }else{
          v.setVisibility(View.GONE);
        }
      }
    };

    for (TextView e : texts) {
      e.addTextChangedListener(listener);
    }
  }

  public static void setButtonToListen(final View[] buttons, final TextView... texts) {
    TextWatcher listener = new TextWatcher() {
      @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override public void onTextChanged(CharSequence s, int start, int before, int count) {

      }

      @Override public void afterTextChanged(Editable s) {
        boolean enable = false;
        for (TextView e : texts) {
          enable = (e.getText() != null && e.getText().length() > 0);
          if(!enable) break;
        }
        for (View btn : buttons) {
          btn.setEnabled(enable);
        }
      }
    };

    for (TextView e : texts) {
      e.addTextChangedListener(listener);
    }
  }

  public static void setButtonToListen(final View btn, final TextView originalField,
      final TextView confirmationField, final String errorMessage1, final String errorMessage2,
      final TextView... others) {
    TextWatcher listener = new TextWatcher() {
      @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override public void onTextChanged(CharSequence s, int start, int before, int count) {

      }

      @Override public void afterTextChanged(Editable s) {
        boolean enable = false;

        Timber.e("avesina end "+s.toString());

        if (others == null || others.length < 1) {

          if (originalField.getText().length() == 0 && confirmationField.getText().length() == 0) {
            return;
          }

          if (!originalField.getText().toString().equalsIgnoreCase(confirmationField.getText().toString())) {
            Timber.e("avesina end start "+confirmationField.getSelectionStart());
            Timber.e("avesina end "+confirmationField.getSelectionEnd());

              if(confirmationField.getSelectionStart() > 0) {
                confirmationField.setError(errorMessage2);
              }
            enable = false;
          }
          else {
            confirmationField.setError(null);
            enable = true;
          }
          btn.setEnabled(enable);
        }

        for (TextView e : others) {

          if (originalField.getText().length() == 0 && confirmationField.getText().length() == 0) {
            return;
          }

          if (!originalField.getText().toString().equalsIgnoreCase(confirmationField.getText().toString())) {
            Timber.e("avesina end start "+confirmationField.getSelectionStart());
            Timber.e("avesina end "+confirmationField.getSelectionEnd());

            if(confirmationField.getSelectionStart() > 0) {
              confirmationField.setError(errorMessage2);
            }
            enable = false;
          } else {
            confirmationField.setError(null);
            enable = true;
          }

          btn.setEnabled(e.getText() != null
              && e.getText().length() > 0
              && enable
              && originalField.getText().length() > 0
              && confirmationField.getText().length() > 0);
        }
      }
    };

    originalField.addTextChangedListener(listener);
    confirmationField.addTextChangedListener(listener);

    for (TextView e : others) {
      e.addTextChangedListener(listener);
    }
  }

  public static float getRandomValue() {
    return (float) (Math.random() * 100f);
  }

  public static int getRandomValue(int value) {
    return (int) (Math.random() * value);
  }

  public static class SimpleTextWatcer implements TextWatcher {

    @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override public void afterTextChanged(Editable editable) {

    }
  }
}
