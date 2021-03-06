package SFTP.server;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class CredentialsHandler {
	
	private String PATH = System.getProperty("user.dir");
	
	private String ID = null;
	private String account = null;
	private String password = null;
	private boolean isAuthorized = false;
	
	public CredentialsHandler(boolean runFromCMD) {
		if (runFromCMD) {
			PATH += "/SFTP/server/data/";
		}
		else {
			PATH +="/src/SFTP/server/data/";
		}
	}
	
	public String USER(String[] args) {

		String userID = args[1];
		
		ArrayList<String> userList = readFile(PATH + "users.txt");
		
		for (int i = 0; i < userList.size(); i++) {
			if (userID.equals(userList.get(i))) {
				ID = userID;
				if (isAuthorized) {
					return "!" + userID + " logged in";
				}
				else {
					if (userID.equals("guest")) {
						isAuthorized = true;
						return "!guest logged in";
					}
					else {
						return "+User-id valid, send account and password";
					}
					
				}
			}
		}
		return "-Invalid user-id, try again";
	}
	
	
	public String ACCT(String[] args, FileSystemHandler fileSystemHandler) {
		
		String accountName = args[1];
		
		ArrayList<String> accountList = readFile(PATH + "accounts.txt");
		
		if (password == null) {
			/* Check if account exists, if it does, set the account*/
			for (int i = 0; i < accountList.size(); i++) {
				if (accountName.equals(accountList.get(i))) {
					account = accountName;
					if (fileSystemHandler.getCDIRState() == "PENDING") {
						return "+account ok, send password";
					}
					else {
						return "+Account valid, send password";
					}
					
				}
			}
			if (fileSystemHandler.getCDIRState() == "PENDING") {
				return "-invalid account";
			}
			else {
				return "-Invalid account, try again";	
			}
			
		}
		/* If account matches with password */
		else {
			ArrayList<String> passwords = readFile(PATH + "passwords.txt");
			int accountIndex = accountList.indexOf(accountName);
			
			if (passwords.get(accountIndex).equals(password)) {
				isAuthorized = true;
				if (fileSystemHandler.getCDIRState() == "PENDING") {
					fileSystemHandler.authorizePendingPath();
					return "!Changed working dir to " + fileSystemHandler.getCanonicalPath();
				}
				else {
					return "! Account valid, logged-in";
				}
			}
			else {
				if (fileSystemHandler.getCDIRState() == "PENDING") {
					return "-invalid account";
				}
				else {
					return "-Invalid account, try again";
				}
				
			}
		}

	}
	
	
	public String PASS(String[] args, FileSystemHandler fileSystemHandler) {
		
		String password = args[1];
		
		ArrayList<String> passwords = readFile(PATH + "passwords.txt");
		
		if (account == null) {
			/* Check if password exists, if it does, set the password*/
			for (int i = 0; i < passwords.size(); i++) {
				if (password.equals(passwords.get(i))) {
					this.password = password;
					if (fileSystemHandler.getCDIRState() == "PENDING") {
						return "+password ok, send account";
					}
					else {
						return "+Send account";
					}
					
				}
			}
			if (fileSystemHandler.getCDIRState() == "PENDING") {
				return "-invalid password";
			}
			else {
				return "-Wrong password, try again";
			}
			
		}
		else {
			/* If password matches with account */
			ArrayList<String> accountList = readFile(PATH + "accounts.txt");
			int accountIndex = accountList.indexOf(account);
			
			if (passwords.get(accountIndex).equals(password)) {
				isAuthorized = true;
				if (fileSystemHandler.getCDIRState() == "PENDING") {
					fileSystemHandler.authorizePendingPath();
					return "!Changed working dir to " + fileSystemHandler.getCanonicalPath();
				}
				else {
					return "! Logged in";
				}
			}
			else {
				if (fileSystemHandler.getCDIRState() == "PENDING") {
					return "-invalid password";
				}
				else {
					return "-Wrong password, try again";
				}
			}
		}
	}
	
	
	private ArrayList<String> readFile(String filename) {
		
		ArrayList<String> results = new ArrayList<String>();
		
		try {
			String line;
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			
			/* Read until end of file and then return results */
			while((line = reader.readLine()) != null) {
				results.add(line);
			}
			reader.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return results;
	}

	
	public void setID(String ID) {
		this.ID = ID;
	}
	
	public void setAccount(String account) {
		this.account = account;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getID() {
		return ID;
	}
	
	public String getAccount() {
		return account;
	}
	
	public String getPassword() {
		return password;
	}
	
	public boolean isAuthorized() {
		return isAuthorized;
	}
	
}
