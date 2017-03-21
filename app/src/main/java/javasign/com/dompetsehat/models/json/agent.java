package javasign.com.dompetsehat.models.json;

import android.text.TextUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import timber.log.Timber;

/**
 * Created by avesina on 1/9/17.
 */

public class agent {
  public int id;
  public int user_id;
  public int agent_id;
  public String status;
  public String created_at;
  public String updated_at;
  public String deleted_at;
  public String agent_name;
  public String agent_ava;
  public String permission;
  public Map<String, Boolean> h_permission;
  public String fcm;
  Type type = new TypeToken<Map<String, Boolean>>() {
  }.getType();
  Gson gson = new Gson();

  public void populatePermission() {
    if (!TextUtils.isEmpty(permission)) {
      try {
        h_permission = gson.fromJson(permission, type);
        Timber.e("avesina "+h_permission);
      }catch (Exception e){
        Timber.e("ERROR "+e);
      }
      return;
    }
    h_permission = new HashMap<>();
  }
}
