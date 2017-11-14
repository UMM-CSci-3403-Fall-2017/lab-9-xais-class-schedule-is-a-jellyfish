package segmentedfilesystem;

import java.util.ArrayList;

public class Main {
    
    public static void main(String[] args) {
        ArrayList<int[]> File1 = new ArrayList<int[]>();
        ArrayList<int[]> File2 = new ArrayList<int[]>();
        ArrayList<int[]> File3 = new ArrayList<int[]>();
        
        
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
