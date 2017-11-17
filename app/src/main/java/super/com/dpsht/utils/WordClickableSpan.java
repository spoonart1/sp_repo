package javasign.com.dompetsehat.utils;

import android.graphics.Color;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

public class WordClickableSpan extends ClickableSpan {

    private int id;
    private TextPaint textpaint;
    public boolean shouldHilightWord = false;

    public WordClickableSpan() {

    }

    @Override
    public void updateDrawState(TextPaint ds) {
        textpaint = ds;
        ds.setColor(Color.parseColor("#00A2E9"));
        ds.setUnderlineText(true);
    }

    public void changeSpanBgColor(View widget) {
        shouldHilightWord = true;
        updateDrawState(textpaint);
        widget.invalidate();


    }

    @Override
    public void onClick(View widget) {


    }


    /**
     * This function sets the span to record the word number, as the span ID
     *
     * @param spanID
     */
    public void setSpanTextID(int spanID) {
        id = spanID;
    }

    /**
     * Return the wordId of this span
     *
     * @return id
     */
    public int getSpanTextID() {
        return id;
    }
}