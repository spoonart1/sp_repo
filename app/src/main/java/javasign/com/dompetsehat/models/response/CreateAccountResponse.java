package javasign.com.dompetsehat.models.response;

import com.google.gson.annotations.SerializedName;

import javasign.com.dompetsehat.models.json.account;

/**
 * Created by avesina on 9/9/16.
 */

public class CreateAccountResponse extends ParentResponse {
    public Response response;
    public class Response{
        public int ClientStat;
        @SerializedName("accounts") public account account;
    }
}
