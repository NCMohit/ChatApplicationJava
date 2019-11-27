import java.io.*;  
import java.net.*;
import java.util.Scanner;  
import javax.swing.*;
import java.awt.event.*;
import java.sql.*;
public class ChatApplication_Mohit extends JFrame implements ActionListener{
	JButton createserver;
	JButton createclient;
	JTextField ip;
	JLabel chat;
	JLabel error;
	String chatstr;
	JTextField enterchat;
	JButton send;
	DataInputStream is;
	DataOutputStream os;
	JLabel enterusernamelabel;
	JLabel enterpasslabel;
	JTextField enterusername;
	JPasswordField enterpass;
	JButton login;
	JButton register;
	String myname;
	String messagegetter;
	int loggedin = 0;
	int usernameexists = 0;  
	ChatApplication_Mohit(){

		// GUI FOR CHAT APPLICATION

		createserver = new JButton("Start Server");
		createclient = new JButton("Connect to server");
		ip = new JTextField("Server IP");
		chat = new JLabel();
		enterchat = new JTextField();
		send = new JButton("->");
		enterusernamelabel = new JLabel("Username: ");
		enterpasslabel = new JLabel("Password: ");
		enterusername = new JTextField("");
		enterpass = new JPasswordField("");
		login = new JButton("Login");
		register = new JButton("Register");
		error = new JLabel("");

		createserver.setBounds(50,630,150,30);
		ip.setBounds(430,630,100,30);
		createclient.setBounds(530,630,150,30);
		chat.setBounds(50,50,500,500);
		enterchat.setBounds(50,550,500,50);
		send.setBounds(550,550,100,50);
		enterusernamelabel.setBounds(100,250,100,50);
		enterpasslabel.setBounds(100,350,100,50);
		enterusername.setBounds(250,250,150,50);
		enterpass.setBounds(250,350,150,50);
		login.setBounds(300,450,100,50);
		register.setBounds(300,550,100,50);
		error.setBounds(200,600,200,50);

		createserver.addActionListener(this);
		createclient.addActionListener(this);
		send.addActionListener(this);
		login.addActionListener(this);
		register.addActionListener(this);

		add(createclient);
		add(createserver);
		add(ip);
		add(chat);
		add(enterchat);
		add(send);
		add(enterusername);
		add(enterusernamelabel);
		add(enterpass);
		add(enterpasslabel);
		add(login);
		add(register);
		add(error);
		enterchat.setVisible(false);
		send.setVisible(false);
		ip.setVisible(false);
		createserver.setVisible(false);
		createclient.setVisible(false);
		

		setSize(700,700);
		setTitle("Chat Application by N.C.Mohit");
		setLayout(null);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	public void actionPerformed(ActionEvent e){

		// LOGIN BUTTON CHECKS USERNAME AND PASSWORD AND MATCHES WITH VALUES IN DATABASE

		if(e.getActionCommand() == "Login"){
			try{ 
				
				Class.forName("com.mysql.cj.jdbc.Driver");  
				Connection con=DriverManager.getConnection(  
				"jdbc:mysql://localhost/userdb?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC","root","");
				Statement stmt=con.createStatement();  
				ResultSet rs=stmt.executeQuery("select * from usertable"); 
				while(rs.next()){
					String username = rs.getString("username");
					String pass = rs.getString("pass");
					// System.out.println(username+" "+pass);
					// System.out.println(enterpass.getPassword());
					if( enterusername.getText().equals(username) && enterpass.getText().equals(pass) ){
						loggedin = 1;
						break;
					}
				}
				con.close();
			}
			catch(Exception e1){
				error.setText("SQL server not started");
			}
			if(loggedin == 1){
				ip.setVisible(true);
				createserver.setVisible(true);
				createclient.setVisible(true);
				enterusername.setVisible(false);
				enterpass.setVisible(false);
				enterusernamelabel.setVisible(false);
				enterpasslabel.setVisible(false);
				login.setVisible(false);
				register.setVisible(false);
				error.setVisible(false);
				myname = enterusername.getText();
			}
			else{
				error.setText("Login name or Password not correct !");
			}
		}

		// REGISTER BUTTON INSERTS USERNAME AND PASSWORD AS NEW ENTRIES IN DATABASE
		
		else if(e.getActionCommand() == "Register"){
			try{
				Class.forName("com.mysql.cj.jdbc.Driver");  
				Connection con=DriverManager.getConnection(  
				"jdbc:mysql://localhost/userdb?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC","root","");
				Statement stmt=con.createStatement();  
				ResultSet rs=stmt.executeQuery("select * from usertable");
				while(rs.next()){
					String username = rs.getString("username");
					String pass = rs.getString("pass");
					if((enterusername.getText() == username)){
						error.setText("Username already taken");
						usernameexists = 1;
						break;
					}
				}
				if(usernameexists == 0){
					Statement stmt2=con.createStatement();  
					stmt2.executeUpdate("INSERT INTO usertable VALUES ('"+enterusername.getText()+"','"+enterpass.getText()+"')");
					error.setText("User succcessfully registered !");
				}
				con.close();
			}
			catch(Exception e2){
				error.setText("SQL server not started");
			}
		}

		//  ON PRESSING CREATE SERVER, SERVER IS STARTED ALONG WITH TEXT 

		if(e.getActionCommand() == "Start Server"){	
			chatstr = "<html>"+"User Connected !";
			chat.setText(chatstr);
			createclient.setEnabled(false);
			createserver.setEnabled(false);
			enterchat.setVisible(true);
			send.setVisible(true);
			createserverconnection();
		}

		// ON PRESSING CONNECT TO SERVER, SOCKETS ARE CONNECTED TO SPECIFIED IP

		else if(e.getActionCommand() == "Connect to server"){
			chatstr = "<html>"+"Connected to "+ip.getText();
			chat.setText(chatstr);
			createclient.setEnabled(false);
			createserver.setEnabled(false);
			enterchat.setVisible(true);
			send.setVisible(true);
			createclientconnection(ip.getText());
		}
		// THIS BUTTON IS ENABLED AFTER PRESSING EITHER CONNECT / OR CREATE SERVER, AND SENDS DATA TO THE OUTPUTSTREAM
		else if(e.getActionCommand() == "->"){
			send(os,enterchat.getText());
		}

	}
	// THIS IS STARTED ON PRESSING START SERVER, THIS STARTS A INPUTSTREAM THREAD
	public void createserverconnection(){
		try{  
			ServerSocket ss=new ServerSocket(6666);  
			Socket s=ss.accept();  
			is=new DataInputStream(s.getInputStream());
			os=new DataOutputStream(s.getOutputStream());
			messagegetter =(String)is.readUTF();
			os.writeUTF(myname);
			Thread inputstreamthreadobject = new Thread(new ISThread(is,messagegetter,chat)); 
            inputstreamthreadobject.start(); 
		}catch(Exception e){System.out.println(e);} 		
	}
	// THIS IS STARTED ON PRESSING CONNECT, THIS STARTS A INPUTSTREAM THREAD
	public void createclientconnection(String ip){
		try{
			Socket s=new Socket(ip,6666);  
			os=new DataOutputStream(s.getOutputStream());
			is=new DataInputStream(s.getInputStream());
			os.writeUTF(myname);  
			messagegetter =(String)is.readUTF();
			Thread inputstreamthreadobject = new Thread(new ISThread(is,messagegetter,chat)); 
            inputstreamthreadobject.start();   
		}catch(Exception e){System.out.println(e);}  		
	}
	// THIS IS EXECUTED ON PRESSING THE BUTTON SEND, SENDS MESSAGE IN BOX TO OUTPUT STREAM AND LOGS THE INFORMATION
	public void send(DataOutputStream os,String msg){
		try{
			os.writeUTF(msg);
			String chatstr = chat.getText();
			chatstr += "<br>" + myname+"-> " + msg;
			chat.setText(chatstr);
			String path = System.getProperty("user.dir");
			FileWriter fw=new FileWriter(path+"\\chathistory.html");
			fw.write(chat.getText());
			fw.close();
		}
		catch(Exception e){
			System.out.println("Error sending message!");
		}
	}
	public static void main(String[] args) {
		new ChatApplication_Mohit();
	}
}
// THIS THREAD IS USED TO KEEP ON RUNNING INPUTSTREAM WITHOUT EFFECTING OTHER FUNCTIONALITIES OF CHAT APPLICATION
class ISThread implements Runnable{ 
	DataInputStream is;
	JLabel chat;
	String messagegetter;
    public ISThread(DataInputStream is,String messagegetter,JLabel chat){
    	this.is = is;
    	this.chat = chat;
    	this.messagegetter = messagegetter;
    }
    public void run(){ 
        try{ 
        	while(1==1){
        		String  str=(String)is.readUTF();
        		String chatstr = chat.getText();
        		chatstr += "<br>" + messagegetter+"-> " +str;
        		chat.setText(chatstr);
        	}
        } 
        catch (Exception e){  
            System.out.println ("Server has been closed"); 
        } 
    } 
} 
