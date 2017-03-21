package javasign.com.dompetsehat;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

import javasign.com.dompetsehat.utils.SessionManager;
import javasign.com.dompetsehat.base.MyCustomApplication;

/**
 * Created by Spoonart on 9/22/2015.
 */
public class PassCodeActivity extends AppCompatActivity implements View.OnClickListener{

    public final static boolean ENABLE_LOCK_MODE = true;
    public final static String KEY_TAG_LOCKMODE = "lockmode";
    public final static String KEY_TAG_CLEARMODE = "clearmode";

    TextView tv_passcode_title,tv_passcode_clear,tv_passcode_done;
    List<ImageView> indicators = new ArrayList<ImageView>();

    final int MODE_SET = 0;
    final int MODE_REPEAT = 1;
    final int MODE_END = 2;
    final int NOT_MATCH = -1;

    protected  boolean MODE_THROUGH = false;
    protected  boolean MODE_CLEAR = false;

    int mModeFlag = 0;
    ViewGroup root;
    Animation woble;

    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_passcode);
        getSupportActionBar().hide();

        root = (ViewGroup) getWindow().getDecorView().findViewById(android.R.id.content);
        sessionManager = new SessionManager(PassCodeActivity.this, false);
        woble = AnimationUtils.loadAnimation(this, R.anim.woble);

        tv_passcode_title = (TextView)findViewById(R.id.tv_passcode_title);
        tv_passcode_clear = (TextView)findViewById(R.id.tv_passcode_clear);
        tv_passcode_done = (TextView)findViewById(R.id.tv_passcode_done);
        setTextByMode(MODE_SET);


        tv_passcode_clear.setOnClickListener(this);
        tv_passcode_done.setOnClickListener(this);

        boolean lockmode = getIntent().getBooleanExtra(KEY_TAG_LOCKMODE,false);
        boolean clearmode = getIntent().getBooleanExtra(KEY_TAG_CLEARMODE,false);
        MODE_THROUGH = lockmode;
        MODE_CLEAR = clearmode;

        setListeners(root);
    }
    private ViewGroup setListeners(ViewGroup root){
        ViewGroup container = null;
        final int childCount = root.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = root.getChildAt(i);
            if (child instanceof ViewGroup) {
                container = (ViewGroup)child;
                final int innerCount = container.getChildCount();
                for(int j=0; j<innerCount;j++){
                    View target = container.getChildAt(j);
                    if(target instanceof Button) {
                        target.setOnClickListener(this);
                    }
                    else if(target instanceof ImageView){
                        indicators.add((ImageView) target);
                    }
                }
                setListeners(container);
            }
        }
        return container;
    }

    String setPass = "";
    String setConfirm = "";
    String[] savedPass = new String[2];
    @Override
    public void onClick(View v) {
        boolean clearIndicator = false;
        String texted="";
        if(v instanceof Button){
            Button clicked = (Button)v;
            switch (mModeFlag){
                case MODE_SET:
                    if(setPass.length() != indicators.size()){
                        setPass += clicked.getText().toString();
                        if(setPass.length() == indicators.size()) {
                            saveTyped(setPass, 0);
                            mModeFlag = MODE_REPEAT;

                            // tanpa repeat password
                            if(MODE_THROUGH){
                                String currentPass = sessionManager.getPrefPascode();
                                if(setPass.equals(currentPass)){
                                    setResult(RESULT_OK,getIntent());
                                    finish();
                                }
                                else {
                                    //jika salah kembalikan ke mode set
                                    resetScreen();
                                }
                            }else
                                setTextByMode(mModeFlag);

                            clearIndicator = true;
                        }
                        texted = setPass;
                    }
                    break;
                case  MODE_REPEAT:
                    if(setConfirm.length() != indicators.size() && !MODE_THROUGH){
                        setConfirm += clicked.getText().toString();
                        if(!savedPass[0].equalsIgnoreCase(setConfirm)) setTextByMode(NOT_MATCH);
                        else {
                            if (setConfirm.length() == indicators.size()) {
                                saveTyped(setConfirm, 1);
                                mModeFlag = MODE_END;
                                setTextByMode(mModeFlag);
                            }
                        }
                        texted = setConfirm;
                    }
                    break;
                default:
                    break;
            }
            if(texted.length() > 0)
                setIndicator(texted.length() - 1, clearIndicator);
        }

        //button textview
        if(v.getId() == R.id.tv_passcode_done && v.getVisibility() == View.VISIBLE){
            if(mModeFlag == MODE_END){
                root.clearAnimation();

                //SAVE TO PREFFERENCE
                if(!MODE_THROUGH) {
                    if (savedPass[0].equalsIgnoreCase(savedPass[1])) {
                        //Log.d("MODE_SAVE", "SAVE NOW :" + savedPass[0]); //bebas meh index 1 opo 0 podo wae
                        sessionManager.savePassCodeToPreff(savedPass[0]); //bebas meh index 1 opo 0 podo wae
                        finish();
                    }
                }
                else if(MODE_THROUGH) {
                    //TODO:uncomment jika ingin tekan tombol OK dahulu untuk verifikasi
                    //root.setAnimationCacheEnabled(false);
                    /*String currentPass = sessionManager.getPrefPascode();
                    if(savedPass[0].equals(currentPass)){ //harus index ke 0 karena, mode input passcode, bukan set passcode;
                       finish();
                    }
                    else {
                        //jika salah kembalikan ke mode set
                        setTextByMode(NOT_MATCH);
                        root.setAnimation(woble);
                        mModeFlag = MODE_SET;
                    }*/
                }
            }
        }
        if (v.getId() == R.id.tv_passcode_clear){
            if(mModeFlag == MODE_SET){
                setPass = "";
            }
            if(mModeFlag == MODE_REPEAT || mModeFlag == MODE_END){
                mModeFlag = MODE_REPEAT;
                setConfirm = "";
                setTextByMode(MODE_REPEAT);
            }
            setIndicator(0, true);
        }
    }
    private void setIndicator(int pos, Boolean clear){
        int index =  pos;
        if(index > indicators.size()-1)
            index = indicators.size()-1;

        ImageView target = indicators.get(index);
        Drawable drawable = getResources().getDrawable(R.drawable.shape_oval);
        int warna = getResources().getColor(R.color.green);
        drawable.setColorFilter(warna, PorterDuff.Mode.SRC_ATOP);
        if(clear){
            warna = getResources().getColor(R.color.white);
            drawable.setColorFilter(warna, PorterDuff.Mode.SRC_ATOP);
            for(int i=0;i<indicators.size();i++)
                indicators.get(i).setImageDrawable(drawable);
        }
        else
            target.setImageDrawable(drawable);
    }
    private void saveTyped(String code, int index){
        savedPass[index] = code;
    }
    private void resetScreen(){
        setPass = "";
        setTextByMode(NOT_MATCH);
        root.setAnimation(woble);
        mModeFlag = MODE_SET;
        setIndicator(0, true);
    }

    private void setTextByMode(int mode){
        switch (mode){
            case MODE_SET:
                tv_passcode_title.setText(getString(R.string.set_your_passcode));
                break;
            case MODE_REPEAT:
                tv_passcode_title.setText(getString(R.string.repeat_your_passcode));
                break;
            case MODE_END:

                tv_passcode_title.setText(getString(R.string.press_done_to_save));
                tv_passcode_done.setText(getString(R.string.done));
                if(tv_passcode_done.getVisibility() == View.INVISIBLE)
                    tv_passcode_done.setVisibility(View.VISIBLE);

                break;
            case NOT_MATCH:
                tv_passcode_title.setText(getString(R.string.passcode_not_match));
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if(!MODE_THROUGH)
            finish();

        if(MODE_CLEAR) {
          setResult(RESULT_CANCELED);
          finish();
        }
    }

    @Override
     protected void onResume() {
        super.onResume();
        MyCustomApplication.activityResumed(this, getClass().getSimpleName());
    }
}
