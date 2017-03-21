package javasign.com.dompetsehat.ui.activities.plan.pojo;

/**
 * Created by lafran on 9/28/16.
 */

public class DanaKuliahEvent {

  public int view_containerId;
  public int[] fieldsId;
  public String[] messages;

  public DanaKuliahEvent(int...fieldViewId){
    this.fieldsId = fieldViewId;
  }

  public DanaKuliahEvent withMessages(String...messages){
    this.messages = messages;
    return this;
  }

  public DanaKuliahEvent withContainerId(int containerId){
    this.view_containerId = containerId;
    return this;
  }

}
