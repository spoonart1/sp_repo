package javasign.com.dompetsehat.services;

import android.text.TextUtils;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.HashMap;
import javasign.com.dompetsehat.models.response.ErrorModel;
import retrofit2.adapter.rxjava.HttpException;
import timber.log.Timber;

/**
 * Created by avesina on 10/25/16.
 */

public class ErrorUtils {
  public static String[] getErrorUserMessage(Throwable error) {
    if (error instanceof HttpException) {
      HttpException httpError = (HttpException) error;
      try {
        String messageBody = httpError.response().errorBody().string();
        Timber.d("getErrorUserMessage(): string message : %s", messageBody);
        ErrorModel errorModel = new Gson().fromJson(messageBody, ErrorModel.class);
        if (!TextUtils.isEmpty(errorModel.msg)) {
          return new String[] { errorModel.msg };
        }
        if (!TextUtils.isEmpty(errorModel.message)) {
          return new String[] { errorModel.message };
        }
        if (errorModel.data.validation != null) {
          HashMap<String,String> map_error = new HashMap<>();
          for (int i = 0; i < errorModel.data.validation.size(); i++) {
            map_error.put(errorModel.data.validation.get(i).code,errorModel.data.validation.get(i).message);
          }
          String[] msgs = new String[map_error.size()];
          int i = 0;
          for (HashMap.Entry<String, String> entry : map_error.entrySet()) {
            msgs[i] = entry.getValue();
            i++;
          }
          return msgs;
        }
      } catch (IOException e) {
        Timber.e("getErrorUserMessage() io : %s", e.getLocalizedMessage());
      } catch (Exception er) {
        Timber.e("getErrorUserMessage() ex : %s", er.getLocalizedMessage());
      }
    } else if (error instanceof IOException) {
      return new String[] { "No Connection Error" };
    }
    return new String[] { "Unknown Error" };
  }

  public static int getErrorCode(Throwable error) {
    try {
      HttpException httpError = (HttpException) error;
      return httpError.code();
    } catch (Exception e) {
      return 500;
    }
  }
}
