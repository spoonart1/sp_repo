package javasign.com.dompetsehat.ui.activities.webview;

import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.base.BaseActivity;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by lafran on 1/5/17.
 */

public class WebLoaderActivity extends BaseActivity {

  @Bind(R.id.webView) WebView webView;
  @Bind(R.id.progressbar) ProgressBar progressbar;
  @Bind(R.id.ic_back) View ic_back;
  private String url = null;

  public static final int INPUT_FILE_REQUEST_CODE = 1;
  public static final String EXTRA_FROM_NOTIFICATION = "EXTRA_FROM_NOTIFICATION";

  private ValueCallback<Uri[]> mFilePathCallback;
  private String mCameraPhotoPath;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_webloader);
    ButterKnife.bind(this);
    getActivityComponent().inject(this);
    setTitle("");
    init();
  }

  @Override public void setTitle(CharSequence title) {
    TextView tv_title = ButterKnife.findById(this, R.id.tv_title);
    tv_title.setText(title);
  }

  @OnClick(R.id.ic_back) void onBack(){
    finish();
  }

  @OnClick(R.id.btn_more) void loadUrl(View v){
    //v.setVisibility(View.GONE);
    //beginLoadUrl(url);
  }

  private void init() {
    if(getIntent() == null){
      throw new Error("Intent extra not found");
    }
    if(getIntent().hasExtra("title")){
      setTitle(getIntent().getStringExtra("title"));
    }

    url = TextUtils.isEmpty(getIntent().getStringExtra("url")) ? null : getIntent().getStringExtra("url");
    beginLoadUrl(url);
  }

  private void beginLoadUrl(String url){
    if(url == null){
      throw new Error("URL is NULL");
    }
    ic_back.setVisibility(View.GONE);
    setTitle(getString(R.string.loading_page));
    webView.getSettings().setJavaScriptEnabled(true);
    webView.loadUrl(url);

    webView.setWebViewClient(new WebViewClient(){

      boolean loadingFinished = true;
      boolean redirect = false;

      @Override
      public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        super.onReceivedSslError(view, handler, error);
        handler.proceed();
      }

      @Override public void onLoadResource(WebView view, String url) {
        super.onLoadResource(view, url);
      }

      @Override
      public boolean shouldOverrideUrlLoading(WebView view, String urlNewString) {

        if (!loadingFinished) {
          redirect = true;
        }

        loadingFinished = false;
        view.loadUrl(urlNewString);
        return true;
      }

      @Override public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        return super.shouldOverrideUrlLoading(view, request);
      }

      @Override
      public void onPageFinished(WebView view, String url) {
        if(!redirect){
          loadingFinished = true;
        }

        if(loadingFinished && !redirect){
          progressbar.setVisibility(View.GONE);
          ic_back.setVisibility(View.VISIBLE);
          setTitle("");
        } else{
          redirect = false;
        }

      }
    });

    webView.setWebChromeClient(new WebChromeClient() {
      public boolean onShowFileChooser(
          WebView webView, ValueCallback<Uri[]> filePathCallback,
          WebChromeClient.FileChooserParams fileChooserParams) {
        if(mFilePathCallback != null) {
          mFilePathCallback.onReceiveValue(null);
        }
        mFilePathCallback = filePathCallback;

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
          // Create the File where the photo should go
          File photoFile = null;
          try {
            photoFile = createImageFile();
            takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath);
          } catch (IOException ex) {
            // Error occurred while creating the File
            //Log.e(TAG, "Unable to create Image File", ex);
          }

          // Continue only if the File was successfully created
          if (photoFile != null) {
            mCameraPhotoPath = "file:" + photoFile.getAbsolutePath();
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(photoFile));
          } else {
            takePictureIntent = null;
          }
        }

        Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
        contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
        contentSelectionIntent.setType("image/*");

        Intent[] intentArray;
        if(takePictureIntent != null) {
          intentArray = new Intent[]{takePictureIntent};
        } else {
          intentArray = new Intent[0];
        }

        Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
        chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
        chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);

        startActivityForResult(chooserIntent, INPUT_FILE_REQUEST_CODE);

        return true;
      }
    });
  }

  private File createImageFile() throws IOException {
    // Create an image file name
    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    String imageFileName = "JPEG_" + timeStamp + "_";
    File storageDir = Environment.getExternalStoragePublicDirectory(
        Environment.DIRECTORY_PICTURES);
    File imageFile = File.createTempFile(
        imageFileName,  /* prefix */
        ".jpg",         /* suffix */
        storageDir      /* directory */
    );
    return imageFile;
  }

  @TargetApi(Build.VERSION_CODES.HONEYCOMB)
  private void setUpWebViewDefaults(WebView webView) {
    WebSettings settings = webView.getSettings();

    // Enable Javascript
    settings.setJavaScriptEnabled(true);

    // Use WideViewport and Zoom out if there is no viewport defined
    settings.setUseWideViewPort(true);
    settings.setLoadWithOverviewMode(true);

    // Enable pinch to zoom without the zoom buttons
    settings.setBuiltInZoomControls(true);

    if(Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
      // Hide the zoom controls for HONEYCOMB+
      settings.setDisplayZoomControls(false);
    }

    // Enable remote debugging via chrome://inspect
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      WebView.setWebContentsDebuggingEnabled(true);
    }

    // We set the WebViewClient to ensure links are consumed by the WebView rather
    // than passed to a browser if it can
    webView.setWebViewClient(new WebViewClient());
  }

  @Override
  public void onActivityResult (int requestCode, int resultCode, Intent data) {
    if(requestCode != INPUT_FILE_REQUEST_CODE || mFilePathCallback == null) {
      super.onActivityResult(requestCode, resultCode, data);
      return;
    }

    Uri[] results = null;

    // Check that the response is a good one
    if(resultCode == RESULT_OK) {
      if(data == null) {
        // If there is not data, then we may have taken a photo
        if(mCameraPhotoPath != null) {
          results = new Uri[]{Uri.parse(mCameraPhotoPath)};
        }
      } else {
        String dataString = data.getDataString();
        if (dataString != null) {
          results = new Uri[]{Uri.parse(dataString)};
        }
      }
    }

    mFilePathCallback.onReceiveValue(results);
    mFilePathCallback = null;
    return;
  }
}
