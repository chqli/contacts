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
      current = current.append(str.charAt(i));
    }
    current = current.append(' ');
    str = contact.getLastName();
    for (int i = 0; i < str.length(); i++) {
      current = current.append(str.charAt(i));
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
    current.allSuffixes(strings, new StringBuilder(text));
    return strings.stream().map(Contact::parseContact).collect(Collectors.toList());
  }
}

class Node {

  Map<Character, Node> nextMap;
  boolean terminal;

  public Node() {
    this(false);
  }

  public Node(boolean terminal) {
    this.terminal = terminal;
    this.nextMap = new HashMap<>(26, 0.75f);
  }

  public Node getNextNode(char val) {
    if (!nextMap.containsKey(val)) {
      return null;
    }
    return nextMap.get(val);
  }

  public Node append(final char val) {
    Node node = new Node();
    nextMap.put(val, node);
    return node;
  }

  public void allSuffixes(List<String> result, StringBuilder sb) {
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

  public boolean isTerminal() {
    return terminal;
  }

  public void setTerminal(boolean terminal) {
    this.terminal = terminal;
  }
}