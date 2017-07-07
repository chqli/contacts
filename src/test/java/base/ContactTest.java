package base;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ContactTest {

  @Rule
  public ExpectedException ee = ExpectedException.none();

  @Test
  public void testParseContactFirstLastName() throws Exception {
    Contact contact = Contact.parseContact("Abc Xyz");
    assertThat(contact.getFirstName(), is("Abc"));
    assertThat(contact.getLastName(), is("Xyz"));
  }

  @Test
  public void testParseContactFirstName() throws Exception {
    Contact contact = Contact.parseContact("Abc");
    assertThat(contact.getFirstName(), is("Abc"));
    assertThat(contact.getLastName(), is(""));
  }

  @Test
  public void testParseContactExtraSpaces() throws Exception {
    ee.expect(IllegalArgumentException.class);
    Contact.parseContact("Abc x y");
  }

}