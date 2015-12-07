package Dynatac.Bus;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;


public class DynatacBusClientSocket  extends DynatacBusBase implements Runnable {	
	
	/**
	 * Construictor 
	 * 
	 * @param addr to connect to
	 * @param port to connect to
	 */
	public DynatacBusClientSocket (String addr, int port) {
		remoteAddr_ = addr;
		remotePort_ = port;
	}

	/**
	 * Start the connection to server
	 * 
	 * @param addr remote server ip addres
	 * @param port remote server port
	 * 
	 * @throws IOException when could not connect
	 */
	private void connect (String addr, int port) throws IOException
	{
		InetAddress serverAddr  = InetAddress.getByName(addr);

		socket_ = new Socket (serverAddr, port);
		
		if (socket_ != null)
		{		
			streamInitializations (socket_.getInputStream(),socket_.getOutputStream());
			
			new Thread (this).start();
		}
	}
	
	/**
	 * End up connection
	 * 
	 * @throws IOException when not posible to connect
	 */
	private void disconnect () throws IOException
	{
		if (socket_ != null)
		{
			socket_.close();
			socket_ = null;
		}
	}

	/**
	 * Thread to get new input information
	 */
	public void run() {
		initialize ();
		
		while (!Thread.currentThread().isInterrupted())
		{
			dataReady();	
		}
		
		//
	}

	/**
	 * Common method to initialize
	 */
	protected void initialize() {
		try {
			connect (remoteAddr_, remotePort_);
		} catch (IOException e1) {
			System.err.println(e1.toString());
			setStatus (DYNATAC_BUS_STATUS_STARTING_FAILED);
		}
	}
	
	/**
	 * Common method to end up
	 */
	protected void close() {
		try {
			disconnect();
		} catch (IOException e) {
			System.err.println(e.toString());
			setStatus (DYNATAC_BUS_STATUS_ENDING_FAILED);
		}		
	}
	
	/**
	 * Internal class variables
	 */
	private String 	remoteAddr_ = "";
	private int 	remotePort_ = 0;
	private Socket 	socket_ 	= null;
}
