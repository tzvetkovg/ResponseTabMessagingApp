package responseTab.domain;

import java.util.ArrayList;
import java.util.List;

public class PersonWrapper {

  List<Person> mPeople = new ArrayList<>();

  public PersonWrapper() {

  }

  public PersonWrapper(List<Person> aPeople) {
    mPeople = aPeople;
  }

  public List<Person> getPeople() {
    return mPeople;
  }

  public void setPeople(List<Person> aPeople) {
    mPeople = aPeople;
  }
}
