package javasign.com.dompetsehat.ui.activities.setting.sharing.permission.model;

/**
 * Created by lafran on 1/5/17.
 */

public class PermissionModel {
  public String name;
  public String keyName;
  public boolean isVisible = true;

  public PermissionModel(String name, String keyName) {
    this.name = name;
    this.keyName = keyName;
  }

  public PermissionModel setDataIsVisible(boolean isVisible) {
    this.isVisible = isVisible;
    return this;
  }
}
