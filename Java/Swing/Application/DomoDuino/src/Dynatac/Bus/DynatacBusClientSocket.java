package Dynatac.Bus;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import java.io.BufferedReader;

import java.io.InputStreamReader;
import java.io.PrintWriter;



public class DynatacBusClientSocket implements IDynatacBus, Runnable {
	private List<IDynatacBusSuscriptor > suscriptors_ = new ArrayList<IDynatacBusSuscriptor>();
	private String serverAddress_;
	private int serverPort_;
	
	DynatacBusClientSocket (String addr, int port) {
		serverAddress_ = addr;
		serverPort_ = port;
	}
	
	private Socket socket_;
	private BufferedReader input_; 
	private PrintWriter    output_;
	
	void open (int address, int port) throws IOException
	{
		socket_ = new Socket (serverAddress_, serverPort_);
		input_  = new BufferedReader(new InputStreamReader(socket_.getInputStream()));
		output_ = new PrintWriter(socket_.getOutputStream(), true); 		
	}
	
	void close () throws IOException
	{
		input_  = null;
		output_ = null;
		socket_.close();
	}
	/* IDynatacBus methods*/

	public void write(String data) {
		output_.write(data);
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

	public void run() {
		while (true)
		{
			// if available data, notify suscriptors
			try {
				String line = null;
				line = input_.readLine();
				notifySuscriptors (line);
			} catch (IOException e) {
				
				e.printStackTrace();
			}	
		
		}
		
	}

}
