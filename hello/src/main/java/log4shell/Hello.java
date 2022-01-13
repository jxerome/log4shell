package log4shell;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Hello {
  private static final Logger log = LogManager.getLogger(Hello.class);

  public static void main(String[] args) {
    System.setProperty("com.sun.jndi.ldap.object.trustURLCodebase", "true");

    if (args.length == 0) {
      log.info("Hello, World!");
    } else {
      for (String name : args) {
        log.info("Hello, {}!", name);
      }
    }
  }
}
