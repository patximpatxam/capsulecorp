package Dynatac.Bus;

import java.net.ServerSocket;
import java.io.IOException;
import java.net.Socket;
//import java.net.SocketException;

import Dynatac.Bus.IDynatacBus.IDynatacBusListener;

/**
 * 
 * @author elotro
 *
 */
public class DynatacBusServerSocket extends DynatacBusCommon implements IDynatacBusListener,Runnable {

	/**
	 * 
	 * @author elotro
	 *
	 */
	private class InternalMiniServer extends DynatacBusBase implements Runnable {
		/**
		* Constructor
		*/
		/**
		 * Dynatac Bus serving on a socket
		 * 
		 * @param aSocket
		 */
		public InternalMiniServer (Socket aSocket) {
			socket_ = aSocket;
			
			// Open the streams
			//
			try {
				streamInitializations (socket_.getInputStream(), socket_.getOutputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			// Start the new thread
			// 
			new Thread (this).start();
		}

		/**
		 * Internal thread, used to read data
		 * 
		 */
		public void run() {
			while (true)
			{
				// if not data ready means the client has closed its connection
				//
				if (!dataReady())
				{
					break;
				}
			}
			
		    close();
		}
		
		// Ending method
		// 
		protected void close() {
			try {
				socket_.close();
				myConnectedClients_.removeBus(this);
			} catch (IOException e) {
				e.printStackTrace();
			}			
		}

		
		/**
		 * Internal class variables
		 */
		private Socket socket_ = null;
	}

	/**
	* Constructor
	*/
	public DynatacBusServerSocket (int aPort) {
		myPort_ = aPort;
		myConnectedClients_ = new DynatacBusBridge();
		myConnectedClients_.installListener(this);
		
		new Thread (this).start();
	}
	
	/**
	 * write data to all connections
	 * 
	 */
	public void write(String data) {
		myConnectedClients_.write(data);
	}

	/**
	 * Starts listening a socket
	 * 
	 * @throws IOException
	 */
	private void waitForConnections() throws IOException
	{
		Socket aSocket = listener_.accept();
		
		InternalMiniServer miniServer = new InternalMiniServer(aSocket);
		
		myConnectedClients_.addBus(miniServer);
	}
	
	public void dataAvailable(String data, IDynatacBus bus) {
		notifyListeners (data);
	}
	

	/**
	 * Listener thread
	 */
	public void run() {
		initialize ();
	
		while (true)
		{
			try {
				waitForConnections ();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		
		//close();
	}

	/**
	 * Common method to initialize
	 */
	protected void initialize() {
		try {
			listener_ = new ServerSocket(myPort_);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Common method to end up
	 */
	protected void close() {
	}
	
	/**
	 * Internal class variables
	 */
	private int 		 myPort_;	
	private ServerSocket listener_;
	
	private DynatacBusBridge myConnectedClients_;
}
