/**
 * This class is used to save user input into text file after each user operation
 * User can add contents and delete the contents in the file by the line number
 * The storage is limit for this version is 10 lines of information
 * In the case more than 10 lines of information was entered,
 * we store only the latest one. The command format is given by the example interaction below:

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
	private static final File inputFile = new File("chat.txt");
	private static final String MESSAGE_ADDED = "added to %1$s: \"%2$s\"";
	private static final String MESSAGE_DISPLAY = "%1$d. %2$s";
	private static final String MESSAGE_EMPTY = "%1$s is empty";
	private static final String MESSAGE_CLEAR = "all content deleted from %1$s";
	private static final String MESSAGE_DELETE = "deleted from %1$s: \"%2$s\"";
	private static final String MESSAGE_DELETEERROR = "content is not in %1$s!";
	private static final String MESSAGE_INVALID_FORMAT = "invalid command format :%1$s";
	private static Queue<String> writeToTxt;
	private static final int MAX = 10;
	private static int count=0;
		// These are the possible command types
		enum COMMAND_TYPE {
			ADD,DELETE,DISPLAY,CLEAR,INVALID, EXIT
		};
	private static Scanner scanner = new Scanner(System.in);
	public static void main(String[] args) {
		System.out.println(WELCOME_MESSAGE + inputFile);
		writeToTxt = new LinkedList<String>();
		while (true) {
			System.out.print("Command: ");
			String input = scanner.nextLine();
			String command = getCommand(input);
			executeCommand(command,input);
		}
	}
	private static void executeCommand(String command, String input) {
		COMMAND_TYPE commandType = determineCommandType(command);
		switch (commandType) {
		case ADD:
			String chatLine = getText(input);
			System.out.println(addContent(input,chatLine));
			try {
				saveFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		case DELETE:
			int numToDelete = getNum(input);
			System.out.println(delete(numToDelete));
			try {
				saveFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			break;
		case DISPLAY:
			System.out.println(display(input));
			break;
		case CLEAR:
			System.out.println(clear());
			try {
				saveFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		case INVALID:
			System.out.println(String.format(MESSAGE_INVALID_FORMAT, input));
			break;
		case EXIT:
			System.exit(0);
			break;
		default:
			//throw an error if the command is not recognized
			System.out.println(MESSAGE_INVALID_FORMAT);
			throw new Error("Unrecognized command type");
		}
	}
	
	private static String addContent(String input, String chatLine) {
		if(count >= MAX){
			writeToTxt.poll();
			writeToTxt.offer(chatLine);
		}
		else{
			count++;
			//System.out.println(count);//test the count
			writeToTxt.offer(chatLine);
		}
		return String.format(MESSAGE_ADDED, inputFile,chatLine);
	}
	private static String delete(int input) {
		Queue<String> tempQueue = new LinkedList<String>();
		String tempString="";
		if(input>0 && input<=count){
			for(int i=1;i<=count;i++){
				if(i==input)tempString = writeToTxt.poll();
				else tempQueue.offer(writeToTxt.poll());
			}
			count = count-1;
			//System.out.println(count); //check count
			writeToTxt = tempQueue;
			return String.format(MESSAGE_DELETE,inputFile,tempString);
		}
		else
			return String.format(MESSAGE_DELETEERROR,inputFile);
	}
	private static String display(String input) {
		Queue<String> tempQueue = new LinkedList<String>();
		String tempString="";
		if(writeToTxt.isEmpty())
			return String.format(MESSAGE_EMPTY, inputFile);
		for(int i=1;i<count;i++){
			tempString = writeToTxt.poll();
			tempQueue.offer(tempString);
			System.out.println(String.format(MESSAGE_DISPLAY, i,tempString));
		}
		tempString = writeToTxt.poll();
		tempQueue.offer(tempString);
		writeToTxt = tempQueue;
		return String.format(MESSAGE_DISPLAY, count,tempString);
	}
	private static String clear() {
		writeToTxt.clear();
		count = 0;
		return String.format(MESSAGE_CLEAR, inputFile);
	}
	private static void saveFile() throws IOException {
		Queue<String> tempQueue = new LinkedList<String>();
		FileWriter fw = new FileWriter(inputFile);
		BufferedWriter bw = new BufferedWriter(fw);
		while(writeToTxt.peek()!=null){
			String tempString = writeToTxt.poll(); 
			tempQueue.offer(tempString);
			bw.write(tempString);
			bw.newLine();
		}
		bw.flush();
		bw.close();
		writeToTxt = tempQueue;
	}
	//get the command type
	private static COMMAND_TYPE determineCommandType(String commandTypeString) {
		if (commandTypeString == null)
			throw new Error("command type string cannot be null!");

		if (commandTypeString.equalsIgnoreCase("add")) {
			return COMMAND_TYPE.ADD;
		} else if (commandTypeString.equalsIgnoreCase("delete")) {
			return COMMAND_TYPE.DELETE;
		} else if (commandTypeString.equalsIgnoreCase("clear")) {
			return COMMAND_TYPE.CLEAR;
		}else if (commandTypeString.equalsIgnoreCase("display")) {
			return COMMAND_TYPE.DISPLAY;
		}else if (commandTypeString.equalsIgnoreCase("exit")) {
		 	return COMMAND_TYPE.EXIT;
		} else {
			return COMMAND_TYPE.INVALID;
		}
	}
	//get the text which the user want to store
	private static String getText(String input) {
		String output;
			output = input.replace(getCommand(input), "").trim();
		return output;
	}
	//get the command in String
	private static int getNum(String input) {
		input = getText(input);
		int output;
		try{
			output = Integer.parseInt(input);
		}catch(NumberFormatException e){
			output = -1;
		}
		return output;
	}
	private static String getCommand(String input) {
			String commandTypeString = input.trim().split("\\s+")[0];
			return commandTypeString;
	}
}
