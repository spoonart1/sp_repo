package javasign.com.dompetsehat.VeryFund;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by aves on 4/6/15.
 */
public class ServiceHandler {

    protected String apikey = "nuravesinamustari10april1990";

    static InputStream is = null;
    static String response = null;
    public final int GET = 1;
    public final int POST = 2;
    public final int PUT = 3;
    public final int DELETE = 4;
    public final static String TAG_SUCCESS = "success";
    public final static String TAG_ID = "id";
    public final static String REQUEST_APIKEY = "apikey";
    public final static String VALUE_APIKEY = "kVBkcfp3ivy3GqzSn62Dn2sW27X408VN";
    public final static String REQUEST_ACCESSTOKEN = "accesstoken";
    private String iddelete;
    private String idupdate;

    private String request;
    private String value;

    /**
     * Making service call
     *
     * @url - url to make request
     * @method - http request method
     */


    public String makeServiceCall(Http http, String request, String value) {
        this.request = request;
        this.value = value;
        return this.makeServiceCall(http.getUrl(), http.getMethod(), null, request, value);
    }

    public String makeServiceCall(String url, int method, String request, String value) {
        this.request = request;
        this.value = value;
        return this.makeServiceCall(url, method, null, request, value);
    }

    public String makeServiceCall(Http http, List<NameValuePair> params, String request, String value) {
        this.request = request;
        this.value = value;
        return this.makeServiceCall(http.getUrl(), http.getMethod(), params, request, value);
    }

    public String makeServiceCallDelete(String url, List<NameValuePair> params, String id, String request, String value) {
        this.request = request;
        this.value = value;
        this.iddelete = id;
        return makeServiceCall(url, DELETE, params, request, value);
    }

    public String makeServiceCallUpdate(String url, List<NameValuePair> params, String id, String request, String value) {
        this.request = request;
        this.value = value;
        this.idupdate = id;
        return makeServiceCall(url, PUT, params, request, value);
    }

    /**
     * Making service call
     *
     * @url - url to make request
     * @method - http request method
     * @params - http request params
     */
    public String makeServiceCall(String url, int method, List<NameValuePair> params, String request, String value) {
        if (params != null) {
            // params.add(new BasicNameValuePair("apikey", "1234"));
        } else {
            params = new ArrayList<NameValuePair>();
            //params.add(new BasicNameValuePair("apikey", "1234"));
        }

            try {
                // http client
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpEntity httpEntity = null;
                HttpResponse httpResponse = null;

                // Checking http request method type
                if (method == POST) {
                    HttpPost httpPost = new HttpPost(url);
                    httpPost.setHeader(request, value);
                    // adding post params
                    if (params != null) {
                        httpPost.setEntity(new UrlEncodedFormEntity(params));

                    }

                    httpResponse = httpClient.execute(httpPost);

                } else if (method == GET) {
                    // appending params to url
                    HttpGet httpGet = new HttpGet(url);
                    httpGet.setHeader(request, value);
                    if (params != null) {
                        String paramString = URLEncodedUtils
                                .format(params, "utf-8");
                        url += "?" + paramString;
                    }

                    httpResponse = httpClient.execute(httpGet);

                } else if (method == PUT) {
                    HttpPut httpPut = new HttpPut(url);
                    httpPut.setHeader(request, value);
                    url += "/" + idupdate;
//                    if (params != null) {
//                        String paramString = URLEncodedUtils
//                                .format(params, "utf-8");
//                        url += "?" + paramString;
//                    }
                    httpPut.setEntity(new UrlEncodedFormEntity(params));
                    httpResponse = httpClient.execute(httpPut);

                } else if (method == DELETE) {
                    HttpDelete httpDelete = new HttpDelete(url);
                    httpDelete.setHeader(request, value);
                    if (params != null) {

                        String paramString = URLEncodedUtils
                                .format(params, "utf-8");
                        url += "?" + paramString;
                    }
                    httpResponse = httpClient.execute(httpDelete);
                }
                httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ConnectTimeoutException e) {
                e.printStackTrace();
            } catch (SocketTimeoutException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (SocketException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        is, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();
                response = sb.toString();
            } catch (Exception e) {

            }


            return response;

    }
}

