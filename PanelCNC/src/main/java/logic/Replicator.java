package logic;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import PanelCNC.InterfaceCNC;
import views.MainFrame;

public class Replicator implements Runnable
{
	Pattern status;
	Pattern respondOK;
	Pattern respondError;
	
	Matcher statusMatcher;
	Matcher respondOKMatcher;
	Matcher respondErrorMatcher;
	
	String statusPattern	  = "<([A-Z]+[a-z]*:?\\d?)\\|WPos:(-?\\d*.\\d{3}),(-?\\d*.\\d{3}),(-?\\d*.\\d{3})\\|Bf:(\\d\\d?).*\\s*";
	String respondOKPattern	  = "ok\\s*";
	String respondErrorPatern = "error:?(\\d?\\d?)\\s*";

	String respondFromSerial;
	
	InterfaceCNC	interfaceCNC;
	MainFrame 		mainFrame;
	Data			data;

	public Replicator(InterfaceCNC interfaceCNC, MainFrame mainFrame, Data data)
	{
		this.interfaceCNC = interfaceCNC;
		this.mainFrame	  = mainFrame;
		this.data		  = data;
		
		respondOK 	 = Pattern.compile(respondOKPattern);
		respondError = Pattern.compile(respondErrorPatern);
		status		 = Pattern.compile(statusPattern);
	}
	
	@Override
	public void run()
	{
		while (!Thread.currentThread().isInterrupted())
		{
			
			if ((respondFromSerial = data.receive())!= null)
			{
				respondOKMatcher 	= respondOK.matcher(respondFromSerial);
				respondErrorMatcher = respondError.matcher(respondFromSerial);
				statusMatcher 		= status.matcher(respondFromSerial);
				
				if (statusMatcher.find())
				{
					String state 	= statusMatcher.group(1);
					String field_X 	= statusMatcher.group(2);
					String field_Y 	= statusMatcher.group(3);
					String field_Z	= statusMatcher.group(4);
					
					mainFrame.navigationPanelView.setStatus(state, field_X, field_Y, field_Z);	
				}
				
				if (respondOKMatcher.find())
				{
					if (interfaceCNC.threadGcode != null)
						if(interfaceCNC.gCodeReader.status == true)
							interfaceCNC.gCodeReader.unlock();
					mainFrame.consoleView.appendLog("Gcode line accepted");
					System.err.println("GCODE LINE ACCEPTED");
				}
				if (respondErrorMatcher.find())
				{
					//window.comunicator.writeToSerial('!');
					mainFrame.consoleView.appendErrorLog("Gcode line not accepted");
					System.err.println("ERROR NR:" + respondErrorMatcher.group(1) + " OCCURRED WHILE READING GCODE");
				}
				
				interfaceCNC.access(new Runnable() {
					  @Override
					  public void run() {
						  interfaceCNC.ui.push();					    
					  }
					});
				
				Thread.yield();
			}
		}
	}
}