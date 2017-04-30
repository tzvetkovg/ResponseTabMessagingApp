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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;
import responseTab.domain.Person;
import responseTab.domain.PersonWrapper;
import responseTab.rabbitmq.Producer;
import responseTab.rabbitmq.Receiver;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTest {

  @MockBean
  private Producer mProducer;

  @Resource
  private RabbitTemplate mRabbitTemplate;

  @Resource
  private Receiver mReceiver;

  @Test
  public void test() throws Exception {
    Person person = new Person(1l,"+4414222");
    PersonWrapper personWrapperTest = new PersonWrapper();
    personWrapperTest.getPeople().add(person);
    mRabbitTemplate.convertAndSend(Application.RESPONSE_TAB_EXCHANGE,Application.RESPONSE_TAB_ROUTING_KEY, personWrapperTest);
    mReceiver.getLatch().await(10, TimeUnit.SECONDS);
  }


}
