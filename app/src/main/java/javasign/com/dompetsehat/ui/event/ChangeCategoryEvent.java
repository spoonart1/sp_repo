package javasign.com.dompetsehat.ui.event;

import javasign.com.dompetsehat.models.Category;
import javasign.com.dompetsehat.models.UserCategory;

/**
 * Created by aves on 9/1/16.
 */

public class ChangeCategoryEvent {
  public Category category;
  public UserCategory userCategory;
  public boolean dontFinishActivity;

  public ChangeCategoryEvent(Category category) {
    this.category = category;
  }

  public ChangeCategoryEvent(UserCategory userCategory) {
    this.userCategory = userCategory;
  }

  public ChangeCategoryEvent setDontFinishActivity(boolean dontFinishActivity){
    this.dontFinishActivity = dontFinishActivity;
    return this;
  }
}
