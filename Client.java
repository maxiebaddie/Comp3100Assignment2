package copy;

import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.PrintWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.*;
import java.io.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;

public class Client {
	private Socket socket = null;
	private PrintWriter out = null;
	private BufferedReader input = null;
	private String input1 = "";
	private static final String xmlPath = "ds-system.xml";
	private static String SERVER;
	
	private static String SERVER2;
	private static int MEM2;
	private static int DISK2;
	private static int CORE2;
	
	private static String SERVER3;
	private static int MEM3;
	private static int DISK3;
	private static int CORE3;
	
	private static String SERVER4;
	private static int MEM4;
	private static int DISK4;
	private static int CORE4;
	
	
	//  job info
	private int jobCpuCores, jobMemory, jobDisk, jobSub, jobID, jobTime;
	private int jobCount = 0;
	
	// server info
	private String serverType;
	private int serverTime, serverState, serverCpuCores, serverMemory, serverDisk;
	private int serverID;
	
	private int finalServerID = 0;
	private String finalServer = "";

	


	public Client(String algo ,String address, int port) throws UnknownHostException, IOException, SAXException, ParserConfigurationException {
		//start connection with server
		openConnection(address,port); 
		if(newStatus("OK")) {
			sendToServer("AUTH " + System.getProperty("user.name"));
		}
		readSysInfo();
		readSysInfo2();
		readSysInfo3();
		readSysInfo4();

		while (!newStatus("NONE")) {
			if(currentStatus("OK")) {			
				sendToServer("REDY");
			}else if(input1.startsWith("JCPL")) {
				sendToServer("REDY");
			} else if (input1.startsWith("JOBN")) {
					
				String[] jobInput = input1.split("\\s+");
				jobSub = Integer.parseInt(jobInput[1]);
				jobID = Integer.parseInt(jobInput[2]);
				jobTime = Integer.parseInt(jobInput[3]);
				jobCpuCores = Integer.parseInt(jobInput[4]);
				jobMemory = Integer.parseInt(jobInput[5]);
				jobDisk = Integer.parseInt(jobInput[6]);
				
				if(jobCpuCores == CORE2 && jobMemory < MEM2 && jobDisk < DISK2) {
					sendToServer("SCHD"+ " " + jobID + SERVER2);
				} else if (jobCpuCores == CORE3 && jobMemory < MEM3 && jobDisk < DISK3) {
					sendToServer("SCHD"+ " " + jobID + SERVER3);
				} else if(jobCpuCores == CORE4 && jobMemory < MEM4 && jobDisk < DISK4) {
					sendToServer("SCHD"+ " " + jobID + SERVER4);
				} else {
					sendToServer("SCHD"+ " " + jobID + SERVER);
				}
		
			}
				
				 
				}	
		
		closeConnection();
	}
	private void readSysInfo() {                                                                    // Read ds-system.xml by using JAVA parsing functions.
        try {
            File inputFile = new File(xmlPath);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("server");                             // Looks for target type of server
            int max = 0;
            for (int i = 0; i < nList.getLength(); i++) {                                    // Stores servers as nodes inside a list
                Node n = nList.item(i);
                Element e = (Element) n;
                int cCount = Integer.parseInt(e.getAttribute("coreCount"));                  // checks coreCount attribute of each element and sorts
                if (cCount > max) {
                    SERVER = e.getAttribute("type");
                    max = cCount;
                }
            }
            SERVER = " " + SERVER + " 0";                                                    //Return target server type and ID as a string object
        } catch (Exception i) {
            System.out.println(i);
        }
    }
	private void readSysInfo2() {                                                                    // Read ds-system.xml by using JAVA parsing functions.
        try {
            File inputFile = new File(xmlPath);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("server");                                                                                                                       // Stores servers as nodes inside a list
                Node n = nList.item(0);
                Element e = (Element) n;
                  SERVER2 = e.getAttribute("type");
                   MEM2 = Integer.parseInt(e.getAttribute("memory"));
                   DISK2 = Integer.parseInt(e.getAttribute("disk"));
                   CORE2 = Integer.parseInt(e.getAttribute("coreCount"));
                
            
            SERVER2 = " " + SERVER2 + " 0";                                                    //Return target server type and ID as a string object
        } catch (Exception i) {
            System.out.println(i);
        }
    }
	private void readSysInfo3() {                                                                    // Read ds-system.xml by using JAVA parsing functions.
        try {
            File inputFile = new File(xmlPath);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("server");                             
                                           
                Node n = nList.item(1);
                Element e = (Element) n;
                
                SERVER3 = e.getAttribute("type");
                MEM3 = Integer.parseInt(e.getAttribute("memory"));
                DISK3 = Integer.parseInt(e.getAttribute("disk"));
                CORE3 = Integer.parseInt(e.getAttribute("coreCount"));
                
                
                
                SERVER3 = " " + SERVER3 + " 0";
               
                
            
                                                              
        } catch (Exception i) {
            System.out.println(i);
        }
    }
	private void readSysInfo4() {                                                                    
        try {
            File inputFile = new File(xmlPath);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("server");                                                                      
                Node n = nList.item(nList.getLength()-2);
                Element e = (Element) n;
                
                SERVER4 = e.getAttribute("type");
                MEM4 = Integer.parseInt(e.getAttribute("memory"));
                DISK4 = Integer.parseInt(e.getAttribute("disk"));
                CORE4 = Integer.parseInt(e.getAttribute("coreCount"));
             
            SERVER4 = " " + SERVER4 + " 0";                                                    
        } catch (Exception i) {
            System.out.println(i);
        }
    }
	
	

	 
	public void jobRecieve() {
		String[] jobInput = input1.split("\\s+");
		jobSub = Integer.parseInt(jobInput[1]);
		jobID = Integer.parseInt(jobInput[2]);
		jobTime = Integer.parseInt(jobInput[3]);
		jobCpuCores = Integer.parseInt(jobInput[4]);
		jobMemory = Integer.parseInt(jobInput[5]);
		jobDisk = Integer.parseInt(jobInput[6]);
		
	}

	
	public void serverRecieve() {
		String[] serverInput = input1.split("\n");
		serverType = serverInput[0];
		serverID = Integer.parseInt(serverInput[1]);
		serverState = Integer.parseInt(serverInput[2]);
		serverTime = Integer.parseInt(serverInput[3]);
		serverCpuCores = Integer.parseInt(serverInput[4]);
		serverMemory = Integer.parseInt(serverInput[5]);
		serverDisk = Integer.parseInt(serverInput[6]);
	}


	
	public void closeConnection() throws IOException {
		sendToServer("QUIT");
		input.close();
		out.close();
		socket.close();
	}

	
	public void openConnection(String address, int port) throws UnknownHostException, IOException {
		socket = new Socket(address, port);
		out = new PrintWriter(socket.getOutputStream());
		input = new BufferedReader( new InputStreamReader(socket.getInputStream()));
		sendToServer("HELO");
	}

	
	public void sendToServer(String x) {
		out.write(x + "\n");
		out.flush();
	}

	
	public boolean newStatus(String x) throws IOException {
		input1 = input.readLine();
		if(input1.equals(x)){
			return true;
		}
		return false;
	}

	public boolean currentStatus(String x) {
		if(input1.equals(x)){
			return true;
		}
		return false;
	}


	
	public NodeList readFile() throws SAXException, IOException, ParserConfigurationException {
		//initialize the nodelist for the xml reader
		NodeList systemXML = null;

		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse("ds-system.xml");
		doc.getDocumentElement().normalize();

		systemXML = doc.getElementsByTagName("server");

		return systemXML;



	}

	public static void main(String[] args) throws UnknownHostException, IOException, SAXException, ParserConfigurationException {

		//default algorithm 
		String algo = "myAlgorithm";
		
		//if there is an input parameter, set algo to the input algorithm 
		if (args.length == 2 && args[0].equals("-a")) {
			algo = args[1]; 
		}       


		Client client = new Client(algo, "127.0.0.1", 50000);
	}
}
