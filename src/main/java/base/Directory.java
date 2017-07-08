package base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Directory {

  private final Node dir;
  private final Node lastNameDir;

  public Directory() {
    dir = new Node();
    lastNameDir = new Node();
  }

  /**
   * add a contact to the directory
   */
  public void addContact(Contact contact) {

    if (!contact.getLastName().isEmpty()) {
      addStringToDir(contact.getFirstName() + " " + contact.getLastName(), dir);
      addStringToDir(contact.getLastName() + " " + contact.getFirstName(), lastNameDir);
    } else {
      addStringToDir(contact.getFirstName(), dir);
    }
  }

  private void addStringToDir(String txt, Node node) {
    Node current = node;
    for (int i = 0; i < txt.length(); i++) {
      current = current.appendIfAbsent(txt.charAt(i));
    }
    current.setTerminal(true);
  }

  /**
   * find list of contacts with prefix as given text
   */
  public List<Contact> findContact(String text) {
    if (text.isEmpty()) {
      throw new IllegalArgumentException("Input can't be empty");
    }
    List<String> fromDir = findContactsInDir(dir, text);
    List<Contact> dirContacts = parseToFirstnameLastname(fromDir);
    List<Contact> result = new ArrayList<>();
    result.addAll(dirContacts);
    mergeResultsFromLastNameDir(result, text);
    return result;
  }

  private void mergeResultsFromLastNameDir(List<Contact> addTo, String text) {
    List<String> fromLastNameDir = findContactsInDir(lastNameDir, text);
    List<Contact> lastNameDirContacts = parseToLastnameFirstname(fromLastNameDir);
    Set<Contact> lastNameSet = new HashSet<>(lastNameDirContacts);
    lastNameSet.removeAll(addTo);
    addTo.addAll(lastNameSet);

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

  private List<String> findContactsInDir(Node directory, String text) {
    Node current = directory;
    ArrayList<String> result = new ArrayList<>();
    for (int i = 0; i < text.length(); i++) {
      current = current.getNextNode(text.charAt(i));
      if (current == null) {
        return Collections.emptyList();
      }
    }
    if (current.isTerminal()) {
      result.add(text);
    }
    current.addAllSuffixes(result, new StringBuilder(text));
    return result;
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
     * add a key-value pair with key as given character and new node as value
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
     * add to given list, for all the suffixes ending with terminal node, by prefixing it with given text
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
        }
        next.addAllSuffixes(result, sb);
        sb.deleteCharAt(sb.length() - 1);
      }
    }

    /**
     * check if a node forms a contact
     */
    boolean isTerminal() {
      return terminal;
    }

    void setTerminal(boolean terminal) {
      this.terminal = terminal;
    }
  }
}

