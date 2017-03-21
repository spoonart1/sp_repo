package javasign.com.dompetsehat.ui.activities.ask;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import java.util.ArrayList;
import java.util.List;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.base.BaseActivity;
import javasign.com.dompetsehat.services.mandrill.EmailMessage;
import javasign.com.dompetsehat.services.mandrill.MandrillMessage;
import javasign.com.dompetsehat.services.mandrill.Recipient;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.utils.MCryptNew;
import javasign.com.dompetsehat.utils.Validate;
import javasign.com.dompetsehat.utils.Words;
import lecho.lib.hellocharts.model.Line;

/**
 * Created by lafran on 1/26/17.
 */

public class AskActivity extends BaseActivity {

  @Bind(R.id.btn_send) Button btn_send;
  @Bind(R.id.title) TextView title;
  @Bind(R.id.et_email) EditText et_email;
  @Bind(R.id.et_msg) EditText et_msg;
  @Bind(R.id.rootview) LinearLayout rootview;
  private MCryptNew mCryptNew = new MCryptNew();

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_ask);
    ButterKnife.bind(this);
    getActivityComponent().inject(this);
    setTitle(R.string.help);
    init();
  }

  @Override public void setTitle(CharSequence title) {
    TextView tv_title = ButterKnife.findById(this, R.id.tv_title);
    tv_title.setText(title);
  }

  @OnClick(R.id.ic_back) void onBack() {
    finish();
  }

  @OnClick(R.id.btn_send) void sendComplain() {
    if (!Validate.isValidEmail(et_email.getText().toString())) {
      et_email.setError(getString(R.string.error_email_invalid));
      return;
    }
    EmailMessage message = new EmailMessage();
    message.setFromEmail("info@dompetsehat.com");
    message.setFromName("DompetSehat");
    //message.setHtml("<p>Crash report</p>");
    message.setText(et_msg.getText().toString());
    message.setSubject(title.getText().toString());

    List<Recipient> recipients = new ArrayList<>();
    Recipient recipient = new Recipient();
    recipient.setEmail("dev@dompetsehat.com");
    recipient.setName("Developer DompetSehat");
    recipients.add(recipient);
    recipient.setEmail(et_email.getText().toString());
    recipient.setName("User DompetSehat");
    recipients.add(recipient);

    message.setTo(recipients);

    MandrillMessage allMessage =
        new MandrillMessage(mCryptNew.decrypt(getString(R.string.mandril_key)));
    allMessage.setMessage(message);
    allMessage.send(this, new MandrillMessage.AsyncTaskInterface() {
      @Override public void onPostExecute() {
        Helper.showCustomSnackBar(rootview, getLayoutInflater(), "Email berhasil di kirim", true,
            ContextCompat.getColor(getBaseContext(), R.color.color_orange), Gravity.BOTTOM);
        title.setText("");
        et_email.setText("");
        et_msg.setText("");
      }

      @Override public void doInBackground() {

      }
    });
  }

  //Intent i = new Intent(Intent.ACTION_SEND);
  //i.setType("message/rfc822");
  //i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"info@dompetsehat.com"});
  //i.putExtra(Intent.EXTRA_SUBJECT,title.getText().toString());
  //i.putExtra(Intent.EXTRA_TEXT   ,et_email.getText().toString());
  //try {
  //  startActivity(Intent.createChooser(i, "Send mail..."));
  //} catch (android.content.ActivityNotFoundException ex) {
  //  Toast.makeText(this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
  //}
  //Intent intent = new Intent(Intent.ACTION_SENDTO); // it's not ACTION_SEND
  //intent.setType("text/plain");
  //intent.putExtra(Intent.EXTRA_SUBJECT,title.getText().toString());
  //intent.putExtra(Intent.EXTRA_TEXT,et_msg.getText().toString());
  //intent.setData(Uri.parse("mailto:info@dompetsehat.com")); // or just "mailto:" for blank
  //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // this will make such that when user returns to your app, your app is displayed, instead of the email app.
  //startActivity(intent);

  private void init() {
    Words.setButtonToListen(btn_send, title, et_msg);
  }
}
