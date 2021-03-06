package log4shell;

import picocli.CommandLine;

import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

public class Config {
  @CommandLine.Option(names = {"-a", "--listen-address"})
  private InetAddress listenAddress;

  @CommandLine.Option(
      names = {"--ldap-port"},
      defaultValue = "1389")
  private int ldapPort;

  @CommandLine.Option(
      names = {"--http-port"},
      defaultValue = "5389")
  private int httpPort;

  public InetAddress getListenAddress() {
    if (listenAddress == null) {
      try {
        listenAddress = InetAddress.getLocalHost();
      } catch (UnknownHostException e) {
        listenAddress = InetAddress.getLoopbackAddress();
      }
    }
    return listenAddress;
  }

  public void setListenAddress(InetAddress listenAddress) {
    this.listenAddress = listenAddress;
  }

  public int getLdapPort() {
    return ldapPort;
  }

  public void setLdapPort(int ldapPort) {
    this.ldapPort = ldapPort;
  }

  public int getHttpPort() {
    return httpPort;
  }

  public void setHttpPort(int httpPort) {
    this.httpPort = httpPort;
  }

  public URI getHttpServerUri() {
    try {
      return new URI("http", null, getListenAddress().getHostAddress(), httpPort, "/", null, null);
    } catch (URISyntaxException e) {
      throw new IllegalStateException("Invalid Codebase", e);
    }
  }
}
