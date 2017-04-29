package responseTab.rabbitmq;

import responseTab.Application;
import responseTab.domain.PersonWrapper;
import responseTab.domain.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class Producer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(Producer.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private Receiver receiver;

    @Autowired
    private ConfigurableApplicationContext context;


    @Override
    public void run(String... args) throws Exception {
        log.info("Sending message");
        Person person = new Person(1l,"+447428171589");
        PersonWrapper personWrapper = new PersonWrapper();
        personWrapper.getPeople().add(person);
        rabbitTemplate.convertAndSend(Application.RESPONSE_TAB_QUEUE, personWrapper);
        receiver.getLatch().await(5000, TimeUnit.MILLISECONDS);
        context.close();
    }

}
