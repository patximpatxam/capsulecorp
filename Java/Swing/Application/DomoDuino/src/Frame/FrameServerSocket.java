package Frame;

import java.net.ServerSocket;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import java.io.BufferedReader;

import java.io.InputStreamReader;
import java.io.PrintWriter;


public class FrameServerSocket implements IFrame, Runnable {
	private List<IFrameSuscriptor > suscriptors_ = new ArrayList<IFrameSuscriptor>();
	
	
	/// Constructor
	FrameServerSocket (int port) {
		myPort_ = port;
	}
	private ServerSocket listener_;
	private Socket socket_;
	private int myPort_;
	
	private BufferedReader inputBuffer_;
    private PrintWriter    outputBuffer_;
	
	private void openConnection (int port) throws IOException
	{
		listener_ = new ServerSocket(port);
		socket_   = listener_.accept();		
		
		inputBuffer_ = new BufferedReader(new InputStreamReader(socket_.getInputStream()));
		outputBuffer_= new PrintWriter(socket_.getOutputStream(), true);
	}
	
	private void closeConnection () throws IOException
	{
		socket_.close();
		listener_.close();
	}
	
	/* IFrame methods*/
	public void write(String data) {
		outputBuffer_.write(data);
	}

	public void setOnDataAvailable(IFrameSuscriptor s) {
		if (!suscriptors_.contains(s))
		{
			suscriptors_.add(s);
		}
	}

	protected void notifySuscriptors (String data)
	{
		for (int z = 0; z<suscriptors_.size(); z++)
		{
			IFrameSuscriptor s = suscriptors_.get(z);
			
			s.dataAvailable(data);
		}
	}

	/* Runnable methods */
	public void run() {
		boolean finished = false;
		try {
			openConnection (myPort_);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
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
			closeConnection();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
}