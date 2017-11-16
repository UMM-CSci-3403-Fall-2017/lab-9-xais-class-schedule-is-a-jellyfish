package segmentedfilesystem;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;

public class Main {
    
    public static void main(String[] args) throws IOException {
        ArrayList<byte[]> File1 = new ArrayList<byte[]>();
        ArrayList<byte[]> File2 = new ArrayList<byte[]>();
        ArrayList<byte[]> File3 = new ArrayList<byte[]>();
        
        byte[] emptyBuffer = new byte[256];
        byte[] buffer = new byte[8000];
        
        //<3ofgold.more is.you em en.education (!$^.%&.##.%%) port 6014
        DatagramSocket s = new DatagramSocket(6014);
        InetAddress address = InetAddress.getByName("heartofgold.morris.umn.edu");
        DatagramPacket initialSend = new DatagramPacket(emptyBuffer, 0, address, 6014);
        s.send(initialSend);
        
        DatagramPacket receiver = new DatagramPacket(buffer, buffer.length);
        
        byte[] receive = new byte[8000];
        
        while(true){
        	s.receive(receiver);
        	receive = receiver.getData();
        	
        	distributeData(File1, File2, File3, receive);
        	
        	
        }
    }
    
    public static void distributeData(ArrayList<byte[]> f1, ArrayList<byte[]> f2,ArrayList<byte[]> f3, byte[] b){
    	String k = Integer.toBinaryString(b[0]);
    	byte[] holder1, holder2, holder3;
    	
    	holder1 = f1.get(1);
    	holder2 = f2.get(1);
    	holder3 = f3.get(1);
    	
    	if(f1.isEmpty() || holder1[1] == b[1]){
    		f1.add(b);
    	} else if (f2.isEmpty() || holder2[1] == b[1]){
    		f2.add(b);
    	} else if (f3.isEmpty() || holder3[1] == b[1]){
    		f3.add(b);
    	}
    	
    }

}

/*
 * Steps:
 * fill arraylists with arrays of length 8000 (1kb)
 * 	placed into arraylist based on array indexes 8 to 15 (file id)
 * sort based on indexes 0-7 (status byte) 
 * and if not a header sort by indexes 16-31 (packet number)
 * Once end packet is received (2nd bit in status byte == 1) we know the largest packet number that will be received for this file
 * 
 */
