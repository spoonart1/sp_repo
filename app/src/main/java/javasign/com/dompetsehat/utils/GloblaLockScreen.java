package javasign.com.dompetsehat.utils;

import android.content.Context;
import android.content.Intent;

import javasign.com.dompetsehat.PassCodeActivity;
import timber.log.Timber;

/**
 * Created by Spoonart on 9/25/2015.
 */
public class GloblaLockScreen {
    Context context;
    SessionManager sessionManager;
    static String lockPreff;
    public GloblaLockScreen(Context context){
        this.context = context;
        sessionManager = new SessionManager(this.context);
        performPassCode();
        lockPreff = sessionManager.getPrefPascode();
    }
    public static void performPasscodeDialog(Context context){
        new GloblaLockScreen(context);

    }
    public static String getLockPreff(){
        return lockPreff;
    }

    private void performPassCode() {
        Timber.e("passcode "+sessionManager.getPrefPascode());
        if(sessionManager.getPrefPascode().length() > 0){
            Intent i = new Intent(this.context, PassCodeActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra(PassCodeActivity.KEY_TAG_LOCKMODE, PassCodeActivity.ENABLE_LOCK_MODE);
            context.startActivity(i);
        }
    }
}
