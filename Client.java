package restart;

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

public class Client {
	private Socket socket= null;
	private PrintWriter out = null;
	private BufferedReader input = null;
	private String input1 = "";

	private int jobCpuCores, jobMemory, jobDisk, jobSub, jobID, jobTime;
	private String serverType;
	private int serverTime, serverState, serverCpuCores, serverMemory, serverDisk;
	private int serverID;
	private int jobCount = 0;
	private int finalServerID = 0;
	private String finalServer = "";

	

	private final int INT_MAX = Integer.MAX_VALUE;
	private int bestFit = INT_MAX;
	private int minAvail = INT_MAX;


	private final int INT_MIN = Integer.MIN_VALUE;
	
	private Float FLOAT_MAX = Float.MAX_VALUE;
	


	public Client(String algo ,String address, int port) throws UnknownHostException, IOException, SAXException, ParserConfigurationException {
		
		openConnection(address,port); 
		if(newStatus("OK")) {
			sendToServer("AUTH " + System.getProperty("user.name"));
		}

		while (!newStatus("NONE")) {
			if(currentStatus("OK")) {
				
				sendToServer("REDY");
			}else if (input1.startsWith("JCPL")) {
				sendToServer("REDY");
			}
			while(!newStatus(".")) {
				if(input1.startsWith("JOBN")) {
					jobRecieve();
					sendToServer("GETS All");
					System.out.println(input1);
				}
				if (input1.startsWith("DATA")) {
					
					sendToServer("OK");
					serverRecieve();
					
					if(algo.equals("bf") && bestFit == INT_MAX) {
						bestFitAlgo("read");
					} else {
						bestFitAlgo("dont_read");
					}
				}
				sendToServer("OK");
						
			}	
	
				sendToServer("SCHD " + jobCount + " " + finalServer + " " + finalServerID);
				jobCount++;

			}
		
		
		
		closeConnection();
	}



	public void bestFitAlgo(String readXML) throws SAXException, IOException, ParserConfigurationException {

		

		if(jobCpuCores <= serverCpuCores && jobDisk <= serverDisk && jobMemory <= serverMemory) {
			if(serverCpuCores < bestFit || (serverCpuCores == bestFit && serverTime < minAvail)) {	
				bestFit = serverCpuCores;
				minAvail = serverTime;
				finalServer = serverType;
				finalServerID = serverID; 
			}

		}

		
		else if(readXML == "read") {

		

			NodeList xml = readFile(); 

			for(int i = 0; i < xml.getLength(); i++) {


				serverType = xml.item(i).getAttributes().item(6).getNodeValue();

			

				serverID = 0;  

				serverCpuCores = Integer.parseInt(xml.item(i).getAttributes().item(1).getNodeValue());
				serverMemory = Integer.parseInt(xml.item(i).getAttributes().item(4).getNodeValue());
				serverDisk = Integer.parseInt(xml.item(i).getAttributes().item(2).getNodeValue());

				if(jobCpuCores <= serverCpuCores && jobDisk <= serverDisk && jobMemory <= serverMemory) {
					if(serverCpuCores < bestFit || (serverCpuCores == bestFit && serverTime < minAvail)) {
						bestFit = serverCpuCores;
						minAvail = serverTime;
						finalServer = serverType;
						finalServerID = serverID; 
					}
				}
			}
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
		bestFit = INT_MAX;
		minAvail = INT_MAX;
		
	}

	  
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
		String algo = "allToLargest";
		
		//if there is an input parameter, set algo to the input algorithm 
		if (args.length == 2 && args[0].equals("-a")) {
			algo = args[1]; 
		}       


		Client client = new Client(algo, "127.0.0.1", 50000);
	}
}
