package base.driver;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import base.Contact;
import base.Directory;

public class Main {

  static String prompt = "1) Add contact 2) Search 3) Exit";
  static String namePrompt = "Enter name :";

  public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);
    Directory directory = new Directory();
    boolean stop = false;
    while (true) {
      System.out.println(prompt);
      int input;
      try {
        input = sc.nextInt();
      } catch (InputMismatchException ime) {
        sc.nextLine();
        System.err.println("Invalid Command");
        continue;
      }
      sc.nextLine();
      String line;
      switch (input) {
        case 1:
          System.out.print(namePrompt);
          line = sc.nextLine();
          directory.addContact(Contact.parseContact(line));
          break;
        case 2:
          System.out.print(namePrompt);
          line = sc.nextLine();
          List<Contact> contact = directory.findContact(line);
          if (!contact.isEmpty()) {
            contact.forEach(t -> System.out.println(t.toString()));
          } else {
            System.out.println("NO MATCH !!");
          }
          break;
        case 3:
          stop = true;
          break;
        default:
          System.out.println("Invalid Command");
      }
      if (stop) {
        break;
      }
    }
  }
}
