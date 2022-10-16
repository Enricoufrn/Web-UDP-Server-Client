import java.io.IOException;
import java.net.*;
import java.util.Random;

public class EchoClient {

    private static final int DEFAULT_BUF_LENGTH = Integer.MAX_VALUE;
    private static final int DEFAULT_TIMEOUT = 5000;
    public static final String DEFAULT_ADDRESS_NAME = "localhost";

    private DatagramSocket socket;
    private volatile boolean running;
    private byte[] buf;
    private int port;
    private int bufLength;
    private InetAddress address;

    public EchoClient(InetAddress address, int port) throws UnknownHostException {
        boolean setAddress = false;
        if(port > 0)
            this.port = port;
        else
            this.port = EchoServer.DEFAULT_SERVER_PORT;
        if(address != null) {
            this.address = address;
            setAddress = true;
        }
        if(!setAddress)
            this.address = InetAddress.getByName(DEFAULT_ADDRESS_NAME);
        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

    public void send(){
        try {
            buf = generateRandomByteArray();
            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
            String messageToSend = "ECHO_CLIENT: SEND\n"
                    + "==========================\n"
                    + "SERVER PORT: " + port + "\n"
                    + "SERVER ADDRESS: " + address + "\n"
                    + "MESSAGE: " + getMessage(buf) + "\n"
                    + "==========================\n";
            System.out.println(messageToSend);
            socket.setSoTimeout(DEFAULT_TIMEOUT);
            socket.send(packet);
            packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);
            String receivedMsg = "ECHO_CLIENT: RECEIVE\n"
                    + "==========================\n"
                    + "SERVER PORT: " + port + "\n"
                    + "SERVER ADDRESS: " + address + "\n"
                    + "RECEIVED MESSAGE: " + getReceivedMessage(packet) + "\n"
                    + "==========================\n";
            System.out.println(receivedMsg);
        } catch (IOException e) {
            System.out.println("ECHO_CLIENT: STOP");
            if(e instanceof SocketTimeoutException)
                System.out.println("======== TIMEOUT ========");
            throw new RuntimeException(e);
        }
    }

    public void stop(){
        if(socket != null){
            socket.close();
        }
    }

    private String getMessage(byte[] data){
        if(data != null && data.length > 0){
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < data.length; i++) {
                if(data[i] != 0){
                    stringBuilder.append(data[i]);
                }
            }
            return stringBuilder.toString();
        }else{
            return "";
        }
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

    public byte[] generateRandomByteArray(){
        Random random = new Random();
        int upperbound = (bufLength == 0) ? 256 : bufLength;
        int arrayLength = random.nextInt(upperbound);
        byte[] byteArray = new byte[arrayLength];
        random.nextBytes(byteArray);
        return byteArray;
    }
}
