package hello;

import java.io.BufferedReader;
import java.io.InputStreamReader;
//import java.io.OutputStream;
import gnu.io.CommPortIdentifier; 
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent; 
import gnu.io.SerialPortEventListener; 
//import java.util.Enumeration;



public class SerialTest implements SerialPortEventListener {
	
	public interface SerialTestSuscriptor
	{
		public void newLine(String s);
		
		public void connectionError ();
	   //Any number of final, static fields
	   //Any number of abstract method declarations\
	}
	
	SerialTestSuscriptor aSuscriptor = null;
	
	static SerialPort serialPort;
	
    /** The port we're normally going to use. */
	private static final String PORT_NAMES[] = { 
			"/dev/tty.usbserial-A9007UX1", // Mac OS X
			"/dev/ttyACM0", // Raspberry Pi
			"/dev/ttyUSB0", // Linux
			"COM3", // Windows
	};
	
	/**
	* A BufferedReader which will be fed by a InputStreamReader 
	* converting the bytes into characters 
	* making the displayed results codepage independent
	*/
	
	private static BufferedReader input;
	/** The output stream to the port */
	//private static OutputStream output;
	/** Milliseconds to block while waiting for port open */
	private static final int TIME_OUT = 2000;
	/** Default bits per second for COM port. */
	private static final int DATA_RATE = 9600;
	
	
	/** 
	 * Singleton pattern and protected constructor
	 * */
	private static SerialTest instance = null;
	
	protected SerialTest ()
	{
		System.out.println("SerialTest creted");
		
	}
	
	public static SerialTest getSerialTest ()
	{		
		if (instance == null)
		{
			System.out.println("Going to create serial test");
			instance = new SerialTest ();
		}
		
		System.out.println("Going to return SerialTest");
		
		return instance;		
	}

	/**
	 * Public methods, all static because it is a singleton 
	 * 
	 */
	public void initialize(SerialTestSuscriptor s) {
		aSuscriptor = s;
/*
        // the next line is for Raspberry Pi and 
        // gets us into the while loop and was suggested here was suggested http://www.raspberrypi.org/phpBB3/viewtopic.php?f=81&t=32186
        System.setProperty("gnu.io.rxtx.SerialPorts", "/dev/ttyACM0");
*/

		CommPortIdentifier portId = null;
		//Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();
		java.util.Enumeration<CommPortIdentifier> portEnum = CommPortIdentifier.getPortIdentifiers();		

		System.out.println ("Start looking for ports..");
		//First, Find an instance of serial port as set in PORT_NAMES.
		while (portEnum.hasMoreElements()) {
			CommPortIdentifier currPortId = portEnum.nextElement();
			System.out.println ("Expected name: "+currPortId);
			for (String portName : PORT_NAMES) {
				System.out.println("Trying port name: " + portName);
				if (currPortId.getName().equals(portName)) {
					portId = currPortId;
					System.out.println("COM port found:."+portName);
					break;
				}
			}
		}
		
		if (portId == null) {
			System.out.println("Could not find COM port.");
			return;
		}

		try {
			// open serial port, and use class name for the appName.
			serialPort = (SerialPort) portId.open(instance.getClass().getName(),
					TIME_OUT);

			// set port parameters
			serialPort.setSerialPortParams(DATA_RATE,
					SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1,
					SerialPort.PARITY_NONE);

			// open the streams
			input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
			//output = serialPort.getOutputStream();

			// add event listeners
			serialPort.addEventListener(instance);
			serialPort.notifyOnDataAvailable(true);
		} catch (Exception e) {
			System.err.println(e.toString());
		}
		
		System.out.println("Initialized");
	}

	/**
	 * This should be called when you stop using the port.
	 * This will prevent port locking on platforms like Linux.
	 */
	public synchronized void close() {
		if (serialPort != null) {
			serialPort.removeEventListener();
			serialPort.close();
		}
	}

	/**
	 * Handle an event on the serial port. Read the data and print it.
	 */
	public synchronized void serialEvent(SerialPortEvent oEvent) {
		if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
			try {
				String inputLine=input.readLine();
				
				
				
				aSuscriptor.newLine(inputLine);
			} catch (Exception e) {
				System.err.println(e.toString());
				aSuscriptor.connectionError();
			}
		}
		// Ignore all the other eventTypes, but you should consider the other ones.
	}

/*	
	public static void main(String[] args) throws Exception {
		SerialTest main = new SerialTest();
		main.initialize();
		Thread t=new Thread() {
			public void run() {
				//the following line will keep this app alive for 1000 seconds,
				//waiting for events to occur and responding to them (printing incoming messages to console).
				try {Thread.sleep(1000000);} catch (InterruptedException ie) {}
			}
		};
		t.start();
		
	}
	*/	
}
