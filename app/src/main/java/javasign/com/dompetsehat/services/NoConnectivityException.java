package javasign.com.dompetsehat.services;


/**
 * Created by avesina on 12/5/16.
 */

public class NoConnectivityException extends Exception {
  public NoConnectivityException() { super(); }
  public NoConnectivityException(String message) { super(message); }
  public NoConnectivityException(String message, Throwable cause) { super(message, cause); }
  public NoConnectivityException(Throwable cause) { super(cause); }
}

