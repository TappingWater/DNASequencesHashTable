
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
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
 * This is the main class that handles:
 * 1) Read and interpret the command line
 * 2) Parse the input file for the needed information
 * 3) Execute the commands using the other classes in the program
 * 4) Outputs the information to file
 * 
 * @author Chanaka Perera (chanaka1)
 * @version 5/5/2019
 */
public class DNAdbase {

    // Local Variables
    // ---------------------------------------------------------

    // This is the local variable that holds the hash table
    private static HashTable hashTable;
    // This is the local variable that holds the memory manager
    private static MemoryManager memManager;


    // Methods
    // ----------------------------------------------------------

    /**
     * Default main method that takes the string arguments and makes use of the
     * hash table and memory manager to execute the commands as required.
     * 
     * @param args
     *            The cmd line arguments
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

        // Prints an error message if the number of arguments passed is
        // incorrect
        if (args.length != 4) {
            System.out.println("Invalid command arguments");
            System.out.println("Correct Format: DNAdbase <cmd_file> <hash_file>"
                + " <hashTable_size> <memory_file>");
            System.exit(0);
        }

        // Check whether the size is a multiple of 32
        if (Integer.valueOf(args[2]) % 32 != 0) {
            System.out.println(
                "The hash table size needs to be a multiple of 32");
            System.exit(0);
        }

        memManager = new MemoryManager(args[3]);
        hashTable = new HashTable(args[1], Integer.valueOf(args[2]),
            memManager);

        execute(args[0]);

    }


    /**
     * This method takes the name of the cmd file as a parameter and executes
     * the commands in a line by line manner
     * 
     * @param cmdFileP
     *            The name of the file containing the commands that need to be
     *            executed
     * @throws IOException
     */
    private static void execute(String cmdFileP) throws IOException {

        // Open an input stream to read the command file
        InputStream cmdStream = new FileInputStream(cmdFileP);
        Scanner cmdFile = new Scanner(cmdStream);

        // Keep reading in commands until we reach the EOF
        String line = cmdFile.nextLine();
        while (line != null) {

            if (line.contains("insert")) {
                // Parse the sequenceID, sequence, and their lengths from the
                // command file
                Scanner sequenceIDLine = new Scanner(line);
                sequenceIDLine.next();
                String sequenceID = sequenceIDLine.next();
                int sequenceIDLength = sequenceIDLine.nextInt();
                line = cmdFile.nextLine();
                Scanner sequenceLine = new Scanner(line);
                String sequence = sequenceLine.next();
                insert(sequenceID, sequenceIDLength, sequence);
                sequenceIDLine.close();
                sequenceLine.close();
            }

            else if (line.contains("remove")) {
                Scanner removeLine = new Scanner(line);
                removeLine.next();
                String sequenceID = removeLine.next();
                remove(sequenceID);
                removeLine.close();
            }

            else if (line.contains("print")) {
                print();
            }

            else if (line.contains("search")) {
                Scanner searchLine = new Scanner(line);
                searchLine.next();
                String sequenceID = searchLine.next();
                searchLine.close();

                search(sequenceID);
            }

            if (cmdFile.hasNextLine()) {
                line = cmdFile.nextLine();
            }
            else {
                line = null;
            }
        }
        cmdFile.close();
    }


    /**
     * This method is used to insert a sequence into the memory manager
     * as well as the hashTable. If for some reason, the hashTable cannot
     * accept the sequence it is rejected
     * 
     * @param sequenceIDP
     *            The sequenceID of the sequence to insert into the table and
     *            memory manager
     * @param lengthP
     *            The length of the sequence to insert
     * @param sequenceP
     *            The sequence to insert into the table and memory manager
     * @throws IOException
     *             If either the memFile or the Hash File cannot be read from or
     *             written to
     */
    private static void insert(
        String sequenceIDP,
        int lengthP,
        String sequenceP)
        throws IOException {
        // Check the length
        if (lengthP <= 0) {
            System.out.println("Invalid Length");
            return;
        }

        // Check for duplicates
        Handle[] duplicate = hashTable.search(sequenceIDP);
        if (duplicate != null) {
            System.out.println("SequenceID " + sequenceIDP + " exists");
            return;
        }

        // Add both sequenceID and sequence to the memory manager
        Handle[] toInsert = new Handle[2];
        toInsert[0] = memManager.insert(sequenceIDP, sequenceIDP.length());
        toInsert[1] = memManager.insert(sequenceP, lengthP);

        // Add both to the table
        boolean canInsert = hashTable.insert(sequenceIDP, toInsert[0],
            toInsert[1]);

        // If the bucket is full
        if (!canInsert) {
            memManager.remove(toInsert[0]);
            memManager.remove(toInsert[1]);
            memManager.removeEndBlock();
            System.out.println("Bucket Full. Sequence " + sequenceIDP
                + " could not be inserted");
        }
    }


    /**
     * This method takes a sequenceID as a parameter and searches
     * the hashTable to check whether there is a sequence that can
     * be removed. It found it removes the sequence from both the
     * memory manager as well as the hash table
     * 
     * @param sequenceIDP
     *            The sequenceID that needs to be removed from the hash table
     *            and memory manager
     * @throws IOException
     *             If the memory file or the hash file cannot be read from or
     *             written
     *             to
     */
    private static void remove(String sequenceIDP) throws IOException {
        // Get ID from table
        Handle[] toRemove = hashTable.search(sequenceIDP);

        // Check if it wasn't in the table
        if (toRemove == null) {
            System.out.println("SequenceID " + sequenceIDP + " not found");
            return;
        }

        // If found get the sequence that needs to be removed
        String sequence = memManager.getStringSequence(toRemove[1]);

        // Remove the sequence from both the table as well as the hash table
        memManager.remove(toRemove[0]);
        memManager.remove(toRemove[1]);

        hashTable.remove(sequenceIDP);

        System.out.println("Sequence Removed " + sequenceIDP + ":");
        System.out.println(sequence);
        memManager.removeEndBlock();
    }


    /**
     * This method uses the default print methods to print both
     * the tables as well as the memory manager
     */
    private static void print() {
        // Output the table
        System.out.println(hashTable);
        // Output free blocks
        System.out.println(memManager);
    }


    /**
     * This method searches the hashTable for the given sequenceID.
     * If found, it prints the sequence, else prints an error message
     * 
     * @param sequenceIDP
     *            The sequenceID to search for in the hash table
     * @throws IOException
     */
    private static void search(String sequenceIDP) throws IOException {
        Handle[] searchSequence = hashTable.search(sequenceIDP);

        // If the given sequenceID is not found in the hash table
        if (searchSequence == null) {
            System.out.println("SequenceID " + sequenceIDP + " not found");
            return;
        }

        // Get the sequence
        String entry = memManager.getStringSequence(searchSequence[1]);

        System.out.println("Sequence Found: " + entry);
    }

}
