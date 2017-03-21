package javasign.com.dompetsehat.data;

import android.content.Context;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;
import com.google.gson.Gson;
import java.io.IOException;

import javasign.com.dompetsehat.presenter.interceptor.InterceptorInterface;
import javasign.com.dompetsehat.presenter.interceptor.InterceptorPresenter;

import javasign.com.dompetsehat.utils.SessionManager;
import javasign.com.dompetsehat.base.MyCustomApplication;
import javasign.com.dompetsehat.injection.ApplicationContext;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import timber.log.Timber;

/**
 * Created by aves on 8/10/16.
 */

public class HeaderInterceptor implements Interceptor, InterceptorInterface {

  SessionManager sessionManager;
  InterceptorPresenter presenter;
  private Context context;
  ConnectivityManager cm;

  public HeaderInterceptor(@ApplicationContext Context context) {
    this.context = context;
    sessionManager = new SessionManager(context);
    ((MyCustomApplication) context).getApplicationComponent().inject(this);
    presenter = new InterceptorPresenter(context, sessionManager, new Gson());
    presenter.attachView(this);
    this.cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
  }

  @Override public Response intercept(Chain chain) throws IOException {
    NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
    boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    Timber.e("Connected "+isConnected);
    if (!isConnected) {
      ((MyCustomApplication) context).showMessage(context,"Tidak terdapat koneksi internet, silahkan cek koneksi anda");
      //Toast.makeText(context,"Tidak terdapat koneksi internet, silahkan cek koneksi anda",Toast.LENGTH_LONG).show();
      throw new IOException();
    }

    String access_token = "";
    if (sessionManager.getAuthToken() != null) {
      access_token = sessionManager.getAuthToken();
      Timber.e("ACCESSTOKEN " + access_token);
    }
    MyCustomApplication.get(context).getApplicationComponent().inject(this);
    Request request = chain.request();
    Request newRequest;
    newRequest = request.newBuilder()
        .addHeader("APIKEY", "kVBkcfp3ivy3GqzSn62Dn2sW27X408VN")
        .addHeader("ACCESSTOKEN", access_token)
        .build();
    Response response = chain.proceed(newRequest);
    if(response.code() ==  500){
      //MyCustomApplication.get(context).showMessage("Internal Service Error");
    }
    MediaType contentType = response.body().contentType();
    String json = response.body().string();
    ResponseBody body = ResponseBody.create(contentType, json);
    validateResponse(json);
    return response.newBuilder().body(body).build();
  }

  private void validateResponse(String body) {
    presenter.validateToken(body);
  }
}
