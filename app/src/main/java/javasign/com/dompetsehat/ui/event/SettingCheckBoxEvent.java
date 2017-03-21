package javasign.com.dompetsehat.ui.event;

import javasign.com.dompetsehat.ui.activities.setting.Setting;

/**
 * Created by avesina on 12/30/16.
 */

public class SettingCheckBoxEvent {
  public boolean checked;
  public Setting type;
  public SettingCheckBoxEvent(boolean checked,Setting type){
    this.checked = checked;
    this.type = type;
  }
}
