/**
 * This class is used to save user input into text file after each user operation.
 * User can add contents and delete the contents in the file by the line number.
 * User able to sort lines alphabetically.
 * Search command to search for a word in the file and return the lines containing that word.
 * The storage is limit for this version is 10 lines of information.
 * In the case more than 10 lines of information was entered,
 * we store only the latest one. 
 * The command format is given by the example interaction below:

 Welcome to TextBuddy. Hello buddy(;!
 Your Chat will be recorded to chat.txt
 Command: add little brown fox
 added to chat.txt: "little brown fox"
 Command: add jumped over the moon
 added to chat.txt: "jumped over the moon"
 Command: display
 
 1. little brown fox
 2. jumped over the moon
 Command: delete 3
 content is not in chat.txt!
 Command: delete 2
 deleted from chat.txt: "jumped over the moon"
 Command: add little red hat
 added to chat.txt: "little red hat"
 Command: display
 1. little brown fox
 2. little red hat
 Command: clear
 all content deleted from chat.txt
 Command: display
 chat.txt is empty
 Command: exit

 * @author Xu Haoting Regine
 */
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Collections;

public class TextBuddy {
	
	private static final String WELCOME_MESSAGE = "Welcome to TextBuddy. Hello buddy(;!\nYour Chat will be recorded to ";
	private static final String MESSAGE_ADDED = "added to %1$s: \"%2$s\"";
	private static final String MESSAGE_DISPLAY = "%1$d. %2$s";
	private static final String MESSAGE_EMPTY = "%1$s is empty";
	private static final String MESSAGE_CLEAR = "all content deleted from %1$s";
	private static final String MESSAGE_DELETE = "deleted from %1$s: \"%2$s\"";
	private static final String MESSAGE_DELETEERROR = "content is not in %1$s!";
	private static final String MESSAGE_INVALID_FORMAT = "invalid command format :%1$s";
	private static final String MESSAGE_FILESAVEERROR = "%1$d not found.";
	private static final String MESSAGE_CHANGEFILENAME = "do you want to change file name %1$s? YES or NO";
	private static final String MESSAGE_KEYNEWFILENAME = "Key new save file name: ";
	private static final String MESSAGE_ADDNEWFILENAME = "save file name change to %1$s.";
	private static final String MESSAGE_SORT = "lines sorted alphabetically";
	private static final String MESSAGE_SEARCH = "those line(s) containing %1$s is/are";
	private static final String MESSAGE_ZERO_SEARCH_RESULT = "none of the lines.";
	private static final int MAX = 500; //max length of line (can be edit)
	private static String strFileName = "chat";
	protected static File saveFileNameTest = new File(strFileName+".txt");
	private static File saveFileName = saveFileNameTest;
	private static Queue<String> testAllChatQueue = new LinkedList<String> ();
	private static Queue<String> allChatQueue = testAllChatQueue; //store text
	private static Scanner scanner = new Scanner(System.in); //read input
	private static int totalNumChat = 0; //total number of lines
	
	/**
	 *  These are the possible command types
	 */
	enum CommandType {
		ADD, DELETE, DISPLAY, CLEAR, SORT, SEARCH, INVALID, EXIT
	};
	
	/**User guide**
	 * @param ADD      add, text here
	 * @param DELETE   delete, number line here
	 * @param DISPLAY  number, text here
	 * @param CLEAR    clear all text in the file
	 * @param INVAILD  format invalid, key again
	 * @param EXIT     exit the program
	 * @param Default  throw an error
	 */
	public static void main(String[] args) {

		System.out.println(WELCOME_MESSAGE + saveFileName);
		allChatQueue = new LinkedList<String> ();
		isFileNameChanged();
		saveFileName = new File(strFileName+".txt");
		runTextBuddy();
	}
	
	/**
	 * The Example method is change save file name
	 */
	protected static boolean isFileNameChanged(){
		
		System.out.println(String.format(MESSAGE_CHANGEFILENAME, saveFileName));
		String change = scanner.nextLine();
		String newFileName;
		
		if (change.equals("YES")) {
			System.out.println(MESSAGE_KEYNEWFILENAME);
			newFileName = scanner.nextLine();
			strFileName = newFileName;
			System.out.println(String.format(MESSAGE_ADDNEWFILENAME, strFileName+".txt"));
			return true;
		}
		return false;
	}
	
	/**
	 * The Example method is get the command and user input and display output
	 */
	private static void runTextBuddy(){
		
		while (true) {
			System.out.print("Command: ");
			String input = scanner.nextLine();
			String command = getFirstWord(input);
			executeCommand(determineCommandType(command),input);
		}
	}
	
	/**
	 * The Example method execute the command key by the user
	 * ADD:     add new text to file
	 * DELETE:  delete the text by it's number 
	 * DISPLAY: display all text saved
	 * CLEAR:   clear all text 
	 * SORT:    sort command to sort lines alphabetically
	 * SEARCH:  search for a word in the file and return the lines containing that word.
	 * INVAILD: the command key is invalid
	 * Default: throw an error if the command is not recognized
	 */
	private static void executeCommand(CommandType command, String input) {
		
		switch (command) {	
		case ADD :
			String chatText = getText(input);
			System.out.println(addContent(chatText));
			isFileSaved();
			break;
		
		case DELETE :
			int numToDelete = getLineNum(input);
			System.out.println(delete(numToDelete));
			isFileSaved();
			break;
		
		case DISPLAY :
			System.out.println(display());
			break;
		
		case CLEAR :
			System.out.println(clear());
			isFileSaved();
			break;
		
		case SORT:
			System.out.println(sort());
			break;
		
		case SEARCH:
			String strSearchKey = getText(input);
			search(strSearchKey);
			break;
			
		case INVALID :
			System.out.println(String.format(MESSAGE_INVALID_FORMAT, input));
			break;
		
		case EXIT :
			System.exit(0);
			break;
		
		default : 
			System.out.println(String.format(MESSAGE_INVALID_FORMAT, input));
			throw new Error("Unrecognized command type");
		}
	}
	
	/**
	 * The Example method is to search for a word in the file and
	 * return the lines containing that word.
	 */
	private static void search(String strSearchKey) {
		
		Queue<String> tempQueue = new LinkedList<String> ();
		int number = 0;
		System.out.println(String.format(MESSAGE_SEARCH, strSearchKey));
		
		while(!allChatQueue.isEmpty()){
			
			if(hasSearchKey(strSearchKey,allChatQueue.peek())){
				number+=1;
				System.out.println(String.format(MESSAGE_DISPLAY, number,allChatQueue.peek()));
			}
			tempQueue.offer(allChatQueue.poll());
		}
		if(number==0){
			System.out.println(String.format(MESSAGE_ZERO_SEARCH_RESULT));
		}
		allChatQueue = tempQueue;
	}

	
	/**
	 * The Example method is to search the if the words contain search key.
	 * return true if it has the key and false if it don't
	 */
	protected static boolean hasSearchKey(String strSearchKey, String line){
		
		String[] splitSentence = line.split("\\s");
		
		for(String word : splitSentence){
			if(word.equals(strSearchKey)){
				return true;
			}
		}
		return false;	
	}
	
	/**
	 * The Example method is sort command to sort lines alphabetically.
	 */
	protected static String sort() {
		
		ArrayList<String> tempArrList = new ArrayList<String> ();
		
		while (!allChatQueue.isEmpty()) {
			tempArrList.add(allChatQueue.poll());
		}
		Collections.sort(tempArrList);
		
		try {
			writeSortedListToFile(tempArrList);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return MESSAGE_SORT;
	}
	
	/**
	 * The Example method is  write sorted lines into file.
	 */
	private static void writeSortedListToFile(ArrayList<String> list) throws IOException{
		
		FileWriter fileWriter = new FileWriter(saveFileName);
		BufferedWriter buffWriter = new BufferedWriter(fileWriter);
		
		for(String line : list){
			allChatQueue.offer(line);
			buffWriter.write(line);
			buffWriter.newLine();
		}
		buffWriter.flush(); //upload content to file
		buffWriter.close(); //close file
	}

	/**
	 * The Example method is add new chat and if it exceed max number of lines, earliest text will be deleted.
	 * It return a feedback for user
	 */
	protected static String addContent(String chatText) {

		if (totalNumChat >= MAX) { 
			allChatQueue.poll();
		} else {
			totalNumChat++;
		}
		allChatQueue.offer(chatText);
		return String.format(MESSAGE_ADDED, saveFileName, chatText);
	}
	
	/**
	 * The Example method is delete the chat with the line number and return feedback to user.
	 * If line not found, return error message
	 */
	protected static String delete(int input) {
		
		Queue<String> tempQueue = new LinkedList<String> ();
		String tempString = "";
		
		if (input > 0 && input <= totalNumChat) {
			for (int i = 1; i <= totalNumChat; i++) {	
				if (i == input) {
					tempString = allChatQueue.poll();
				} else {
					tempQueue.offer(allChatQueue.poll());
				}
			}
			totalNumChat = totalNumChat - 1;
			allChatQueue = tempQueue;
			return String.format(MESSAGE_DELETE, saveFileName, tempString);
		} else {
			return String.format(MESSAGE_DELETEERROR, saveFileName);
		}
	}
	
	/**
	 * The Example method is display all saved chat.
	 */
	protected static String display() {
		
		Queue<String> tempQueue = new LinkedList<String> ();
		String tempString = "";
		
		if (allChatQueue.isEmpty()) {
			return String.format(MESSAGE_EMPTY, saveFileName);
		} else {
			for (int i = 1; i < totalNumChat; i++) {
				tempString = allChatQueue.poll();
				tempQueue.offer(tempString);
				System.out.println(String.format(MESSAGE_DISPLAY, i, tempString));
			}
			
			tempString = allChatQueue.poll();
			tempQueue.offer(tempString);
			allChatQueue = tempQueue;
			return String.format(MESSAGE_DISPLAY, totalNumChat, tempString);
		}
	}
	
	/**
	 * The Example method is to clear all chat from the file.
	 */
	protected static String clear() {
		
		allChatQueue = testAllChatQueue;
		allChatQueue.clear();
		totalNumChat = 0;
		return String.format(MESSAGE_CLEAR, saveFileName);
	}
	
	/**
	 * The Example method is to get current file name for testing purpose.
	 */
	protected static String getFileName(){
		return strFileName;
	}
	
	/**
	 * The Example method is to write the chat into the file.
	 */
	private static void toSaveFile() throws IOException {
		
		Queue<String> tempQueue = new LinkedList<String> ();
		FileWriter fileWriter = new FileWriter(saveFileName);
		BufferedWriter buffWriter = new BufferedWriter(fileWriter);
		
		while (allChatQueue.peek() != null) {
			String tempString = allChatQueue.poll(); 
			tempQueue.offer(tempString);
			buffWriter.write(tempString);
			buffWriter.newLine();
		}
		
		buffWriter.flush(); //upload content to file
		buffWriter.close(); //close file
		allChatQueue = tempQueue;
	}
	
	/**
	 * The Example method is to check if file is saved properly.
	 */
	protected static boolean isFileSaved() {
		
		try {
			toSaveFile();
			return true;
		} catch (IOException e1) {
			e1.printStackTrace();
			System.out.println(String.format(MESSAGE_FILESAVEERROR, saveFileName));
			return false;
		}
	}
	
	/**
	 * The Example method is to determine the command type.
	 */
	protected static CommandType determineCommandType(String commandTypeString) {
	
		if (commandTypeString == null) {
			throw new Error("command type string cannot be null!");
		}
		
		if (commandTypeString.equalsIgnoreCase("add")) {
			return CommandType.ADD;
		} else if (commandTypeString.equalsIgnoreCase("delete")) {
			return CommandType.DELETE;
		} else if (commandTypeString.equalsIgnoreCase("clear")) {
			return CommandType.CLEAR;
		} else if (commandTypeString.equalsIgnoreCase("display")) {
			return CommandType.DISPLAY;
		} else if (commandTypeString.equalsIgnoreCase("sort")) {
			return CommandType.SORT;
		} else if (commandTypeString.equalsIgnoreCase("search")) {
			return CommandType.SEARCH;
		} else if (commandTypeString.equalsIgnoreCase("exit")) {
		 	return CommandType.EXIT;
		} else {
			return CommandType.INVALID;
		}
	}
	
	/**
	 * The Example method is to get the text which the user want to store.
	 */
	protected static String getText(String input) {
		
		return input.replace(getFirstWord(input), "").trim();
	}
	
	/**
	 * The Example method is to get line number to be delete.
	 */
	protected static int getLineNum(String input) {
		
		input = getText(input);
		int line;
		
		try{
			line = Integer.parseInt(input);
		} catch (NumberFormatException e) {
			line = -1;
		}
		return line;
	}
	
	/**
	 * The Example method is to get the first word from the input.
	 */
	protected static String getFirstWord(String input) {
		
		String firstName = input.trim().split("\\s+")[0];
		return firstName;
	}
}
		