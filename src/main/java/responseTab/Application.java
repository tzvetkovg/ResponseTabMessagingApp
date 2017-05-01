package responseTab;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.ConditionalRejectingErrorHandler;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.amqp.rabbit.listener.exception.ListenerExecutionFailedException;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;
import org.springframework.util.ErrorHandler;
import responseTab.domain.PersonWrapper;
import responseTab.rabbitmq.RabbitErrorHandler;

@SpringBootApplication
@EnableRabbit
public class Application implements RabbitListenerConfigurer {

  public final static String RESPONSE_TAB_QUEUE = "RESPONSE_TAB_QUEUE";
  public final static String RESPONSE_TAB_ROUTING_KEY = "RESPONSE_TAB_ROUTING_KEY";
  public final static String RESPONSE_TAB_EXCHANGE = "RESPONSE_TAB_ROUTING_KEY";

  @Bean
  public TopicExchange exchange() {
    return new TopicExchange(RESPONSE_TAB_EXCHANGE);
  }

  @Bean
  public Queue queue() {
    return new Queue(RESPONSE_TAB_QUEUE);
  }

  @Bean
  public Binding declareBinding() {
    return BindingBuilder.bind(queue()).to(exchange()).with(RESPONSE_TAB_ROUTING_KEY);
  }

  @Bean
  public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory) {
    final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
    rabbitTemplate.setMessageConverter(jsonMessageConverter());
    return rabbitTemplate;
  }

  @Bean
  public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
    SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
    factory.setConnectionFactory(connectionFactory);
    factory.setMessageConverter(jsonMessageConverter());
    factory.setErrorHandler(errorHandler());
    factory.setDefaultRequeueRejected(false);
    return factory;
  }

  @Bean
  public ErrorHandler errorHandler() {
    return new RabbitErrorHandler();
  }

  @Bean
  MessageConverter jsonMessageConverter() {
    Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
    DefaultClassMapper mapper = new DefaultClassMapper();
    mapper.setDefaultType(PersonWrapper.class);
    converter.setClassMapper(mapper);
    return converter;
  }

  @Bean
  public DefaultMessageHandlerMethodFactory myHandlerMethodFactory() {
    DefaultMessageHandlerMethodFactory factory = new DefaultMessageHandlerMethodFactory();
    factory.setMessageConverter(new MappingJackson2MessageConverter());
    return factory;
  }

  @Bean
  public PhoneNumberUtil phoneNumberUtil() {
    return PhoneNumberUtil.getInstance();
  }

  @Override
  public void configureRabbitListeners(RabbitListenerEndpointRegistrar aRabbitListenerEndpointRegistrar) {
    aRabbitListenerEndpointRegistrar.setMessageHandlerMethodFactory(myHandlerMethodFactory());
  }

  public static void main(String[] args) throws InterruptedException {
    SpringApplication.run(Application.class, args);
  }
}
