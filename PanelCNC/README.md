PanelCNC
==============

PanelCNC is a user interfence for CNC machine which based on GRBL controller. This application created as WEB App in Java witch Vaadin framework. The application provides basic communication functions between the user and the CNC controller e.g. connecting to the machine, axis control, position display, log delivery, uploading G-code.

<i>Example mian view</i>
![PanelCNC example view](https://user-images.githubusercontent.com/27443559/70094141-2c878700-1622-11ea-8d9c-32ea78514a21.PNG)

![consoleView](https://user-images.githubusercontent.com/27443559/70094490-e7178980-1622-11ea-9118-ae425558fbab.PNG)

![connectionView](https://user-images.githubusercontent.com/27443559/70094546-057d8500-1623-11ea-96b5-1ca8193ea422.PNG)

Workflow
========

To compile the entire project, run "mvn install".

To run the application, run "mvn jetty:run" and open http://localhost:8080/ .

To produce a deployable production mode WAR:
- change productionMode to true in the servlet class configuration (nested in the UI class)
- run "mvn clean package"
- test the war file with "mvn jetty:run-war"

Client-Side compilation
-------------------------

The generated maven project is using an automatically generated widgetset by default. 
When you add a dependency that needs client-side compilation, the maven plugin will 
automatically generate it for you. Your own client-side customizations can be added into
package "client".

Debugging client side code
  - run "mvn vaadin:run-codeserver" on a separate console while the application is running
  - activate Super Dev Mode in the debug window of the application

Developing a theme using the runtime compiler
-------------------------

When developing the theme, Vaadin can be configured to compile the SASS based
theme at runtime in the server. This way you can just modify the scss files in
your IDE and reload the browser to see changes.

To use the runtime compilation, open pom.xml and comment out the compile-theme 
goal from vaadin-maven-plugin configuration. To remove a possibly existing 
pre-compiled theme, run "mvn clean package" once.

When using the runtime compiler, running the application in the "run" mode 
(rather than in "debug" mode) can speed up consecutive theme compilations
significantly.

It is highly recommended to disable runtime compilation for production WAR files.

Using Vaadin pre-releases
-------------------------

If Vaadin pre-releases are not enabled by default, use the Maven parameter
"-P vaadin-prerelease" or change the activation default value of the profile in pom.xml .
