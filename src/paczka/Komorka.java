package paczka;

import java.awt.Color;

public class Komorka 
{
	private int ID;
	private int energia_wew;
	private boolean na_granicy;
	private boolean prev;
	private boolean is_prev;
	private boolean is_next;
	private boolean zywa;
	private boolean sprawdzona;
	private boolean next;
	private boolean dostepna;
	private boolean zrekrystalizowana;
	private double dyslokacja;
	private Color color;
	
	public Komorka(Color color)
	
	{
		this.color = color;
		this.zywa = false;
		this.dyslokacja = 0;
		this.dostepna = true;
		this.zrekrystalizowana = false;
		this.na_granicy = false;
		this.prev = false;
		this.is_prev = false;
		this.next = false;
		this.is_next = false;
		this.energia_wew = 0;
		this.sprawdzona = false;
	}
	
	public Komorka(Color color, boolean zywa)
	{
		this.color = color;
		this.zywa = zywa;
		this.dyslokacja = 0;
		this.zrekrystalizowana = false;
		this.na_granicy = false;
		this.prev = false;
		this.is_prev = false;
		this.next = false;
		this.is_next = false;
		this.energia_wew = 0;
		this.sprawdzona = false;
	}
	
	public boolean isNext()
	{
		return this.next;
	}
	public boolean isPrev()
	{
		return this.prev;
	}
	public boolean isZrekrystalizowana()
	{
		return this.zrekrystalizowana;
	}
	public boolean isDostepna()
	{
		return this.dostepna;
	}
	public boolean isZywa() 
	{
		return this.zywa;
	}
	public boolean isSprawdzona()
	{
		return this.sprawdzona;
	}
	public void sprawdzona(boolean x)
	{
		this.sprawdzona = x;
	}
	public void setID(int x)
	{
		this.ID = x;
	}
	public void setNext(boolean r)
	{
		this.is_next = r;	
	}
	public void setPrev(boolean r)
	{
		this.is_prev = r;	
	}
	public void doNext()
	{
		if(this.is_next)
		{
			this.is_next = false;
			this.next = true;
		}
		else
			this.next = false;
			
	}
	public void doPrev()
	{
		if(this.is_prev)
		{
			this.is_prev = false;
			this.prev = true;
		}
		else
			this.prev = false;
			
	}
	public void setZrekrystalizowana(boolean r)
	{
		this.zrekrystalizowana = r;
	}
	public void setDostepna(boolean av)
	{
		this.dostepna = av;
	}
	public void setZywa(boolean zywa) 
	{
		this.zywa = zywa;
	}
	public void setNa_granicy(boolean x)
	{
		this.na_granicy = x;
	}
	public void setEnergia_wew(int x)
	{
		this.energia_wew = x;
	}
	public int getID()
	{
		return this.ID;
	}
	public int getEnergia_wew()
	{
		return this.energia_wew;
	}
	public boolean isNa_granicy()
	{
		return this.na_granicy;
	}
	public Color getColor() 
	{
		return this.color;
	}

	public void setColor(Color color) 
	{
		this.color = color;
	}
	public double getDyslokacja()
	{
		return this.dyslokacja;
	}
	public void setDyslokacja(double d)
	{
		this.dyslokacja = d;
	}
}