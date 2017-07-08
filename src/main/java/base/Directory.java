package base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Directory {

  private final Node dir;

  public Directory() {
    this.dir = new Node();
  }

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


  public List<Contact> findContact(String text) {
    if (text.isEmpty()) {
      throw new IllegalArgumentException("Input can't be empty");
    }
    Node current = dir;
    ArrayList<String> strings = new ArrayList<>();
    for (int i = 0; i < text.length(); i++) {
      current = current.getNextNode(text.charAt(i));
      if (current == null) {
        return null;
      }
    }
    if (current.isTerminal()) {
      strings.add(text);
    }
    current.allSuffixes(strings, new StringBuilder(text));
    return strings.stream().map(Contact::parseContact).collect(Collectors.toList());
  }

  class Node {

    Map<Character, Node> nextMap;
    boolean terminal;

    Node() {
      this(false);
    }

    Node(boolean terminal) {
      this.terminal = terminal;
      this.nextMap = new HashMap<>(26, 0.75f);
    }

    Node getNextNode(char val) {
      if (!nextMap.containsKey(val)) {
        return null;
      }
      return nextMap.get(val);
    }

    Node appendIfAbsent(final char val) {
      if (nextMap.containsKey(val)) {
        return nextMap.get(val);
      }
      Node node = new Node();
      nextMap.put(val, node);
      return node;
    }

    void allSuffixes(List<String> result, StringBuilder sb) {
      for (char c : nextMap.keySet()) {
        Node next = nextMap.get(c);
        sb.append(c);
        if (next.isTerminal()) {
          result.add(sb.toString());
        } else {
          next.allSuffixes(result, sb);
        }
        sb.deleteCharAt(sb.length() - 1);
      }
    }

    boolean isTerminal() {
      return terminal;
    }

    void setTerminal(boolean terminal) {
      this.terminal = terminal;
    }
  }
}

