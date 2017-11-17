package javasign.com.dompetsehat.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javasign.com.dompetsehat.R;

/**
 * Created by aves on 4/6/15.
 */
public class ConnectionDetector {

    private Context _context;

    public ConnectionDetector(Context context) {
        this._context = context;
    }

    /**
     * Checking for all possible internet providers
     * *
     */
    public boolean isConnectingToInternet() {
        ConnectivityManager connectivity = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
        }
        return false;
    }

    public void showToast() {
        Toast.makeText(_context, _context.getString(R.string.app_need_connection), Toast.LENGTH_LONG).show();
    }

    public boolean isAvailable() {

        // first check if there is a WiFi/data connection available... then:

        URL url = null;
        try {
            url = new URL("http://api.kesles.in");
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return false;
        }
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        connection.setRequestProperty("Connection", "close");
        connection.setConnectTimeout(10000); // Timeout 10 seconds
        try {
            connection.connect();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        // If the web service is available
        try {
            if (connection.getResponseCode() == 200) {
                return true;
            } else return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
