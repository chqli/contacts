package base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Directory {

  private final Node dir;

  public Directory() {
    this.dir = new Node();
  }

  /**
   * adds a contact to the directory
   */
  public void addContact(Contact contact) {

    String str = contact.getFirstName();
    Node current = dir;
    for (int i = 0; i < str.length(); i++) {
      current = current.appendIfAbsent(str.charAt(i));
    }
    if (!contact.getLastName().isEmpty()) {
      current = current.appendIfAbsent(' ');
      str = contact.getLastName();
      for (int i = 0; i < str.length(); i++) {
        current = current.appendIfAbsent(str.charAt(i));
      }
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
    Node current = dir;
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
    return strings.stream().map(Contact::parseContact).collect(Collectors.toList());
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

