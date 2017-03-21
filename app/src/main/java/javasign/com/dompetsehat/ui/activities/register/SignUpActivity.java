package javasign.com.dompetsehat.ui.activities.register;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.rengwuxian.materialedittext.MaterialEditText;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.VeryFund.GeneralHelper;
import javasign.com.dompetsehat.base.BaseActivity;
import javasign.com.dompetsehat.base.MyCustomApplication;
import javasign.com.dompetsehat.ui.activities.login.SignInActivity;
import javasign.com.dompetsehat.utils.DbHelper;
import javasign.com.dompetsehat.utils.MCryptNew;
import javasign.com.dompetsehat.utils.SessionManager;

public class SignUpActivity extends BaseActivity{

    private ProgressDialog pDialog;
    private DbHelper db;
    private SessionManager sessionManager;
    private MCryptNew mcrypt = new MCryptNew();
    private GeneralHelper helper = GeneralHelper.getInstance();

    @Bind(R.id.et_email) MaterialEditText et_email;
    @Bind(R.id.et_password) MaterialEditText et_password;
    @Bind(R.id.et_confirm_password) MaterialEditText et_confirm_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);

        MyCustomApplication.initTracker(this,"Page : Sign up");

        db = DbHelper.getInstance(SignUpActivity.this);
        sessionManager = new SessionManager(SignUpActivity.this);

    }

    @OnClick(R.id.btn_signUp) void doRegister(View v){
        boolean isValid = true;
        if (et_email.getText().toString().equals("")) {
            et_email.setError(getString(R.string.error_email_if_blank));
            isValid = false;
        }
        else if(!isEmailValid(et_email.getText().toString())){
            et_email.setError(getString(R.string.error_email_invalid));
            isValid = false;
        }

        if (et_password.getText().toString().equals("")) {
            et_password.setError(getString(R.string.error_password_if_blank));
            isValid = false;
        }
        if(!et_confirm_password.getText().toString().equalsIgnoreCase(et_password.getText().toString())){
            et_confirm_password.setError(getString(R.string.error_password_doesnt_match));
            isValid = false;
        }

        if(!isValid)
            return;

        new AlertDialog.Builder(SignUpActivity.this)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle(getString(R.string.is_this_right))
            .setMessage(getString(R.string.you_are_going_to_register_with)+" \n " + et_email.getText().toString().toLowerCase())
            .setPositiveButton(getString(R.string.register), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendJSONRequestRegister();
                    }
                })
            .setNegativeButton(getString(R.string.no), null)
            .show();
    }

    @OnClick(R.id.img_back) void doBack(View v){
        finish();
    }

    @OnClick(R.id.btn_login) void doLogin(View v){
        startActivity(new Intent(this, SignInActivity.class));
    }

    private void sendJSONRequestRegister() {
        Toast.makeText(SignUpActivity.this, "Should request to API end point", Toast.LENGTH_LONG).show();
    }



    public void setError(EditText et, String message) {
        et.setError(message);
    }

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    @Override
    protected void onStop() {
        helper.dismissProgressDialog(pDialog);
        super.onStop();
    }


    @Override
    protected void onDestroy() {
        helper.dismissProgressDialog(pDialog);
        super.onDestroy();
    }
}