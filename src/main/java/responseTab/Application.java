package responseTab;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;

@SpringBootApplication
@EnableRabbit
public class Application implements RabbitListenerConfigurer {

  public final static String RESPONSE_TAB_QUEUE = "RESPONSE_TAB_QUEUE";
  public final static String RESPONSE_TAB_ROUTING_KEY = "RESPONSE_TAB_ROUTING_KEY";
  public final static String RESPONSE_TAB_EXCHANGE = "RESPONSE_TAB_ROUTING_KEY";


  @Bean
  public TopicExchange exChange() {
    return new TopicExchange(RESPONSE_TAB_EXCHANGE);
  }

  @Bean
  public Queue queue() {
    return new Queue(RESPONSE_TAB_QUEUE);
  }

  @Bean
  public Binding declareBindingSpecific() {
    return BindingBuilder.bind(queue()).to(exChange()).with(RESPONSE_TAB_ROUTING_KEY);
  }

  @Bean
  public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory) {
    final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
    rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
    return rabbitTemplate;
  }

  @Bean
  public DefaultMessageHandlerMethodFactory messageHandlerMethodFactory() {
    DefaultMessageHandlerMethodFactory factory = new DefaultMessageHandlerMethodFactory();
    factory.setMessageConverter(new MappingJackson2MessageConverter());
    return factory;
  }

  @Override
  public void configureRabbitListeners(final RabbitListenerEndpointRegistrar registrar) {
    registrar.setMessageHandlerMethodFactory(messageHandlerMethodFactory());
  }

  @Bean
  PhoneNumberUtil phoneNumberUtil() {
    return PhoneNumberUtil.getInstance();
  }

  public static void main(String[] args) throws InterruptedException {
    SpringApplication.run(Application.class, args);
  }

}
