package javasign.com.dompetsehat.ui.event;

import android.graphics.Bitmap;
import timber.log.Timber;

/**
 * Created by avesina on 10/14/16.
 */

public class ChangeProfile {
  Bitmap bitmap;

  public ChangeProfile setBitmap(Bitmap bitmap) {
    this.bitmap = bitmap;
    Timber.e("image saved");
    return this;
  }

  public Bitmap getBitmap() {
    return bitmap;
  }
}
