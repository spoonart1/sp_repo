package javasign.com.dompetsehat.models;

import org.json.JSONException;
import org.json.JSONObject;

import javasign.com.dompetsehat.models.json.user;
import timber.log.Timber;

/**
 * Created by Xenix on 11/30/2015.
 */
public class User {
    private String access_token;
    private int id;
    private int user_id;
    private String username;
    private String email;
    private String birthday;
    private String affiliation;
    private String occupation;
    private String address;
    private String postal_code;
    private String avatar;
    private String referral_code;
    private String referrer;
    private String updated_at;
    private String created_at;
    private String deleted_at;
    private String phone;
    private String gender;
    private String last_sync_cash;
    private String last_sync_budget;
    private String last_sync_alarm;
    private double penghasilan;
    private double cicilan;
    private int anak;

    public User() {
        super();
    }

    public User(JSONObject jsonObject) {
        try {
            if(jsonObject.has("access_token"))
                setAccess_token(jsonObject.getString("access_token"));
            setUser_id(jsonObject.getInt("id"));
            setUsername(jsonObject.getString("username"));
            setEmail(jsonObject.getString("email"));
            setBirthday(jsonObject.getString("birthday"));
            setAffiliation(jsonObject.getString("occupation"));
            setAddress(jsonObject.getString("address"));
            setPostal_code(jsonObject.getString("postal"));
            setGender(jsonObject.getString("gender"));
            setAvatar(jsonObject.getString("avatar_name"));
            if(jsonObject.has("referral_code")) {
                setReferral_code(jsonObject.getString("referral_code"));
                setReferrer(jsonObject.getString("referrer"));
            }
            setUpdated_at(jsonObject.getString("updated_at"));
            setCreated_at(jsonObject.getString("created_at"));
            setLast_sync_cash(jsonObject.getString("last_sync"));
            setLast_sync_budget(jsonObject.getString("last_sync_budget"));
            setLast_sync_alarm(jsonObject.getString("last_sync_alarm"));
            if(jsonObject.has("anak")){
                setAnak(jsonObject.getInt("anak"));
            }

            if(jsonObject.has("cicilan")){
                setCicilan(jsonObject.getDouble("cicilan"));
            }

            if(jsonObject.has("penghasilan")){
                setPenghasilan(jsonObject.getDouble("penghasilan"));
            }

        } catch (JSONException e) {
            Timber.e("User.java Catch User : %s", e.toString());
        }
    }

    public User(user data) {
        try {
            setUser_id(data.id);
            setUsername(data.username);
            setEmail(data.email);
            setBirthday(data.birthday);
            setAffiliation(data.occupation);
            setAddress(data.address);
            setPostal_code(data.postal);
            setGender(data.gender);
            setAvatar(data.avatar_name);
            setPhone(data.phone);
            if(!data.referral_code.equals("")) {
                setReferral_code(data.referral_code);
                setReferrer(data.referrer);
            }
            setUpdated_at(data.updated_at);
            setCreated_at(data.created_at);
            setLast_sync_cash(data.last_sync);
            setLast_sync_budget(data.last_sync_budget);
            setLast_sync_alarm(data.last_sync_alarm);
            setPenghasilan(data.income);
            setAnak(data.kids);
        } catch (Exception e) {
            Timber.e("User.java Catch User : %s", e.toString());
        }
    }

    public String getAccess_token() {
        return access_token;
    }

    public User setAccess_token(String access_token) {
        this.access_token = access_token;
        return this;
    }

    public int getUser_id() {
        return user_id;
    }

    public User setUser_id(int user_id) {
        this.user_id = user_id;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public User setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public User setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getBirthday() {
        return birthday;
    }

    public User setBirthday(String birthday) {
        this.birthday = birthday;
        return this;
    }

    public String getAffiliation() {
        return affiliation;
    }

    public User setAffiliation(String affiliation) {
        this.affiliation = affiliation;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public User setAddress(String address) {
        this.address = address;
        return this;
    }

    public String getPostal_code() {
        return postal_code;
    }

    public User setPostal_code(String postal_code) {
        this.postal_code = postal_code;
        return this;
    }

    public String getAvatar() {
        return avatar;
    }

    public User setAvatar(String avatar) {
        this.avatar = avatar;
        return this;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public User setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
        return this;
    }

    public String getCreated_at() {
        return created_at;
    }

    public User setCreated_at(String created_at) {
        this.created_at = created_at;
        return this;
    }

    public int getId() {
        return id;
    }

    public User setId(int id) {
        this.id = id;
        return this;
    }

    public String getGender() {
        return gender;
    }

    public User setGender(String gender) {
        this.gender = gender;
        return this;
    }

    public String getDeleted_at() {
        return deleted_at;
    }

    public User setDeleted_at(String deleted_at) {
        this.deleted_at = deleted_at;
        return this;
    }

    public String getLast_sync_cash() {
        return last_sync_cash;
    }

    public User setLast_sync_cash(String last_sync_cash) {
        this.last_sync_cash = last_sync_cash;
        return this;
    }

    public String getLast_sync_alarm() {
        return last_sync_alarm;
    }

    public User setLast_sync_alarm(String last_sync_alarm) {
        this.last_sync_alarm = last_sync_alarm;
        return this;
    }

    public String getLast_sync_budget() {
        return last_sync_budget;
    }

    public User setLast_sync_budget(String last_sync_budget) {
        this.last_sync_budget = last_sync_budget;
        return this;
    }

    public String getReferral_code() {
        return referral_code;
    }

    public User setReferral_code(String referral_code) {
        this.referral_code = referral_code;
        return this;
    }

    public String getReferrer() {
        return referrer;
    }

    public User setReferrer(String referrer) {
        this.referrer = referrer;
        return this;
    }

    public String getOccupation() {
        return occupation;
    }

    public User setOccupation(String occupation) {
        this.occupation = occupation;
        return this;
    }

    public User setAnak(int anak) {
        this.anak = anak;
        return this;
    }

    public int getAnak() {
        return anak;
    }

    public User setCicilan(double cicilan) {
        this.cicilan = cicilan;
        return this;
    }

    public double getCicilan() {
        return cicilan;
    }

    public User setPenghasilan(double penghasilan) {
        this.penghasilan = penghasilan;
        return this;
    }

    public double getPenghasilan() {
        return penghasilan;
    }

    public User setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public String getPhone() {
        return phone;
    }
}
