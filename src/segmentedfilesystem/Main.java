package segmentedfilesystem;

import java.io.File;
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
        
        
        
        
        
        int counter = 0;
        while(true){
        	emptyBuffer = new byte[256];
            buffer = new byte[8000];
        	//System.out.println("it got here "+counter++);
        	byte[] receiveChunk = new byte[8000];
        	DatagramPacket receiver = new DatagramPacket(buffer, buffer.length);
        	s.receive(receiver);
        	receiveChunk = receiver.getData();
        	
        	distributeData(File1, File2, File3, receiveChunk);
        	
        	if(file1Done && file2Done && file3Done){
        		System.out.println("then it got here");
        		Collections.sort(File1, new ByteArrayComparator());
        		Collections.sort(File2, new ByteArrayComparator());
        		Collections.sort(File3, new ByteArrayComparator());
        		
        		writeFiles(File1);
        		writeFiles(File2);
        		writeFiles(File3);
        		
        		break;
        	}
        	
        }
        
        
    }
    
    public static void writeFiles(ArrayList<byte[]> fileData) throws IOException{
    	int charCode;
		byte[] byteHolder = fileData.get(0);
		String FileName = "";
		for(int i = 2; i < byteHolder.length; i++)
		{
			charCode = byteHolder[i];
			if(charCode != 0){
				FileName += new Character((char)charCode).toString();
			//	System.out.println(FileName);
			}
		}
		
		File f = new File(FileName);
		FileOutputStream fileOutput = new FileOutputStream(f);
		for(int i = 1; i < fileData.size(); i++)
			{
				byteHolder = fileData.get(i);
				for (int j = 4; j < byteHolder.length; j++)
				{
					fileOutput.write(byteHolder[j]);
				}
			}
    }
    
    public static void distributeData(ArrayList<byte[]> f1, ArrayList<byte[]> f2,ArrayList<byte[]> f3, byte[] b){
    	//System.out.println("This is the fileID byte "+(int) b[1]);
    	//k = Integer.toBinaryString(b[3]);
    	int c;
    	c=(Math.abs(b[2])<<8) + Math.abs(b[3]);
    	//System.out.println("This is a data byte "+c);
    	byte[] holder1 = {};
    	byte[] holder2 = {}; 
    	byte[] holder3 = {};
    	
    	if(!f1.isEmpty()){
    		holder1 = f1.get(0);
    	}
    	if(!f2.isEmpty()){
    		holder2 = f2.get(0);
    	}
    	if(!f3.isEmpty()){
    		holder3 = f3.get(0);
    	}
    	
    	if(f1.isEmpty() || holder1[1] == b[1]){
    		f1.add(b);
    		if(b[0]%2 == 1){
    			System.out.println("packet for 1");
    			//may need to convert to integer
    			if((b[0]%4) == 3){

    				file1Packets = ((int)(Math.abs(b[2])<<8)) + Math.abs(b[3]);
    				System.out.println("end packet for 1: "+file1Packets);
    			}
    		}
    	} else if (f2.isEmpty() || holder2[1] == b[1]){
    		f2.add(b);
    		if(b[0]%2 == 1){
    			System.out.println("packet for 2");
    			//may need to convert to integer
    			if((b[0]%4) == 3){
    				file2Packets = ((int)(Math.abs(b[2])<<8)) + Math.abs(b[3]);
    				System.out.println("end packet for 2: "+file2Packets);
    			}
    		}
    	} else if (f3.isEmpty() || holder3[1] == b[1]){
    		f3.add(b);
    		if(b[0]%2 == 1){
    			System.out.println("packet for 3");
    			//may need to convert to integer
    			if((b[0]%4) == 3){
    				
    				file3Packets = ((int)(Math.abs(b[2])<<8)) + Math.abs(b[3]);
    				System.out.println("end packet for 3: "+file3Packets);
    			}
    		}
    	}
    	
    	checkComplete(f1, f2, f3);
    }
    
    public static void checkComplete(ArrayList<byte[]> f1, ArrayList<byte[]> f2, ArrayList<byte[]> f3){
    	if(f1.size() == file1Packets+2){
    		file1Done = true;
    		System.out.println("File1 done");
    	}
    	if(f2.size() == file2Packets+2) {
    		file2Done = true;
    		System.out.println("File2 done");
    	}
    	if(f3.size() == file3Packets+2){
    		file3Done = true;
    		System.out.println("File3 done");
    	}
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
