package Dynatac.Bus;

import java.net.ServerSocket;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import java.io.BufferedReader;

import java.io.InputStreamReader;
import java.io.PrintWriter;


public class DynatacBusServerSocket implements IDynatacBus, Runnable {
	private List<IDynatacBusSuscriptor > suscriptors_ = new ArrayList<IDynatacBusSuscriptor>();
	
	
	/// Constructor
	DynatacBusServerSocket () {
	}

	private ServerSocket listener_;
	private Socket socket_;
	
	private BufferedReader inputBuffer_;
	private PrintWriter    outputBuffer_;

	public void startServer (int myPort) throws IOException
	{
		listener_ = new ServerSocket(myPort);
		socket_   = listener_.accept();		
		
		inputBuffer_ = new BufferedReader(new InputStreamReader(socket_.getInputStream()));
		outputBuffer_= new PrintWriter(socket_.getOutputStream(), true);
	}
	
	public void stopServer () throws IOException
	{
		socket_.close();
		listener_.close();
	}
	
	/* IDynatacBus methods*/
	public void write(String data) {
		outputBuffer_.write(data);
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
		boolean finished = false;
		
		while (!finished)
		{
			try {
				String line = null;
				line = inputBuffer_.readLine();
				notifySuscriptors (line);
			} catch (IOException e) {
				
				e.printStackTrace();
			}	
		}
		
		// these lines will never be executed
		try {
			stopServer();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
}
