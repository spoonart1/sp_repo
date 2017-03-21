package javasign.com.dompetsehat.VeryFund;

import javasign.com.dompetsehat.utils.MCryptNew;



import com.jakewharton.retrofit.Ok3Client;
import javasign.com.dompetsehat.BuildConfig;
import okhttp3.OkHttpClient;
import retrofit.RestAdapter;

/**
 * Created by Fernando on 7/9/2015.
 */
public class RESTClient {
    private static MCryptNew mcrypt = new MCryptNew();
    private static RESTInterface REST_CLIENT;
    private static String URL = "https://"+mcrypt.decrypt(BuildConfig.SERVER_URL);

    static {
        setRestClient();
    }

    private RESTClient(){}
    public static RESTInterface getRestClient(){
        return REST_CLIENT;
    }

    //setup resclient
    //set timeout  = 30 s
    //using OkHttpClient we can customize access method of restclient
    private static void setRestClient(){
        OkHttpClient okHttpClient = new OkHttpClient();
//        okHttpClient.connectTimeout(60, TimeUnit.SECONDS);
//        okHttpClient.readTimeout(60, TimeUnit.SECONDS);

        RestAdapter restBuilder = new RestAdapter.Builder()
                .setLogLevel(BuildConfig.DEBUG_MODE?RestAdapter.LogLevel.FULL:RestAdapter.LogLevel.NONE)
                .setClient(new Ok3Client(okHttpClient))
                .setEndpoint(URL)
                .build();
        REST_CLIENT = restBuilder.create(RESTInterface.class);
    }
}
