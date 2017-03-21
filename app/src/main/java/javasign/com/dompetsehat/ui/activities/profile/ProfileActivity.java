package javasign.com.dompetsehat.ui.activities.profile;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.facebook.CallbackManager;
import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import javasign.com.dompetsehat.BuildConfig;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.VeryFund.GeneralHelper;
import javasign.com.dompetsehat.VeryFund.RESTClient;
import javasign.com.dompetsehat.base.BaseActivity;
import javasign.com.dompetsehat.base.MyCustomApplication;
import javasign.com.dompetsehat.models.User;
import javasign.com.dompetsehat.presenter.profile.ProfileInterface;
import javasign.com.dompetsehat.presenter.profile.ProfilePresenter;
import javasign.com.dompetsehat.presenter.sync.SyncPresenter;
import javasign.com.dompetsehat.ui.activities.badges.BadgesActivity;
import javasign.com.dompetsehat.ui.activities.help.HelpActivity;
import javasign.com.dompetsehat.ui.activities.landing.LandingPages;
import javasign.com.dompetsehat.ui.activities.login.SignInActivity;
import javasign.com.dompetsehat.ui.activities.setting.SettingActivity;
import javasign.com.dompetsehat.ui.activities.verification.EmailVerificationActivity;
import javasign.com.dompetsehat.ui.activities.webview.WebLoaderActivity;
import javasign.com.dompetsehat.ui.globaladapters.StatisticAdapter;
import javasign.com.dompetsehat.utils.AdsUtils;
import javasign.com.dompetsehat.utils.DbHelper;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.utils.MCryptNew;
import javasign.com.dompetsehat.utils.RxBus;
import javasign.com.dompetsehat.utils.SessionManager;
import javasign.com.dompetsehat.utils.State;
import javax.inject.Inject;
import org.json.JSONObject;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

public class ProfileActivity extends BaseActivity implements ProfileInterface {

  private int RESULT_LOAD_IMG = 1;

  @Bind(R.id.tv_username) TextView tv_username;
  @Bind(R.id.tv_email) TextView tv_email;
  @Bind(R.id.btn_action) View btn_action;
  @Bind(R.id.recycleview) RecyclerView recyclerView;
  @Bind(R.id.content_buttons) View content_buttons;
  @Bind(R.id.iv_photo) RoundedImageView iv_photo;
  @Bind(R.id.rootview) LinearLayout rootview;
  @Bind(R.id.banner) ImageView banner;
  @Bind(R.id.btn_done) Button btn_done;
  @Bind(R.id.btn_level) Button btn_level;
  @Bind(R.id.btn_logout) LinearLayout btn_setting;
  @Bind({ R.id.t1, R.id.t2, R.id.t3, R.id.t4, R.id.t5, R.id.t6 }) TextView[] textViews;

  private DbHelper db;
  private SessionManager session;
  private User user;
  private MCryptNew mcrypt = new MCryptNew();
  private GeneralHelper helper = GeneralHelper.getInstance();
  protected boolean isRegisteredUser = false;
  private StatisticAdapter adapter;

  // avesina
  public boolean FLAG_STATUS = false;
  CallbackManager callbackManager;
  Gson gson = new Gson();
  @Inject ProfilePresenter presenter;
  @Inject SyncPresenter presenterSync;
  private RxBus rxBus;
  private ProgressDialog pDialog;

  static int REQUEST_DATA = 1;
  private Uri mCropImageUri;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_profile);
    ButterKnife.bind(this);
    db = DbHelper.getInstance(ProfileActivity.this);
    session = new SessionManager(ProfileActivity.this);

    // avesina
    getActivityComponent().inject(this);
    presenter.attachView(this);
    presenter.catchRefresh();
    rxBus = MyCustomApplication.getRxBus();
    pDialog = new ProgressDialog(getActivityComponent().context());
    pDialog.setMessage(getString(R.string.dialog_loading));
    pDialog.setCancelable(false);
    pDialog.setCanceledOnTouchOutside(false);
    State.registerBroadCast(this, onDataUpdated);
    Helper.trackThis(this, "user membuka tampilan profile");
    presenter.attachView(this);
    initialization();
  }

  @Override protected void onResume() {
    super.onResume();
    presenter.setProfile();
    presenter.setAdapter(REQUEST_DATA);
  }

  @OnClick(R.id.banner) void gotoBanner(View v) {
    String url = "https://koinworks.com/lenders/new?utm_source=DSPRF";
    Helper.goTo(this, WebLoaderActivity.class, new Intent().putExtra("url", url));
    Helper.trackThis(this, "User klik KoinWorks di bottom profile");
  }

  private void initialization() {
    AdsUtils.adsKoinWorks(this, banner,
        ((MyCustomApplication) getApplication()).checkScreenResolution());
    btn_setting.setClickable(true);
    adapter = new StatisticAdapter(this, isRegisteredUser);
    final LinearLayoutManager layoutManager =
        new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
    recyclerView.setLayoutManager(layoutManager);
    recyclerView.setAdapter(adapter);
    presenter.setAdapter(REQUEST_DATA);
    presenter.checkBadge();

    isRegisteredUser = session.isLoggedIn();

    if (!isRegisteredUser) {
      GeneralHelper.greenStatusBar(getWindow(), getResources());
      disableSomeView();
      return;
    }

    hideUnnecessary();
  }

  @Override public void setProfile(String username, String phone, String email, String password,
      String tanggal_birthday, Double p, Integer anak, Bitmap bitmap, boolean fb_connected,
      String fb_id) {
    runOnUiThread(() -> {
      if (username != null) {
        tv_username.setText(username);
      }
      if (email != null) {
        tv_email.setText(email);
      }
      if (bitmap != null) {
        iv_photo.setColorFilter(null);
        iv_photo.setImageBitmap(bitmap);
      } else {
        iv_photo.setImageBitmap(
            BitmapFactory.decodeResource(getResources(), R.drawable.not_registered_user));
        iv_photo.setColorFilter(ContextCompat.getColor(this, R.color.blue_grey_700));
      }
    });
  }

  @Override public void setPhoneEmail(String phone, String email) {

  }

  @Override public void finishValidate() {

  }

  @Override public void setLevel(String level) {
    runOnUiThread(() -> {
      Timber.e("avesina level "+level);
      btn_level.setText(level.trim());
    });
  }

  private void setFieldAsRegisteredUser() {
    int textcolor = Color.BLACK;
    int softTextColor = ContextCompat.getColor(this, R.color.grey_700);
    tv_username.setTextColor(textcolor);
    tv_email.setTextColor(softTextColor);
    btn_done.setTextColor(Helper.GREEN_DOMPET_COLOR);
    ViewCompat.setBackground(btn_done, ContextCompat.getDrawable(this, R.drawable.button_white));
    try {
      int resId = R.color.state_button_white_landing_register;
      ColorStateList colors = getResources().getColorStateList(resId);
      btn_level.setTextColor(colors);
    } catch (Exception e) {
      // handle exceptions
    }

    ViewCompat.setBackground(btn_level,
        ContextCompat.getDrawable(this, R.drawable.button_rounded_white_landing_register_stroked));
    rootview.setBackgroundColor(Color.WHITE);

    if (BuildConfig.DEBUG) iv_photo.setColorFilter(softTextColor);
  }

  private void disableSomeView() {
    for (TextView t : textViews) {
      t.setEnabled(false);
    }
    tv_username.setVisibility(View.GONE);
    content_buttons.setVisibility(View.GONE);
  }

  private void hideUnnecessary() {
    ButterKnife.findById(this, R.id.btn_level).setVisibility(View.VISIBLE);
    Button btn_login = ButterKnife.findById(this, R.id.btn_login);
    btn_login.setVisibility(View.GONE);
    btn_login.setEnabled(false);
    ButterKnife.findById(this, R.id.btn_signUp).setVisibility(View.GONE);

    setFieldAsRegisteredUser();
  }

  @OnClick(R.id.btn_login) void doLogin(View v) {
    startActivity(new Intent(this, SignInActivity.class));
  }

  @OnClick(R.id.btn_signUp) void doRegister(View v) {
    startActivity(new Intent(this, LandingPages.class));
  }

  @OnClick(R.id.iv_photo) void clickPhoto() {
    changePicture();
  }

  @OnClick(R.id.tv_photo) void clickText() {
    changePicture();
  }

  @OnClick(R.id.btn_action) void verifyEmail() {
    Helper.goTo(this, EmailVerificationActivity.class);
  }

  public void showEmailVerificationButton(boolean show) {
    btn_action.setVisibility(show ? View.VISIBLE :  View.GONE);
  }

  private void changePicture() {
    if (!isRegisteredUser) return;
    Intent pickIntent = new Intent();
    pickIntent.setType("image/*");
    pickIntent.setAction(Intent.ACTION_GET_CONTENT);

    Intent takePhotoIntent =
        new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT,
            CropImage.getCaptureImageOutputUri(this));

    String pickTitle = "Select or take a new Picture"; // Or get from strings.xml
    Intent chooserIntent = Intent.createChooser(pickIntent, pickTitle);
    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] { takePhotoIntent });
    startActivityForResult(chooserIntent, RESULT_LOAD_IMG);
  }

  private void startCropImageActivity(Uri imageUri) {
    Timber.e("avesina image data keren " + imageUri);
    CropImage.activity(imageUri)
        .setGuidelines(CropImageView.Guidelines.ON)
        .setMinCropResultSize(500, 500)
        .setMaxCropResultSize(1000, 1000)
        .setAutoZoomEnabled(true)
        .setRequestedSize(600, 600)
        .setAllowRotation(true)
        .setAspectRatio(1, 1)
        .setOutputCompressQuality(80)
        .setCropShape(CropImageView.CropShape.OVAL)
        .setMultiTouchEnabled(true)
        .start(this);
  }

  @OnClick(R.id.btn_level) void openBadges(View v) {
    if (!isRegisteredUser) return;
    Intent in = new Intent(ProfileActivity.this, BadgesActivity.class);
    startActivity(in);
  }

  @OnClick(R.id.btn_done) void doFinish(View v) {
    finish();
  }

  @OnClick(R.id.btn_logout) void doLogout(View view) {
    Helper.trackThis(this, "user membuka pengaturan dari profile");
    Helper.goTo(this, SettingActivity.class);
  }

  @OnClick(R.id.btn_edit) void doEdit(View view) {
    if (!isRegisteredUser) return;

    Helper.goTo(this, EditProfileActivity.class);
  }

  @OnClick(R.id.ll_help) void openHelp(View v) {
    if (!isRegisteredUser) return;

    Helper.trackThis(this, "user klik bantuan di profile");
    Helper.goTo(this, HelpActivity.class);
  }

  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    Timber.e("request code " + requestCode);
    if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
      CropImage.ActivityResult result = CropImage.getActivityResult(data);
      if (resultCode == RESULT_OK) {
        Uri resultUri = result.getUri();
        presenter.saveAvatar(resultUri);
        iv_photo.setColorFilter(null);
        iv_photo.setImageURI(resultUri);
        Helper.trackThis(this, "user mengganti profile picture");
      } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
        Exception error = result.getError();
      }
    } else if (requestCode == RESULT_LOAD_IMG) {
      Timber.e("avesina image data " + data);
      Uri imageUri = CropImage.getPickImageResultUri(this, data);
      Timber.e("avesina image imageUri " + imageUri);
      // For API >= 23 we need to check specifically that we have permissions to read external storage.
      if (CropImage.isReadExternalStoragePermissionsRequired(this, imageUri)) {
        Timber.e("avesina image here 1");
        // request permissions and handle the result in onRequestPermissionsResult()
        mCropImageUri = imageUri;
        requestPermissions(new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },
            RESULT_LOAD_IMG);
      } else {
        Timber.e("avesina image here 2");
        // no permissions required or already grunted, can start crop image activity
        startCropImageActivity(imageUri);
      }
    }
  }

  public void onRequestPermissionsResult(int requestCode, String permissions[],
      int[] grantResults) {
    if (requestCode == CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE) {
      if (mCropImageUri != null
          && grantResults.length > 0
          && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        // required permissions granted, start crop image activity
        startCropImageActivity(mCropImageUri);
      } else {
        Toast.makeText(this, "Cancelling, required permissions are not granted", Toast.LENGTH_LONG)
            .show();
      }
    }
  }

  private void copyInputStreamToFile(InputStream in, File file) {
    try {
      OutputStream out = new FileOutputStream(file);
      byte[] buf = new byte[1024];
      int len;
      while ((len = in.read(buf)) > 0) {
        out.write(buf, 0, len);
      }
      out.close();
      in.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void sendJSONRequestUpdatePhoto(Bitmap bitmap, String fileName) {
    RESTClient.getRestClient()
        .userinfo_updatePhoto(session.getAuthToken(), encodeTobase64(bitmap), fileName,
            new Callback<Response>() {
              @Override public void success(Response response_api, Response response) {
                try {
                  JSONObject json = new JSONObject(helper.responseRetrofitToString(response_api));
                  if (json.getString("status").equals("OK")) {
                    //do nothing
                  } else {
                    Toast.makeText(ProfileActivity.this, json.getString("message"),
                        Toast.LENGTH_LONG).show();
                  }
                } catch (Exception e) {
                  Toast.makeText(ProfileActivity.this, getString(R.string.please_try_again),
                      Toast.LENGTH_LONG).show();
                }
              }

              @Override public void failure(RetrofitError error) {
                Toast.makeText(ProfileActivity.this, getString(R.string.please_try_again),
                    Toast.LENGTH_LONG).show();
              }
            });
  }

  public String encodeTobase64(Bitmap image) {
    Bitmap immagex = image;
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    immagex.compress(Bitmap.CompressFormat.JPEG, 100, baos);
    byte[] b = baos.toByteArray();
    String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);
    return imageEncoded;
  }

  @Override protected void onDestroy() {
    helper.dismissProgressDialog(pDialog);
    State.unRegisterBroadCast(this, onDataUpdated);
    if (presenter != null) {
      presenter.detachView();
    }
    super.onDestroy();
  }

  @Override public void onLoad(int REQUEST_ID) {

  }

  @Override public void onComplete(int REQUEST_ID) {

  }

  @Override public void onError(int REQUEST_ID) {

  }

  @Override public void onNext(int REQUEST_ID) {

  }

  @Override public void putData(Object data) {
    Thread thread = new Thread(new Runnable() {
      @Override public void run() {
        HashMap<String, Object> set = (HashMap<String, Object>) data;
        Integer[] counts = (Integer[]) set.get("counts");
        Double[] values = (Double[]) set.get("values");
        adapter = new StatisticAdapter(ProfileActivity.this, isRegisteredUser, counts, values);
        final LinearLayoutManager layoutManager =
            new LinearLayoutManager(ProfileActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
      }
    });
    thread.run();
  }

  private BroadcastReceiver onDataUpdated = new BroadcastReceiver() {
    @Override public void onReceive(Context context, Intent intent) {
      String eventname = intent.getStringExtra(State.DEFAULT_KEY_STR);

      if (eventname.equalsIgnoreCase(State.EVENT_CONTENT_NEED_UPDATE)) {
        initialization();
      }
    }
  };
}