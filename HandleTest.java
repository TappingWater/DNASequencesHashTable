import student.TestCase;

/**
 * This class tests the methods in the handle class and whether
 * they work as expected
 * 
 * @author Chanaka Perera (chanaka1)
 * @version 5/5/2019
 *
 */
public class HandleTest extends TestCase {

    /**
     * Basic method that tests the methods in the handle class
     */
    public void testHandle() {
        Handle handle1 = new Handle(2, 3);
        Handle handle2 = new Handle(4, 6);
        Handle handle3 = new Handle(2, 3);
        assertEquals(handle1.getLength(), 3);
        assertEquals(handle1.getOffset(), 2);
        assertEquals(handle1.getBytes(), 1);
        assertTrue(handle1.equals(handle3));
        assertFalse(handle1.equals(handle2));
    }
}
