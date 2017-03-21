package javasign.com.dompetsehat.ui.activities.institusi.loader;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.SparseIntArray;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.mikepenz.iconics.view.IconicsTextView;
import im.delight.android.webview.AdvancedWebView;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javasign.com.dompetsehat.BuildConfig;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.VeryFund.GeneralHelper;
import javasign.com.dompetsehat.ui.activities.RuntimePermissionsActivity;
import javasign.com.dompetsehat.ui.activities.closing.ClosingActivity;
import javasign.com.dompetsehat.ui.activities.institusi.vendor.manulife.LoginKlikMamiActivity;
import javasign.com.dompetsehat.ui.activities.institusi.vendor.manulife.RegisterMamiActivity;
import javasign.com.dompetsehat.ui.activities.main.NewMainActivity;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.view.AccountView;
import timber.log.Timber;

/**
 * Created by bastianbentra on 8/22/16.
 */
public class WebviewInstutisiActivity extends RuntimePermissionsActivity {

  public static final String URL_KEY = "url";

  public final static String URL_MAMI_DISCLAIMER =
      "https://www.klikmami.com/Reksadana/Disclaimer/RegistrationDisclaimer";

  public final static String URL_NEW_ACCOUNT =
      "https://www.klikmami.com/Reksadana/AccountOpening/NewAccount";

  public final static String URL_MAMI_HOME = "https://www.klikmami.com/";
  public final static String URL_MAMI_REQUEST = "http://111.221.107.97/mmincwsuatdev/Home/Index";
  public final static String URL_MAMI_REQUEST_NEW_PROFILE =
      "http://111.221.107.97/mmincwsuatdev/NewProfile/Index";
  public final static String URL_MAMI_REQUEST_WITH_ENCRIPT =
      "http://111.221.107.97/mmincwsuatrefsvc2/Home/Index";
  public final static String URL_MAMI_FORGOT_PASS =
      "https://www.klikmami.com/Reksadana/Authentication/RequestPassword";

  public final static String FLAG_TO_HIDE = "else";

  @Bind(R.id.webView) WebView webView;
  @Bind(R.id.ic_menu) View ic_menu;
  @Bind(R.id.progressbar) ProgressBar progressbar;
  @Bind(R.id.ic_back) View ic_back;
  @Bind(R.id.tab_container) View v;

  private String mUrl = URL_MAMI_HOME;
  public static final int INPUT_FILE_REQUEST_CODE = 1;
  public static final int FILECHOOSER_RESULTCODE = 2;
  public static final String EXTRA_FROM_NOTIFICATION = "EXTRA_FROM_NOTIFICATION";
  private ValueCallback<Uri[]> mFilePathCallback;
  private ValueCallback<Uri> mUploadMessage;
  private Uri mCapturedImageURI = null;
  private String mCameraPhotoPath;
  private SparseIntArray mErrorString;
  private static final int REQUEST_PERMISSIONS = 20;
  Intent chooserIntent = null;


  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_register_manulife_webview);
    ButterKnife.bind(this);

    setTitle("Sudah punya akun Manulife? ");
    init();
  }

  @Override public void setTitle(CharSequence title) {
    TextView tv = ButterKnife.findById(this, R.id.tv_title);
    IconicsTextView ic_back = ButterKnife.findById(this, R.id.ic_back);

    tv.setTextColor(Color.WHITE);
    ic_back.setTextColor(Color.WHITE);
    tv.setText(title);
  }

  @OnClick(R.id.ic_back) void onBack() {
    finish();
  }

  @OnClick(R.id.ic_menu) void doLoginVendor() {
    Helper.goTo(this, LoginKlikMamiActivity.class);
    finish();
  }

  private void init() {

    Bundle b = getIntent().getExtras();
    String from = "";
    int accentColor = AccountView.MNL_COLOR;

    if (b == null) {
    } else {
      mUrl = b.getString("url", "");
      from = b.getString("from", "");

      accentColor = b.getInt("accent-color", AccountView.MNL_COLOR);
    }

    Helper.setbackgroundColor(v, accentColor);
    GeneralHelper.statusBarColor(getWindow(), accentColor);

    progressbar.getIndeterminateDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
    hideTitleAndLoginBtnOnBar(from);
    beginLoad(mUrl);
  }

  private void hideTitleAndLoginBtnOnBar(String from) {
    if (from.equalsIgnoreCase("finalregsitration")
        || mUrl.equalsIgnoreCase(URL_MAMI_HOME)
        || from.equalsIgnoreCase(FLAG_TO_HIDE)) {
      setTitle("");
      ic_menu.setVisibility(View.GONE);
    }
  }

  private void beginLoad(String url) {
    Timber.e("url "+url);
    webView.getSettings().setJavaScriptEnabled(true);
    setUpWebViewDefaults(webView);
    if (Build.VERSION.SDK_INT >= 19) {
      webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
    }
    else if(Build.VERSION.SDK_INT >=11 && Build.VERSION.SDK_INT < 19) {
      webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }
    webView.setWebViewClient(new WebViewClient() {

      @Override public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        showProgress();
      }

      @Override public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        hideProgress();
      }

      @Override public void onReceivedHttpError(WebView view, WebResourceRequest request,
          WebResourceResponse errorResponse) {
        super.onReceivedHttpError(view, request, errorResponse);
        hideProgress();

      }

      @Override public void onReceivedError(WebView view, WebResourceRequest request,
          WebResourceError error) {
        super.onReceivedError(view, request, error);
        hideProgress();
      }

      @Override
      public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        if(BuildConfig.DEBUG_MODE) {
          handler.proceed();
          //final AlertDialog.Builder builder = new AlertDialog.Builder(WebviewInstutisiActivity.this);
          //builder.setMessage("SSL certificate is invalid");
          //builder.setPositiveButton("continue", new DialogInterface.OnClickListener() {
          //  @Override
          //  public void onClick(DialogInterface dialog, int which) {
          //    handler.proceed();
          //  }
          //});
          //builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
          //  @Override
          //  public void onClick(DialogInterface dialog, int which) {
          //    handler.cancel();
          //  }
          //});
          //final AlertDialog dialog = builder.create();
          //dialog.show();
        }else {
          super.onReceivedSslError(view, handler, error);
        }
      }

      @Override public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        return super.shouldOverrideUrlLoading(view, request);
      }


      @Override public void onLoadResource(WebView view, String url) {
        if (url.equalsIgnoreCase(URL_NEW_ACCOUNT)) {
          Helper.goTo(WebviewInstutisiActivity.this, RegisterMamiActivity.class);
          finish();
        }
        super.onLoadResource(view, url);
      }

      private void hideProgress() {
        ic_back.setVisibility(View.VISIBLE);
        progressbar.setVisibility(View.GONE);
      }

      private void showProgress() {
        ic_back.setVisibility(View.GONE);
        progressbar.setVisibility(View.VISIBLE);
      }
    });
    webView.setWebChromeClient(new WebChromeClient() {
      public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback,
          WebChromeClient.FileChooserParams fileChooserParams) {
        if (mFilePathCallback != null) {
          mFilePathCallback.onReceiveValue(null);
        }
        mFilePathCallback = filePathCallback;

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(WebviewInstutisiActivity.this.getPackageManager())
            != null) {
          // Create the File where the photo should go
          File photoFile = null;
          try {
            photoFile = createImageFile();
            takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath);
          } catch (IOException ex) {
            // Error occurred while creating the File
            Timber.e("Unable to create Image File" + ex);
          }

          // Continue only if the File was successfully created
          if (photoFile != null) {
            mCameraPhotoPath = "file:" + photoFile.getAbsolutePath();
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
          } else {
            takePictureIntent = null;
          }
        }

        Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
        contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
        contentSelectionIntent.setType("image/*");

        Intent[] intentArray;
        if (takePictureIntent != null) {
          intentArray = new Intent[] { takePictureIntent };
        } else {
          intentArray = new Intent[0];
        }

        chooserIntent = new Intent(Intent.ACTION_CHOOSER);
        chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
        chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);

        WebviewInstutisiActivity.super.requestAppPermissions(new String[] {
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        }, R.string.runtime_permissions_txt, REQUEST_PERMISSIONS);

        return true;
      }

      public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
        mUploadMessage = uploadMsg;
        // Create AndroidExampleFolder at sdcard
        // Create AndroidExampleFolder at sdcard
        File imageStorageDir = new File(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES)
            , "AndroidExampleFolder");
        if (!imageStorageDir.exists()) {
          // Create AndroidExampleFolder at sdcard
          imageStorageDir.mkdirs();
        }
        // Create camera captured image file path and name
        File file = new File(
            imageStorageDir + File.separator + "IMG_"
                + String.valueOf(System.currentTimeMillis())
                + ".jpg");
        mCapturedImageURI = Uri.fromFile(file);
        // Camera capture image intent
        final Intent captureIntent = new Intent(
            android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        // Create file chooser intent
        Intent chooserIntent = Intent.createChooser(i, "Image Chooser");
        // Set camera intent to file chooser
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS
            , new Parcelable[] { captureIntent });
        // On select image call onActivityResult method of activity
        startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE);
      }

      //For Android 4.1+ only
      protected void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
        openFileChooser(uploadMsg, acceptType);
      }

      protected void openFileChooser(ValueCallback<Uri> uploadMsg) {
        openFileChooser(uploadMsg, "");
      }
    });
    webView.loadUrl(url);
  }

  private void showAttachmentDialog(ValueCallback<Uri> uploadMsg) {
    this.mUploadMessage = uploadMsg;

    Intent i = new Intent(Intent.ACTION_GET_CONTENT);
    i.addCategory(Intent.CATEGORY_OPENABLE);
    i.setType("*/*");

    this.startActivityForResult(Intent.createChooser(i, "Choose type of attachment"), FILECHOOSER_RESULTCODE);
  }

  private File createImageFile() throws IOException {
    // Create an image file name
    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    String imageFileName = "JPEG_" + timeStamp + "_";
    File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
    File imageFile = File.createTempFile(imageFileName,  /* prefix */
        ".jpg",         /* suffix */
        storageDir      /* directory */);
    return imageFile;
  }

  @TargetApi(Build.VERSION_CODES.HONEYCOMB) private void setUpWebViewDefaults(WebView webView) {
    WebSettings settings = webView.getSettings();

    // Enable Javascript
    settings.setJavaScriptEnabled(true);
    settings.setAllowFileAccess(true);
    settings.setAllowContentAccess(true);

    // Use WideViewport and Zoom out if there is no viewport defined
    settings.setUseWideViewPort(true);
    settings.setLoadWithOverviewMode(true);

    // Enable pinch to zoom without the zoom buttons
    settings.setBuiltInZoomControls(true);

    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
      // Hide the zoom controls for HONEYCOMB+
      settings.setDisplayZoomControls(false);
    }

    // Enable remote debugging via chrome://inspect
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      WebView.setWebContentsDebuggingEnabled(true);
    }

    // We set the WebViewClient to ensure links are consumed by the WebView rather
    // than passed to a browser if it can
    //webView.setWebViewClient(new WebViewClient());
  }

  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == INPUT_FILE_REQUEST_CODE && mFilePathCallback != null) {
      super.onActivityResult(requestCode, resultCode, data);
      Uri[] results = null;

      // Check that the response is a good one
      if (resultCode == Activity.RESULT_OK) {
        if (data == null) {
          // If there is not data, then we may have taken a photo
          if (mCameraPhotoPath != null) {
            results = new Uri[] { Uri.parse(mCameraPhotoPath) };
          }
        } else {
          String dataString = data.getDataString();
          if (dataString != null) {
            results = new Uri[] { Uri.parse(dataString) };
          }
        }
      }

      mFilePathCallback.onReceiveValue(results);
      mFilePathCallback = null;
      return;
    } else if (requestCode == FILECHOOSER_RESULTCODE) {
      if (requestCode == FILECHOOSER_RESULTCODE) {
        if (null == this.mUploadMessage) {
          return;
        }
        Uri result = null;
        try {
          if (resultCode != RESULT_OK) {
            result = null;
          } else {
            // retrieve from the private variable if the intent is null
            result = data == null ? mCapturedImageURI : data.getData();
          }
        } catch (Exception e) {
          Toast.makeText(getApplicationContext(), "activity :" + e, Toast.LENGTH_LONG).show();
        }
        mUploadMessage.onReceiveValue(result);
        mUploadMessage = null;
      }
    }
  }


  @Override public void onPermissionsGranted(int requestCode) {
    if (chooserIntent != null) {
      startActivityForResult(chooserIntent, INPUT_FILE_REQUEST_CODE);
    }
  }

  private void presentClosingActivityAfterDoneRegister() {
    startActivity(new Intent(this, ClosingActivity.class).putExtra(ClosingActivity.TEXT_2_KEY,
        "Dengan terdaftar di klikmami, kini anda dapat merencakan keuangan dengan berinvestasi di reksa dana!")
        .putExtra(ClosingActivity.BUTTON_LABEL, "Tutup")
        .putExtra(ClosingActivity.TEXT_3_KEY,
            "Selanjutnya dalam maksimal waktu 2x24 jam akun anda akan aktif & berhasil menjadi nasabah Reksa dana Manulife Aset Manajemen Indonesia."));
  }

  @Override public void onBackPressed() {
    if (getIntent().getExtras() != null) {
      if (getIntent().getExtras()
          .getString("from", "")
          .equalsIgnoreCase(WebviewInstutisiActivity.FLAG_TO_HIDE)) {
        onBack();
      } else {
        finish();
      }
    } else {
      finish();
    }
  }

  public void gotoHome() {
    startActivity(new Intent(WebviewInstutisiActivity.this, NewMainActivity.class).addFlags(
        Intent.FLAG_ACTIVITY_CLEAR_TOP
            | Intent.FLAG_ACTIVITY_CLEAR_TASK
            | Intent.FLAG_ACTIVITY_NEW_TASK));
  }
}
