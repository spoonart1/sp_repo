package javasign.com.dompetsehat.ui.activities.reminder.pojo;

import javasign.com.dompetsehat.models.Alarm;
import javasign.com.dompetsehat.models.BaseModel;
import javasign.com.dompetsehat.utils.DSFont;

/**
 * Created by bastianbentra on 8/29/16.
 */
public class ReminderModel extends BaseModel{
  public Integer identifier;
  public String categoryName;
  public String repeatDate;
  public double billAmount;
  public int bgColor;
  public Alarm alarm;
  public boolean isActive = false;
  public String icon = DSFont.Icon.dsf_general.getFormattedName();

  public ReminderModel setIdentifier(int identifier) {
    this.identifier = identifier;
    return this;
  }

  public ReminderModel(String categoryName, String repeatDate, double billAmount, int bgColor){
    this.categoryName = categoryName;
    this.repeatDate = repeatDate;
    this.billAmount = billAmount;
    this.bgColor = bgColor;
  }

  public ReminderModel setIsActive(boolean active){
    isActive = active;
    return this;
  }

  public ReminderModel setAlarm(Alarm alarm){
    this.alarm = alarm;
    return this;
  }

  public ReminderModel withIcon(String icon){
    this.icon = icon;
    return this;
  }
}
