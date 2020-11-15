import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Receiver {

    private static int windowSize = 4;
    private static int count = 0;
    private static int checkAck = 0;
    private static int ackCount = 0;
    private static int max = 200;
    private static byte[] data;
    private static int userNum;
    private static int testNum;
    private static int ackLoss;
    private static ByteBuffer buf;
    private static Path outputFile;

    //same thing as Sender -- probability for ACK loss. -- don't think this is needed

    /**
     * public static int ackLossSim() {
     * user inputs number 0-99
     * Scanner reader = new Scanner(System.in);
     * do {
     * System.out.println("Please enter a number from 0-99:");
     * userNum = Integer.parseInt(reader.nextLine());
     * if (userNum < 0 || userNum > 99) {
     * System.out.println("Invalid. Please enter a number from 0-99: \n");
     * testNum = -1;   //wrong input = loop again
     * } else if (userNum >= 0 && userNum <= 99) {
     * return userNum;
     * }
     * } while (userNum >= 0 && userNum <= 99 || testNum == -1);
     * return userNum;
     * }
     */

    public static void main(String[] args) throws IOException {
//        File destfile = new File("hello.txt");
//        FileOutputStream fos = new FileOutputStream(destfile);
//        BufferedOutputStream bos = new BufferedOutputStream(fos);

        DatagramSocket ds = new DatagramSocket(8888);// open port to listen
        InetAddress ip = InetAddress.getLocalHost();
        byte[] receive = new byte[4096];
        ByteBuffer buff = ByteBuffer.wrap(receive);
        DatagramPacket DpReceive = null;
        DatagramPacket ack;
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
                    String msg = new String(DpReceive.getData(), DpReceive.getOffset(), DpReceive.getLength());// to format the bytes back into strings
                    //System.out.println("Received text = " + msg);

                    Files.writeString(outputFile, msg); //write into Output.txt

                    packet = new Packet(count, true);
                    System.out.println(packet.getSeqNum() + " received"); //"[seq num] received"
                    packetsList.add(packet); //add packet to list
                } catch (SocketTimeoutException e) {
                    packet = new Packet(count, false);
                }
                ++count;
            }

            //if end of frame, send ACK for first packet of frame
            if (count == eachRoundCompare) {
                //userNum = ackLossSim();
                int ackFind = count - windowSize; //get to first packet of frame

                int pseudoNum = new Random(System.currentTimeMillis()).nextInt();

                //if (pseudoNum < userNum) {
                //    ++ackLoss; //keep count of total packet losses
                //} else {

                //loop through arraylist of packets
                for (int i = 0; i < count; ++i) {
                    if (packetsList.get(i).getSeqNum() == ackFind) { //get packet seq num
                        //if the seq num is false, it means this is a lost packet
                        if (packetsList.get(i).getExist() == false) {
                            //do nothing; don't send ACK
                        } else {
                            String ackString = "ACK " + packetsList.get(i).getSeqNum(); //string: ACK [seq Num]
                            byte[] ackByte = ackString.getBytes(); //convert ackString to bytes
                            ack = new DatagramPacket(ackByte, ackByte.length, ip, 8888); //attempted to create new Datagram packet of ackByte
                            ds.send(ack);
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

