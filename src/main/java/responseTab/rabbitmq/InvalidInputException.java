package responseTab.rabbitmq;

/**
 * Created by georgit on 01/05/2017.
 */
public class InvalidInputException extends RuntimeException {

  public InvalidInputException () {

  }

  public InvalidInputException (String message) {
    super (message);
  }

  public InvalidInputException (Throwable cause) {
    super (cause);
  }


}
