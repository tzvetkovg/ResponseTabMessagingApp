package responseTab.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.listener.adapter.ReplyFailureException;
import org.springframework.amqp.rabbit.listener.exception.ListenerExecutionFailedException;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.util.ErrorHandler;


public class RabbitErrorHandler  implements ErrorHandler {

  private static final Logger log = LoggerFactory.getLogger(RabbitErrorHandler.class);

  @Override
  public void handleError(Throwable aThrowable) {
    if (aThrowable.getCause() instanceof AmqpRejectAndDontRequeueException) {
      ListenerExecutionFailedException lefe = (ListenerExecutionFailedException) aThrowable;
      log.error("Failed to process inbound message from queue "
              + lefe.getFailedMessage().getMessageProperties().getConsumerQueue()
              + "; failed message: " + lefe.getFailedMessage(), aThrowable);
    }
    else if (aThrowable.getCause() instanceof MessageConversionException) {
      ListenerExecutionFailedException lefe = (ListenerExecutionFailedException) aThrowable;
      log.error("Invalid json object sent", aThrowable);
    }
    else if(!(aThrowable.getCause() instanceof ReplyFailureException)){
      ListenerExecutionFailedException lefe = (ListenerExecutionFailedException) aThrowable;
      log.error(aThrowable.getMessage(), aThrowable);
    }
  }
}
