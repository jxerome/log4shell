package log4shell.aws;

public class LogCreds implements Runnable {
  private final Creds creds;

  public LogCreds(Creds creds) {
    this.creds = creds;
  }

  @Override
  public void run() {
    System.out.println("AWS Credentials\n" + creds);
  }
}
