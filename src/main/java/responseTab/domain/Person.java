package responseTab.domain;


public class Person {

  private Long id;
  private String telephoneNumber;

  public Person () {

  }

  public Person(Long aId, String aTelephoneNumber) {
    id = aId;
    telephoneNumber = aTelephoneNumber;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long aId) {
    id = aId;
  }

  public String getTelephoneNumber() {
    return telephoneNumber;
  }

  public void setTelephoneNumber(String aTelephoneNumber) {
    telephoneNumber = aTelephoneNumber;
  }

  @Override
  public String toString() {
    return "Person{" +
            "id=" + id +
            ", telephoneNumber='" + telephoneNumber + '\'' +
            '}';
  }
}
