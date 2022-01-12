package log4shell;

import com.unboundid.ldap.listener.InMemoryDirectoryServer;
import com.unboundid.ldap.listener.InMemoryDirectoryServerConfig;
import com.unboundid.ldap.listener.InMemoryListenerConfig;
import com.unboundid.ldap.listener.interceptor.InMemoryInterceptedSearchResult;
import com.unboundid.ldap.listener.interceptor.InMemoryOperationInterceptor;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.ResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ServerSocketFactory;
import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;
import java.io.Closeable;

public class LdapServer implements Closeable {
  private static final Logger log = LoggerFactory.getLogger(LdapServer.class);

  private static final String LDAP_BASE = "dc=example,dc=com";

  private final InMemoryDirectoryServer ds;

  public LdapServer(Config config) throws LDAPException {
    InMemoryDirectoryServerConfig ldapConfig = new InMemoryDirectoryServerConfig(LDAP_BASE);
    ldapConfig.setListenerConfigs(
        new InMemoryListenerConfig(
            "listen",
            config.getListenAddress(),
            config.getLdapPort(),
            ServerSocketFactory.getDefault(),
            SocketFactory.getDefault(),
            (SSLSocketFactory) SSLSocketFactory.getDefault()));

    ldapConfig.addInMemoryOperationInterceptor(new OperationInterceptor(config));

    ds = new InMemoryDirectoryServer(ldapConfig);
    ds.startListening();

    String message =
        String.format(
            "Listening for LDAP on %s:%d", config.getListenAddress(), config.getLdapPort());
    System.out.println(message);
    log.info(message);
  }

  @Override
  public void close() {
    ds.close();
  }

  private static final class OperationInterceptor extends InMemoryOperationInterceptor {
    private final CommandParser commandParser;

    public OperationInterceptor(Config config) {
      this.commandParser = new CommandParser(config);
    }

    /**
     * {@inheritDoc}
     *
     * @see
     *     com.unboundid.ldap.listener.interceptor.InMemoryOperationInterceptor#processSearchResult(com.unboundid.ldap.listener.interceptor.InMemoryInterceptedSearchResult)
     */
    @Override
    public void processSearchResult(InMemoryInterceptedSearchResult result) {
      String rawDn = result.getRequest().getBaseDN();
      try {
        Command command = commandParser.parse(rawDn);
        result.sendSearchEntry(command.response());
        result.setResult(new LDAPResult(0, ResultCode.SUCCESS));

      } catch (LDAPException e) {
        log.error("Can't process request on {}", rawDn, e);
      }
    }
  }
}
