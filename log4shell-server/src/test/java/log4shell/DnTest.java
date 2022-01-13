package log4shell;

import com.unboundid.ldap.sdk.DN;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.RDN;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class DnTest {

  public static final String RAW_DN =
      "CN=Dev Zeenea Test Only Root CA,OU=Test,OU=Professional Services,O=Zeenea,L=Paris,C=FR";

  @Test
  void dnShouldBeParsed() throws LDAPException {
    DN dn = new DN(RAW_DN);
    System.out.println(dn);
    System.out.printf("RDN=%s%n", dn.getRDN());
    System.out.printf("parent=%s%n", dn.getParent());
    System.out.printf("RDN list=%s%n", Arrays.toString(dn.getRDNs()));
  }

  @Test
  void rdnKey() throws LDAPException {
    DN dn = new DN(RAW_DN);
    RDN rdn = dn.getRDN();
    System.out.printf("attributes name = %s", Arrays.toString(rdn.getAttributeNames()));
  }
}
