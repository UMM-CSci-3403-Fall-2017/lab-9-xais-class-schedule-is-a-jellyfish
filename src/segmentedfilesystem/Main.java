package segmentedfilesystem;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Main {
    
	public static boolean file1Done = false;
	public static boolean file2Done = false;
	public static boolean file3Done = false;
	public static int file1Packets = Integer.MAX_VALUE;
	public static int file2Packets = Integer.MAX_VALUE;
	public static int file3Packets = Integer.MAX_VALUE;
	
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
        
        byte[] receiveChunk = new byte[8000];
        
        while(true){
        	s.receive(receiver);
        	receiveChunk = receiver.getData();
        	
        	distributeData(File1, File2, File3, receiveChunk);
        	
        	if(file1Done && file2Done && file3Done){
        		Collections.sort(File1, new ByteArrayComparator());
        		Collections.sort(File2, new ByteArrayComparator());
        		Collections.sort(File3, new ByteArrayComparator());
        		
        		int charCode;
        		byte[] byteHolder = File1.get(0);
        		String File1Name = "";
        		for(int i = 2; i < byteHolder.length; i++)
        		{
        			charCode = byteHolder[i];
        			File1Name += new Character((char)charCode).toString();
        		}
        		
        		FileOutputStream fileOutput1 = new FileOutputStream(File1Name);
        		for(int i = 1; i < File1.size(); i++)
        			{
        				byteHolder = File1.get(i);
        				for (int j = 0; j < byteHolder.length; j++)
        				{
        					fileOutput1.write(byteHolder[j]);
        				}
        			}
        		break;
        	}
        	
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
    		if(b[0]%2 == 1){
    			//may need to convert to integer
    			if((b[0]%100) > 9){
    				file1Packets = (b[2]*1000)+b[3];
    			}
    		}
    	} else if (f2.isEmpty() || holder2[1] == b[1]){
    		f2.add(b);
    		if(b[0]%2 == 1){
    			//may need to convert to integer
    			if((b[0]%100) > 9){
    				file2Packets = (b[2]*1000)+b[3];
    			}
    		}
    	} else if (f3.isEmpty() || holder3[1] == b[1]){
    		f3.add(b);
    		if(b[0]%2 == 1){
    			//may need to convert to integer
    			if((b[0]%100) > 9){
    				file3Packets = (b[2]*1000)+b[3];
    			}
    		}
    	}
    	
    	checkComplete(f1, f2, f3);
    }
    
    public static void checkComplete(ArrayList<byte[]> f1, ArrayList<byte[]> f2, ArrayList<byte[]> f3){
    	if(f1.size() == file1Packets) file1Done = true;
    	if(f2.size() == file2Packets) file2Done = true;
    	if(f3.size() == file3Packets) file3Done = true;
    }
    
    public static class ByteArrayComparator implements Comparator<byte[]> {

    	@Override
    	public int compare(byte[] b1, byte[] b2) {
    		int n = (b1[2]*1000)+b1[3];
    		int m = (b2[2]*1000)+b2[3];
    		
    		int result = 0;
    		if (b1[0]%2 < b2[0]%2) {
    			result = -1;
    		} else if (b1[0]%2 > b2[0]%2) {
    			result = 1;
    		} else if (n < m) {
    			result = -1;				
    		} else if (n > m) {
    			result = 1;
    		}
    		
    		return result;	
    	}
    }

}



/*
 * Steps:
 * fill arraylists with arrays of length 8000 (1kb)
 * 	placed into arraylist based on file id
 * sort based on status byte
 * and if not a header sort by packet number
 * Once end packet is received (2nd bit in status byte == 1) we know the largest packet number 
 * that will be received for this file
 * 
 */
