package assignment5;

import java.io.IOException;
import javafx.application.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.media.AudioClip;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.scene.control.*;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;


public class ClientMain extends Application  {
	protected String hostAddress = new String("10.147.89.222");
	protected Integer portAddress = new Integer(4242);
	protected Socket myconnection=null;
	protected ObjectOutputStream sendToServer;
	protected ObjectInputStream getFromServer;
	protected Integer stage= new Integer(0);
	protected AudioClip buttonSound = new AudioClip(this.getClass().getResource("Button.wav").toString());
	
	private void display(GridPane grid, Integer stage, Stage primaryStage)
	{
		if (stage ==0)
		{
			Scene scene = new Scene(grid, 600, 600);
			primaryStage.setScene(scene);                  // now scene displays
			primaryStage.show();
		}
		
		if (stage == 1)
		{
			Scene scene = new Scene(grid, 600, 600);
			primaryStage.setScene(scene);                  // now scene displays
			Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
			primaryStage.setX(500);
			primaryStage.setY(500);
			primaryStage.setWidth(600);
			primaryStage.setHeight(600);
			primaryStage.show();
		}
	}
	
	public static void main (String[] args) {

		launch(args);

	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void start(Stage primaryStage) {
		
		GridPane grid = new GridPane();
		GridPane ipGrid = new GridPane();
		TextArea chat = new TextArea();
		TextField userName = new TextField();
		
	    UserInfo thisClient = new UserInfo();	
	    ObjectOutputStream sendToServer1=null;
		ObjectInputStream getFromServer1=null;
		
		
		Label ipLabel = new Label("IP Address: ");
		ipLabel.setPadding(new Insets(5,5,5,5));
		GridPane.setConstraints(ipLabel, 0, 0);
		ipGrid.getChildren().add(ipLabel);
		
		TextField ipAddress = new TextField();
		GridPane.setConstraints(ipAddress, 1, 0);
		ipGrid.getChildren().add(ipAddress);
		
		Label portLabel = new Label("Port Number: ");
		portLabel.setPadding(new Insets(5,5,5,5));
		GridPane.setConstraints(portLabel, 0, 1);
		ipGrid.getChildren().add(portLabel);
		
		TextField portInput = new TextField();
		GridPane.setConstraints(portInput, 1, 1);
		ipGrid.getChildren().add(portInput);
		
		Button ipButton = new Button("Connect");
		GridPane.setConstraints(ipButton, 0, 2);
		ipGrid.getChildren().add(ipButton);
		
//		boolean connectionFlag = false;
//		
//		while (connectionFlag == false)
//		{
//			try {
//				 myconnection = new Socket(hostAddress, 4242);
//				 connectionFlag = true;
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				ipAddress.setText("Couldn't find server");
//			}
//		}
//		 try {
//			  sendToServer1 = new ObjectOutputStream(myconnection.getOutputStream());
//			  getFromServer1= new ObjectInputStream(myconnection.getInputStream());
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			System.out.println("error getting IO stream");
//		}
//		 	final ObjectOutputStream sendToServer = sendToServer1;
//		 	final ObjectInputStream getFromServer = getFromServer1;
		 	
		 
		
		
		
		primaryStage.setTitle("Chat Room");
		
		grid.setPrefHeight(600);
		grid.setPrefWidth(600);
		
		Label usernameLabel = new Label("UserName: ");                // Username label
		usernameLabel.setPadding(new Insets(0,5,0,5));
		GridPane.setConstraints(usernameLabel, 0, 0);
		grid.getChildren().add(usernameLabel);
		
		Label passwordLabel = new Label("Password: ");
		passwordLabel.setPadding(new Insets(0,5,0,5));
		GridPane.setConstraints(passwordLabel, 2, 0);
		grid.getChildren().add(passwordLabel);
		
		TextField passwordField = new TextField();                       //area for password
		GridPane.setConstraints(passwordField, 3,0);
		grid.getChildren().add(passwordField);
		passwordField.setPadding(new Insets(5,0,0,0));
		
		
		//chat text area
		chat.setEditable(false);
		chat.setPrefWidth(500);
		chat.setPrefHeight(300);
		GridPane.setConstraints(chat, 0, 3, 4, 1);
		chat.setDisable(true);
		grid.getChildren().add(chat);
		
		TextField chatBox = new TextField();                   //area to type into chat
		chatBox.setPrefWidth(400);
		GridPane.setConstraints(chatBox, 0,4, 3,1);
		grid.getChildren().add(chatBox);
		chatBox.setPadding(new Insets(5,0,0,0));
		chatBox.setDisable(true);
		
		Button chatButton = new Button ("Send");                  // send button
		GridPane.setConstraints(chatButton, 3, 4);
		grid.getChildren().add(chatButton);
		chatButton.setDisable(true);
		
		
		Label groupLabel = new Label("Group: ");                // Group select Label
		groupLabel.setPadding(new Insets(0,5,0,5));
		GridPane.setConstraints(groupLabel, 0, 5);
		grid.getChildren().add(groupLabel);
		groupLabel.setDisable(true);
		
		ChoiceBox<String> groupSelection = new ChoiceBox<String>();          //choicebox to select group
		groupSelection.getItems().add("Select Group");
		groupSelection.setValue("Select Group");
		groupSelection.setPrefWidth(100);
		GridPane.setConstraints(groupSelection, 1, 5,1,1);
		grid.getChildren().add(groupSelection);
		groupSelection.setDisable(true);
		
		chatButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				buttonSound.play();
				String groupType = groupSelection.getValue();
				Group newMessage = new Group (new String(groupType), chatBox.getText(), thisClient.getName());
				SenderClass message = new SenderClass(0, newMessage);
				try {
					sendToServer.writeObject(message);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					System.out.println("couldn't send message to server");
				}
				
				
			}
		});
		
		ChoiceBox<String> personAdd = new ChoiceBox<String>();          //choicebox to select person to add
		personAdd.getItems().add("Select Person");
		personAdd.setValue("Select Person");
		personAdd.setPrefWidth(100);
		GridPane.setConstraints(personAdd, 2, 5,1,1);
		grid.getChildren().add(personAdd);
		personAdd.setDisable(true);
		
		Button addButton = new Button ("Add");                  // add person to group button
		GridPane.setConstraints(addButton, 3, 5);                
		grid.getChildren().add(addButton);
		addButton.setDisable(true);
		
		Label newGlabel = new Label("New Group:");                // Group select Label
		newGlabel.setPadding(new Insets(0,5,0,5));
		GridPane.setConstraints(newGlabel, 0, 6);
		grid.getChildren().add(newGlabel);
		newGlabel.setDisable(true);
		
		TextField newGroup = new TextField();                   //area to type for new Group
		newGroup.setPrefWidth(200);
		GridPane.setConstraints(newGroup, 1,6, 2,1);
		grid.getChildren().add(newGroup);
		newGroup.setPadding(new Insets(5,0,0,0));
		newGroup.setDisable(true);
		
		Button createButton = new Button ("Create");                  // create group button
		GridPane.setConstraints(createButton, 3, 6);                
		grid.getChildren().add(createButton);
		createButton.setDisable(true);
		
		
		createButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				//create a new group with information
				//create a senderclass and send the data using sendToServer
				buttonSound.play();
				Group createGroup = new Group(new String(newGroup.getText()), null, userName.getText());    // remember to disable after logging in
				SenderClass sendGroup = new SenderClass(1, createGroup);
				try {
					sendToServer.writeObject(sendGroup);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					System.out.println("couldn't send new Group to server");
				}
				
			}
		});
		
		
		
		
		
	    //area to type in username
		userName.setPrefWidth(200);
		GridPane.setConstraints(userName, 1, 0);
		grid.getChildren().add(userName);
		
		Button logInButton = new Button ("Log In");                  // login button
		GridPane.setConstraints(logInButton, 0, 1);
		grid.getChildren().add(logInButton);
		
		Label newPassword = new Label("Change Password: ");                // Group select Label
		newPassword.setPadding(new Insets(0,5,0,5));
		GridPane.setConstraints(newPassword, 0, 7);
		grid.getChildren().add(newPassword);
		newPassword.setDisable(true);
		
		TextField changeField = new TextField();                   //area to type for new Group
		changeField.setPrefWidth(400);
		GridPane.setConstraints(changeField, 1,7,2,1);
		grid.getChildren().add(changeField);
		changeField.setPadding(new Insets(5,0,0,0));
		changeField.setDisable(true);
		
		Button change = new Button ("Change");                  // create group button
		GridPane.setConstraints(change, 3, 7);                
		grid.getChildren().add(change);
		change.setDisable(true);
		
		change.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				SenderClass changedPassword = new SenderClass(6, new String(userName.getText()), new String(changeField.getText()));
				try {
					sendToServer.writeObject(changedPassword);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					System.out.println("Couldn't send changed password to server");
				}
				
				
			}	
		});
		
		
		
		logInButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				buttonSound.play();
				thisClient.setName(userName.getText());
				thisClient.setPass(passwordField.getText());
				//thisClient = new UserInfo(userName.getText(), passwordField.getText());
				try {
					sendToServer.writeObject(thisClient);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					System.out.println("couldn't send userinfo to server");
				}
				
				
				
				try {
					SenderClass loginStatus = (SenderClass)getFromServer.readObject();
					if(loginStatus.getType()==5)
					{
						System.exit(0);
					}
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					System.out.println("didn't receive login status from server");
				}
				new Thread( new Messagehandler(getFromServer, chat,groupSelection,personAdd)).start();
				chat.setDisable(false);                       //enable buttons after logging in
				chatBox.setDisable(false);
				chatButton.setDisable(false);
				groupLabel.setDisable(false);
				groupSelection.setDisable(false);
				personAdd.setDisable(false);
				addButton.setDisable(false);
				newGlabel.setDisable(false);
				newGroup.setDisable(false);
				createButton.setDisable(false);
				newPassword.setDisable(false);
				change.setDisable(false);
				changeField.setDisable(false);
			}
		});                                                     //login button click end
		
		addButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				buttonSound.play();
				String personToAdd = personAdd.getValue();
				String groupToAdd = groupSelection.getValue();
				SenderClass message = new SenderClass(4, new String(personToAdd), new String(groupToAdd));
				try {
					sendToServer.writeObject(message);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					System.out.println("Couldn't send person to add to Server");
				}
				
			}
				
		});
		
		ipButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				buttonSound.play();
				hostAddress = ipAddress.getText();
				portAddress = Integer.parseInt(portInput.getText());
				try {
					 myconnection = new Socket(hostAddress, portAddress);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					ipAddress.setText("Couldn't find server");
					System.exit(0);
				}
				 try {
				  sendToServer = new ObjectOutputStream(myconnection.getOutputStream());
				  getFromServer= new ObjectInputStream(myconnection.getInputStream());
//				  new Thread( new Messagehandler(getFromServer, chat,groupSelection,personAdd)).start();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				System.out.println("error getting IO stream");
				System.exit(0);
			}
				 stage = 1;
				 display(grid, stage, primaryStage);
				
			}
		});
		
		Button quitButton = new Button("Quit");
		GridPane.setConstraints(quitButton, 0, 8);
		grid.getChildren().add(quitButton);
		quitButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				buttonSound.play();
				SenderClass quit = new SenderClass(7, userName.getText());
				try {
					sendToServer.writeObject(quit);
				} catch (IOException e2) {
					// TODO Auto-generated catch block
					System.out.println("Couldn't send quit data to server");
				}
				try {
					myconnection.close();
				} catch (IOException e1) {
					System.out.println("Socket close fail");
				}
				System.exit(0);
				
			}
		});
		
		
		
		display(ipGrid, stage, primaryStage);
		
//		Scene scene = new Scene(grid, 600, 600);
//		primaryStage.setScene(scene);                  // now scene displays
		Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
		primaryStage.setX(500);
		primaryStage.setY(500);
		primaryStage.setWidth(600);
		primaryStage.setHeight(600);
//		primaryStage.show();                        // display to user
		grid.setGridLinesVisible(false);
		

		
//		new Thread( new Messagehandler(getFromServer, chat,groupSelection,personAdd)).start();
	
	
		
	}

	}


//class LoggingInfo implements Serializable
//{
//	/**
//	 * 
//	 */
//	private static final long serialVersionUID = 1L;
//	private String userName;
//	public LoggingInfo(String userName)
//	{
//		this.userName = userName;
//	}
//	
//	public String getLoggingInfo()
//	{
//		return this.userName;
//	}
//}

class Messagehandler implements Runnable
{
	protected AudioClip messageSound = new AudioClip(this.getClass().getResource("Atone.wav").toString());
	SenderClass message;
	ObjectInputStream input;
	TextArea text;
	ChoiceBox<String> groupSelection;
	ChoiceBox<String> allClients;
	public Messagehandler(ObjectInputStream input, TextArea text, ChoiceBox<String> groupSelection, ChoiceBox<String> personAdd)
	{
		
		this.input = input;
		this.text = text;
		this.groupSelection = groupSelection;
		this.allClients = personAdd;
			
	}
	@Override
	public void run() {
		while (true) {
		try {
			message = (SenderClass)input.readObject();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Couldn't get input from server");
		}
		
		if (message.getType()==0)
		{
		messageSound.play();
		text.appendText("<" + message.getMessage().getgroupName() + ">" + "<" + message.getMessage().getUserName() + ">: " + message.getMessage().getMessage()+"\n");
		}
		else if (message.getType()==2)
		{
			ArrayList<String> updatedGroups = message.getGroupList();
			groupSelection.getItems().clear();
			groupSelection.getItems().add("Select Group");
			groupSelection.setValue("Select Group");
			for (int i=0; i<updatedGroups.size();i++)
			{
				groupSelection.getItems().add(updatedGroups.get(i));
			}
		}
		else if (message.getType()==3)
		{
			ArrayList<String> clients = message.getClientList();
			allClients.getItems().clear();
			allClients.getItems().add("Select Person");
			
			for (int i=0; i< clients.size();i++)
			{
				allClients.getItems().add(clients.get(i));
			}
				
				
				
		}
		
		
		
		
		}
		
	}
	
}



class UserInfo implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String userName;
	private String password;
	
	public UserInfo()
	{
		this.userName = null;
	}
	public UserInfo(String userName, String password)
	{
		this.userName = userName;
		this.password = password;
	}
	public String getName()
	{
		return this.userName;
	}
	public String getPassword()
	{
		return this.password;
	}
	public void setName(String userName)
	{
		this.userName = userName;
	}
	public void setPass(String password)
	{
		this.password = password;
	}
}


