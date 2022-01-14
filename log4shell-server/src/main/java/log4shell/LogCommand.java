package log4shell;

import com.unboundid.ldap.sdk.Attribute;
import com.unboundid.ldap.sdk.DN;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.RDN;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class LogCommand extends Command {
  private static final Logger found = LoggerFactory.getLogger("log4shell.found");

  private final String message;

  public LogCommand(DN dn, String message) {
    super(dn);
    this.message = message;
  }

  public LogCommand(DN dn) {
    this(dn, null);
  }

  @Override
  Entry response() {
    StringBuilder sb = new StringBuilder();
    sb.append("======== ");
    if (message != null) {
      sb.append(message);
    }
    sb.append(" ========");
    sb.append(System.lineSeparator());
    for (RDN rdn : getDn().getRDNs()) {
      for (Attribute attribute : rdn.getAttributes()) {
        sb.append("export ");
        sb.append(attribute.getName());
        sb.append("='");
        sb.append(attribute.getValue());
        sb.append('\'');
        sb.append(System.lineSeparator());
      }
    }
    found.info(sb.toString());

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
