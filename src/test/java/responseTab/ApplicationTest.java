

package responseTab;

import mockit.Expectations;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpMessageReturnedException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import responseTab.domain.Person;
import responseTab.domain.PersonWrapper;
import responseTab.rabbitmq.RabbitErrorHandler;
import responseTab.rabbitmq.Receiver;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTest {

  @Resource
  private RabbitTemplate mRabbitTemplate;

  @Resource
  private Receiver mReceiver;

  @Test
  public void testReturn() throws Exception {
    Person person = new Person(1l, "+447428171589");
    Person person2 = new Person(2l, "+35988611153");
    PersonWrapper personWrapper = new PersonWrapper();
    personWrapper.getPeople().add(person);
    personWrapper.getPeople().add(person2);
    PersonWrapper result = (PersonWrapper) mRabbitTemplate.convertSendAndReceive(Application.RESPONSE_TAB_EXCHANGE, Application.RESPONSE_TAB_ROUTING_KEY, personWrapper);
    mReceiver.getLatch().await(10, TimeUnit.SECONDS);
    assertNotNull(result);
    assertEquals(2, result.getPeople().size());
  }

  @Test
  public void testEmptyList() {
    PersonWrapper personWrapper = new PersonWrapper();
    PersonWrapper result = (PersonWrapper) mRabbitTemplate.convertSendAndReceive(Application.RESPONSE_TAB_EXCHANGE, Application.RESPONSE_TAB_ROUTING_KEY, personWrapper);
    assertEquals(0, result.getPeople().size());
  }

  @Test
  public void testInvalidTelephoneFormat() throws Exception {
    final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(RabbitErrorHandler.class);
    Person person = new Person(1l, "447428171589");
    Person person2 = new Person(2l, "35988611153");
    PersonWrapper personWrapper = new PersonWrapper();
    personWrapper.getPeople().add(person);
    personWrapper.getPeople().add(person2);
    new Expectations(logger) {{
      logger.error("Invalid data format");
      times = 1;
    }};
    PersonWrapper result = (PersonWrapper) mRabbitTemplate.convertSendAndReceive(Application.RESPONSE_TAB_EXCHANGE, Application.RESPONSE_TAB_ROUTING_KEY, personWrapper);
  }

  @Test
  public void testNullID() throws Exception {
    Person person = new Person(null, "+447428171589");
    PersonWrapper personWrapper = new PersonWrapper();
    personWrapper.getPeople().add(person);
    PersonWrapper result = (PersonWrapper) mRabbitTemplate.convertSendAndReceive(Application.RESPONSE_TAB_EXCHANGE, Application.RESPONSE_TAB_ROUTING_KEY, personWrapper);
    assertEquals(null, result.getPeople().get(0).getId());
  }

  @Test
  public void testInvalidJson() throws InterruptedException {
    Person person = new Person(1l, "+447428171589");
    PersonWrapper result = (PersonWrapper) mRabbitTemplate.convertSendAndReceive(Application.RESPONSE_TAB_EXCHANGE, Application.RESPONSE_TAB_ROUTING_KEY, person);
    assertEquals(null, result);
  }


  @Test
  public void testDuplicateIDInput() throws InterruptedException {
    final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(RabbitErrorHandler.class);
    Person person = new Person(1l, "+447428171589");
    Person person2 = new Person(1l, "+35988611153");
    PersonWrapper personWrapper = new PersonWrapper();
    personWrapper.getPeople().add(person);
    personWrapper.getPeople().add(person2);
    new Expectations(logger) {{
      logger.error("Non unique ids");
      times = 1;
    }};
    PersonWrapper result = (PersonWrapper) mRabbitTemplate.convertSendAndReceive(Application.RESPONSE_TAB_EXCHANGE, Application.RESPONSE_TAB_ROUTING_KEY, personWrapper);
  }

  @Test
  public void testStringTypeInput() {
    Person person = new Person(1l, "+447428171589");
    PersonWrapper personWrapper = new PersonWrapper();
    personWrapper.getPeople().add(person);
    PersonWrapper result = (PersonWrapper) mRabbitTemplate.convertSendAndReceive(Application.RESPONSE_TAB_EXCHANGE, Application.RESPONSE_TAB_ROUTING_KEY, personWrapper, message -> {
      message.getMessageProperties().setContentType("text/other");
      return message;
    });
    assertEquals(null, result);
  }

  @Test
  public void testJsonTypeInput() {
    Person person = new Person(1l, "+447428171589");
    PersonWrapper personWrapper = new PersonWrapper();
    personWrapper.getPeople().add(person);
    PersonWrapper result = (PersonWrapper) mRabbitTemplate.convertSendAndReceive(Application.RESPONSE_TAB_EXCHANGE, Application.RESPONSE_TAB_ROUTING_KEY, personWrapper, message -> {
      message.getMessageProperties().setContentType("application/json");
      return message;
    });
    assertTrue(null, result != null);
    assertEquals(person.getId(), result.getPeople().get(0).getId());
  }

}
