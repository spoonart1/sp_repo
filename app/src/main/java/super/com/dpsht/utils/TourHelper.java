package javasign.com.dompetsehat.utils;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import butterknife.ButterKnife;
import java.util.ArrayList;
import tourguide.tourguide.ChainTourGuide;
import tourguide.tourguide.Overlay;
import tourguide.tourguide.Pointer;
import tourguide.tourguide.Sequence;
import tourguide.tourguide.ToolTip;
import tourguide.tourguide.TourGuide;

/**
 * Created by lafran on 1/20/17.
 */

public class TourHelper {

  public interface TourListener {
    void onDismiss();
  }

  private ArrayList<View> viewsToAttach;

  private SessionManager sessionManager;
  private String[] tourDescriptions;
  private String[] tourTitles;
  private int[] gravities;
  private Activity activity;
  private ChainTourGuide tourHandler;
  private ArrayList<ChainTourGuide> guides;
  private FloatingButtonHelper buttonHelper;
  private int expandInSequence = -1;
  private TourListener listener;
  private Sequence sequence;
  private String sessionKey = "";
  private boolean isDefaultButtonEnable = false;
  private boolean isCreate = false;
  private View rootView;

  public TourHelper(Activity activity) {
    this.activity = activity;
    this.sessionManager = SessionManager.getIt(activity);
  }

  public TourHelper withDefaultButtonEnable(boolean isDefaultButtonEnable) {
    this.isDefaultButtonEnable = isDefaultButtonEnable;
    return this;
  }

  public static TourHelper init(Activity activity) {
    TourHelper th = new TourHelper(activity);
    return th;
  }

  public TourHelper setGravities(int... gravities) {
    this.gravities = gravities;
    return this;
  }

  public TourHelper setSessionKey(String sessionKey) {
    this.sessionKey = sessionKey;
    return this;
  }

  public TourHelper setSupportFloatingButtonHelper(FloatingButtonHelper floatingButtonHelper,
      int expandOnPage) {
    this.buttonHelper = floatingButtonHelper;
    this.expandInSequence = expandOnPage;
    return this;
  }

  public TourHelper setTourDescriptions(String... tourDescriptions) {
    this.tourDescriptions = tourDescriptions;
    return this;
  }

  public TourHelper setTourListener(TourListener listener) {
    this.listener = listener;
    return this;
  }

  public TourHelper setTourTitles(String... tourTitles) {
    this.tourTitles = tourTitles;
    return this;
  }

  public TourHelper setViewsToAttach(View rootview, int... resIds) {
    this.rootView = rootview;
    if (viewsToAttach == null) viewsToAttach = new ArrayList<>();
    for (int id : resIds) {
      View view = ButterKnife.findById(rootview, id);
      if (view == null) {
        view = ButterKnife.findById(activity, id);
      }
      viewsToAttach.add(view);
    }
    if (isTourGuideAllowedToShow()) {
      setEnable(isDefaultButtonEnable);
    }
    return this;
  }

  public void showSingleTourGuide(View anchorView, String title, String description, int gravityy) {

    Animation enterAnimation = new AlphaAnimation(0f, 1f);
    enterAnimation.setDuration(500);
    enterAnimation.setFillAfter(true);

    Animation exitAnimation = new AlphaAnimation(1f, 0f);
    exitAnimation.setDuration(500);
    exitAnimation.setFillAfter(true);

    final TourGuide handler = TourGuide.init(activity)
        .with(TourGuide.Technique.Click)
        .setToolTip(new ToolTip().setTitle(title).setGravity(gravityy).setDescription(description));
    handler.setOverlay(new Overlay().setOnClickListener(v -> {
      dismiss();
      handler.cleanUp();
    }).setEnterAnimation(enterAnimation).setExitAnimation(exitAnimation)).playOn(anchorView);
  }

  public TourHelper create() {
    if (viewsToAttach == null) {
      throw new Error(" viewsToAttach is null");
    }
    getButtonHelperAction(true);
    guides = new ArrayList<>();
    for (int i = 0; i < viewsToAttach.size(); i++) {
      guides.add(
          createTour(tourTitles[i], tourDescriptions[i], viewsToAttach.get(i), gravities[i]));
    }
    isCreate = true;
    return this;
  }

  public TourHelper show() {
    if (!isTourGuideAllowedToShow()) return this;

    Animation enterAnimation = new AlphaAnimation(0f, 1f);
    enterAnimation.setDuration(500);
    enterAnimation.setFillAfter(true);

    Animation exitAnimation = new AlphaAnimation(1f, 0f);
    exitAnimation.setDuration(500);
    exitAnimation.setFillAfter(true);

    final boolean isSingleData = guides.size() == 1;
    if (isSingleData) {
      guides.add(createTour("fake", "fake", viewsToAttach.get(0), Gravity.TOP));
    }
    if (sequence == null) {
      sequence =
          new Sequence.SequenceBuilder().add(guides.toArray(new ChainTourGuide[guides.size()]))
              .setDefaultOverlay(new Overlay().setEnterAnimation(enterAnimation)
                  .setExitAnimation(exitAnimation)
                  .setOnClickListener(new View.OnClickListener() {
                    private int clicked = 0;

                    @Override public void onClick(View v) {
                      clicked++;
                      if (!isSingleData) {
                        tourHandler.next();
                        if (sequence.mCurrentSequence == expandInSequence) {
                          getButtonHelperAction(true);
                        } else if (clicked >= viewsToAttach.size()) {
                          getButtonHelperAction(false);
                          dismiss();
                          if (listener != null) listener.onDismiss();
                        } else {
                          getButtonHelperAction(false);
                        }
                      } else {
                        dismiss();
                        if (listener != null) listener.onDismiss();
                      }
                    }
                  }))
              .setDefaultPointer(null)
              .setContinueMethod(Sequence.ContinueMethod.OverlayListener)
              .build();
    }

    if (tourHandler == null) {
      if (buttonHelper != null) {
        buttonHelper.actionsMenu.getViewTreeObserver()
            .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
              @Override public void onGlobalLayout() {
                tourHandler = ChainTourGuide.init(activity).playInSequence(sequence);
                buttonHelper.actionsMenu.getViewTreeObserver().removeOnGlobalLayoutListener(this);
              }
            });
      }
      else{
        tourHandler = ChainTourGuide.init(activity).playInSequence(sequence);
      }
    }
    return this;
  }

  private ChainTourGuide createTour(String title, String desc, View anchor, int gravity) {
    ChainTourGuide guide = ChainTourGuide.init(activity)
        .setToolTip(new ToolTip().setTitle(title).setDescription(desc).setGravity(gravity))
        .playLater(anchor);
    return guide;
  }

  private void getButtonHelperAction(boolean expand) {
    if (!isTourGuideAllowedToShow()) return;
    if (buttonHelper != null) {
      if (expand && expandInSequence != -1) {
        buttonHelper.expand();
        return;
      }

      if (buttonHelper.actionsMenu.isExpanded()) {
        buttonHelper.collapse();
      }
    }
  }

  public boolean isTourGuideAllowedToShow() {
    return sessionManager.isAllowedToShowGuide(sessionKey);
  }

  public void closeGuideForever() {
    sessionManager.setTourGuideShow(sessionKey, false);
  }

  public TourListener getTourListener() {
    return listener;
  }

  public boolean dismiss() {
    if (tourHandler.getOverlay() != null) tourHandler.cleanUp();

    setEnable(true);
    closeGuideForever();
    isCreate = false;
    return isTourGuideAllowedToShow();
  }

  protected void setEnable(boolean enable) {
    for (View v : viewsToAttach) {
      v.setEnabled(enable);
    }
  }

  public boolean isCreated() {
    if (!isTourGuideAllowedToShow()) {
      return true;
    }
    return isCreate;
  }

  public void removeGuideAtPosition(int position) {
    if (position >= guides.size() || position >= viewsToAttach.size()) {
      return;
    }
    viewsToAttach.remove(position);
    guides.remove(position);
  }

  public void removeGuideAtPositionAndShow(int position) {
    removeGuideAtPosition(position);
    show();
  }
}
