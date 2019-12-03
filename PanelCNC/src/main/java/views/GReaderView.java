package views;

import java.io.IOException;
import java.io.OutputStream;

import com.vaadin.navigator.View;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;

import PanelCNC.InterfaceCNC;

public class GReaderView extends GReaderDesign implements View, Receiver, SucceededListener {

	private int totalLine;
	private int executeLine;
	private InterfaceCNC  interfaceCNC;
	private MainFrame     mainframe;
	private StringBuilder content;
	private Upload        uploadFile;

	public GReaderView(MainFrame mainframe, InterfaceCNC interfaceCNC) {
		super();
		this.interfaceCNC = interfaceCNC;
		this.mainframe = mainframe;

		uploadFile = new Upload(null, this);
		uploadFile.setButtonCaption("Get G Code file to upload");
		uploadFile.setWidth("100%");
		uploadFile.addSucceededListener(this);
		greaderLayout.addComponent(uploadFile);
		buttonStart.addClickListener(event -> start());
		buttonPause.addClickListener(event -> pause());
		buttonReaload.addClickListener(event -> reload());
		buttonOneStep.addClickListener(event -> oneStep());
		enabledButton(false, false, false, false, false);		
	}
	
	private void pause()
	{
		interfaceCNC.gCodeReader.setPause();
	}
	
	private void oneStep()
	{
		interfaceCNC.gCodeReader.readGcode();
		enabledButton(true, true, false, true, true);
	}
	
	private void reload() {
		resetCounter();
		interfaceCNC.gCodeReader.reloadContent();
		enabledButton(true, true, false, true, true);
	}

	private void start()
	{
		if (content != null)
		{
			if (buttonStart.getCaption().equals("Start"))
			{
				buttonStart.setCaption("Stop");
				mainframe.navigationPanelView.enabledButton(true, false);
				mainframe.connectionView.enabledButton(false);
				//mainframe.consoleView.enabledAllButton(false);
				enabledButton(true, false, true, false, false);
				interfaceCNC.gCodeReader.status = true;////////////////////////////////////////////////////////////
				interfaceCNC.threadGcode = new Thread(interfaceCNC.gCodeReader);///////////////////////////////////
				interfaceCNC.threadGcode.setName("GCODE READER");
				interfaceCNC.threadGcode.start();//////////////////////////////////////////////////////////////////
				interfaceCNC.comunicator.writeData("");////////////////////////////////////////////////////////////
				mainframe.consoleView.appendLog("Program script started \n");
			}
			else
			{
				buttonStart.setCaption("Start");
				mainframe.navigationPanelView.enabledButton(true, true);
				mainframe.connectionView.enabledButton(true);
				mainframe.consoleView.enabledButton(true);
				enabledButton(false, true, false, true, true);
				
				interfaceCNC.threadGcode.interrupt();
				interfaceCNC.gCodeReader.turnOFF();
				mainframe.consoleView.appendLog("Program script Stopped \n");
			}
		}
		else
		{
			String log = "No Gcode source file was selected\n";
			System.err.println(log);
			mainframe.consoleView.appendLog(log);
		}
	}

	public void incrementCounter() 
	{
		this.executeLine++;
		exeCountField.getUI().getSession().getLockInstance().lock();
		try
		{
			exeCountField.setValue(executeLine + " / " + totalLine);
		}
		finally {exeCountField.getUI().getSession().getLockInstance().unlock();}
	}
	
	public void resetCounter() {
		this.executeLine = 0;
		exeCountField.setValue(executeLine + " / " + totalLine);
	}

	void enabledButton(Boolean start, Boolean oneStep, Boolean pause, Boolean reload, Boolean up) {
		buttonStart.setEnabled(start);
		buttonOneStep.setEnabled(oneStep);
		buttonPause.setEnabled(pause);
		buttonReaload.setEnabled(reload);
		this.uploadFile.setEnabled(up);
	}

	@Override
	public OutputStream receiveUpload(String filename, String mimeType) {
		totalLine = 0;
		pathFileField.setValue(filename);
		content = new StringBuilder();
		return new OutputStream() {
			char temp;

			@Override
			public void write(int b) throws IOException {
				temp = (char) b;
				content.append(temp);
				if (temp == 10)
					++totalLine;
			}
		};
	}

	@Override
	public void uploadSucceeded(SucceededEvent event) {
		Notification.show("File upload successfully\n" + totalLine + " lines were loaded", Type.TRAY_NOTIFICATION);
		resetCounter();
		interfaceCNC.gCodeReader.loadContent(new String(content.toString()));
		enabledButton(true, true, false, false, true);
		
	}

}
