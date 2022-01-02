package log4shell;

import com.unboundid.ldap.sdk.DN;
import com.unboundid.ldap.sdk.Entry;

public final class NoOpCommand extends Command {

  public NoOpCommand(DN dn) {
    super(dn);
  }

  @Override
  Entry response() {
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
