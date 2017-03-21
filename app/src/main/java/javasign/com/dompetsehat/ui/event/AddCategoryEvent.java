package javasign.com.dompetsehat.ui.event;

import javasign.com.dompetsehat.models.UserCategory;

/**
 * Created by lafran on 10/25/16.
 */

public class AddCategoryEvent {
  public UserCategory userCategory;
  public AddCategoryEvent(UserCategory userCategory){
    this.userCategory = userCategory;
  }
}
