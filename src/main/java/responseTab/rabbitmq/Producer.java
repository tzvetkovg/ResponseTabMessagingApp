package responseTab.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class Producer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(Producer.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private Receiver receiver;


    @Override
    public void run(String... args) throws Exception {
      /*log.info("Sending message");
        Person person = new Person(1l,"+4411111");
        Person person2 = new Person(2l,"+35922222");
        PersonWrapper personWrapper = new PersonWrapper();
        personWrapper.getPeople().add(person);
        personWrapper.getPeople().add(person2);
        rabbitTemplate.convertAndSend(Application.RESPONSE_TAB_EXCHANGE,Application.RESPONSE_TAB_ROUTING_KEY, person);
        receiver.getLatch().await(10, TimeUnit.SECONDS);*/
    }
}
