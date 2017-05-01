package responseTab.rabbitmq;


public class InvalidInputException extends RuntimeException {

  public InvalidInputException () {

  }

  InvalidInputException (String message) {
    super (message);
  }

  InvalidInputException (Throwable cause) {
    super (cause);
  }


}
