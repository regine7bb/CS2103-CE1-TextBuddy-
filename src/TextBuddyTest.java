import static org.junit.Assert.*;

import java.io.File;
import org.junit.Test;

public class TextBuddyTest {
	private static final String MESSAGE_CLEAR = "all content deleted from %1$s";
	private static final File saveFileName = new File("chat.txt"); //saved file name
	private static final String MESSAGE_SORT = "lines sorted alphabetically";
	private static final String MESSAGE_ADDED = "added to %1$s: \"%2$s\"";
	private static final String MESSAGE_DELETEERROR = "content is not in %1$s!";
	private static final String MESSAGE_EMPTY = "%1$s is empty";
	String sortCommand = "sort";
	String displayCommand = "display";
	String clearCommand = "clear";
	@Test
	public void testIsFileNameChanged() {
		assertFalse(TextBuddy.isFileNameChanged());
	}
	@Test
	public void testFileName() {
		assertEquals("chat.txt", TextBuddy.getFileName()+".txt");
	}
	@Test
	public void testHasSearchKey() {
		assertFalse(TextBuddy.hasSearchKey("a", "abcde"));
	}
	@Test
	public void testAddContent(){
		String str = "here";
		assertEquals(String.format(MESSAGE_ADDED, saveFileName,str), TextBuddy.addContent(str));
	}
	@Test
	public void testDelete(){
		assertEquals(String.format(MESSAGE_DELETEERROR, saveFileName),TextBuddy.delete(1));
	}
	@Test
	public void testClear(){
		assertEquals(String.format(MESSAGE_CLEAR, saveFileName), TextBuddy.clear());
	}
	@Test
	public void testDisplay(){
		assertEquals(String.format(MESSAGE_EMPTY, saveFileName), TextBuddy.display());
	}
	@Test
	public void testFileSaved(){
		assertTrue(TextBuddy.isFileSaved());
	}
	@Test
	public void testCommandType(){
		assertEquals(TextBuddy.CommandType.ADD, TextBuddy.determineCommandType("add"));
	}
	@Test
	public void testGetCommand(){
		assertEquals("add", TextBuddy.getFirstWord("add hello"));
	}
	@Test
	public void testGetLineNum(){
		assertEquals(1, TextBuddy.getLineNum("delete 1"));
	}
	@Test
	public void testGetText(){
		assertEquals("hello",TextBuddy.getText("add hello"));
	}
	@Test
	public void testSort(){
		assertEquals(MESSAGE_SORT,TextBuddy.sort());
	}
}
