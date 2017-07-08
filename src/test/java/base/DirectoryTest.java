package base;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.stream.Stream;

import org.junit.Test;

public class DirectoryTest {

  @Test
  public void testShouldAddAContact() {
    Contact contact = Contact.parseContact("Chris Harris");
    Directory d = new Directory();
    d.addContact(contact);
    String input = "Chris";
    List<Contact> findResult = d.findContact(input);
    assertThat(findResult, hasItem(contact));
  }

  @Test
  public void testNotMatchingSearch() {
    Contact contact = Contact.parseContact("Chris Harris");
    Directory d = new Directory();
    d.addContact(contact);
    String input = "hris";
    List<Contact> findResult = d.findContact(input);
    assertThat(findResult.isEmpty(), is(true));
  }

  @Test
  public void testSearchOnLastName() {
    Contact contact = Contact.parseContact("Chris Harris");
    Directory d = new Directory();
    d.addContact(contact);
    String input = "Har";
    List<Contact> findResult = d.findContact(input);
    assertThat(findResult, hasItem(contact));
  }

  @Test
  public void testSearchSameFirstnameLastname() {
    Contact c2 = Contact.parseContact("Chris Harris");
    Contact c1 = Contact.parseContact("Chris Chris");
    Contact c3 = Contact.parseContact("Chris");
    Directory d = new Directory();
    d.addContact(c1);
    d.addContact(c2);
    d.addContact(c3);
    String input = "Chris";
    List<Contact> findResult = d.findContact(input);
    assertThat(findResult.get(0), is(c3));
    assertThat(findResult, hasItem(c1));
    assertThat(findResult, hasItem(c2));
  }
  @Test
  public void testShouldSaveMulitpleSameContactsAsSingleRecord() {
    Contact contact = Contact.parseContact("Chris Harris");
    Directory d = new Directory();
    for (int i = 0; i < 1000000; i++) {
      d.addContact(contact);
    }
    String input = "Chris";
    List<Contact> findResult = d.findContact(input);
    assertThat(findResult.size(), is(1));
  }

  @Test
  public void testShouldAddContactWithOnlyFirstName() {
    Contact contact = Contact.parseContact("Chris");
    Directory d = new Directory();
    d.addContact(contact);
    String input = "Chris";
    List<Contact> findResult = d.findContact(input);
    assertThat(findResult, hasItem(contact));
  }

  @Test
  public void testShouldReturnMultipleContacts() {
    Contact c1 = Contact.parseContact("Chris Harris");
    Contact c2 = Contact.parseContact("Chris Borris");
    Directory d = new Directory();
    d.addContact(c1);
    d.addContact(c2);
    String input = "Chris";
    List<Contact> exactContact = d.findContact(input);
    assertThat(exactContact, hasItem(c1));
    assertThat(exactContact, hasItem(c2));
  }

  @Test
  public void testShouldRankExactMatchBeforeOthers() {
    Contact c2 = Contact.parseContact("Chris");
    Contact c1 = Contact.parseContact("Chris Harris");
    Directory d = new Directory();
    d.addContact(c1);
    d.addContact(c2);
    String input = "Chris";
    List<Contact> findResult = d.findContact(input);
    assertThat(findResult.get(0), is(c2));
    assertThat(findResult.get(1), is(c1));
  }

  @Test
  public void testShouldReturnContactsBeyondFirstTerminalContact() {
    Contact c2 = Contact.parseContact("nirav");
    Contact c1 = Contact.parseContact("niraj");
    Contact c3 = Contact.parseContact("nirav pranami");
    Directory d = new Directory();
    d.addContact(c1);
    d.addContact(c2);
    d.addContact(c3);
    String input = "ni";
    List<Contact> findResult = d.findContact(input);
    assertThat(findResult, hasItem(c1));
    assertThat(findResult, hasItem(c2));
    assertThat(findResult, hasItem(c3));
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

  @Test
  public void test72000NamesFromFile() throws IOException, URISyntaxException {
    Directory d = new Directory();
    try (Stream<String> stream = Files
        .lines(Paths.get((getClass().getClassLoader()
            .getResource("names.txt")).toURI()))) {
      stream.forEach(t -> d.addContact(Contact.parseContact(t)));
    }
    Instant before = Instant.now();
    List<Contact> contactList = d.findContact("Tim");
    Instant after = Instant.now();
    System.out.println(Duration.between(before, after).toMillis());
    try (Stream<String> stream = Files
        .lines(Paths.get((getClass().getClassLoader()
            .getResource("names_output.txt")).toURI()))) {
      stream.forEach(t -> assertThat(contactList, hasItem(Contact.parseContact(t))));
    }
    //sed -n -e '/^Tim/p' names.txt |sort |uniq |wc -l
    assertThat(contactList.size(), is(27));
    before = Instant.now();
    assertThat(d.findContact("ZZZZZZZZZ").size(), is(0));
    after = Instant.now();
    System.out.println(Duration.between(before, after).toMillis());
  }

  @Test
  public void test72000NamesWith50CharsFromFile() throws IOException, URISyntaxException {
    Directory d = new Directory();
    try (Stream<String> stream = Files
        .lines(Paths.get((getClass().getClassLoader()
            .getResource("names_50_chars.txt")).toURI()))) {
      stream.forEach(t -> d.addContact(Contact.parseContact(t)));
    }
    Instant before = Instant.now();
    List<Contact> contactList = d.findContact("Tim");
    Instant after = Instant.now();
    System.out.println(Duration.between(before, after).toMillis());
    try (Stream<String> stream = Files
        .lines(Paths.get((getClass().getClassLoader()
            .getResource("names_50_chars_output.txt")).toURI()))) {
      stream.forEach(t -> assertThat(contactList, hasItem(Contact.parseContact(t))));
    }
    //sed -n -e '/^Tim/p' names_50_chars.txt |sort |uniq |wc -l
    assertThat(contactList.size(), is(27));
    before = Instant.now();
    assertThat(d.findContact("ZZZZZZZZZ").size(), is(0));
    after = Instant.now();
    System.out.println(Duration.between(before, after).toMillis());
  }
}