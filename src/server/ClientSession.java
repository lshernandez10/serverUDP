package server;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class ClientSession {
	
	private int lostDatagrams;
	
	private double averageTime;
	
	private String IP;
	
	private String port;
	
	private ArrayList<Integer> receivedDatagrams;
	
	private long numberRecievedDatagrams;
	
	private String fileName;
	
	private String summaryFile;
	
	private long duracion;
	
	public ClientSession(String IP, String port, int clientNumber) 
	{
		this.lostDatagrams = 0;
		this.averageTime = 0.0;
		this.numberRecievedDatagrams = 0;
		this.IP = IP;
		this.port = port;
		this.fileName = "logUser" + clientNumber + ".txt";
		this.summaryFile = "summary" + clientNumber + ".text";
		
		// gets the buffer for this client
		this.receivedDatagrams = new ArrayList<>();
	}
	
	public long getDuracion() {
		return duracion;
	}



	public void setDuracion(long duracion) {
		this.duracion = duracion;
	}



	public String getIP(){
		return this.IP;
	}
	
	public String getPort(){
		return this.port;
	}
	
	public void addRegister(String datagramNumber, long delta, int totalNumberOfDatagrams)
	{
		receivedDatagrams.add(Integer.parseInt(datagramNumber));
		//System.out.println("Received datagram: "+ datagramNumber);
		
		recalculateAverage(delta);
		this.numberRecievedDatagrams +=1;
		System.out.println("left to arrive:" + (totalNumberOfDatagrams - numberRecievedDatagrams));
		if(totalNumberOfDatagrams - (Integer.parseInt(datagramNumber) + 1) == 0 )
		{
			processFinalResults();
		}
		
		try 
		{
			FileWriter fw = new FileWriter(this.fileName, true);
			BufferedWriter bw = new BufferedWriter(fw);
	        PrintWriter out = new PrintWriter(bw);
	    
		    out.println(datagramNumber + ":"+ delta);
		    out.close();
			
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

	private void processFinalResults() 
	{
		// Packets arrived
		// Packets lost
		// Average time of response
		
		try {
			FileWriter fw = new FileWriter(this.summaryFile, true);
			BufferedWriter bw = new BufferedWriter(fw);
	    PrintWriter out = new PrintWriter(bw);
	    
	    // preprocessing
	    Collections.sort(receivedDatagrams);
	    
	    // arrived
	    String goodDatagrams = "Arrived: ";
	    String badDatagrams = "Lost: ";
	    int currentDatagramNumber = 0;
	    for (int i = 0; i < receivedDatagrams.size(); i++) {
	    	int current = receivedDatagrams.get(i);
	    	if(currentDatagramNumber != current){
	    		badDatagrams += currentDatagramNumber + " ";
	    	}
	    	else {
	    		goodDatagrams += current + " ";
	    	}
	    	currentDatagramNumber++;
			}
	    out.println(goodDatagrams);
	    out.println(badDatagrams);
	    out.println("Average delay: "+ averageTime);
	    out.close();
		}
		catch (Exception e){
			
		}

	}

	private void recalculateAverage(long delta) {
		this.averageTime = ((this.averageTime* this.numberRecievedDatagrams) + delta)/ (this.numberRecievedDatagrams + 1);
		//System.out.println("Average time:" + this.averageTime);
	}
}
