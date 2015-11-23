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
	private boolean connected_;
	
	DynatacBusClientSocket () {
		connected_ = false;
	}
	
	private Socket socket_;
	private BufferedReader input_; 
	private PrintWriter    output_;

	void connect (String addr, int port) throws IOException
	{
		socket_ = new Socket (addr, port);
		input_  = new BufferedReader(new InputStreamReader(socket_.getInputStream()));
		output_ = new PrintWriter(socket_.getOutputStream(), true); 		

		connected_ = true;
	}
	
	void disconnect () throws IOException
	{
		input_  = null;
		output_ = null;
		if (socket_ != null)
		{
			socket_.close();
			socket_ = null;
		}

		connected_ = false; 
	}

	/* IDynatacBus methods*/
	public void write(String data) {
		if (connected_)
		{
			output_.write(data);
		}
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
			if (connected_)
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

}