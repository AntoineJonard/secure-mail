package application;

public class ServerConfig {

    private int port;
    private String adress;

    public ServerConfig(int port, String adress) {
        this.port = port;
        this.adress = adress;
    }

    public int getPort() {
        return port;
    }

    public String getAdress() {
        return adress;
    }
}
