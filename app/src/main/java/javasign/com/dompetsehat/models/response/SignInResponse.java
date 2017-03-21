package javasign.com.dompetsehat.models.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import javasign.com.dompetsehat.models.json.account;
import javasign.com.dompetsehat.models.json.alarm;
import javasign.com.dompetsehat.models.json.budget;
import javasign.com.dompetsehat.models.json.category;
import javasign.com.dompetsehat.models.json.plan;
import javasign.com.dompetsehat.models.json.user;
import javasign.com.dompetsehat.models.json.user_defined;
import javasign.com.dompetsehat.models.json.vendor;

/**
 * Created by aves on 8/8/16.
 */

public class SignInResponse extends ParentResponse {
    public Response response;
    public class Response{
        public String app_id;
        public String access_token;
        @SerializedName("userinfo") public user user;
        public List<account> accounts;
        public List<category> categories;
        public List<budget> budgets;
        public List<alarm> alarms;
        public List<vendor> vendors;
        public List<plan> plans;
        public List<user_defined> user_categories;
    }
}
