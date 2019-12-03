package logic;

import java.awt.HeadlessException;
import java.util.Scanner;
import views.MainFrame;

public class GcodeReader implements Runnable
{

	private boolean readyToWrite = false;
	private boolean pause        = false;
	public boolean  status        = true;
	public boolean  hasNextLine;

	Integer passLine     = 0;
	String  gcodeContent = null;
	Scanner scan         = null;
	
	MainFrame   mainFrame   = null;
	Comunicator comunicator = null;

	public GcodeReader(MainFrame mainFrame, Comunicator comunicator)
	{
		this.mainFrame   = mainFrame;
		this.comunicator = comunicator;
	}
	
	
	public void loadContent(String gcodeContent) {
		try {
			this.gcodeContent = gcodeContent;
			System.out.println(this.gcodeContent);
			scan = new Scanner(this.gcodeContent);
			hasNextLine = scan.hasNextLine();

		} catch (HeadlessException e) {
			e.printStackTrace();
		}
	}

	public void reloadContent() {
		scan = new Scanner(gcodeContent);
	}

	public synchronized boolean getReady() {
		return this.readyToWrite;
	}

	public void setPause() {
		this.pause = !this.pause;
	}

	public void turnOFF() {
		this.status = false;
	}

	public synchronized void unlock() {
		while (!readyToWrite) {
			try {
				wait();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				e.printStackTrace();
				break;
			}
		}
		readyToWrite = false;
		notifyAll();
	}

	public synchronized void writeGcode() {
		while (readyToWrite) {
			try {
				wait();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				e.printStackTrace();
				break;
			}
		}

		if ((hasNextLine = scan.hasNextLine()) == true) {
			comunicator.writeData(scan.nextLine());
			mainFrame.gReaderView.incrementCounter();
		} else {
			String message = "Work pass - end Gcode file\n" + passLine + " lines of code were executed";
			mainFrame.consoleView.appendLog("Work pass - end Gcode file\n" + passLine + " lines of code were executed");
			System.out.println(message);
			comunicator.writeToSerial('!');
		}
		readyToWrite = true;
	}

	public String readGcode() {
		String output = "";
		if ((hasNextLine = scan.hasNextLine()) == true) {
			mainFrame.gReaderView.incrementCounter();
			output = scan.nextLine();
			mainFrame.consoleView.appendLog(output);
			System.err.println(output);
			return output;
		} else {
			String message = "Work pass - end Gcode file\n" + passLine + " lines of code were executed";
			mainFrame.consoleView.appendLog(message);
			System.err.println(message);
			return "!";
		}
	}

	@Override
	public void run() {
		int cont = 0;
		while (this.status == true) {
			if (pause == false) // condition dependent on pressed pause button
			{
				writeGcode();
				System.err.println("Write: " + ++cont + " line of Gcode");
			}
			Thread.yield();
		}
	}
}
