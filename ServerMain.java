/* CHAT ROOM <MyClass.java>
 *  EE422C Project 7 submission by
 *  Replace <...> with your actual data.
 *  <Adri Dutta>
 *  <ad38742> 
 *  <16360>
 *  Slip days used: <0>
 *  Fall 2018*
 *  //*Describe here known bugs or issues in this file. If your issue spans multiplefiles, or you are not sure about details, add comments to the README.txt file.*/


package assignment5;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;


public class ServerMain {

	//public static ArrayList<Group> allclients = new ArrayList<Group>();
	private static Group broadcast = new Group(new String("broadcast"),new String(),null);
	protected static HashMap<String,listOfGroups> allGroupsObserve = new HashMap<String,listOfGroups>();
	protected static ArrayList<Group> totalGroups = new ArrayList<Group>();
	protected static clientsObservable allClients = new clientsObservable();
	protected static ArrayList<UserInfo> loginCheck = new ArrayList<UserInfo>();
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		totalGroups.add(broadcast);
		new Thread(()->
		{
			try {
				@SuppressWarnings("resource")
				ServerSocket server = new ServerSocket(4242);
				while (true)
				{
				Socket socket = server.accept();
				new Thread( new clienthandler(socket, broadcast)).start();
				}
			
			}// try block
			catch (Exception e) {System.out.println("server start problem");};
			
		
		
		}).start();

	
	
	}

}

class clienthandler implements Runnable
{
	private Socket clientsocket;
	private ObjectInputStream clientinput;
	private ObjectOutputStream clientoutput;
	private listOfGroups allGroups = new listOfGroups(new ArrayList<Group>());
	private boolean passCheck;
	private boolean userCheck;
	public clienthandler(Socket socket, Group broadcast)
	{
		userCheck = false;
		passCheck = false;
		clientsocket = socket;
		allGroups.getList().add(broadcast);
		try
		{
			clientinput = new ObjectInputStream(clientsocket.getInputStream());
		    clientoutput = new ObjectOutputStream(clientsocket.getOutputStream());	
		}
		catch (Exception e)
		{
			System.out.println("Client handler error");
		}
	}
	
	@Override
	public void run()
	{
		try {                                                           //get username of client
			UserInfo user = (UserInfo)clientinput.readObject();
			System.out.println(user.getName());
			
			for (int i=0; i< ServerMain.loginCheck.size();i++)                   //check if previous user
			{
				
				if (ServerMain.loginCheck.get(i).getName().equals(user.getName()))
				{
					userCheck = true;                                                      
					if (ServerMain.loginCheck.get(i).getPassword().equals(user.getPassword()))
					{
						passCheck = true;
					}
				}
			}
			
			if (userCheck == true && passCheck == false)                                        //wrong password
			{
				Group myGroup = new Group(null, null, null);
				SenderClass loginError = new SenderClass(5, myGroup);
				clientoutput.writeObject(loginError);
				return;
			}
			
			ServerMain.loginCheck.add(user);
			SenderClass newUser = new SenderClass(1, new Group(null,null,null));            //new user
			clientoutput.writeObject(newUser);
	
			UserInfoObserver newClient = new UserInfoObserver(user.getName(), clientinput, clientoutput); // send list of Groups
			groupUpdater currentGroups = new groupUpdater(allGroups.getList(), clientoutput);
			allGroups.addObserver(currentGroups);
			ServerMain.allGroupsObserve.put(newClient.sendUserName(),allGroups);
			currentGroups.sendGroup();
			
			//send client list to all
			clientUpdater myUpdater = new clientUpdater(clientoutput);
			ServerMain.allClients.addObserver(myUpdater);
			ServerMain.allClients.clientAdd(newClient);
			
			
			String broadcast = new String("broadcast");
			Group broadcastGroup = null;
			for (int i=0; i< allGroups.getList().size();i++)
			{
				if(allGroups.getList().get(i).getgroupName().equals(broadcast))
				{
					broadcastGroup = allGroups.getList().get(i);
				}
			}
			broadcastGroup.addObserver(newClient);
			
			while (true)                                               //process the message
			{
				
				SenderClass clientMessage = (SenderClass) clientinput.readObject();
				if (clientMessage.getType()==0)                         //send message to a group
				{
					Group groupMessage = clientMessage.getMessage();
					Group correctGroup = null;
					for (int i=0; i<allGroups.getList().size();i++)                                  // see which group
					{
						if(allGroups.getList().get(i).getgroupName().equals(groupMessage.getgroupName()))
						{
							correctGroup = allGroups.getList().get(i);
						}
					}
					
					//set message and send it to whole group
					if (correctGroup !=null)
					{
						correctGroup.setMessage(groupMessage.getMessage(),groupMessage.getUserName());
					}
				}
				else if (clientMessage.getType()==1)
				{
					Group newGroup = clientMessage.getNewGroup();    //get the wanted group
					ServerMain.totalGroups.add(newGroup);			//add it to the total groups
					newGroup.addObserver(newClient);                 //add observer so client is notified when changed
					ArrayList<Group> addGroup = allGroups.getList(); //get clients groups
					addGroup.add(newGroup);
					allGroups.changeGroup(addGroup);
					
				}
				else if (clientMessage.getType()==4)
				{
					String personToAdd = clientMessage.getPersonToAdd();
					String groupToAdd = clientMessage.getGrouptoAdd();
					UserInfoObserver newMember=null;
					Set<String> allMembers = ServerMain.allGroupsObserve.keySet();
					Group newGroup=null;
					
					for (int i=0; i<ServerMain.allClients.getUsers().size();i++)             // get info so we can attach observer to new group
					{
						if(ServerMain.allClients.getUsers().get(i).sendUserName().equals(personToAdd))
								{
									newMember = ServerMain.allClients.getUsers().get(i);
								}
					}
					
					// find group to attach observer
					for (int i=0; i<ServerMain.totalGroups.size();i++)             
					{
						if(ServerMain.totalGroups.get(i).getgroupName().equals(groupToAdd))
								{
										ServerMain.totalGroups.get(i).addObserver(newMember);
										newGroup = ServerMain.totalGroups.get(i);
								}
					}
					// add member to listofGroup
					ArrayList<Group> clientGroups =ServerMain.allGroupsObserve.get(personToAdd).getList();
					clientGroups.add(newGroup);
					ServerMain.allGroupsObserve.get(personToAdd).changeGroup(clientGroups);
					
					
				}
				else if (clientMessage.getType()==6)
				{
					for (int i=0; i<ServerMain.loginCheck.size();i++)
					{
						if (ServerMain.loginCheck.get(i).getName().equals(clientMessage.getUserName()))
						{
							UserInfo passwordChange = ServerMain.loginCheck.get(i);
							passwordChange.setPass(clientMessage.getPassword());
						}
					}
				}
				else if (clientMessage.getType()==7)
				{
					ServerMain.allClients.clientRemove(clientMessage.getUserName());
				}
				
				
				
				
				
				
				
				//				if (clientMessage.getgroupName().equals("broadcast"))
//				{
//					correctGroup.setMessage(clientMessage.getMessage(), clientMessage.getUserName());
//				}
//				else
//				{
//					System.out.println("Error");
//				}
	
			}
		
		
		} catch (Exception e) {
			System.out.println("didnt receive data on server");
		} 
		
		
	}
}



 class Group extends Observable implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String groupName;
	private String message;
	private String userName;
	

	public Group(String groupName, String message, String userName)
	{
		this.groupName = groupName;
		this.message = message;
		this.userName = userName;
		
	}
	
	public void setMessage(String message, String userName)
	{
		this.message = message;
		this.userName = userName;
		System.out.println(this.message);
		Group changedMessage = new Group(this.groupName,this.message, this.userName);
		setChanged();
		notifyObservers(changedMessage);
	}
	
	public String getgroupName()
	{
		return this.groupName; 
	}
	public String getMessage()
	{
		return this.message;
	}
	public String getUserName()
	{
		return this.userName;
	}
	
	
}
 
class UserInfoObserver implements Observer
{
	String userName;
	ObjectInputStream input;
	ObjectOutputStream output;
	
	public UserInfoObserver(String userName, ObjectInputStream input, ObjectOutputStream output)
	{
		this.userName = userName;
		this.input = input;
		this.output = output;
	}
	@Override
	public void update(Observable arg0, Object arg1) {
		try {
			Group object = (Group)arg1;
			SenderClass toWrite = new SenderClass(0, object);
			output.writeObject(toWrite);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Error sending data");
		}
		
	}
	
	public String sendUserName()
	{
		return this.userName;
	}
	public ObjectOutputStream getOutput()
	{
		return this.output;
	}
	
}

class groupUpdater implements Observer
{
	private ArrayList<Group> allGroups;
	private ObjectOutputStream output;
	private ArrayList<String> groupNames;
	public groupUpdater(ArrayList<Group> allGroups, ObjectOutputStream output)
	{
		this.allGroups = allGroups;
		this.output = output;
	}
	
	public void sendGroup()
	{
		groupNames = new ArrayList<String>();
		for (int i=0; i< allGroups.size();i++)
		{
			groupNames.add(allGroups.get(i).getgroupName());
		}
		SenderClass newGroup = new SenderClass (2, groupNames);
		try {
			
			output.writeObject(newGroup);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("couldn't send grouplist");
		}
	}
	@Override
	public void update(Observable arg0, Object arg1) {
		listOfGroups updated = (listOfGroups) arg1;
		allGroups = updated.getList();
		groupNames = new ArrayList<String>();
		for (int i=0; i<allGroups.size();i++)
		{
			groupNames.add(allGroups.get(i).getgroupName());
		}
		SenderClass sendUpdate = new SenderClass(2, groupNames);
		try {
			output.writeObject(sendUpdate);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Error sending list of groups");
		}
		
		
	}

}

class listOfGroups extends Observable
{
	private ArrayList<Group> currentGroups;
	public listOfGroups(ArrayList<Group> currentGroups)
	{
		this.currentGroups = currentGroups;
	}
	
	public void changeGroup(ArrayList<Group> change)
	{
		this.currentGroups = change;
		setChanged();
		notifyObservers(this);
	}
	public ArrayList<Group> getList()
	{
		return this.currentGroups;
	}
	
}

class SenderClass implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int type;      // 0 for message, 1 to create group, 2 to send GroupList, 3 for clientlist, 4 to add person to group, 5 for login error, 6 for password change, 7 for exit
	private Group message;
	private Group newGroup;
	private ArrayList<String> groupList;
	private ArrayList<String> clientList;
	private String personToAdd;
	private String groupToAddTo;
	private String userName;
	private String changedPassword;
	
	
	public SenderClass (int type, String userName)
	{
		this.type = type;
		this.userName= userName;
	}
	public String getUserName()
	{
		return this.userName;
	}
	public String getPassword()
	{
		return this.changedPassword;
	}
	
	public SenderClass (int type, Group message)
	{
		this.type = type;
		if (this.type ==0) {this.message = message;}
		if (this.type ==1) {this.newGroup= message;}
		if (this.type ==5) {this.message= message;}
		
	}
	public SenderClass (int type, ArrayList<String> grouplist)
	{
		this.type = type;
		if(this.type == 2) this.groupList = grouplist;
		if(this.type==3) this.clientList = grouplist;
	}
	public SenderClass (int type, String personToAdd, String groupToAddTo )
	{
		if (type==4)
		{
			this.type = type;
			this.personToAdd = personToAdd;
			this.groupToAddTo = groupToAddTo;
		}
		if (type ==6)
		{
			this.type = type;
			this.userName = personToAdd;
			this.changedPassword = groupToAddTo;
		}
		
	}
	
	public String getPersonToAdd()
	{
		return this.personToAdd;
	}
	
	public String getGrouptoAdd()
	{
		return this.groupToAddTo;
	}
	
	public int getType()
	{
		return this.type;
	}
	public Group getMessage()
	{
		return this.message;
	}
	public Group getNewGroup()
	{
		return this.newGroup;
	}
	public ArrayList<String> getGroupList()
	{
		return this.groupList;
	}
	public ArrayList<String> getClientList()
	{
		return this.clientList;
	}

	
}

class clientsObservable extends Observable
{
	ArrayList<UserInfoObserver> allClients;
	public clientsObservable()
	{
		allClients = new ArrayList<UserInfoObserver>();
	}
	
	public ArrayList<UserInfoObserver> getUsers()
	{
		return this.allClients;
	}
	
	public void clientAdd (UserInfoObserver client)
	{
		allClients.add(client);
		this.setChanged();
		this.notifyObservers(allClients);
	}
	public void clientRemove(String userName)
	{
		for (int i=0; i<allClients.size();i++)
		{
			if (allClients.get(i).sendUserName().equals(userName))
			{
				allClients.remove(i);
				this.setChanged();
				this.notifyObservers(allClients);
			}
		}
	}
}

class clientUpdater implements Observer
{
	private ArrayList<String> clientString;
	ObjectOutputStream out;
	
	public clientUpdater( ObjectOutputStream out)
	{
		this.out = out;
	}
	
//	public void sendList()
//	{
//		SenderClass sendList = new SenderClass(3, clientString);
//		
//		try {
//			out.writeObject(sendList);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			System.out.println("error sending clientList");
//		}
//	}
	
	@Override
	public void update(Observable arg0, Object arg1) {
		ArrayList<UserInfoObserver> allClients = (ArrayList<UserInfoObserver>) arg1;
		clientString = new ArrayList<String>();
		for (int i=0; i<allClients.size();i++)
		{
			clientString.add(allClients.get(i).sendUserName());
		}
		SenderClass sendList = new SenderClass(3, clientString);
		try {
			out.writeObject(sendList);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("error updating clientList from server");
		}
		
	}
	
}
