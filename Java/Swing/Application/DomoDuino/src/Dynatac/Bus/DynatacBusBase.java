package Dynatac.Bus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * 
 * @author elotro
 *
 */
public abstract class DynatacBusBase extends DynatacBusCommon implements IDynatacBus {
	
	/****************************************
	 *  INHERITATION INTERFACE 						
	 ****************************************/
	/**
	* Methods implemented by the different implementations
	*/
	//protected abstract void initialize();

	protected abstract void close();
	
	/****************************************
	 *  OBJECT CONSTRUCTION	
	 ****************************************/
	/**
	* Constructor and finalize method
	*/
	protected DynatacBusBase ()
	{
	}

	protected void finalize() //throws Throwable   
	{
	 	close();
	}
	
	
	/****************************************
	 *  PUBLIC METHODS 						
	 ****************************************/
	public void write(String data) {
		System.out.println("Sent: " + data);
		try {
			//output_.write(data.getBytes());
			output_.println (data);
		} catch (Exception e) {
			System.err.println("Could not write to output buffer.");
			e.printStackTrace();
			//assert(0);
		}
	}
	
	/****************************************
	 *  INTERNAL METHODS 						
	 ****************************************/
	
	/**
	 * This method is called by specific bus and read, base dynatac bus will perform a common read line
	 */
	protected boolean dataReady ()
	{
		// Read buffered line
		//
		String inputLine;
		try {
			inputLine = input_.readLine();
			
			if (inputLine == null)
			{
				return false;
			}
			else
			{
				notifyListeners (inputLine);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return true;
	}
	
	/**
	 * Once the specific bus has a input/output bus must call this method to initialize the base bus
	 * 
	 * @param input stream
	 * @param output stream
	 */
	protected void streamInitializations (InputStream input, OutputStream output)
	{
		output_ = new PrintStream (output);
		input_  = new BufferedReader(new InputStreamReader(input));
	}


	/****************************************
	 *  INTERNAL VARS 						
	 ****************************************/

	/**
	* Protected variables inherited by other implementations.
	*/

	/**
	* A BufferedReader which will be fed by a InputStreamReader 
	* converting the bytes into characters 
	* making the displayed results codepage independent
	*/
	private BufferedReader   input_  = null;
	/** The output stream to the port */
	//private OutputStream 	 output_ = null;
	private PrintStream 	 output_ = null;
}
