package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

public class Server 
{

	private int clientCounter;
	private ArrayList<ClientSession> sessions;

	public final static int MAX_PACKET_SIZE = 65507; // this can vary, but its
	// value cannot be less than
	// 8192 for safety

	public Server(int serverPort) throws IOException 
	{
		clientCounter = 0;
		sessions = new ArrayList<>();

		//Create datagram socket
		DatagramSocket serverSocket = new DatagramSocket(serverPort);

		//Set the buffer size. You can vary it.
		byte[] receiveData = new byte[MAX_PACKET_SIZE];
		
		String receiveFile[] = null;
		while (true) 
		{
			//Create space for received datagram
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			
			//Received datagram
			serverSocket.receive(receivePacket);
			
			// Get IP addr port #, of sender
			InetAddress IPAddress = receivePacket.getAddress();
			int port = receivePacket.getPort();

			// For manage client session
			String IPasString = IPAddress + "";
			String portAsString = port + "";
			ClientSession session = darSesionCliente(IPasString, portAsString);
			if (session == null) 
			{
				// Create session for new client and add it to the list
				session = new ClientSession(IPasString, portAsString, clientCounter + 1);
				sessions.add(session);

			}

			
			byte[] data = receivePacket.getData();
			System.out.println("data recieved is: " + receivePacket.getLength());
			int realByteSize = receivePacket.getLength();
			System.out.println("data byte array length is : + " + data.length);

			// los primeros 5000 son de archivo (puede que haya un extra de 7's que
			// haya que quitar)
			// lo que sobre el la metadata con la informacion

			// saco el archivo
			byte[] filePart = new byte[5000];
			for (int i = 0; i < filePart.length; i++) 
			{ 
				filePart[i] = data[i];
			}
			
			// saco la metadata
			int metadataLength = data.length - 5000;
			byte[] metadataBytes = new byte[metadataLength];
			int metadataIndex = 0;
			for (int i = 5000; i < realByteSize; i++) 
			{
				metadataBytes[metadataIndex] = data[i];
				metadataIndex += 1;
			}

			// parseo la metadata en un string

			// notice how getData() starts at offset, so we have to make sure its
			// always 0, I guess
			String metadata = new String(metadataBytes, "UTF-8");
			//System.out.println("metadata is: " + metadata);
			String[] parts = metadata.split("&");
			String datagramNumber = parts[0];
			//String extraAsString = parts[1]; // todavia no hago nada con este extra
			String NOPString = parts[1];
			System.out.println("Total of parts "+ NOPString);
			String receivedData = parts[2];			
			long inicio = Long.parseLong(parts[3]);
			long tiempoEnvio = System.currentTimeMillis() - inicio;
			session.setDuracion(tiempoEnvio);

			
			// String datagramNumber = parts[0].split("#")[1];
			int totalNumberOfDatagrams = Integer.parseInt(NOPString);
			
			session.addRegister(datagramNumber, 1, totalNumberOfDatagrams);
			// System.out.println("Message: " + datagramNumber + " Delta: "+ delta);
			
			if(Integer.parseInt(datagramNumber) == Integer.parseInt(NOPString)-1){
				int hash = receiveFile.hashCode();
				System.out.println("El hash es "+ hash);
			}
		}
	}

	public ClientSession darSesionCliente(String ip, String port) 
	{
		ClientSession s = null;
		for (int i = 0; i < sessions.size(); i++) 
		{
			s = sessions.get(i);
			if (s.getIP().equals(ip) && s.getPort().equals(port)) 
			{
				return s;
			}
		}
		return null;
	}

	public static void main(String[] args) 
	{
		System.out.println("Puerto "+ args[0]);
		try 
		{
			int port = Integer.parseInt(args[0]);
			Server server = new Server(port);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
}
