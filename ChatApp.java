import java.io.*;  
import java.net.*;
import java.util.Scanner;  
import javax.swing.*;
import java.awt.event.*;
class InputStreamThread implements Runnable{ 
	DataInputStream dis;
	JLabel chat;
	String opponame;
    public InputStreamThread(DataInputStream dis,JLabel chat,String opponame){
    	this.dis = dis;
    	this.chat = chat;
    	this.opponame = opponame;
    }
    public void run(){ 
        try{ 
        	while(1==1){
        		String  str=(String)dis.readUTF();
        		String chatstr = chat.getText();
        		chatstr += "<br>" + opponame+": " +str;
        		chat.setText(chatstr);
        	}
        } 
        catch (Exception e){  
            System.out.println ("Exception is caught"); 
        } 
    } 
} 
public class ChatApp extends JFrame implements ActionListener{
	JButton createserver;
	JButton createclient;
	JTextField enterip;
	JLabel chat;
	String chatstr;
	JTextField enterchat;
	JButton send;
	DataInputStream dis;
	DataOutputStream dos;
	JTextField enterusername;
	JTextField enterpass;
	JButton login;
	String myname;
	String opponame;
	ChatApp(){
		createserver = new JButton("Create Server");
		createclient = new JButton("Connect to a server");
		enterip = new JTextField("Server IP");
		chat = new JLabel();
		enterchat = new JTextField();
		send = new JButton("Send");
		enterusername = new JTextField("Username: ");
		enterpass = new JTextField("Password: ");
		login = new JButton("Login");

		createserver.setBounds(50,630,150,30);
		enterip.setBounds(430,630,100,30);
		createclient.setBounds(530,630,150,30);
		chat.setBounds(50,50,500,500);
		enterchat.setBounds(50,550,500,50);
		send.setBounds(550,550,100,50);
		enterusername.setBounds(250,250,150,50);
		enterpass.setBounds(250,350,150,50);
		login.setBounds(300,450,100,50);

		createserver.addActionListener(this);
		createclient.addActionListener(this);
		send.addActionListener(this);
		login.addActionListener(this);

		add(createclient);add(createserver);add(enterip);add(chat);add(enterchat);add(send);add(enterusername);add(enterpass);add(login);
		enterchat.setVisible(false);
		send.setVisible(false);
		enterip.setVisible(false);
		createserver.setVisible(false);
		createclient.setVisible(false);
		

		setSize(700,700);
		setTitle("Chat Application by N.C.Mohit");
		setLayout(null);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	public void actionPerformed(ActionEvent e){		
		if(e.getActionCommand() == "Create Server"){	
			chatstr = "<html>"+"Waiting for connection....";
			chat.setText(chatstr);
			createclient.setEnabled(false);
			createserver.setEnabled(false);
			enterchat.setVisible(true);
			send.setVisible(true);
			createserverfunc();
		}
		else if(e.getActionCommand() == "Connect to a server"){
			chatstr = "<html>"+"Connecting....";
			chat.setText(chatstr);
			createclient.setEnabled(false);
			createserver.setEnabled(false);
			enterchat.setVisible(true);
			send.setVisible(true);
			createclientfunc(enterip.getText());
		}
		else if(e.getActionCommand() == "Send"){
			sendmessage(dos,enterchat.getText());
		}
		else if(e.getActionCommand() == "Login"){
			enterip.setVisible(true);
			createserver.setVisible(true);
			createclient.setVisible(true);
			enterusername.setVisible(false);
			enterpass.setVisible(false);
			login.setVisible(false);
			myname = enterusername.getText();
		}
	}
	public void createserverfunc(){
		try{  
			ServerSocket ss=new ServerSocket(6666);  
			Socket s=ss.accept();//establishes connection   
			dis=new DataInputStream(s.getInputStream());
			dos=new DataOutputStream(s.getOutputStream());
			opponame =(String)dis.readUTF();
			dos.writeUTF(myname);
			Thread myinobject = new Thread(new InputStreamThread(dis,chat,opponame)); 
            myinobject.start(); 
		}catch(Exception e){System.out.println(e);} 		
	}
	public void createclientfunc(String ip){
		try{
			Socket s=new Socket(ip,6666);  
			dos=new DataOutputStream(s.getOutputStream());
			dis=new DataInputStream(s.getInputStream());
			dos.writeUTF(myname);  
			opponame =(String)dis.readUTF();
			Thread myinobject = new Thread(new InputStreamThread(dis,chat,opponame)); 
            myinobject.start();   
		}catch(Exception e){System.out.println(e);}  		
	}
	public void sendmessage(DataOutputStream dos,String msg){
		try{
			dos.writeUTF(msg);
			String chatstr = chat.getText();
			chatstr += "<br>" + myname+": " + msg;
			chat.setText(chatstr);
			String path = System.getProperty("user.dir");
			FileWriter fw=new FileWriter(path+"\\log.html");
			fw.write(chat.getText());
			fw.close();
		}
		catch(Exception e){
			System.out.println("Error!");
		}
	}
	public static void main(String[] args) {
		new ChatApp();
	}
}