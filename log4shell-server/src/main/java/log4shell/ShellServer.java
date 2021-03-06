/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package log4shell;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import java.io.Closeable;

public class ShellServer implements Closeable {
  private static final Logger log = LoggerFactory.getLogger(ShellServer.class);

  private HttpServer httpServer;
  private LdapServer ldapServer;

  public ShellServer(Config config) {
    try {
      httpServer = new HttpServer(config);
      ldapServer = new LdapServer(config);

      log.info("Ready Player One");
      System.out.println("Ready Player One");

    } catch (Exception e) {
      System.err.println("Failed to start: " + e.getMessage());
      log.error("Failed to start", e);
    }
  }

  @Override
  public void close() {
    ldapServer.close();
    httpServer.close();
  }

  public static void main(String[] args) {
    Config config = new Config();
    new CommandLine(config).parseArgs(args);
    ShellServer server = new ShellServer(config);
    Runtime.getRuntime().addShutdownHook(new Thread(server::close));
  }
}
