package base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class Directory {

  private final Node dir;
  private final Node lastNameDir;

  public Directory() {
    dir = new Node();
    lastNameDir = new Node();
  }

  /**
   * adds a contact to the directory
   */
  public void addContact(Contact contact) {

    addStringToDir(contact.getFirstName() + " " + contact.getLastName(), dir);
    addStringToDir(contact.getLastName() + " " + contact.getFirstName(), lastNameDir);
  }

  private void addStringToDir(String txt, Node node) {
    Node current = node;
    for (int i = 0; i < txt.length(); i++) {
      current = current.appendIfAbsent(txt.charAt(i));
    }
    current.setTerminal(true);
  }

  /**
   * finds list of contacts with prefix as given text
   */
  public List<Contact> findContact(String text) {
    if (text.isEmpty()) {
      throw new IllegalArgumentException("Input can't be empty");
    }
    List<String> fromDir = findContactInDir(dir, text);
    List<String> fromLastNameDir = findContactInDir(lastNameDir, text);
    List<Contact> dirContacts = parseToFirstnameLastname(fromDir);
    List<Contact> lastNameContacts = parseToLastnameFirstname(fromLastNameDir);
    List<Contact> result = new ArrayList<>();
    Contact exactMatchFull = getExactMatch(dirContacts, text);
    Contact exactMatchLastName = getExactMatch(lastNameContacts, text);
    int fromIndexFull = 0;
    int fromIndexLastname = 0;
    if (exactMatchFull != null && exactMatchLastName != null) {
      result.add(0, exactMatchFull);
      fromIndexFull = 1;
      fromIndexLastname = 1;
      addAllFromIndex(result, dirContacts, 1);
    } else if (exactMatchLastName != null) {
      result.add(0, exactMatchLastName);
      fromIndexLastname = 1;
    } else if (exactMatchFull != null) {
      result.add(0, exactMatchFull);
      fromIndexFull = 1;
    }
    addAllFromIndex(result, dirContacts, fromIndexFull);
    addAllFromIndex(result, lastNameContacts, fromIndexLastname);
    return result;
  }

  private void addAllFromIndex(List<Contact> contacts, List<Contact> from, int fromIndex) {
    for (int i = fromIndex; i < from.size(); i++) {
      contacts.add(from.get(i));
    }
  }

  private List<Contact> parseToFirstnameLastname(List<String> list) {
    List<Contact> contacts = new ArrayList<>();
    for (String s : list) {
      Contact contact = Contact.parseContact(s);
      contacts.add(contact);
    }
    return contacts;
  }

  private List<Contact> parseToLastnameFirstname(List<String> list) {
    List<Contact> contacts = new ArrayList<>();
    for (String s : list) {
      Contact contact = Contact.parseContact(s);
      contact = new Contact(contact.getLastName(), contact.getFirstName());
      contacts.add(contact);
    }
    return contacts;
  }

  private Contact getExactMatch(List<Contact> list, String str) {
    if (list == null || list.isEmpty()) {
      return null;
    }
    if (list.get(0).toString().equals(str)) {
      return list.get(0);
    }
    return null;
  }

  public List<String> findContactInDir(Node directory, String text) {
    Node current = directory;
    ArrayList<String> strings = new ArrayList<>();
    for (int i = 0; i < text.length(); i++) {
      current = current.getNextNode(text.charAt(i));
      if (current == null) {
        return Collections.emptyList();
      }
    }
    if (current.isTerminal()) {
      strings.add(text);
    }
    current.addAllSuffixes(strings, new StringBuilder(text));
    return strings;
  }

  private class Node {

    Map<Character, Node> nextMap;
    boolean terminal;

    Node() {
      this(false);
    }

    Node(boolean terminal) {
      this.terminal = terminal;
      this.nextMap = new HashMap<>(26, 0.75f);
    }

    /**
     * get next Node corresponding to given character
     */
    Node getNextNode(char val) {
      if (!nextMap.containsKey(val)) {
        return null;
      }
      return nextMap.get(val);
    }

    /**
     * adds a key-value pair with key as given character and new node as value
     *
     * @param val character to append to trie
     * @return a node corresponding to the character
     */
    Node appendIfAbsent(final char val) {
      Node next = getNextNode(val);
      if (null != next) {
        return next;
      }
      Node n = new Node();
      nextMap.put(val, n);
      return n;
    }

    /**
     * adds to given list, for all the suffixes ending with terminal node, by prefixing it with given text
     * e.g. prefix = jo
     *      terminal suffixes = {hn, hn Doe}
     * will add following to the given list
     *      john
     *      john Doe
     *
     * @param result list to which a terminal suffix (prefixed by sb) is added
     * @param sb prefix
     */
    void addAllSuffixes(List<String> result, StringBuilder sb) {
      for (char c : nextMap.keySet()) {
        Node next = nextMap.get(c);
        sb.append(c);
        if (next.isTerminal()) {
          result.add(sb.toString());
        } else {
          next.addAllSuffixes(result, sb);
        }
        sb.deleteCharAt(sb.length() - 1);
      }
    }

    /**
     * if a node is terminal, it is a contact
     */
    boolean isTerminal() {
      return terminal;
    }

    void setTerminal(boolean terminal) {
      this.terminal = terminal;
    }
  }
}

