package javasign.com.dompetsehat.models.json;

import java.util.List;

/**
 * Created by aves on 8/10/16.
 */

public class account {
    public int id;
    public int user_id;
    public String username;
    public String nickname;
    public String last_activity;
    public String login_status;
    public String login_info;
    public String email_alert;
    public vendor vendor;
    public List<product> products;
    public String properties;
    public String created_at;
    public String updated_at;
    public String deleted_at;
}
