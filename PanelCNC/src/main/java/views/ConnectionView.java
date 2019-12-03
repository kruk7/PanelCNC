package views;

import java.util.Collection;

import com.vaadin.navigator.View;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

import PanelCNC.InterfaceCNC;

public class ConnectionView extends ConnectionDesign implements View 
{
	
	MainFrame	 mainFrame;
	InterfaceCNC interfaceCNC;
	
	public ConnectionView(MainFrame mainFrame, InterfaceCNC interfacecnc) 
	{
		super();
		this.mainFrame = mainFrame;
		this.interfaceCNC = interfacecnc;
		buttonConnect.addClickListener(ClickEvent -> connect());
		buttonRefresh.addClickListener(ClickEvent -> refreshPort());
	}
	
	private void refreshPort()
	{
		
		interfaceCNC.comunicator.disconnect();
		comboPort.clear();
		interfaceCNC.comunicator.searchForPorts();
		Notification.show("Refreshing ports", Type.TRAY_NOTIFICATION);
	}
	
	private void connect()
	{
		if(interfaceCNC.comunicator.getConnected() == false)
		{
			interfaceCNC.comunicator.connect();
			if(interfaceCNC.comunicator.getConnected() == true)
			{
				if(interfaceCNC.comunicator.initIOStream() == true)
				{
					interfaceCNC.comunicator.initListener();
					interfaceCNC.activeThread(true);
				}
			}
			buttonConnect.setCaption("Disconnect");
			enabledButton(false);
			mainFrame.navigationPanelView.enabledButton(true, true);
			mainFrame.gReaderView.enabledButton(false, false, false, false, true);
			mainFrame.consoleView.enabledButton(true); 
			
		}
		else
		{
			interfaceCNC.activeThread(false);
			interfaceCNC.comunicator.disconnect();
			buttonConnect.setCaption("Connect");	
			enabledButton(true);
			mainFrame.navigationPanelView.enabledButton(false, false);
			mainFrame.gReaderView.enabledButton(false, false, false, false, false);
			mainFrame.consoleView.enabledButton(false); 
		}
	}
	
	public void setComboPortList(Collection<String> portList)
	{
		comboPort.clear();
		comboPort.setItems(portList);
	}
	
	public void setComboSpeedList(Collection<String> speedList)
	{
		comboSpeed.clear();
		comboSpeed.setItems(speedList);
	}
	
	public String getPortSelectedItem()
	{
		String port = new String(comboPort.getSelectedItem().get());
		return port;
	}
	
	public int getSpeedSelectedItem()
	{
		Integer speed = Integer.parseInt(comboSpeed.getSelectedItem().get());	
		return speed.intValue();
	}
	
	void enabledButton(Boolean enabled)
	{
		buttonRefresh.setEnabled(enabled);
		comboPort.setEnabled(enabled);
		comboSpeed.setEnabled(enabled);
	}
}
