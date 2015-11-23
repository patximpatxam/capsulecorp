package Dynatac.Bus;

import java.net.ServerSocket;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;


public class DynatacBusServerSocket implements IDynatacBus, Runnable {
	private List<IDynatacBusSuscriptor > suscriptors_ = new ArrayList<IDynatacBusSuscriptor>();
	
	
	/// Constructor
	public DynatacBusServerSocket () {
	}

	private boolean serverIsStarted_;
	private ServerSocket listener_;
	private Socket socket_;
	
	//private BufferedReader inputBuffer_;
	private BufferedReader inputBuffer_ = null;
	private PrintStream    outputBuffer_ = null;

	public void startServer (int myPort) throws IOException
	{
		listener_ = new ServerSocket(myPort);
		socket_   = listener_.accept();		
		
		inputBuffer_ = new BufferedReader(new InputStreamReader(socket_.getInputStream()));
		outputBuffer_= new PrintStream(socket_.getOutputStream());
		serverIsStarted_ = true;
		
		new Thread (this).start();
	}
	
	public void stopServer () throws IOException
	{
		serverIsStarted_ = false;
		socket_.close();
		listener_.close();
	}
	
	/* IDynatacBus methods*/
	public void write(String data) {
		System.out.println("Sent: " + data);
		outputBuffer_.println(data);
	}

	public void setOnDataAvailable(IDynatacBusSuscriptor s) {
		if (!suscriptors_.contains(s))
		{
			suscriptors_.add(s);
		}
	}

	protected void notifySuscriptors (String data)
	{
		for (int z = 0; z<suscriptors_.size(); z++)
		{
			IDynatacBusSuscriptor s = suscriptors_.get(z);
			
			s.dataAvailable(data);
		}
	}

	/* Runnable methods */
	public void run() {
		while (serverIsStarted_)
		{
			while (inputBuffer_ == null)
			{
				System.out.println("no input buffer...");
				
			}
			try {
				String line = null;
				line = inputBuffer_.readLine();
				notifySuscriptors (line);
				System.out.println("New line received: " + line);
			} catch (IOException e) {
				
				e.printStackTrace();
			}	
		}
		
		try {
			stopServer();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
}