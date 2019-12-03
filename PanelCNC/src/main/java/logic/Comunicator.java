package logic;

import gnu.io.*;
import views.MainFrame;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.TooManyListenersException;

import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

@SuppressWarnings("restriction")
public class Comunicator implements SerialPortEventListener
{
	// passed from main GUI
	Data 	  data		= null;
	MainFrame mainFrame	= null;

	// for containing the ports that will be found
	private Enumeration<?> ports = null;
	// map the port names to CommPortIdentifiers
	private HashMap<String, CommPortIdentifier> portMap = new HashMap<String, CommPortIdentifier>();
	
	private ArrayList<String> portList  = new ArrayList<String>();
	private ArrayList<String> speedList = new ArrayList<String>();

	// this is the object that contains the opened port
	private CommPortIdentifier selectedPortIdentifier = null;
	private SerialPort         serialPort 			  = null;

	// input and output streams for sending and receiving data
	private InputStream  input 	= null;
	private OutputStream output = null;

	// just a boolean flag that i use for enabling
	// and disabling buttons depending on whether the program
	// is connected to a serial port or not
	private boolean bConnected = false;

	// the speed value for connect (BPS)
	private int speed = 115200;
	// the timeout value for connecting with the port
	final static int TIMEOUT = 2000;

	// some ascii values for for certain things
	final static int SPACE_ASCII 	 = 32;
	final static int CARRIAGE_RETURN = 13;
	final static int NEW_LINE_ASCII  = 10;
	final static int STATUS_SIGN 	 = 63;

	// a string for recording what goes on in the program
	// this string is written to the GUI
	String logText = "";

	// class constructor
	public Comunicator(MainFrame mainFrame, Data data)
	{
		this.mainFrame = mainFrame;
		this.data 	= data;
		initPortSpeedList();
	}
	
	// a method that complements 
	// the arraylist with certain values
	public void initPortSpeedList()
	{
		speedList.add("1200");
		speedList.add("2400");
		speedList.add("4800");
		speedList.add("19200"); 
		speedList.add("38400"); 
		speedList.add("57600"); 
		speedList.add("115200");
	}

	// search for all the serial ports
	// adds all the found ports to a combo box on the GUI
	public void searchForPorts()
	{
		portList = new ArrayList<String>();
		ports = CommPortIdentifier.getPortIdentifiers();

		while (ports.hasMoreElements())
		{
			CommPortIdentifier curPort = (CommPortIdentifier) ports.nextElement();

			// get only serial ports
			if (curPort.getPortType() == CommPortIdentifier.PORT_SERIAL)
			{
				portList.add(curPort.getName());
				portMap.put(curPort.getName(), curPort);
			}
		}	
		mainFrame.connectionView.setComboPortList(portList);
		mainFrame.connectionView.setComboSpeedList(speedList);
	}

	// connect to the selected port in the combo box
	// pre: ports are already found by using the searchForPorts method
	// post: the connected comm port is stored in commPort, otherwise,
	// an exception is generated
	public void connect()
	{
		String selectedPort = mainFrame.connectionView.getPortSelectedItem();
		selectedPortIdentifier = (CommPortIdentifier) portMap.get(selectedPort);
		speed = mainFrame.connectionView.getSpeedSelectedItem();

		CommPort commPort = null;

		try
		{
			// the method below returns an object of type CommPort
			commPort = selectedPortIdentifier.open("PanelCNC", TIMEOUT);
			// the CommPort object can be casted to a SerialPort object
			serialPort = (SerialPort) commPort;
			serialPort.setSerialPortParams(speed, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
			serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);

			// for controlling GUI elements
			setConnected(true);

			// logging
			logText = selectedPort + " - Connection successfully";
			mainFrame.consoleView.appendErrorLog(logText);
			System.out.println(logText);
			Notification.show(selectedPort + "\nSuccessfully connected to the GRBL controller", Type.TRAY_NOTIFICATION);

		} catch (PortInUseException e)
		{
			logText = selectedPort + " is in use. (" + e.toString() + ")";
			mainFrame.consoleView.appendErrorLog(logText);
			System.out.println(logText);
			
		} catch (Exception e)
		{
			logText = "Failed to open " + selectedPort + "(" + e.toString() + ")";
			mainFrame.consoleView.appendErrorLog(logText);
			System.out.println(logText);
		}
	}

	// open the input and output streams
	// pre: an open port
	// post: initialized intput and output streams for use to communicate data
	public boolean initIOStream()
	{
		// return value for whather opening the streams is successful or not
		boolean successful = false;

		try
		{
			// init stream
			input  = serialPort.getInputStream();
			output = serialPort.getOutputStream();

			writeData("Stream's was initiated");
			System.out.println("Stream's was initiated");

			successful = true;
			return successful;
		} catch (IOException e)
		{
			logText = "I/O Streams failed to open. (" + e.toString() + ")";
			mainFrame.consoleView.appendErrorLog(logText);
			System.out.println(logText);
			return successful;
		}
	}

	// starts the event listener that knows whenever data is available to be read
	// pre: an open serial port
	// post: an event listener for the serial port that knows when data is recieved
	public void initListener()
	{
		try
		{
			serialPort.addEventListener(this);
			serialPort.notifyOnDataAvailable(true);
			System.out.println("Listners's was initiated");
			
		} catch (TooManyListenersException e)
		{
			logText = "Too many listeners. (" + e.toString() + ")";
			mainFrame.consoleView.appendErrorLog(logText);
			System.out.println(logText);
		}
	}

	// disconnect the serial port
	// pre: an open serial port
	// post: clsoed serial port
	public void disconnect()
	{
		// close the serial port
		try
		{
			writeData("Connection closed");
			serialPort.removeEventListener();
			serialPort.close();
			input.close();
			output.close();
			setConnected(false);

			logText = "Disconnected.";
			mainFrame.consoleView.appendErrorLog(logText);
			System.out.println(logText);
			Notification.show(logText, Type.TRAY_NOTIFICATION);
		} catch (Exception e)
		{
			logText = "Failed to close " + serialPort.getName() + "(" + e.toString() + ")";
			mainFrame.consoleView.appendErrorLog(logText);
			System.out.println(logText);
			Notification.show(logText, Type.TRAY_NOTIFICATION);
		}
	}

	final public boolean getConnected()
	{
		return bConnected;
	}

	public void setConnected(boolean bConnected)
	{
		this.bConnected = bConnected;
	}

	// what happens when data is received
	// pre: serial event is triggered
	// post: processing on the data it reads
	public void serialEvent(SerialPortEvent evt)
	{
		if (evt.getEventType() == SerialPortEvent.DATA_AVAILABLE)
		{
			try
			{
				String message = "";
				int i = 0;

				while ((i = input.read()) != -1)
				{
					message += (char) i;
				}
				data.send(message);
				logText = message;
				mainFrame.consoleView.appendLog(logText);
				System.out.print(logText);

			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	// method that can be called to send one mark
	// pre: open serial port
	// post: data sent to the other device
	public synchronized void writeToSerial(int c)
	{
		try
		{
			this.output.write(c);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	// method that can be called to send data
	// pre: open serial port
	// post: data sent to the other device
	public synchronized void writeData(String msg)
	{
		try
		{
			String message = msg;
			BufferedInputStream buffer = new BufferedInputStream(
					new ByteArrayInputStream(message.getBytes(StandardCharsets.UTF_8)));
			int c;
			while ((c = buffer.read()) > -1)
			{
				{
					writeToSerial(c);
				}
			}
			writeToSerial(CARRIAGE_RETURN);
		} catch (Exception e)
		{
			logText = "FAILED TO WRITE DATA. (" + e.toString() + ")";
			mainFrame.consoleView.appendErrorLog(logText);
			System.err.println(logText);
		}
	}
}
