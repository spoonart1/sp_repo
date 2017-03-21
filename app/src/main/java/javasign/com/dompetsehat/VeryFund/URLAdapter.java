package javasign.com.dompetsehat.VeryFund;

/**
 * Created by aves on 4/6/15.
 */
public class URLAdapter {

    public int GET = 1;
    public int POST = 2;
    public int PUT = 3;
    public int DELETE = 4;

//    private static String ip="http://192.168.1.12";
//    private static String domain=ip+"/"+"kesles.webservice";
//    private static String prefix = "public";

    private String ip = "https://finfini.com";
    private String domain = ip;
    private String prefix = "api/v1";

    public final String synccashbyid = domain + "/" + prefix + "/account/sync/";
    public final String cashflow_lastconnect_byaccount = domain + "/" + prefix + "/cashflow/lastconnect/account/";


    Http http;

    public Http index(String url) {
        http = new Http();
        http.setUrl(url);
        http.setMethod(GET);
        return http;
    }

    public Http create(String url) {
        http = new Http();
        http.setUrl(url);
        http.setMethod(GET);
        return http;
    }

    public Http store(String url) {
        http = new Http();
        http.setUrl(url);
        http.setMethod(POST);
        return http;
    }

    public Http show(String url) {
        http = new Http();
        http.setUrl(url);
        http.setMethod(GET);
        return http;
    }

    public Http edit(String url) {
        http = new Http();
        http.setUrl(url);
        http.setMethod(GET);
        return http;
    }

    public Http update(String url) {
        http = new Http();
        http.setUrl(url);
        http.setMethod(PUT);
        return http;
    }

    public Http destroy(String url) {
        http = new Http();
        http.setUrl(url);
        http.setMethod(DELETE);
        return http;
    }

    public Http manual(String url) {
        http = new Http();
        http.setUrl(url);
        http.setMethod(DELETE);
        return http;
    }
}
