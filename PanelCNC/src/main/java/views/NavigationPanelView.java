package views;

import com.vaadin.navigator.View;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

import PanelCNC.InterfaceCNC;

public class NavigationPanelView extends NavigationPanelDesign implements View {

	InterfaceCNC	interfaceCNC;
	MainFrame		mainFrame;

	public NavigationPanelView(MainFrame mainFrame, InterfaceCNC interfaceCNC)
	{
		super();
		this.interfaceCNC = interfaceCNC;
		this.mainFrame	  = mainFrame;

		ratioDistance.setSelectedItem("10");
		ratioFeed.setSelectedItem("1000");
		enabledButton(false, false);
		
		buttonHold.addClickListener(event -> writeChar('!', "Hold"));
		buttonReset.addClickListener(event -> writeChar((char)24, "Reset"));
		buttonUnlock.addClickListener(event -> writeCommand("$x"));
		buttonResume.addClickListener(event -> writeChar('~', "Resume"));
		
		button0X.addClickListener(event -> writeCommand("G90 G10 L20 P0 X0"));
		button0Y.addClickListener(event -> writeCommand("G90 G10 L20 P0 Y0"));
		button0Z.addClickListener(event -> writeCommand("G90 G10 L20 P0 Z0"));
		
		buttonPlusX.addClickListener(event ->writeCommand("$J=G91G21X"+getDistance()+" F"+getFeed()));
		buttonPlusY.addClickListener(event -> writeCommand("$J=G91G21Y"+getDistance()+" F"+getFeed()));
		buttonPlusZ.addClickListener(event -> writeCommand("$J=G91G21Z"+getDistance()+" F"+getFeed()));
		buttonMinusX.addClickListener(event -> writeCommand("$J=G91G21X-"+getDistance()+" F"+getFeed()));
		buttonMinusY.addClickListener(event -> writeCommand("$J=G91G21Y-"+getDistance()+" F"+getFeed()));
		buttonMinusZ.addClickListener(event -> writeCommand("$J=G91G21Z-"+getDistance()+" F"+getFeed()));
	}

	void enabledButton(Boolean control, Boolean others) 
	{
		//Control buttons
		buttonHold.setEnabled(control);
		buttonReset.setEnabled(control);
		buttonUnlock.setEnabled(control);
		buttonResume.setEnabled(control);
		
		//Position buttons
		button0X.setEnabled(others);
		button0Y.setEnabled(others);
		button0Z.setEnabled(others);

		//Jogging buttons
		ratioDistance.setEnabled(others);
		ratioFeed.setEnabled(others);
		buttonPlusX.setEnabled(others);
		buttonPlusY.setEnabled(others);
		buttonPlusZ.setEnabled(others);
		buttonMinusX.setEnabled(others);
		buttonMinusY.setEnabled(others);
		buttonMinusZ.setEnabled(others);
	}
	
	private String getDistance()
	{
		return ratioDistance.getSelectedItem().get();
	}
	
	private String getFeed()
	{
		return ratioFeed.getSelectedItem().get();
	}
	
	private void writeCommand(String command)
	{
		System.out.println(command);
		interfaceCNC.comunicator.writeData(command);
		mainFrame.consoleView.appendLog(command + "\n");
	}
	
	private void writeChar(char c, String message)
	{
		System.out.println(message);
		interfaceCNC.comunicator.writeToSerial(c);
		mainFrame.consoleView.appendLog(message + "\n");
		Notification.show(message, Type.TRAY_NOTIFICATION);
	}

	public void setStatus(String state, String field_X, String field_Y, String field_Z) 
	{
		stateField.setValue(state);
		xPositionField.setValue(field_X);
		yPositionFiled.setValue(field_Y);
		zPositionField.setValue(field_Z);
	}
}
