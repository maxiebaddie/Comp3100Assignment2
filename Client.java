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

	private int jobCpuCores, jobMemory, jobDisk, jobSub, jobID, jobTime;
	private String serverType;
	private int serverTime, serverState, serverCpuCores, serverMemory, serverDisk;
	private int serverID;
	private int jobCount = 0;
	private int finalServerID = 0;
	private String finalServer = "";

	


	public Client(String algo ,String address, int port) throws UnknownHostException, IOException, SAXException, ParserConfigurationException {
		//start connection with server
		openConnection(address,port); 
		if(newStatus("OK")) {
			sendToServer("AUTH " + System.getProperty("user.name"));
		}
		readSysInfo();

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
		
				sendToServer("GETS Capable" +" " + jobCpuCores +" "+ jobMemory +" "+ jobDisk);
			}
			else if(input1.startsWith("DATA")) {
					System.out.println(input1);
					sendToServer("OK");
				}		
			else if(currentStatus(".")) {
					System.out.println(input1);
					sendToServer("SCHD"+ " " + jobID + SERVER);
				}
			else if(input1.contains(SERVER)) {
				sendToServer("OK");
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

	 
	public void jobRecieve() {
		String[] jobInput = input1.split("\\s+");
		jobSub = Integer.parseInt(jobInput[1]);
		jobID = Integer.parseInt(jobInput[2]);
		jobTime = Integer.parseInt(jobInput[3]);
		jobCpuCores = Integer.parseInt(jobInput[4]);
		jobMemory = Integer.parseInt(jobInput[5]);
		jobDisk = Integer.parseInt(jobInput[6]);
		
	}

	//this function works the same as the jobInput however it is called
	//when we get need to get the server state info instead of the job info.  
	public void serverRecieve() {
		String[] serverInput = input1.split("\\s+");
		serverType = serverInput[0];
		serverID = Integer.parseInt(serverInput[1]);
		serverState = Integer.parseInt(serverInput[2]);
		serverTime = Integer.parseInt(serverInput[3]);
		serverCpuCores = Integer.parseInt(serverInput[4]);
		serverMemory = Integer.parseInt(serverInput[5]);
		serverDisk = Integer.parseInt(serverInput[6]);
	}


	//close connection just closes all the input and output streams + Socket opened 
	//in the openConneciton function. 
	//We use the sendToServer() function to send the string QUIT to end the running process. 
	public void closeConnection() throws IOException {
		sendToServer("QUIT");
		input.close();
		out.close();
		socket.close();
	}

	//open connection is very similar to the close connection function
	//however it opens the socket and input and output streams then sends the string HELO
	public void openConnection(String address, int port) throws UnknownHostException, IOException {
		socket = new Socket(address, port);
		out = new PrintWriter(socket.getOutputStream());
		input = new BufferedReader( new InputStreamReader(socket.getInputStream()));
		sendToServer("HELO");
	}

	//the sent to server function utilizes PrintWriters write function to 
	//be able to send messages to the server and then we flush the output stream
	//so we can get ready to send another message. 
	public void sendToServer(String x) {
		out.write(x + "\n");
		out.flush();
	}

	//the newStatus function first initializes the input1 variable
	//and assigns it to the value if the input stream. 
	//this allows us to read the data that the server is sending to us
	//after we initialize the variable we call the value of itself so that we can use
	//it as a conditional while setting the variable at the same time.
	public boolean newStatus(String x) throws IOException {
		input1 = input.readLine();
		if(input1.equals(x)){
			return true;
		}
		return false;
	}

	//The current status function is the same as the newStatus fucntion, 
	//however it does not set the value of input1. it only checks to see if it is equal
	//to the input parameter. 
	public boolean currentStatus(String x) {
		if(input1.equals(x)){
			return true;
		}
		return false;
	}


	//The readFile function is used to read the value within the system.xml file
	//we first create an empty nodelist and then use a DOM parser to get the server values from the file
	//using the "server" tagname
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
		String algo = "allToLargest";
		
		//if there is an input parameter, set algo to the input algorithm 
		if (args.length == 2 && args[0].equals("-a")) {
			algo = args[1]; 
		}       


		Client client = new Client(algo, "127.0.0.1", 50000);
	}
}
