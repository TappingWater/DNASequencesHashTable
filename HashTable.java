import java.io.IOException;
import java.io.RandomAccessFile;
// On my honor:
//
// - I have not used source code obtained from another student,
// or any other unauthorized source, either modified or
// unmodified.
//
// - All source code and documentation used in my program is
// either my original work, or was derived by me from the
// source code published in the textbook for this course.
//
// - I have not discussed coding details about this project with
// anyone other than my partner (in the case of a joint
// submission), instructor, ACM/UPE tutors or the TAs assigned
// to this course. I understand that I may discuss the concepts
// of this program with other students, and that another student
// may help me debug my program so long as neither of us writes
// anything during the discussion or modifies any computer file
// during the discussion. I have violated neither the spirit nor
// letter of this restriction.

/**
 * This class is a representation of the hash table object
 * that holds the handle objects to retrieve the sequences
 * from the memory bin file
 * 
 * @author Chanaka Perera (chanaka1)
 * @version 5/6/2019
 */
public class HashTable {

    // Local Variables
    // ---------------------------------------------------------------------
    // Local variable that holds the pointer to the byte array stored on the
    // disk. Used to store and access sequences given offsets.
    private RandomAccessFile hashFile;
    // Integer variable that designates the size of the hash table
    private int size;
    // Local variable that represents the memory manager object that holds the
    // sequences
    private MemoryManager memManager;

    /**
     * Local variables that define the min and max handle values
     */
    private Handle minHandle;
    private Handle maxHandle;


    // Methods
    // ------------------------------------------------------------------------

    /**
     * Default constructor method for the HashTable that is used to initialize
     * the local variables as required
     * 
     * @param fileNameP
     *            The name of the file that is used to hold the hashTable
     * @param sizeP
     *            The size of the hash table used in the program which is always
     *            a multiple of 32
     * @param memManagerP
     *            The memory manager object that holds the sequences
     * @throws IOException
     *             IF the file cannot be written to or read from
     */
    public HashTable(String fileNameP, int sizeP, MemoryManager memManagerP)
        throws IOException {
        hashFile = new RandomAccessFile(fileNameP, "rw");
        hashFile.setLength(0);
        hashFile.setLength(sizeP * 16);

        minHandle = new Handle(0, 0);
        maxHandle = new Handle(Integer.MAX_VALUE, Integer.MAX_VALUE);
        size = sizeP;
        memManager = memManagerP;
    }


    /**
     * Method that inserts the sequence into the first available slot. It uses
     * the sfold
     * algorithm given in the specification for insertion purposes
     * 
     * @param sequenceIDP
     *            the sequenceID/key value which is used to insert a sequence
     * @param iDHandleP
     *            the handle object which is used to store the sequenceID
     * @param sequenceHandleP
     *            the handle object that contains the actual sequence stored in
     *            the bin file
     * @return
     *         True if the sequence was successfully inserted or false otherwise
     * @throws IOException
     */
    public boolean insert(
        String sequenceIDP,
        Handle iDHandleP,
        Handle sequenceHandleP)
        throws IOException {

        long index = sfold(sequenceIDP, size);
        for (int i = 0; i < 32; i++) {
            Handle[] handles = retrieveHandleInfo(sequenceIDP, i);
            if (handles != null && (handles[0].equals(minHandle) || handles[0]
                .equals(maxHandle))) {
                long pointer = index + i;

                if (i >= 32 - (index % 32)) {
                    pointer -= 32;
                }

                hashFile.seek(pointer * 16);
                hashFile.writeInt(iDHandleP.getOffset());
                hashFile.writeInt(iDHandleP.getLength());
                hashFile.writeInt(sequenceHandleP.getOffset());
                hashFile.writeInt(sequenceHandleP.getLength());

                return true;
            }

        }

        return false;
    }


    /**
     * Searches the Hash file for the associated SequenceID offset and
     * and length as well as the associated sequence offset and length
     * in the memory file and returns it as a handle[] array
     * 
     * @param sequenceIDP
     *            the sequence ID to retrieve the results for
     * @param offsetP
     *            the value by which the index needs to be offset by
     * @return
     *         the handle[] containing the offset and length variables
     *         of both the sequence ID as well as the actual sequence
     * @throws IOException
     *             If the hash file cannot be read from or written to
     */
    private Handle[] retrieveHandleInfo(String sequenceIDP, int offsetP)
        throws IOException {
        long sfold = sfold(sequenceIDP, size);
        long index = sfold + offsetP;

        if (offsetP >= 32 - (sfold % 32)) {
            index = index - 32;
        }

        Handle[] result = new Handle[2];

        if ((index * 16) > hashFile.length()) {
            return null;
        }

        hashFile.seek(index * 16);

        int sequenceIDOffset = hashFile.readInt();
        int sequenceIDLength = hashFile.readInt();
        int sequenceOffset = hashFile.readInt();
        int sequenceLength = hashFile.readInt();

        result[0] = new Handle(sequenceIDOffset, sequenceIDLength);
        result[1] = new Handle(sequenceOffset, sequenceLength);

        return result;
    }


    /**
     * This is the hashing algorithm given in the project specifications
     * 
     * @param s
     *            The String sequence that needs to be hashed
     * @param m
     *            The size of the hash table
     * @return
     *         The index of the hash table where the sequence needs to be stored
     */
    private long sfold(String s, int m) {
        int intLength = s.length() / 4;
        long sum = 0;
        for (int j = 0; j < intLength; j++) {
            char[] c = s.substring(j * 4, (j * 4) + 4).toCharArray();
            long mult = 1;
            for (int k = 0; k < c.length; k++) {
                sum += c[k] * mult;
                mult *= 256;
            }
        }

        char[] c = s.substring(intLength * 4).toCharArray();
        long mult = 1;
        for (int k = 0; k < c.length; k++) {
            sum += c[k] * mult;
            mult *= 256;
        }

        sum = (sum * sum) >> 8;
        return (Math.abs(sum) % m);
    }


    /**
     * Remove both the sequenceID handle values as well
     * as the sequence handle values and replace these values with
     * the Integer.Max values
     * 
     * @param sequenceIDP
     *            The sequence ID that has to be removed from the hash file
     * @throws IOException
     *             If the hash file cannot be read from or written to
     */
    public void remove(String sequenceIDP) throws IOException {
        for (int i = 0; i < 32; i++) {

            Handle[] toRemove = retrieveHandleInfo(sequenceIDP, i);

            if (toRemove != null && !toRemove[0].equals(minHandle)
                && !toRemove[0].equals(maxHandle)) {
                String sequence = memManager.getStringSequence(toRemove[0]);
                if (sequence.equals(sequenceIDP)) {
                    long sfold = sfold(sequenceIDP, size);
                    long index = sfold + i;
                    if ((32 - (sfold % 32)) <= i) {
                        index = index - 32;
                    }

                    hashFile.seek(index * 16);
                    for (int j = 0; j < 4; j++) {
                        hashFile.writeInt(Integer.MAX_VALUE);
                    }

                }
            }
        }
    }


    /**
     * The default search method that can be used to search the
     * hash file for a sequenceID. It uses the sfold value to get
     * the initial index value for searching then continues in a linear manner
     * to find an exact match
     * 
     * @param sequenceIDP
     *            The seqeunceID that needs to be searched
     * @return
     *         A handle array containing both the sequenceID as well as the
     *         sequence handles
     * @throws IOException
     */
    public Handle[] search(String sequenceIDP) throws IOException {
        for (int i = 0; i < 32; i++) {
            Handle[] searchSequence = retrieveHandleInfo(sequenceIDP, i);
            if (searchSequence[0].equals(minHandle)) {
                return null;
            }
            if (searchSequence != null && !searchSequence[0].equals(
                maxHandle)) {
                String sequenceID = memManager.getStringSequence(
                    searchSequence[0]);
                if (sequenceID.equals(sequenceIDP)) {
                    return searchSequence;
                }
            }
        }
        return null;
    }


    /**
     * This is the default method used for printing the hash table
     * 
     * @return
     *         prints the elements stored in the hash table
     */
    public String toString() {
        String output = "Sequence IDs:";
        int sequenceIDOffset;
        int sequenceIDLength;
        int sequenceOffset;
        int sequenceLength;

        Handle[] sequenceHandle = new Handle[2];

        for (int i = 0; i < size; i++) {

            try {
                hashFile.seek(i * 16);
                sequenceIDOffset = hashFile.readInt();
                sequenceIDLength = hashFile.readInt();
                sequenceOffset = hashFile.readInt();
                sequenceLength = hashFile.readInt();
                sequenceHandle[0] = new Handle(sequenceIDOffset,
                    sequenceIDLength);
                sequenceHandle[1] = new Handle(sequenceOffset, sequenceLength);
                if (!sequenceHandle[0].equals(minHandle) && !sequenceHandle[0]
                    .equals(maxHandle)) {
                    String sequenceID = memManager.getStringSequence(
                        sequenceHandle[0]);
                    output = output + "\n" + sequenceID + ": hash slot [" + i
                        + "]";
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }

        }

        return output;
    }

}
