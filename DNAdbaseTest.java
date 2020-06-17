import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import student.TestCase;

/**
 * Tests the main method and the associated hash table
 * and memory manager classes by comparing the differences
 * of the sample input to the sample output
 * 
 * @author Chanaka Perera(chanaka1)
 * @version 5/6/2019
 *
 */
public class DNAdbaseTest extends TestCase {

    /**
     * Tests the main method by comparing the output to
     * the desired console output
     * 
     * @throws IOException
     *             If the files can be opened to read from or written to
     */
    public void testMain() throws IOException {
        PrintStream out = new PrintStream(new FileOutputStream("output.txt"));
        System.setOut(out);
        @SuppressWarnings("unused")
        DNAdbase dBase = new DNAdbase();
        String[] args = new String[4];
        args[0] = "P4SampleInput.txt";
        args[1] = "hash_file";
        args[2] = "64";
        args[3] = "memory_file";
        DNAdbase.main(args);        
        out.close();
        compareFiles("output.txt", "P4SampleOutput.txt");
        assertTrue(compareFiles("output.txt", "P4SampleOutput.txt"));
    }    



    /**
     * Compares 2 text files line by line to see whether they are equal.
     * 
     * @param file1
     *            The first file to compare
     * @param file2
     *            The file to compare against the first file
     * @return
     *         True if the two files have the same output or false otherwise
     * @throws IOException 
     */
    private boolean compareFiles(String file1, String file2)
        throws IOException {
        
        BufferedReader reader1 = new BufferedReader(new FileReader(file1));
        BufferedReader reader2 = new BufferedReader(new FileReader(file2));
            

        String line1 = reader1.readLine();
        String line2 = reader2.readLine();

        boolean areEqual = true;

        int lineNum = 1;

        while (line1 != null || line2 != null) {
            if (line1 == null || line2 == null) {
                areEqual = false;
                break;
            }
            
            else if (!line1.equals(line2)) {
                areEqual = false;
                break;
            }

            line1 = reader1.readLine();
            line2 = reader2.readLine();

            lineNum++;
        }
        reader1.close();
        reader2.close();

        if (areEqual) {            
            return true;
        }
        else {            
            System.out.println(
                "Two files have different content. They differ at line "
                    + lineNum);
            return false;
        }

        
    }
}
