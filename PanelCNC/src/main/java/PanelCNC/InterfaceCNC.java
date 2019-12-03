package PanelCNC;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.navigator.PushStateNavigation;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.communication.PushMode;
import com.vaadin.ui.UI;

import views.*;
import logic.*;

/**
 * This UI is the application entry point. A UI may either represent a browser window 
 * (or tab) or some part of an HTML page where a Vaadin application is embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is intended to be 
 * overridden to add component to the user interface and initialize non-component functionality.
 */
@Push (PushMode.MANUAL)
@Title("PanelCNC - Interface")
@Theme("InterfaceTheme")
@PushStateNavigation

public class InterfaceCNC extends UI 
{
	private static final long serialVersionUID = 1L;
	
	public UI 			 ui = this;
	public Data 		 data;
	public MainFrame 	 mainFrame;
	public StatusManager statusManager;
	public Replicator	 replicator;
	public Comunicator   comunicator;
	public GcodeReader   gCodeReader;
	
	public Thread threadGcode 	   = null;
	public Thread threadStatus	   = null;
	public Thread threadReplicator = null;
	
	public InterfaceCNC() 
	{
		mainFrame = new MainFrame(this);
		data = new Data();
		comunicator = new Comunicator(mainFrame, data);
		gCodeReader = new GcodeReader(mainFrame, comunicator);
		comunicator.searchForPorts();
	}

	
	public void activeThread(boolean active)
	{
		if (active == true)
		{			
			statusManager = new StatusManager(comunicator);
			threadStatus = new Thread(this.statusManager);
			threadStatus.setName("STATUS_MANAGER");
			threadStatus.start();
			
			replicator = new Replicator(this, mainFrame, data);
			threadReplicator = new Thread(replicator);
			threadReplicator.setName("REPLIKATOR");
			threadReplicator.start();
		}
		else
		{
			statusManager.turnOFF();
			threadReplicator.interrupt();
			threadStatus.interrupt();
			//threadGcode.interrupt();
		}
	}
	
    @Override
    protected void init(VaadinRequest vaadinRequest) 
    {
        setContent(mainFrame);
    }

    @WebServlet(urlPatterns = "/*", name = "InterfaceCNCServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = InterfaceCNC.class, productionMode = false)
    public static class InterfaceCNCServlet extends VaadinServlet 
    {private static final long serialVersionUID = 1L;}
}
