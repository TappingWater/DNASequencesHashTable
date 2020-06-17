import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.LinkedList;
//On my honor:
//
//- I have not used source code obtained from another student,
//or any other unauthorized source, either modified or
//unmodified.
//
//- All source code and documentation used in my program is
//either my original work, or was derived by me from the
//source code published in the textbook for this course.
//
//- I have not discussed coding details about this project with
//anyone other than my partner (in the case of a joint
//submission), instructor, ACM/UPE tutors or the TAs assigned
//to this course. I understand that I may discuss the concepts
//of this program with other students, and that another student
//may help me debug my program so long as neither of us writes
//anything during the discussion or modifies any computer file
//during the discussion. I have violated neither the spirit nor
//letter of this restriction.

/**
 * Memory manager class that keeps track of the sequences in memory
 * as well as the blocks that are free for allocation.
 * 
 * @author Chanaka Perera(chanaka1)
 * @version 5/5/2019
 */
public class MemoryManager {

    // Local Variables
    // ------------------------------------------------------------------------

    // Local variable that holds the pointer to the memory file that holds the
    // sequences as well as the sequence IDS.
    private RandomAccessFile memFile;

    // Local variable that represents a linked list of handle objects that
    // represents the
    // free blocks in the memory file.
    private LinkedList<Handle> freeBlocks;


    // Methods
    // ------------------------------------------------------------------------

    /**
     * Default constructor for the memory manager class that initializes the
     * needed
     * variables in the class
     * 
     * @param fileP
     *            The name of the file used by the memory manager for storage
     * @throws IOException
     *             If the file cannot be overwritten
     */
    public MemoryManager(String fileP) throws IOException {
        // Open a file and overwrite if needed
        memFile = new RandomAccessFile(fileP, "rw");
        memFile.setLength(0);

        freeBlocks = new LinkedList<Handle>();
    }


    /**
     * This method inserts a sequence into a free block memory
     * of memory if one is found, else it creates a suitable block
     * of size and inserts it to the end of the file.
     * 
     * @param sequenceP
     *            The sequence to be inserted into the memory file
     * @param lengthP
     *            The length of the sequence to be inserted in terms of
     *            letters
     * @return
     *         The handle object that is a representation of the inserted
     *         sequence within the memory file.
     * @throws IOException
     *             If the memory file cannot be written to
     */
    public Handle insert(String sequenceP, int lengthP) throws IOException {
        // Calculate the number of bytes needed to represent this sequence
        int requiredBytes = (lengthP + 3) / 4;

        // Iterate through the linked list of handle objects to check whether
        // there is a block of sufficient size to store this sequence.
        for (int i = 0; i < freeBlocks.size(); i++) {
            Handle freeBlock = freeBlocks.get(i);
            int offset = freeBlock.getOffset();
            if (requiredBytes <= freeBlock.getBytes()) {
                // Attempt to write to the free block
                memFile.seek(offset);
                memFile.write(getByteSequence(sequenceP, requiredBytes));

                // If the free block has the same amount of bytes as needed
                // to represent the sequence it is removed from the linked list
                if (freeBlock.getBytes() != requiredBytes) {
                    int newBlockOffset = offset + requiredBytes;
                    int newBlockLength = 4 * (freeBlock.getBytes()
                        - requiredBytes);
                    Handle newHandle = new Handle(newBlockOffset,
                        newBlockLength);
                    freeBlocks.set(i, newHandle);

                }
                else {
                    freeBlocks.remove(freeBlock);
                }
                return new Handle(offset, lengthP);
            }
        }

        // If there are no free blocks of sufficient size then we need to append
        // the sequence to the end of the memory file
        int prevLength = (int)memFile.length();
        Handle freeBlock = freeBlockAtEnd();
        if (freeBlock == null) {
            memFile.setLength(prevLength + requiredBytes);
            memFile.seek(prevLength);
        }
        else {
            // Extends the file length by the required number of bytes and
            // removes
            // the free block from the linked list
            memFile.setLength(freeBlock.getOffset() + requiredBytes);
            memFile.seek(freeBlock.getOffset());
            freeBlocks.remove(freeBlock);
            memFile.write(getByteSequence(sequenceP, requiredBytes));
            return new Handle(freeBlock.getOffset(), lengthP);
        }
        memFile.write(getByteSequence(sequenceP, requiredBytes));

        return new Handle(prevLength, lengthP);
    }


    /**
     * Helper method to determine whether there is a free block at the end of
     * the memory file
     * 
     * @return
     *         The Handle object of the free block at the end of the file,
     *         or null if there is none.
     * @throws IOException
     *             IF the memory file cannot be read from
     */
    private Handle freeBlockAtEnd() throws IOException {
        for (int i = 0; i < freeBlocks.size(); i++) {
            Handle freeBlock = freeBlocks.get(i);
            int fileLength = (int)memFile.length();
            int blockLength = freeBlock.getBytes();
            int blockOffset = freeBlock.getOffset();
            if ((fileLength - blockLength) == blockOffset) {
                return freeBlock;
            }
        }
        return null;
    }


    /**
     * This method creates a byte array which represents the sequence to be
     * stored within the memory BIN file
     * 
     * @param sequenceP
     *            The string sequence that needs to be converted to the byte
     *            array
     * @param byteP
     *            The number of bytes needed to represent the sequence
     * @return
     *         The byte array representation of the sequence that is stored in
     *         the file
     */
    private byte[] getByteSequence(String sequenceP, int byteP) {
        byte[] byteSequence = new byte[byteP];
        int curr = 0;
        int count = 0;
        // Convert the sequence to a byte array at one character per time
        for (int i = 0; i < sequenceP.length(); i++) {
            int mod = i % 4;
            if (mod == 0) {
                curr = (charVal(sequenceP.charAt(i))) << 6;
            }
            else if (mod == 1) {
                curr |= (charVal(sequenceP.charAt(i))) << 4;
            }
            else if (mod == 2) {
                curr |= (charVal(sequenceP.charAt(i))) << 2;
            }
            else {
                curr |= charVal(sequenceP.charAt(i));
                byteSequence[count] = (byte)curr;
                count++;
            }
        }

        // For handling edge cases in terms of byte length
        if (count == byteP - 1) {
            byteSequence[count] = (byte)curr;
        }

        return byteSequence;
    }


    /**
     * Returns the binary representation for the given character.
     * 
     * @param charP
     *            The character to be converted to its equivalent binary
     *            representation
     * @return
     *         The binary representation of the character
     */
    private int charVal(char charP) {
        if (charP == 'A') {
            return 0b00;
        }
        if (charP == 'C') {
            return 0b01;
        }
        if (charP == 'G') {
            return 0b10;
        }
        if (charP == 'T') {
            return 0b11;
        }
        return -1;
    }


    /**
     * This method removes a sequence from the memory file
     * and creates a new free block to take its place
     * 
     * @param handleP
     *            The handle of the sequence that is removed
     *            from the memory file
     */
    public void remove(Handle handleP) {
        // Checks for the new position where the free block
        // needs to be inserted into the linked list
        for (int i = 0; i < freeBlocks.size(); i++) {
            Handle freeBlock = freeBlocks.get(i);
            if (handleP.getOffset() < freeBlock.getOffset()) {
                freeBlocks.add(i, handleP);
                mergeFreeBlocks();
                return;
            }
        }

        // If there are no free blocks previous to given to the
        // handle in the parameter then add it to the end
        freeBlocks.add(handleP);
        mergeFreeBlocks();
    }


    /**
     * Helper method that merges any free blocks that are adjacent to one
     * another in the linked list
     */
    private void mergeFreeBlocks() {
        // For each free block, check it against the next block
        for (int i = 0; i < (freeBlocks.size() - 1); i++) {
            Handle first = freeBlocks.get(i);
            Handle second = freeBlocks.get(i + 1);
            int boundary = first.getOffset() + first.getBytes();

            // If 2 free blocks are adjacent to one another we remove
            // the 2nd free block and add merge the 2 blocks together
            if (boundary == second.getOffset()) {
                int mergeLength = first.getBytes() + second.getBytes();
                freeBlocks.remove(second);
                freeBlocks.set(i, new Handle(first.getOffset(), mergeLength
                    * 4));
                i--;
            }
        }
    }
    
    /**
     * Gets rid of any free blocks at the end of the file
     * @throws IOException
     *      If the memory file cannot be read
     */
    public void removeEndBlock() throws IOException {
        if (freeBlocks.size() >= 1) {
            int index = freeBlocks.size() - 1;
            Handle endBlock = freeBlocks.get(index);
            int boundary = endBlock.getOffset() + endBlock.getBytes();
            if (memFile.length() == boundary) {
                memFile.setLength(endBlock.getOffset());
                freeBlocks.remove(index);
            }
        }
    }


    /**
     * This method uses a handle object as a parameter to retrieve
     * the sequence within the memory file in the form of a string
     * 
     * @param handleP
     *            the handle object that is used to retrieve the sequence
     *            within the memory file
     * @return
     *         The relevant sequence within the memory file relative to
     *         the handle object
     * @throws IOException
     *             If the mem file cannot be read
     */
    public String getStringSequence(Handle handleP) throws IOException {

        byte[] bytes = new byte[handleP.getBytes()];

        memFile.seek(handleP.getOffset());
        memFile.read(bytes);

        String sequence = "";

        for (int i = 0; i < bytes.length; i++) {
            sequence = sequence + convertBinToStr(bytes[i]);
        }

        return sequence.substring(0, handleP.getLength());
    }


    /**
     * Helper method that is used to convert binary to string.
     * 
     * @param byteP
     *            The byte to convert to string
     * @return
     *         The string representation of a byte value
     */
    private String convertBinToStr(byte byteP) {
        // Get out each 2-bit value
        int[] charsInByte = { (byteP & 0xC0) >> 6, (byteP & 0x30) >> 4, (byteP
            & 0x0C) >> 2, (byteP & 0x03) };

        // Convert bit-pairs to respective characters
        String output = "";
        for (int c : charsInByte) {
            if (c == 0) {
                output += "A";
            }
            else if (c == 1) {
                output += "C";
            }
            else if (c == 2) {
                output += "G";
            }
            else if (c == 3) {
                output += "T";
            }
        }

        return output;
    }


    /**
     * This method prints out the linked list of free blocks
     * within the memory file
     * 
     * @return
     *         A string denoting the free blocks of memory within the
     *         linked list
     */
    public String toString() {
        // If there are no free blocks of memory
        if (freeBlocks.size() <= 0) {
            return "Free Block List: none";
        }
        // If there are free blocks of memory we iterate through
        // the linked list and print them in order
        String free = "Free Block List:";

        int blockCount = 1;
        for (int i = 0; i < freeBlocks.size(); i++) {
            Handle freeHandle = freeBlocks.get(i);
            free = free + ("\n[Block " + blockCount + "]");
            free = free + " Starting Byte Location: " + freeHandle.getOffset();
            free = free + ", Size " + freeHandle.getBytes() + " bytes";
            blockCount++;
        }

        return free;
    }



}
