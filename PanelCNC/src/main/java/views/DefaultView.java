package views;

import com.vaadin.navigator.View;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class DefaultView extends VerticalLayout implements View {

	public DefaultView() {
		
        Label topLabel = new Label("Choose the view from the menu bar");
        Label author = new Label("Developed by: Kamil Krukliński");
        Label mail = new Label("Mail: kruk7@vp.pl");
        Label version = new Label("Version: 1.0");
        Label date = new Label("Date: 2019");
        
        addComponent(topLabel);
        addComponent(author);
        addComponent(mail);
        addComponent(version);
        addComponent(date);
      
    }
}
