package final_project;

public class FileInvalidException extends Exception{
	public FileInvalidException() {
		super("Error: Input file cannot be parsed due to missing information (i.e. month={}, title={}, etc.)");
	}
	
	public FileInvalidException(String msg) {
		super(msg);
	}

}
