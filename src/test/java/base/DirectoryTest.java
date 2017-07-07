package base;

import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.junit.Assert.assertThat;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import org.junit.Test;

public class DirectoryTest {

  @Test
  public void testShouldAddAContact() {
    Contact contact = Contact.parseContact("Chris Harris");
    Directory d = new Directory();
    d.addContact(contact);
    String input = "Chris";
    List<Contact> exactContact = d.findContact(input);
    assertThat(exactContact, hasItem(contact));
  }

  @Test
  public void testTime50CharsLongContact() {
    Contact contact = Contact.parseContact("Chris HarrisHarrisHarrisHarrisHarrisHarrisHarrisss");
    Directory d = new Directory();
    d.addContact(contact);
    String input = "Chris";
    Instant before = Instant.now();
    List<Contact> exactContact = d.findContact(input);
    Instant after = Instant.now();
    System.out.println(Duration.between(before, after).toMillis());
    assertThat(exactContact, hasItem(contact));
  }
}