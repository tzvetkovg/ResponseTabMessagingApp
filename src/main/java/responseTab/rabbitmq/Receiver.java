package responseTab.rabbitmq;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import responseTab.Application;
import responseTab.domain.Person;
import responseTab.domain.PersonWrapper;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;


@Component
public class Receiver {

  @Resource
  private PhoneNumberUtil mPhoneNumberUtil;

  private static final Logger log = LoggerFactory.getLogger(Receiver.class);
  private CountDownLatch latch = new CountDownLatch(1);

  @RabbitListener(queues = Application.RESPONSE_TAB_QUEUE)
  public List<Person> receiveMessage(@Payload PersonWrapper aPersonWrapper,@Header(AmqpHeaders.CHANNEL) Channel channel,
          @Header(AmqpHeaders.DELIVERY_TAG) Long deliveryTag) throws IOException {
    log.info("receiving message");
    Map<Integer,List<Person>> peopleGroupedByCountryTelCode = aPersonWrapper.getPeople().stream().collect(Collectors.groupingBy(this::processItem));
    printResult(peopleGroupedByCountryTelCode);
    channel.basicAck(deliveryTag,true);
    latch.countDown();
    return aPersonWrapper.getPeople();
  }

  private void printResult(Map<Integer, List<Person>> aPeopleGroupedByCountryTelCode) {
    log.info("Total number of items " + aPeopleGroupedByCountryTelCode.values().size());
    aPeopleGroupedByCountryTelCode.forEach((key, value)->{
      log.info("Code: " + key + " has total items of: " + value.size());
    });
  }

  private Integer processItem(Person aPerson)
  {
    try {
      return mPhoneNumberUtil.parse(aPerson.getTelephoneNumber(), "").getCountryCode();
    } catch (NumberParseException numberParseEx) {
      numberParseEx.printStackTrace();
    }
    return null;
  }

  public CountDownLatch getLatch()
  {
    return latch;
  }

}
