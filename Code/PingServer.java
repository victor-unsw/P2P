/**
 * Created by victorchoudhary on 15/05/15.
 */

import java.io.*;
import java.net.*;

public class PingServer extends Thread{
    
    private static final int AVERAGE_DELAY = 100;       // milliseconds
    private int port;
    private int identity;
    
    private int successor1;
    private int successor2;
    
    private DatagramSocket pingServer;
    
    public PingServer(int identity,int successor1,int successor2){
        this.identity = identity;
        this.port = cdht.getPort(identity);
        this.successor1 = successor1;
        this.successor2 = successor2;
    }
    
    public void run(){
        try {
            pingServer = new DatagramSocket(port);
            Thread successor1Request = new PingRequest(pingServer,successor1);
            successor1Request.start();
            Thread successor2Request = new PingRequest(pingServer,successor2);
            successor2Request.start();
            Thread response = new PingResponse(pingServer);
            response.start();
            successor1Request.join();
            successor2Request.join();
            response.join();
        }catch (IOException e){
            
        }catch (InterruptedException e){
            
        }
    }
}

class PingRequest extends Thread{
    
    private DatagramSocket socket;
    private boolean stopped = false;
    private int targetIdentity;
    private int targetPort;
    private InetAddress targetAddress;
    
    public PingRequest(DatagramSocket socket,int targetIdentity){
        this.socket = socket;
        this.targetIdentity = targetIdentity;
        try {
            targetAddress = InetAddress.getLocalHost();
        }catch (UnknownHostException e){
            e.printStackTrace();
        }
        this.targetPort = cdht.getPort(targetIdentity);
    }
    
    public void halt(){
        this.stopped = true;
    }
    
    public void run(){
        
        DatagramPacket packet;
        String requestMessage = "A Ping Request";
        byte[] requestMessageBytes = requestMessage.getBytes();
        while (!stopped){
            packet = new DatagramPacket(requestMessageBytes,0,requestMessageBytes.length,targetAddress,targetPort);
            try {
                socket.send(packet);
                //System.out.println("sent to : " + targetPort);
                Thread.sleep(1000);
            }catch (IOException e){
                System.out.println("error while sending");
            }catch (InterruptedException e){}
        }
    }
    
}

class PingResponse extends Thread{
    
    private DatagramSocket socket;
    private boolean stopped = false;
    private int buffersize = 1024;
    private InetAddress targetAddress;
    
    public PingResponse(DatagramSocket socket){
        this.socket = socket;
        try {
            targetAddress = InetAddress.getLocalHost();
        }catch (UnknownHostException e){
            
        }
    }
    
    public void halt(){
        this.stopped = true;
    }
    
    public void run(){
        DatagramPacket packet;
        byte[] buffer;
        while (!stopped){
            buffer = new byte[buffersize];
            packet = new DatagramPacket(buffer,buffer.length);
            try {
                socket.receive(packet);
                String message = getMessage(packet);
                String portInfo = String.valueOf(packet.getPort());
                System.out.println(message.trim() + " message was received from Peer " + portInfo.charAt(portInfo.length() - 1));
                if (isRequest(message))
                    sendResponse(packet.getPort());
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
    
    public boolean isRequest(String message){
        CharSequence seq = "Request";
        return message.contains(seq);
    }
    
    public void sendResponse(int targetPort){
        String responseMessage = "A Ping Response";
        byte[] buffer = responseMessage.getBytes();
        DatagramPacket packet = new DatagramPacket(buffer,0,buffer.length,targetAddress,targetPort);
        try {
            socket.send(packet);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    
    private static String getMessage(DatagramPacket request) throws IOException
    {
        // Obtain references to the packet's array of bytes.
        byte[] buf = request.getData();
        
        // Wrap the bytes in a byte array input stream,
        // so that you can read the data as a stream of bytes.
        ByteArrayInputStream bais = new ByteArrayInputStream(buf);
        
        // Wrap the byte array output stream in an input stream reader,
        // so you can read the data as a stream of characters.
        InputStreamReader isr = new InputStreamReader(bais);
        
        // Wrap the input stream reader in a bufferred reader,
        // so you can read the character data a line at a time.
        // (A line is a sequence of chars terminated by any combination of \r and \n.)
        BufferedReader br = new BufferedReader(isr);
        
        // The message data is contained in a single line, so read this line.
        String line = br.readLine();
        
        // Print host address and data received from it.
        return line;
    }
    
}