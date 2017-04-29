package responseTab;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import responseTab.rabbitmq.Receiver;

@SpringBootApplication
public class Application {

  public final static String RESPONSE_TAB_QUEUE = "RESPONSE_TAB_QUEUE";
  public final static String RESPONSE_TAB_ROUTING_KEY = "RESPONSE_TAB_ROUTING_KEY";
  public final static String RESPONSE_TAB_EXCHANGE = "RESPONSE_TAB_ROUTING_KEY";

  @Bean
  Queue queue() {
    return new Queue(RESPONSE_TAB_QUEUE, false);
  }

  @Bean
  TopicExchange exchange() {
    return new TopicExchange(RESPONSE_TAB_EXCHANGE);
  }

  @Bean
  Binding binding(Queue queue, TopicExchange exchange) {
    return BindingBuilder.bind(queue).to(exchange).with(RESPONSE_TAB_ROUTING_KEY);
  }

  @Bean
  SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
          MessageListenerAdapter listenerAdapter) {
    SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
    container.setConnectionFactory(connectionFactory);
    container.setMessageConverter(jsonMessageConverter());
    container.setQueueNames(RESPONSE_TAB_QUEUE);
    container.setMessageListener(listenerAdapter);
    return container;
  }

  @Bean
  MessageListenerAdapter listenerAdapter(Receiver receiver) {
    MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter(receiver, "receiveMessage");
    messageListenerAdapter.setMessageConverter(jsonMessageConverter());
    return messageListenerAdapter;
  }

  @Bean
  RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
    final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
    rabbitTemplate.setMessageConverter(jsonMessageConverter());
    return rabbitTemplate;
  }

  @Bean
  MessageConverter jsonMessageConverter() {
    return new Jackson2JsonMessageConverter();
  }

  @Bean
  PhoneNumberUtil phoneNumberUtil() {
    return PhoneNumberUtil.getInstance();
  }

  public static void main(String[] args) throws InterruptedException {
    SpringApplication.run(Application.class, args);
  }

}
