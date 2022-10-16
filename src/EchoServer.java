import java.io.IOException;
import java.net.*;

public class EchoServer implements Runnable {

    public static final int DEFAULT_SERVER_PORT = 4500;
    private static final int DEFAULT_BUF_LENGTH = 1024;

    private DatagramSocket socket;
    private volatile boolean running;
    private byte[] buf;
    private int port;
    private int bufLength;
    private InetAddress address;
    
    public EchoServer(int port, InetAddress  address){
        boolean setAddress = false;
        if(port > 0)
            this.port = port;
        else
            this.port = DEFAULT_SERVER_PORT;
        if(address != null) {
            this.address = address;
            setAddress = true;
        }
        try {
            if(setAddress)
                socket = new DatagramSocket(port, address);
            else
                socket = new DatagramSocket(port, InetAddress.getByName(EchoClient.DEFAULT_ADDRESS_NAME));
        } catch (SocketException | UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        running = true;
        System.out.println("ECHO_SERVER: LISTEN - " + port);
        while (running){
            if(buf == null || bufLength < 0) {
                buf = new byte[DEFAULT_BUF_LENGTH];
                bufLength = DEFAULT_BUF_LENGTH;
            }
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            try {
                socket.receive(packet);
                InetAddress address = packet.getAddress();
                int port = packet.getPort();
                String receivedMsg = "ECHO_SERVER:\n"
                        + "==========================\n"
                        + "CLIENT SOCKET PORT: " + port + "\n"
                        + "CLIENT SOCKET ADDRESS: " + address + "\n"
                        + "RECEIVED MESSAGE: " + getReceivedMessage(packet) + "\n"
                        + "==========================\n";
                System.out.println(receivedMsg);
                packet = new DatagramPacket(packet.getData(), packet.getLength(), address, port);
                socket.send(packet);
            } catch (IOException e) {
                System.out.println("ECHO_SERVER: STOP");
                throw new RuntimeException(e);
            }
        }
        System.out.println("ECHO_SERVER: STOP");
        socket.close();
    }

    public boolean socketIsClosed(){
        return socket.isClosed();
    }

    private String getReceivedMessage(DatagramPacket packet) {
        if(packet != null){
            StringBuilder stringBuilder = new StringBuilder();
            byte[] data = packet.getData();
            for (int i = 0; i < packet.getLength(); i++) {
                if(data[i] != 0){
                    stringBuilder.append(data[i]);
                }
            }
            return stringBuilder.toString();
        }else{
            return "";
        }
    }

    public void stopEchoServer(){
        running = false;
    }

    public byte[] getBuf() {
        return buf;
    }

    public void setBuf(byte[] buf) {
        this.buf = buf;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getBufLength() {
        return bufLength;
    }

    public void setBufLength(int bufLength) {
        this.bufLength = bufLength;
    }

    public InetAddress getAddress() {
        return address;
    }

    public void setAddress(InetAddress address) {
        this.address = address;
    }
}
