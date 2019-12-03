package views;

import com.vaadin.navigator.View;

import PanelCNC.InterfaceCNC;

public class ConsoleView extends ConsoleDesign implements View
{
	private StringBuilder logger = new StringBuilder();
	private InterfaceCNC interfaceCNC = null;
	final static int LOGLIMIT = 10000;
	final static int TRIMLIMIT = 8000;
	
	public ConsoleView(InterfaceCNC interfaceCNC)
	{
		super();
		this.interfaceCNC = interfaceCNC;
		enabledButton(false); 
		
		buttonSend.addClickListener(event -> 
		{
			interfaceCNC.comunicator.writeData(commandField.getValue());
			commandField.setValue("");
		});
	}
	
	private void logLimiter()
	{
		int howLong = logger.length();
		if(howLong > LOGLIMIT)
			logger.delete(TRIMLIMIT, howLong);
	}
	
	public void appendErrorLog(String errorlog)
	{
		logger.insert(0,errorlog);
		consoleView.setValue("\n" + logger.toString());
		logLimiter();
	}
	
	public void appendLog(String log)
	{
		logger.insert(0,log);
		consoleView.setValue("\n" + logger.toString());
		logLimiter();
	}
	
	void enabledButton(Boolean enabled) 
	{
		buttonSend.setEnabled(enabled);
		commandField.setEnabled(enabled);
	}
}

