package javasign.com.dompetsehat.VeryFund;

/**
 * Created by aves on 4/7/15.
 */
public class Http {
    String url;
    int method;

    public void setMethod(int method) {
        this.method = method;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }
}
