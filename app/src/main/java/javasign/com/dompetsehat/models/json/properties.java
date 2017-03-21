package javasign.com.dompetsehat.models.json;

import com.google.gson.Gson;
import org.json.JSONObject;

/**
 * Created by avesina on 10/28/16.
 */

public class properties extends JSONObject {

  @Override public String toString() {
    return new Gson().toJson(this);
  }
}
