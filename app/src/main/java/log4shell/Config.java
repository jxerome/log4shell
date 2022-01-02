package log4shell;

import picocli.CommandLine;

import java.net.InetAddress;

public class Config {
    @CommandLine.Option(names = {"-a", "--listen-address"}, defaultValue = "0.0.0.0")
    private InetAddress listenAddress;
    @CommandLine.Option(names = {"--ldap-port"}, defaultValue = "1389")
    private int ldapPort;
    @CommandLine.Option(names = {"--http-port"}, defaultValue = "5389")
    private int httpPort;

    public InetAddress getListenAddress() {
        return listenAddress;
    }

    public Config listenAddress(InetAddress listenAddress) {
        this.listenAddress = listenAddress;
        return this;
    }

    public int getLdapPort() {
        return ldapPort;
    }

    public Config ldapPort(int ldapPort) {
        this.ldapPort = ldapPort;
        return this;
    }

    public int getHttpPort() {
        return httpPort;
    }

    public Config httpPort(int httpPort) {
        this.httpPort = httpPort;
        return this;
    }


}
