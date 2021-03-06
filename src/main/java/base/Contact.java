package base;

public class Contact {

  private final String firstName;
  private final String lastName;

  public Contact(String firstName, String lastName) {
    this.firstName = firstName;
    this.lastName = lastName;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  /**
   * splits given text by whitespace to get the firstName and lastName
   */
  public static Contact parseContact(String text) {
    if (text.trim().isEmpty()) {
      throw new IllegalArgumentException("Input empty not allowed");
    }
    String[] split = text.trim().split(" ");
    Contact contact;
    if (split.length == 1) {
      contact = new Contact(split[0].trim(), "");
    } else if (split.length == 2) {
      contact = new Contact(split[0].trim(), split[1].trim());
    } else {
      throw new IllegalArgumentException("Only firstname and lastname are supported");
    }
    return contact;
  }

  @Override
  public String toString() {
    if (!lastName.isEmpty()) {
      return firstName + " " + lastName;
    }
    return firstName;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Contact contact = (Contact) o;

    if (!firstName.equals(contact.firstName)) {
      return false;
    }
    return lastName.equals(contact.lastName);
  }

  @Override
  public int hashCode() {
    int result = firstName.hashCode();
    result = 31 * result + lastName.hashCode();
    return result;
  }
}
