/*
 * Copyright 2012-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package responseTab;

import com.fasterxml.jackson.core.JsonParseException;
import mockit.Expectations;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.exception.ListenerExecutionFailedException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import responseTab.domain.Person;
import responseTab.domain.PersonWrapper;
import responseTab.rabbitmq.RabbitErrorHandler;
import responseTab.rabbitmq.Receiver;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;


@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTest {

  private CachingConnectionFactory connectionFactory;

  @Resource
  private RabbitTemplate mRabbitTemplate;

  @Resource
  private Receiver mReceiver;

  private RabbitTemplate template;

/*
  @Rule
  public ExpectedException inetAddressExceptionRule = ExpectedException.none();

  @Before
  public void create() {
    this.connectionFactory = new CachingConnectionFactory();
    connectionFactory.setHost("localhost");
    connectionFactory.setPort(5672);
    connectionFactory.setPublisherReturns(true);
    connectionFactory.setUsername("guest");
    connectionFactory.setPassword("guest");
    mRabbitTemplate = new RabbitTemplate(connectionFactory);
    //mRabbitTemplate.setSendConnectionFactorySelectorExpression(new LiteralExpression("foo"));
    BeanFactory bf = mock(BeanFactory.class);
    ConnectionFactory cf = mock(ConnectionFactory.class);
    mRabbitTemplate.setBeanFactory(bf);
    //template.setbean.setBeanName(this.testName.getMethodName() + "RabbitTemplate");
  }
*/

  @Test
  public void test() throws Exception {
    Person person = new Person(1l,"+447428171589");
    Person person2 = new Person(2l,"+35988611153");
    PersonWrapper personWrapper = new PersonWrapper();
    personWrapper.getPeople().add(person);
    personWrapper.getPeople().add(person2);
    PersonWrapper out = (PersonWrapper) mRabbitTemplate.convertSendAndReceive(Application.RESPONSE_TAB_EXCHANGE,Application.RESPONSE_TAB_ROUTING_KEY, personWrapper);
    mReceiver.getLatch().await(10, TimeUnit.SECONDS);
    assertEquals(2,out.getPeople().size());
  }

  @Test
  public void testEmptyList() {
    PersonWrapper personWrapper = new PersonWrapper();
    PersonWrapper out = (PersonWrapper) mRabbitTemplate.convertSendAndReceive(Application.RESPONSE_TAB_EXCHANGE,Application.RESPONSE_TAB_ROUTING_KEY, personWrapper);
    assertEquals(0,out.getPeople().size());
  }

  @Test
  public void testInvalidTelephoneFormat() throws Exception {
    final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger( RabbitErrorHandler.class );
    Person person = new Person(1l,"447428171589");
    Person person2 = new Person(2l,"35988611153");
    PersonWrapper personWrapper = new PersonWrapper();
    personWrapper.getPeople().add(person);
    personWrapper.getPeople().add(person2);
    new Expectations(logger) {{
      logger.error("Invalid data format");
      times = 1;
    }};
    PersonWrapper out = (PersonWrapper) mRabbitTemplate.convertSendAndReceive(Application.RESPONSE_TAB_EXCHANGE,Application.RESPONSE_TAB_ROUTING_KEY, personWrapper);
    mReceiver.getLatch().await(10, TimeUnit.SECONDS);
  }

  @Test
  public void testNullID() throws Exception {
    Person person = new Person(null,"+447428171589");
    PersonWrapper personWrapper = new PersonWrapper();
    personWrapper.getPeople().add(person);
    PersonWrapper out = (PersonWrapper) mRabbitTemplate.convertSendAndReceive(Application.RESPONSE_TAB_EXCHANGE,Application.RESPONSE_TAB_ROUTING_KEY, personWrapper);
    assertEquals(null,personWrapper.getPeople().get(0).getId());
    mReceiver.getLatch().await(10, TimeUnit.SECONDS);
  }

  @Test
  public void testInvalidJson() throws InterruptedException {
    final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger( RabbitErrorHandler.class );
    Person person = new Person(1l,"+447428171589");
    new Expectations(logger) {{
      logger.error("Invalid json input");
      times = 1;
    }};
    PersonWrapper out = (PersonWrapper) mRabbitTemplate.convertSendAndReceive(Application.RESPONSE_TAB_EXCHANGE,Application.RESPONSE_TAB_ROUTING_KEY, person);
  }


  @Test
  public void testDuplicateIDInput() throws InterruptedException {
    final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger( RabbitErrorHandler.class );
    Person person = new Person(1l,"+447428171589");
    Person person2 = new Person(1l,"+35988611153");
    PersonWrapper personWrapper = new PersonWrapper();
    personWrapper.getPeople().add(person);
    personWrapper.getPeople().add(person2);
    new Expectations(logger) {{
      logger.error("Non unique ids");
      times = 1;
    }};
    PersonWrapper out = (PersonWrapper) mRabbitTemplate.convertSendAndReceive(Application.RESPONSE_TAB_EXCHANGE,Application.RESPONSE_TAB_ROUTING_KEY, personWrapper);
  }

}
