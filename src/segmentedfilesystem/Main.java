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
    
	//Booleans for the status of the files received
	public static boolean file1Done = false;
	public static boolean file2Done = false;
	public static boolean file3Done = false;
	
	//Integers to hold the file size
	public static int file1Packets = Integer.MAX_VALUE;
	public static int file2Packets = Integer.MAX_VALUE;
	public static int file3Packets = Integer.MAX_VALUE;
	
    public static void main(String[] args) throws IOException {
    	//ArrayLists to hold the arrays of bytes we are receiving
        ArrayList<byte[]> File1 = new ArrayList<byte[]>();
        ArrayList<byte[]> File2 = new ArrayList<byte[]>();
        ArrayList<byte[]> File3 = new ArrayList<byte[]>();
        
        //buffers
        byte[] emptyBuffer = new byte[256];
        byte[] buffer = new byte[8004];
        
        //Port Details
        //set up connection
        DatagramSocket s = new DatagramSocket(6014);
        InetAddress address = InetAddress.getByName("heartofgold.morris.umn.edu");
        DatagramPacket initialSend = new DatagramPacket(emptyBuffer, 0, address, 6014);
        s.send(initialSend);
        
        //int counter = 0;
        //Receive files
        while(true){
        	emptyBuffer = new byte[256];
            buffer = new byte[8004];
        	//System.out.println("it got here "+counter++);
        	byte[] receiveChunk = new byte[8004];
        	DatagramPacket receiver = new DatagramPacket(buffer, buffer.length);
        	s.receive(receiver);
        	receiveChunk = receiver.getData();
        	
        	distributeData(File1, File2, File3, receiveChunk);
        	
        	//Checks if all packets for all files have been received
        	if(file1Done && file2Done && file3Done){
//        		System.out.println("then it got here");
        		Collections.sort(File1, new ByteArrayComparator());
        		Collections.sort(File2, new ByteArrayComparator());
        		Collections.sort(File3, new ByteArrayComparator());
        		
//        		System.out.println(File1.size());
//        		System.out.println(File2.size());
//        		System.out.println(File3.size());
        		
        		writeFiles(File1);
        		writeFiles(File2);
        		writeFiles(File3);
        		
        		break;
        	}
        	
        }
        s.close();
        
    }
    
    /**
     * Takes the sorted byte arrays, and writes the files
     * @param fileData
     * @throws IOException
     */
    public static void writeFiles(ArrayList<byte[]> fileData) throws IOException{
    	int charCode;
    	Collections.sort(fileData, new ByteArrayComparator());
		byte[] byteHolder = fileData.get(0);
		
		//Finds the header packet, saves it, and removes it from the array
		for(int i = 0; i < fileData.size(); i++){
			byteHolder = fileData.get(i);
			if(byteHolder[0]%2 == 0){
				fileData.remove(i);
				break;
			}
		}
		String FileName = "";
		
		//Generates the filename for this byte array
		for(int i = 2; i < byteHolder.length; i++)
		{
			charCode = byteHolder[i];
			if(charCode != 0){
				FileName += new Character((char)charCode).toString();
			//	System.out.println(FileName);
			}
		}
		
		//Writes the files using the generated name
		File f = new File(FileName);
		FileOutputStream fileOutput = new FileOutputStream(f);
		for(int i = 0; i < fileData.size(); i++)
		{
				byteHolder = fileData.get(i);
				int zCounter = 0;
				//Writes each byte of the file separately
				//Doesn't write zeros to avoid writing extra zeros from the buffer
				//Only writes zeroes once it reaches a non-zero value
				//keeps track of the number of zeros it needs to write using zCounter
				for (int j = 4; j < byteHolder.length; j++)
				{
//					System.out.println(byteHolder[j]);
					
					if((int) byteHolder[j] != 0){
						for (int v = 0; v < zCounter; v++){
//							System.out.println("It did a zero");
							fileOutput.write((byte) 0);
						}
						zCounter = 0;
						fileOutput.write(byteHolder[j]);
						fileOutput.flush();
//						System.out.println(byteHolder[j]);
					} else {
						zCounter++;
					}
						
						
				}

		}

		fileOutput.close();
    }
    
    public static void distributeData(ArrayList<byte[]> f1, ArrayList<byte[]> f2,ArrayList<byte[]> f3, byte[] b){
//    	System.out.println("This is the fileID byte "+(int) b[1]);
//    	k = Integer.toBinaryString(b[3]);
    	int c;
    	c=(Math.abs(b[2])<<8) + Math.abs(b[3]);
//    	System.out.println("This is a data byte "+c);
    	//Byte arrays to check the fileID byte (if the ArrayList contains a byte array)
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
    	
    	//Puts the byte arrays into the first ArrayList that is either empty, 
    	//or is of the same fileID as the byte array in question
    	if(f1.isEmpty() || holder1[1] == b[1]){
    		f1.add(b);
    		if(b[0]%2 == 1){
//    			System.out.println("packet for 1");
    			//may need to convert to integer
    			if((b[0]%4) == 3){

    				file1Packets = ((int)(Math.abs(b[2])<<8)) + Math.abs(b[3]);
//    				System.out.println("end packet for 1: "+file1Packets);
    			}
    		}
    	} else if (f2.isEmpty() || holder2[1] == b[1]){
    		f2.add(b);
    		if(b[0]%2 == 1){
//    			System.out.println("packet for 2");
    			//may need to convert to integer
    			if((b[0]%4) == 3){
    				file2Packets = ((int)(Math.abs(b[2])<<8)) + Math.abs(b[3]);
//    				System.out.println("end packet for 2: "+file2Packets);
    			}
    		}
    	} else if (f3.isEmpty() || holder3[1] == b[1]){
    		f3.add(b);
    		if(b[0]%2 == 1){
//    			System.out.println("packet for 3");
    			//may need to convert to integer
    			if((b[0]%4) == 3){
    				
    				file3Packets = ((int)(Math.abs(b[2])<<8)) + Math.abs(b[3]);
//    				System.out.println("end packet for 3: "+file3Packets);
    			}
    		}
    	}
    	
    	checkComplete(f1, f2, f3);
    }
    
    /*
     * checks each ArrayList: If they are of same size as the last packet specified plus 2 
     * (to account for zero indexing and the header packet which has no packet number)
     * It changes the boolean value to true.
     */
    public static void checkComplete(ArrayList<byte[]> f1, ArrayList<byte[]> f2, ArrayList<byte[]> f3){
    	if(f1.size() == file1Packets+2){
    		file1Done = true;
    		//System.out.println("File1 done");
    	}
    	if(f2.size() == file2Packets+2) {
    		file2Done = true;
    		//System.out.println("File2 done");
    	}
    	if(f3.size() == file3Packets+2){
    		file3Done = true;
    		//System.out.println("File3 done");
    	}
    }
    
    /*
     * Compares the byte arrays, to sort the array to the correct order
     */
    public static class ByteArrayComparator implements Comparator<byte[]> {

    	@Override
    	public int compare(byte[] b1, byte[] b2) {
    		int n = ((int)(Math.abs(b1[2])<<8)) + Math.abs(b1[3]);
    		int m = ((int)(Math.abs(b2[2])<<8)) + Math.abs(b2[3]);
    		
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
