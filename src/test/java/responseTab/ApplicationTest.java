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

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import responseTab.domain.Person;
import responseTab.domain.PersonWrapper;
import responseTab.rabbitmq.Receiver;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertNull;


@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTest {

  private CachingConnectionFactory connectionFactory;

  @Resource
  private RabbitTemplate mRabbitTemplate;

  @Resource
  private Receiver mReceiver;

  @Rule
  public TestName testName = new TestName();

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
    Person person = new Person(1l,"+4411111");
    Person person2 = new Person(2l,"+35922222");
    PersonWrapper personWrapper = new PersonWrapper();
    personWrapper.getPeople().add(person);
    //mRabbitTemplate.convertAndSend(Application.RESPONSE_TAB_EXCHANGE,Application.RESPONSE_TAB_ROUTING_KEY, personWrapper);
    PersonWrapper out = (PersonWrapper) mRabbitTemplate.convertSendAndReceive(Application.RESPONSE_TAB_EXCHANGE,Application.RESPONSE_TAB_ROUTING_KEY, personWrapper);
    mReceiver.getLatch().await(10, TimeUnit.SECONDS);
  }

  @Test
  public void testError() throws InterruptedException {
    Person person = new Person(1l,"+4414222");
    PersonWrapper personWrapperTest = new PersonWrapper();
    personWrapperTest.getPeople().add(person);
    mRabbitTemplate.convertAndSend(Application.RESPONSE_TAB_EXCHANGE,Application.RESPONSE_TAB_ROUTING_KEY, personWrapperTest);
    PersonWrapper out = (PersonWrapper) mRabbitTemplate.receiveAndConvert(Application.RESPONSE_TAB_QUEUE);
    mReceiver.getLatch().await(10, TimeUnit.SECONDS);
    //assertNotNull(out);
    //assertEquals("nonblock", out);
    assertNull(mRabbitTemplate.receive(Application.RESPONSE_TAB_QUEUE));

  }


}
