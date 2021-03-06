package logic;

public class Data
{
	private String packet;

	// True if receiver should wait
	// False if sender should wait
	private boolean transfer = true;

	public synchronized void send(String packet)
	{
		while (!transfer)
		{
			try
			{
				wait();
			} catch (InterruptedException e)
			{
				Thread.currentThread().interrupt();
				e.printStackTrace();
				break;
			}
		}
		transfer = false;

		this.packet = packet;
		notifyAll();
	}

	public synchronized String receive()
	{
		while (transfer)
		{
			try
			{
				wait();
			} catch (InterruptedException e)
			{
				Thread.currentThread().interrupt();
				e.printStackTrace();
				break;
			}
		}
		transfer = true;
		notifyAll();
		return packet;
	}
}