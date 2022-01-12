package log4shell;

import com.unboundid.ldap.sdk.DN;
import com.unboundid.ldap.sdk.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class LogCommand extends Command {
  private static final Logger found = LoggerFactory.getLogger("log4shell.found");

  private final String message;

  public LogCommand(DN dn, String message) {
    super(dn);
    this.message = message;
  }

  @Override
  Entry response() {
    found.info(message);

    Entry e = new Entry(getDn());
    e.addAttribute("objectClass", "top");
    e.addAttribute("objectClass", "javaContainer");
    e.addAttribute("objectClass", "javaObject");
    e.addAttribute("objectClass", "javaSerializedObject");
    e.setAttribute("javaClassName", "java.lang.String");
    e.setAttribute("javaSerializedData", IO.EMPTY_STRING);
    return e;
  }
}
