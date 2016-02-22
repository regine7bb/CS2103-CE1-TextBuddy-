/**
 * This class is used to save user input into text file after each user operation.
 * User can add contents and delete the contents in the file by the line number.
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
	private static final String strFileName = "chat.txt";
	private static final File saveFileName = new File("chat.txt"); //saved file name
	private static final int MAX = 10; //max length of line (can be edit)
	private static Queue<String> testAllChatQueue = new LinkedList<String> ();
	private static Queue<String> allChatQueue; //store text
	private static Scanner scanner = new Scanner(System.in); //read input
	private static int totalNumChat = 0; //total number of lines
	
	// These are the possible command types
	enum COMMAND_TYPE {
		ADD,DELETE,DISPLAY,CLEAR,INVALID, EXIT
	};
	/**User guide**
	 * @param ADD      add, text here
	 * @param DELETE   delete, number line here
	 * @param DISPLAY  number, text here
	 * @param CLEAR    clear all text in the file
	 * @param INVAILD  format invalid, key again
	 * @param EXIT     exit the program
	 */
	
	public static void main(String[] args) {
		
		System.out.println(WELCOME_MESSAGE + saveFileName);
		allChatQueue = new LinkedList<String> ();
		getCommand("add hello");
		executeCommand("add","hello");
		while (true) {
			//get the command and user input and display output
			System.out.print("Command: ");
			String input = scanner.nextLine();
			String command = getCommand(input);
			executeCommand(command,input);
		}
	}
	
	private static void executeCommand(String command, String input) {
		
		COMMAND_TYPE commandType = determineCommandType(command);
		
		switch (commandType) {	
		case ADD :
			String chatText = getText(input);
			System.out.println(addContent(input,chatText));
			fileSaved();
			break;
		
		case DELETE :
			int numToDelete = getLineNum(input);
			System.out.println(delete(numToDelete));
			fileSaved();
			break;
		
		case DISPLAY :
			System.out.println(display(input));
			break;
		
		case CLEAR :
			System.out.println(clear());
			fileSaved();
			break;
		
		case INVALID :
			System.out.println(String.format(MESSAGE_INVALID_FORMAT, input));
			break;
		
		case EXIT :
			System.exit(0);
			break;
		
		default : //throw an error if the command is not recognized
			System.out.println(String.format(MESSAGE_INVALID_FORMAT, input));
			throw new Error("Unrecognized command type");
		}
	}
	 
	private static String addContent(String input, String chatText) {
		//add new chat
		if (totalNumChat >= MAX) { 
			//if exceed max number of lines, earliest text will be deleted 
			allChatQueue.poll();
		} else {
			totalNumChat++;
			//System.out.println(count);//test the count
		}
		allChatQueue.offer(chatText);
		return String.format(MESSAGE_ADDED, saveFileName, chatText);
	}
	
	private static String delete(int input) {
		//delete the chat with the line number
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
	
	private static String display(String input) {
		//display all saved chat
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
	
	protected static String clear() {
		//clear all chat
		allChatQueue = testAllChatQueue;
		allChatQueue.clear();
		totalNumChat = 0;
		return String.format(MESSAGE_CLEAR, saveFileName);
	}
	protected static String getFileName(){
		return strFileName;
	}
	private static void toSaveFile() throws IOException {
		//write the chat into the file
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
	
	protected static boolean fileSaved() {
		//check if file is saved properly
		try {
			toSaveFile();
			return true;
		} catch (IOException e1) {
			e1.printStackTrace();
			System.out.println(String.format(MESSAGE_FILESAVEERROR, saveFileName));
			return false;
		}
	}
	
	private static COMMAND_TYPE determineCommandType(String commandTypeString) {
		//get the command type
		if (commandTypeString == null) {
			throw new Error("command type string cannot be null!");
		}
		
		if (commandTypeString.equalsIgnoreCase("add")) {
			return COMMAND_TYPE.ADD;
		} else if (commandTypeString.equalsIgnoreCase("delete")) {
			return COMMAND_TYPE.DELETE;
		} else if (commandTypeString.equalsIgnoreCase("clear")) {
			return COMMAND_TYPE.CLEAR;
		} else if (commandTypeString.equalsIgnoreCase("display")) {
			return COMMAND_TYPE.DISPLAY;
		} else if (commandTypeString.equalsIgnoreCase("exit")) {
		 	return COMMAND_TYPE.EXIT;
		} else {
			return COMMAND_TYPE.INVALID;
		}
	}
	
	protected static String getText(String input) {
		//get the text which the user want to store
		return input.replace(getCommand(input), "").trim();
	}
	
	private static int getLineNum(String input) {
		//get the command in String
		input = getText(input);
		int line;
		
		try{
			line = Integer.parseInt(input);
		} catch (NumberFormatException e) {
			line = -1;
		}
		return line;
	}
	protected static String getCommand(String input) {
		
		String commandTypeString = input.trim().split("\\s+")[0];
		return commandTypeString;
	}
}
		