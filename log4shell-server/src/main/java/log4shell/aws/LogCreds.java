package log4shell.aws;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogCreds implements Runnable {
  private static final Logger found = LoggerFactory.getLogger("log4shell.found");

  private final Creds creds;

  public LogCreds(Creds creds) {
    this.creds = creds;
  }

  @Override
  public void run() {
    found.info("{}", creds);
  }
}
