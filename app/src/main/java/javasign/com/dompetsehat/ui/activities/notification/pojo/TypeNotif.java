package javasign.com.dompetsehat.ui.activities.notification.pojo;

import android.graphics.Color;
import javasign.com.dompetsehat.utils.DSFont;

/**
 * Created by lafran on 3/9/17.
 */

public enum TypeNotif{
  Bank, Transaction, Setting, Reminder,EmailVerification;

  public static final String N001 = "N001";
  public static final String N002 = "N002";
  public static final String N003 = "N003";
  public static final String N004 = "N004";
  public static final String N005 = "N005";
  public static final String N007 = "N007";

  public static TypeNotif codeType(String code){
    switch (code){
      case N001:
        return Setting;
      case N002:
        return Bank;
      case N003:
        return Transaction;
      case N004:
        return Transaction;
      case N005:
        return Reminder;
      case N007:
        return EmailVerification;
      default:
        return Bank;
    }
  }

  public String getIcon(){
    switch (this){
      case Bank: return DSFont.Icon.dsf_trx_bank.getFormattedName();
      case Transaction: return DSFont.Icon.dsf_transactions.getFormattedName();
      case Setting: return DSFont.Icon.dsf_setting.getFormattedName();
      case Reminder: return DSFont.Icon.dsf_reminder_2.getFormattedName();
      case EmailVerification: return DSFont.Icon.dsf_job_n_business.getFormattedName();
    }
    return "";
  }

  public int getColor(){
    switch (this){
      case Bank: return Color.parseColor("#3ba5e0");
      case Transaction: return Color.parseColor("#30d14b");
      case Setting: return Color.parseColor("#897c60");
      case Reminder: return Color.parseColor("#f2a60f");
      case EmailVerification: return Color.parseColor("#f2a60f");
    }
    return Color.BLACK;
  }

}
