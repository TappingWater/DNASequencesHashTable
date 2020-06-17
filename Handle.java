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
 * This class is a representation of the handle that is returned by
 * the memory manager. It stores two value for retrieving a sequence from
 * the dataBase: the offset and the length
 * 
 * @author Chanaka Perera(chanaka1)
 * @version 5/5/2019
 */
public class Handle {

    // Local Variables
    // ---------------------------------------------------------------

    // The integer value for the offset of a sequence held in memory
    private int offset;
    // The length of the particular sequence relative to the handle in memory.
    // The value is based on the number of letters, where 1 byte is equal to 4
    // letters.
    private int length;


    // Methods
    // ---------------------------------------------------------------

    /**
     * Default constructor for the handle class
     * 
     * @param offsetP
     *            integer offset of the starting position of the relevant
     *            sequence
     *            in the memory manager
     * @param lengthP
     *            the integer length of the sequence
     */
    public Handle(int offsetP, int lengthP) {
        offset = offsetP;
        length = lengthP;
    }


    /**
     * Getter method for the length variable of this handle
     * object for the particular sequence
     * 
     * @return
     *         The length of the sequence represented by this
     *         handle object
     */
    public int getLength() {
        return length;
    }


    /**
     * Getter method for the offset variable of this handle
     * object
     * 
     * @return
     *         offset value for starting position of the sequence
     *         in the memory manager
     */
    public int getOffset() {
        return offset;
    }


    /**
     * Calculates the number of bytes required to store this
     * sequence in the memory manager in terms of bytes.
     * 
     * @return
     *         The number of bytes needed to represent the
     *         sequence relevant to this handle
     */
    public int getBytes() {
        return (length + 3) / 4;
    }


    /**
     * Boolean method that compares 2 handle objects and checks for equality
     * based on the offset and length variable
     * 
     * @param handleP
     *            the handle object that is compared against this handle object
     * @return
     *         True if the handle objects have equal offset and length variables
     *         or false otherwise
     * @override
     */
    public boolean equals(Object handleP) {
        if (handleP == null) {
            return false;
        }
        if (handleP.getClass() != Handle.class) {
            return false;
        }
        else {
            int offset2 = ((Handle)handleP).getOffset();
            int length2 = ((Handle)handleP).getLength();
            return ((offset == offset2) && (length == length2));
        }
    }


    /**
     * Method for printing the handles in the format of:
     * [offset Value, length]
     * 
     * @return
     *         A string indicating the offset and length of the sequence
     */
    public String toString() {
        return "[" + offset + ", " + length + "]";
    }
}
