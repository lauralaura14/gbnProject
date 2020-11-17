import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class Receiver {

    private static int windowSize = 4;
    private static int count = 0;
    private static int checkAck = 0;
    private static int ackCount = 0;
    private static int max = 200;
    private static byte[] data;
    private static ByteBuffer buf;
    private static Path outputFile;


    public static void main(String[] args) throws IOException {
//        File destfile = new File("hello.txt");
//        FileOutputStream fos = new FileOutputStream(destfile);
//        BufferedOutputStream bos = new BufferedOutputStream(fos);

        DatagramSocket ds = new DatagramSocket(8888);// open port to listen
        InetAddress ip = InetAddress.getLocalHost();
        byte[] receive = new byte[1024];
        ByteBuffer buff = ByteBuffer.wrap(receive);
        DatagramPacket DpReceive = null;
        byte[] sendAck;
        DatagramPacket pk;
        ArrayList<Packet> packetsList = new ArrayList<>(); // list of all the packets

        outputFile = Path.of("../Output.txt"); //output file created

        while (true) {
            System.out.println("Server is awaiting packets...");
            DpReceive = new DatagramPacket(receive, receive.length); // create appropriate sized data packet

            int eachRoundCompare = count + windowSize; // each window frame
            Packet packet;

            //must account for lost packets -- do not know how to do this
            while (count < eachRoundCompare) {
                try {
                    ds.setSoTimeout(20000); //if exceed this timeframe, then timeout
                    ds.receive(DpReceive);// retrieve data
                    String message = new String(DpReceive.getData(), DpReceive.getOffset(), DpReceive.getLength());// to format the bytes back into strings
                    //System.out.println("Received text = " + msg);

                    packet = new Packet(count, DpReceive.getData(), true);
                    System.out.println(packet.getSeqNum() + " received"); //"[seq num] received"
                    packetsList.add(packet); //add packet to list
                } catch (SocketTimeoutException e) {
                    packet = new Packet(count, null, false);
                }
                ++count;
            }

            //if end of frame, send ACK for first packet of frame
            if (count == eachRoundCompare) {
                int ackFind = count - windowSize; //get to first packet of frame

                //loop through arraylist of packets
                for (int i = 0; i < packetsList.size(); ++i) {
                    if (packetsList.get(i).getSeqNum() == ackFind) { //get packet seq num that needs ACK
                        //if the seq num is false, it means this is a lost packet
                        if (packetsList.get(i).getExist() == false) {
                            //do nothing; don't send ACK
                        } else {
                            //Need to fix this -- sending ACK

                            String ack ="ACK: " + i + "Received";
                            sendAck = ack.getBytes();
                            pk = new DatagramPacket(sendAck, sendAck.length, ip, 8888);
                            ds.send(pk);

                            String message = new String(packetsList.get(i).getMsg());// to format the bytes back into strings
                            //System.out.println("Received text = " + msg);

                            Files.writeString(outputFile, message); //write the packet into the Output.txt after sending ACK
                        }
                    }
                }
            }

            //String msg = new String(DpReceive.getData(), DpReceive.getOffset(), DpReceive.getLength());// to format the bytes back into strings
            //System.out.println("Unmodified packet recieved = " + DpReceive.getData());
            //System.out.println("Recieved text = " + msg);
//            bos.write(msg);// We can add this later so we don't need to continue rewriting
            //buff.clear();
            //buff.rewind(); //reset buffer
//            break;// If we want the server to close, we will have to trigger it with the packet headers

        }
    }
}