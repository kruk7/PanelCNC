package logic;

public class StatusManager implements Runnable
{
	private static final int STATUS_MARK = 63;		// initialize question mark, for status mark
	private static final int INTERVAL 	 = 150; 	// the time interval between sending the status (value in milliseconds)
	private boolean status = true;
	
	private Comunicator comunicator = null;

	public StatusManager(Comunicator comunicator)
	{
		this.comunicator = comunicator;
	}
	
	public void turnOFF()
	{
		this.status = false;
	}
	
	// sends a character to the serial port 
	// to receive a report from the cnc machine
	@Override
	public void run()
	{
		while(!Thread.interrupted() && this.status == true)
		{
			comunicator.writeToSerial(STATUS_MARK);
			try
			{
				Thread.sleep(INTERVAL);
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			Thread.yield();
		}
	}
}
