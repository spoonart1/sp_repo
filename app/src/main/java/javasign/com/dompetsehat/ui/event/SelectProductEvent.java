package javasign.com.dompetsehat.ui.event;

import javasign.com.dompetsehat.models.Product;

/**
 * Created by avesina on 9/16/16.
 */

public class SelectProductEvent {
  public Product product;

  public SelectProductEvent(Product product){
    this.product = product;
  }

}
