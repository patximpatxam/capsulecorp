package Dynatac.Bus;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;


public class DynatacBusClientSocket  extends DynatacBusBase implements Runnable {	
	
	/**
	 * Constructor 
	 * 
	 * @param addr to connect to
	 * @param port to connect to
	 */
	public DynatacBusClientSocket (String addr, int port) {
		remoteAddr_ = addr;
		remotePort_ = port;
		
		new Thread (this).start();
	}

	/**
	 * Start the connection to server
	 * 
	 * @param addr remote server ip addres
	 * @param port remote server port
	 * 
	 * @throws IOException when could not connect
	 */
	private void connect (String addr, int port)
	{
		try {
			InetAddress serverAddr = InetAddress.getByName(addr);
			
			socket_ = new Socket (serverAddr, port);
			
			streamInitializations (socket_.getInputStream(),socket_.getOutputStream());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			setStatus (DYNATAC_BUS_STATUS_STARTING_FAILED);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			setStatus (DYNATAC_BUS_STATUS_STARTING_FAILED);
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
		connect (remoteAddr_, remotePort_);
		
		while (!Thread.currentThread().isInterrupted() && ((getStatus() & DYNATAC_BUS_STATUS_UNAVAILABLE) == 0))
		{
			dataReady();	
		}
		
		close();		
		//
	}

	/**
	 * Common method to initialize
	 */
		
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
