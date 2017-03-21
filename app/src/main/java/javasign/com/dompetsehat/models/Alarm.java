package javasign.com.dompetsehat.models;

import android.text.TextUtils;
import org.json.JSONObject;

import javasign.com.dompetsehat.models.json.alarm;

/**
 * Created by Xenix on 9/30/2015.
 */
public class Alarm {
    private int id;
    private int id_alarm;
    private String deskripsi_alarm;
    private float jumlah_alarm;
    private String date_alarm;
    private int id_user;
    private int id_dompet;
    private int id_category;
    private int user_id_category;
    private int month_alarm;
    public String type;
    private String is_active;
    private String toggle;
    private String created_at;
    private String updated_at;
    private String deleted_at;

    public Alarm() {
        super();
    }

    public Alarm(JSONObject inputcash) {
        try {
            setId_alarm(inputcash.getInt("id"));
            setDeskripsi_alarm(inputcash.getString("description"));
            setJumlah_alarm(inputcash.getLong("debetAmount"));
            setDate_alarm(inputcash.getString("date"));
            setId_user(inputcash.getInt("user_id"));
            setId_dompet(inputcash.getInt("product_id"));
            setId_category(inputcash.getInt("category_id"));
            setUser_id_category(inputcash.getInt("user_category_id"));
            setMonth_alarm(inputcash.getInt("month"));
            setIs_active(inputcash.getString("is_active"));
            setType("common");
            if(inputcash.has("type")){
                if(!TextUtils.isEmpty(inputcash.getString("type"))){
                    setType(inputcash.getString("type"));
                }
            }
            if(inputcash.has("toggle"))
                setToggle(inputcash.getString("toggle"));
            setCreated_at(inputcash.getString("created_at"));
            setUpdated_at(inputcash.getString("updated_at"));
            setDeleted_at(inputcash.getString("deleted_at"));
        } catch (Exception e) {

        }
    }

    public Alarm(alarm data) {
        try {
            setId_alarm(data.id);
            setId(data.id_local);
            setDeskripsi_alarm(data.description);
            setJumlah_alarm(data.amount);
            setDate_alarm(data.date);
            setId_user(data.user_id);
            setId_dompet(data.product_id);
            setId_category(data.category_id);
            setUser_id_category(data.user_category_id);
            setMonth_alarm(data.month);
            setIs_active(data.is_active);
            setType(data.type);
            if(TextUtils.isEmpty(data.type)){
                setType("common");
            }
            setToggle(data.toggle);
            setCreated_at(data.created_at);
            setUpdated_at(data.updated_at);
            setDeleted_at(data.deleted_at);
        } catch (Exception e) {

        }
    }

    public int getId_alarm() {
        return id_alarm;
    }

    public Alarm setId_alarm(int id_alarm) {
        this.id_alarm = id_alarm;
        return this;
    }

    public String getDeskripsi_alarm() {
        return deskripsi_alarm;
    }

    public Alarm setDeskripsi_alarm(String deskripsi_alarm) {
        this.deskripsi_alarm = deskripsi_alarm;
        return this;
    }

    public float getJumlah_alarm() {
        return jumlah_alarm;
    }

    public Alarm setJumlah_alarm(float jumlah_alarm) {
        this.jumlah_alarm = jumlah_alarm;
        return this;
    }

    public String getDate_alarm() {
        return date_alarm;
    }

    public Alarm setDate_alarm(String date_alarm) {
        this.date_alarm = date_alarm;
        return this;
    }

    public int getId_user() {
        return id_user;
    }

    public Alarm setId_user(int id_user) {
        this.id_user = id_user;
        return this;
    }

    public String getIs_active() {
        return is_active;
    }

    public Alarm setIs_active(String is_active) {
        this.is_active = is_active;
        return this;
    }

    public int getId_dompet() {
        return id_dompet;
    }

    public Alarm setId_dompet(int id_dompet) {
        this.id_dompet = id_dompet;
        return this;
    }

    public int getId_category() {
        return id_category;
    }

    public Alarm setId_category(int id_category) {
        this.id_category = id_category;
        return this;
    }

    public int getUser_id_category() {
        return user_id_category;
    }

    public Alarm setUser_id_category(int user_id_category) {
        this.user_id_category = user_id_category;
        return this;
    }

    public int getMonth_alarm() {
        return month_alarm;
    }

    public Alarm setType(String type) {
        this.type = type;
        return this;
    }

    public String getType() {
        return type;
    }

    public Alarm setMonth_alarm(int month_alarm) {
        this.month_alarm = month_alarm;
        return this;
    }

    public int getId() {
        return id;
    }

    public Alarm setId(int id) {
        this.id = id;
        return this;
    }

    public String getCreated_at() {
        return created_at;
    }

    public Alarm setCreated_at(String created_at) {
        this.created_at = created_at;
        return this;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public Alarm setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
        return this;
    }

    public String getDeleted_at() {
        return deleted_at;
    }

    public Alarm setDeleted_at(String deleted_at) {
        this.deleted_at = deleted_at;
        return this;
    }

    public String getToggle() {
        return toggle;
    }

    public Alarm setToggle(String toggle) {
        this.toggle = toggle;
        return this;
    }
}
