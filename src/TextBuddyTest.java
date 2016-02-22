import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

public class TextBuddyTest {
	private static final String MESSAGE_CLEAR = "all content deleted from %1$s";
	private static final File saveFileName = new File("chat.txt"); //saved file name
	@Test
	public void testFileName() {
		assertEquals("chat.txt", TextBuddy.getFileName());
	}
	@Test
	public void testClear(){
		assertEquals(String.format(MESSAGE_CLEAR, saveFileName), TextBuddy.clear());
	}
	@Test
	public void testFileSaved(){
		assertTrue(TextBuddy.fileSaved());
	}
	@Test
	public void testGetCommand(){
		assertEquals("add", TextBuddy.getCommand("add hello"));
	}
	@Test
	public void testGetText(){
		assertEquals("hello",TextBuddy.getText("add hello"));
	}
}
