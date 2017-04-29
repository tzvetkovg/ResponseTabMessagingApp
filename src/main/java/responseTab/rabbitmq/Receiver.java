package responseTab.rabbitmq;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import responseTab.domain.PersonWrapper;
import responseTab.domain.Person;

import javax.annotation.Resource;
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

  public void receiveMessage(PersonWrapper aPersonWrapper) {
    log.info("receiving message");
    Map<Integer,List<Person>> peopleGroupedByCountryTelCode = aPersonWrapper.getPeople().stream().collect(Collectors.groupingBy(this::processItem));
    System.out.println(peopleGroupedByCountryTelCode);
    latch.countDown();
  }

  public CountDownLatch getLatch()
  {
    return latch;
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
}
