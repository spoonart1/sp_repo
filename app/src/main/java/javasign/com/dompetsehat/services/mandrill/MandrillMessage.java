package javasign.com.dompetsehat.services.mandrill;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import javasign.com.dompetsehat.utils.Helper;
import timber.log.Timber;

/**
 * Created by avesina on 2/17/17.
 */

public class MandrillMessage {

  public final static String ENDPOINT = "https://mandrillapp.com/api/1.0/messages/send.json" ;

  public MandrillMessage(String key){
    if(key == null) {
      throw new NullPointerException(
          "'key' is null , please provide Mandrill API key");
    }
    this.key = key ;
  }

  private String key;
  private EmailMessage message;

  public String getKey() {
    return key;
  }
  public EmailMessage getMessage() {
    return message;
  }

  public void setMessage(EmailMessage message) {
    this.message = message;
  }

  public void setKey(String key){
    this.key = key ;
  }


  public String getJson(){
    if(message == null) {
      throw new NullPointerException(
          "'message' is null , please make sure that you set message");
    }
    Gson gson = new Gson();
    return gson.toJson(this);
  }

  public void send(Context context,String load_message,AsyncTaskInterface asyncTaskInterface){
    ProgressDialog dialog = new ProgressDialog(context);
    dialog.setMessage(load_message);
    send(dialog,asyncTaskInterface);
  }

  public void send(Context context,AsyncTaskInterface asyncTaskInterface){
    send(context,"Sedang mengirim email....",asyncTaskInterface);
  }


  public void send(ProgressDialog dialog,AsyncTaskInterface asyncTaskInterface){
    if(message == null) {
      throw new NullPointerException(
          "'message' is null , please make sure that you set message");
    }

    if(dialog != null){
      dialog.show();
    }

    new AsyncTask<Void, Void, Void>() {
      @Override
      protected Void doInBackground(Void... params) {
        StringBuffer result = new StringBuffer("");
        try{
          URL url = new URL(ENDPOINT);
          HttpURLConnection connection = (HttpURLConnection)url.openConnection();
          connection.setRequestMethod("POST");
          connection.connect();


          OutputStream os = connection.getOutputStream();
          OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
          osw.write(getJson());
          osw.flush();
          osw.close();

          BufferedReader br = new BufferedReader(new InputStreamReader( connection.getInputStream(),"utf-8"));
          String line = null;
          while ((line = br.readLine()) != null) {
            result.append(line + "\n");
          }
          br.close();

        } catch (IOException e) {
          // writing exception to log
          e.printStackTrace();
        }
        Log.d("MandrillMessage", result.toString());
        asyncTaskInterface.doInBackground();
        return null;
      }

      @Override protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if(dialog != null){
          dialog.dismiss();
          asyncTaskInterface.onPostExecute();
        }
      }
    }.execute();


  }

  public interface AsyncTaskInterface{
    void onPostExecute();
    void doInBackground();
  }

}

