package views;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.Navigator;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import PanelCNC.InterfaceCNC;


public class MainFrame extends HorizontalLayout 
{
	
	private static final long serialVersionUID = 1L;
	
	public InterfaceCNC 		interfaceCNC;
	public NavigationPanelView  navigationPanelView;	
	public ConnectionView		connectionView;
	public ConsoleView			consoleView;
	public GReaderView			gReaderView;
	
	private Label title = new Label("Menu");
	private Button buttonConnectionView;
	private Button buttonConsoleView;
	private Button buttonGReaderView;
	public  Navigator navigator;

	private CssLayout      menu;
	private VerticalLayout contentContainer;
	private VerticalLayout viewContainer;
	
	public MainFrame(InterfaceCNC interfaceCNC)
	{
		this.interfaceCNC 	= interfaceCNC;
		navigationPanelView = new NavigationPanelView(this, interfaceCNC);
		connectionView		= new ConnectionView(this, interfaceCNC);
		consoleView			= new ConsoleView(interfaceCNC);
		gReaderView			= new GReaderView(this, interfaceCNC);		
			
		//-----Menu----
		title.addStyleName(ValoTheme.MENU_TITLE);
		
		buttonConnectionView = new Button("Connection", e -> interfaceCNC.getNavigator().navigateTo("Connection"));
		buttonConnectionView.addStyleNames(ValoTheme.BUTTON_LINK, ValoTheme.MENU_ITEM);
		buttonConnectionView.setIcon(VaadinIcons.COMPRESS_SQUARE);
        
        buttonConsoleView = new Button("Console", e -> interfaceCNC.getNavigator().navigateTo("Console"));
        buttonConsoleView.addStyleNames(ValoTheme.BUTTON_LINK, ValoTheme.MENU_ITEM);
        buttonConsoleView.setIcon(VaadinIcons.LAPTOP);
        
        buttonGReaderView = new Button("G-Reader", e -> interfaceCNC.getNavigator().navigateTo("G-Reader"));
        buttonGReaderView.addStyleNames(ValoTheme.BUTTON_LINK, ValoTheme.MENU_ITEM);
        buttonGReaderView.setIcon(VaadinIcons.INSERT);
        
        menu = new CssLayout();
        menu.addComponent(title);
        menu.addComponent(buttonConnectionView);
        menu.addComponent(buttonConsoleView);
        menu.addComponent(buttonGReaderView);
        menu.addStyleName(ValoTheme.MENU_ROOT);
        menu.setWidth(180, Unit.PIXELS);
       
        //-----Content-----
        viewContainer = new VerticalLayout();
        viewContainer.setMargin(false);
        viewContainer.setSizeFull();
       
        contentContainer = new VerticalLayout(navigationPanelView, viewContainer);
        contentContainer.setExpandRatio(viewContainer, 5);
        contentContainer.setSizeFull();
        contentContainer.setMargin(new MarginInfo(true, true, true, false));
        contentContainer.setComponentAlignment(navigationPanelView, Alignment.TOP_LEFT);
        contentContainer.setComponentAlignment(viewContainer, Alignment.TOP_LEFT);    
        
        //-----Main-Frame-----
        this.addComponent(menu);
        this.addComponent(contentContainer);
        this.setSizeFull();
        this.setExpandRatio(contentContainer, 5);
        
        //-----Navigator-----
        navigator = new Navigator(interfaceCNC, viewContainer);
        navigator.addView("", DefaultView.class);
        navigator.addView("Connection", connectionView);
        navigator.addView("Console", consoleView);
        navigator.addView("G-Reader", gReaderView);
	}

}
