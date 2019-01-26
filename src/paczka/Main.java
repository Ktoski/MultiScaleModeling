package paczka;
import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JFrame;

public class Main 
{
	public static class Okno extends JFrame 
	{
	    public Okno() 
	    {
	    	setPreferredSize(new Dimension(900,650));
	        Panel panel = new Panel();
	        add(panel);
	        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        pack();
	        setVisible(true);
	        setResizable(false); 
	    }
	}
	
	public static void main(String args[]) 
	{
		EventQueue.invokeLater(new Runnable() 
		{
			@Override
	        public void run() 
	        {
				new Okno();
	        }
		});
    }
}
