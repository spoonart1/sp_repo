package javasign.com.dompetsehat.utils;

/**
 * Created by aves on 4/14/15.
 */

import android.content.res.Resources;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.mikepenz.iconics.view.IconicsTextView;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

import javasign.com.dompetsehat.R;

public class RupiahCurrencyFormat {
  Locale locale = null;
  IconicsTextView btn_save;
  Resources res;

  private static RupiahCurrencyFormat format;

  public static RupiahCurrencyFormat getInstance() {
    if (format == null) {
      return new RupiahCurrencyFormat();
    }

    return format;
  }

  public String toRupiahFormatSimple(float nominal) {
    locale = new Locale("in");
    NumberFormat format = NumberFormat.getInstance(locale);
    String result = format.format(nominal);

    return "Rp " + result;
  }

  public String toRupiahFormatSimple(Double nominal) {
    locale = new Locale("in");
    NumberFormat format = NumberFormat.getInstance(locale);
    String result = format.format(nominal);
    return "Rp " + result;
  }

  public String toRupiahFormatSimple(Double nominal,boolean is_decimal){
    if(!is_decimal){
      return toRupiahFormatSimple(nominal);
    }else{
      locale = new Locale("in");
      NumberFormat format = new DecimalFormat("#,###,###,##0.00",DecimalFormatSymbols.getInstance(locale));
      String result = format.format(nominal);
      return "Rp " + result;
    }
  }

  public String toRupiahFormatSimple(float nominal,boolean is_decimal){
    if(!is_decimal){
      return toRupiahFormatSimple(nominal);
    }else{
      locale = new Locale("in");
      NumberFormat format = new DecimalFormat("#,###,###,##0.00",DecimalFormatSymbols.getInstance(locale));
      String result = format.format(nominal);
      return "Rp " + result;
    }
  }

  public String toRupiahFormatSimple(int nominal) {
    locale = new Locale("in");
    NumberFormat format = NumberFormat.getInstance(locale);
    String result = format.format(nominal);

    return "Rp " + result;
  }

  @Deprecated private String conver(Double harga) {
    DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance();
    DecimalFormatSymbols formatRp = new DecimalFormatSymbols();

    kursIndonesia.applyPattern("###,###.###");
    kursIndonesia.setDecimalFormatSymbols(formatRp);
    return kursIndonesia.format(harga);
  }

  public void setButtonToListen(Resources res, IconicsTextView button) {
    this.btn_save = button;
    this.res = res;
  }

  private void setAbleToSave(boolean save) {
    if (btn_save == null) return;

    if (save) {
      btn_save.setEnabled(save);
      return;
    }

    btn_save.setTextColor(res.getColor(R.color.grey_400));
    btn_save.setEnabled(save);
  }

  public void formatEditText(final EditText...targets) {
    for (EditText text : targets) {
      new TextWatcherCustom(text);
    }
  }

  public void formatEditTextWithCustomPrefix(String prefix, final EditText...targets){
    for (EditText target : targets) {
      new TextWatcherWithPrefix(target, prefix);
    }
  }

  public static String clearRp(String text) {
    String rs = text;

    rs = rs.replace("R", "");
    rs = rs.replace("p ", "");
    rs = rs.replace(" ", "");
    rs = rs.replace(".", "");
    rs = rs.replace("p", "");
    rs = rs.replace(",", "");

    return rs;
  }

  public static String clearPrefix(String currentText, String prefix) {
    String rs = currentText;

    rs = rs.replace(prefix, "");

    return rs;
  }

  class TextWatcherCustom implements TextWatcher {

    private EditText t;
    private String textNow = null;

    public TextWatcherCustom(EditText target) {
      this.t = target;
      this.t.addTextChangedListener(this);
    }

    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {
      if (s.length() > 0) {
        textNow = s.toString();
      }
    }

    @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
      if (s.length() > 0) {
        t.removeTextChangedListener(this);

        String result = t.getText().toString();
        if (result.contains("Rp ")
            || result.contains("R")
            || result.contains("p")
            || result.contains(".")
            || result.contains("Rp.")
            || result.contains("Rp. ")
            || result.contains(",")) {
          result = clearRp(result);
        }

        try {
          if (result.length() > 0) result = toRupiahFormatSimple(Double.parseDouble(result));
        }catch (Exception e){
          result = toRupiahFormatSimple(0);
        }

        t.setText(result);
        textNow = result;

        t.addTextChangedListener(this);
      }
    }

    @Override public void afterTextChanged(Editable s) {
      if (s.length() > 0) {

      }
      if (textNow != null) {
        int l = textNow.length();
        if (l > 0) {
          try {
            t.setSelection(l);
            setAbleToSave(true);
          } catch (Exception e) {
            e.printStackTrace();
            setAbleToSave(false);
          }
        } else {
          setAbleToSave(false);
        }
        textNow = s.toString();
      }
    }
  }

  class TextWatcherWithPrefix implements TextWatcher{

    private String prefix;
    private EditText t;
    private String textNow = null;

    TextWatcherWithPrefix(EditText target, String prefix){
      this.t = target;
      this.prefix = prefix;
      this.t.addTextChangedListener(this);
    }

    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {
      if (s.length() > 0) {
        textNow = s.toString();
      }
    }

    @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
      if (s.length() > 0) {
        t.removeTextChangedListener(this);

        String result = t.getText().toString();
        if (result.contains(prefix)){
          result = clearPrefix(result, prefix);
        }

        if (result.length() > 0) result = result+" "+prefix;

        t.setText(result);
        textNow = result;

        t.addTextChangedListener(this);
      }
    }

    @Override public void afterTextChanged(Editable s) {
      if (s.length() > 0) {

      }
      if (textNow != null) {
        int l = textNow.length();
        if (l > 0) {
          try {
            t.setSelection(l);
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
        textNow = s.toString();
      }
    }
  }
}
