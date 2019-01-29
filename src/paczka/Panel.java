package paczka;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.nio.*;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;
import javax.swing.*;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

class Panel extends JPanel implements MouseListener
{
	private static final long serialVersionUID = -4304008033447345568L;
	
	private JCheckBox lpd;
	private JCheckBox promien;
	private JCheckBox rekrystalizacja;
	private JCheckBox monte_carlo;
	private JComboBox<String> comboBox;
	private JComboBox<String> comboBox2;
	private JComboBox<String> comboBoxSpace;
	private JComboBox<String> inclusionSelect;
	private JComboBox<String> importSelect;
	private JButton okButton;
	private JButton clearButton;
	private JButton showButton;
	private JButton eksportButton;
	private JButton importButton;
	private JButton boundriesColoringButton;
	private JButton startWithInclusions;
	private JTextArea widthArea;
	private JTextArea heightArea;
	private JTextArea numberOfColorsArea;
	private JTextArea numberOfInclusionsArea;
	private JTextArea inclusionRadiusArea;
	private JLabel widthLabel;
	private JLabel heightLabel;
	private JLabel numberOfColorsLabel;
	private JLabel numberOfInclusionsLabel;
	private JLabel inclusionRadiusLabel;

	private static final double A =  86710969050178.5f;
	private static final double B =  9.41f;
	private static final double crit = 80208201.57f;

	public static int licznikWywolan;
	
	public static final int rozmiar = 500;
	public static int height;
	public static int width;
	private double t = 0.0f;
	private Random rand = new Random();
	private Thread th;
	private int IDk;
	public static int numberOfColors;
	public static int numberOfInclusions = 5;
	public static int inclusionRadius = 10;

	static BufferedImage img;
	
	private Komorka [][] tab1 = new Komorka[rozmiar][rozmiar];
	private Komorka [][] tab2 = new Komorka[rozmiar][rozmiar];
	
	private boolean set_borders;
	
	private Runnable run = new Runnable()
	{
		boolean running = true;
		boolean rekrystalizacja = false;
		boolean creating = false;
		boolean monte_carloc = false;
		@Override
		public void run() 
		{
			//Panel.this.set_begining3();  // TODO For testing maybe remove

			Panel.this.set_beging_iclusion_start();
			while(true)
			{
				if(monte_carlo.isSelected())
					monte_carloc = false;
				if(running)
				{
					running = false;
					creating = false;
					rekrystalizacja = false;
					
					for(int i = 0; i < width; i++)
						for(int j = 0; j < height; j++)
							if(!tab1[i][j].isZywa())
							{
								creating = true;
								break;
							}
					if(!creating && !monte_carloc)
					{
						if(set_borders==true)
						{
							borders();
							set_borders = false;
						}
						
						paczka();
						//rysowanie_drugie();
						
						for(int i = 0 ; i < width ; i++)
							for(int j = 0 ; j < height ; j++)
								if(!tab1[i][j].isZrekrystalizowana())
								{
									rekrystalizacja = false;
									break;
								}
						
					}
					if(!creating && monte_carloc)
					{
						zmien_spr();
						borders();
						ustaw_ener();
						monte_carlo();
					}
				}
				try 
				{
					Thread.sleep(5);
				} 
				catch (InterruptedException e) 
				{
					System.out.println("th exception");
					e.printStackTrace();
				}
					running = true;

				if(creating) {

					Panel.this.rysowanie();
				}
				Panel.this.repaint();

			}
		}


	};
	private JLabel lblRodzajSsiedztwa;
	private JLabel lblRodzajRozmieszczenia;
	private JLabel lblOdstp;
	
	private void borders()
	{
		for(int i = 0 ; i < width ; i++)
			for(int j = 0 ; j < height ; j++)
			{
				List<Komorka> sasiedzi = daj_sasiadow(i, j);
				boolean border = false;
				for(int k = 0 ; k < sasiedzi.size() ; k++)
					if(sasiedzi.get(k).getColor() != tab1[i][j].getColor())
					{
						border = true;
						tab1[i][j].setNa_granicy(border);
						break;
					}
			}
	}
	private void paczka()
	{
		double k_dys;										
		double temp_pula = (A/B) + ( (1 - ( A/B) ) * Math.pow( Math.E , ( -B * this.t ) ) );
		this.t = this.t +  0.005;
		double pula = (A/B) + ( (1 - ( A/B) ) * Math.pow( Math.E , ( -B * this.t ) ) );
		pula = pula - temp_pula;
		if(pula < 0)
			System.out.println(pula);
		
		double psr = pula/(width*height);

		for(int i = 0 ; i < width ; i++)
			for(int j = 0 ; j < height ;j++)
			{
				k_dys = tab1[i][j].getDyslokacja();
				int x;
				if(tab1[i][j].isNa_granicy())
					x = rand.nextInt(110)+71;
				else
					x = rand.nextInt(30)+1;
			
				k_dys = k_dys + psr*(x/100);
				tab1[i][j].setDyslokacja(k_dys);
				pula = pula - k_dys;
			}
		if(pula != 0)									
		{
			psr = pula/(width*height);
			for(int i = 0; i < width ; i++)
				for(int j = 0 ; j < height ; j++)
				{
					double temp = tab1[i][j].getDyslokacja();
					tab1[i][j].setDyslokacja(temp + psr);
					pula = pula - psr;
				}
		}
	}	
	private Color losuj_kolor()
	{
		int x = rand.nextInt(7);
		Color kolor = Color.GRAY;
		switch(x)
		{
		case 0:
			kolor = Color.RED;
			break;
		case 1:
			kolor = Color.GREEN;
			break;
		case 2:
			kolor = Color.BLUE;
			break;
		case 3:
			kolor = Color.YELLOW;
			break;
		case 4:
			kolor = Color.PINK;
			break;
		case 5:
			kolor = Color.ORANGE;
			break;
		case 6:
			kolor = Color.MAGENTA;
			break;
		}
		return kolor;
	}
	private void rysowanie_drugie()
	{	
		for(int i = 0 ; i < width ; i++)
			for(int j = 0 ; j < height ; j++)
			{
				tab1[i][j].doPrev();
				tab1[i][j].doNext();
			}
		
		for(int i = 0 ; i < width ; i++)
			for(int j = 0 ; j < height ; j++)
			{
				
				if(tab1[i][j].isPrev())
					tab1[i][j].setPrev(false);
				
				if(!tab1[i][j].isZrekrystalizowana())
				{
					if(tab1[i][j].getDyslokacja() > crit)
					{
						tab1[i][j].setZrekrystalizowana(true);
						tab1[i][j].setDyslokacja(0);
						Color actual = tab1[i][j].getColor();
						Color new_color;
						
						do
							new_color = losuj_kolor();
						while(new_color == actual);
						
						tab1[i][j].setColor(new_color);
						tab1[i][j].setPrev(true);
						
						List<Komorka> sasiedzi = daj_sasiadow(i, j);
						for(int k = 0 ; k < sasiedzi.size() ; k++)
							if(!sasiedzi.get(k).isZrekrystalizowana())
								sasiedzi.get(k).setNext(true);
					}
					else
					{
						if(tab1[i][j].isNext())
						{
							Color kolor;
							List<Komorka> sasiedzi = daj_sasiadow(i, j);
							for(int k = 0 ; k < sasiedzi.size() ; k++)
								if(sasiedzi.get(k).isZrekrystalizowana())
								{
									kolor = sasiedzi.get(k).getColor();
									tab1[i][j].setColor(kolor);
									tab1[i][j].setZrekrystalizowana(true);
									tab1[i][j].setDyslokacja(0);
									tab1[i][j].setPrev(true);
									tab1[i][j].setNext(false);
									
									for(int h = 0 ; h < sasiedzi.size() ; h++)
										if(!sasiedzi.get(h).isZrekrystalizowana())
											sasiedzi.get(h).setNext(true);
									
								break;
								}							
						}
					}
				}
			}
		}
	private void ustaw_ener()
	{
		for(int i = 0 ; i < width ; i++)
			for(int j = 0 ; j < height ; j++)
			{
				if(tab1[i][j].isNa_granicy())
				{
					List<Komorka> sasiedzi = daj_sasiadow(i, j);
					int temp = sprawdz_ener( tab1[i][j].getColor(), tab1[i][j].getID(), sasiedzi );
					tab1[i][j].setEnergia_wew( temp );
					tab1[i][j].sprawdzona(false);
				}
				else
				{
					tab1[i][j].setEnergia_wew(0);
					tab1[i][j].sprawdzona(true);
				}
			}
	}
	private int sprawdz_ener(Color kolor, int id, List<Komorka> sasiedzi)
	{
		int wynik = 0;
		for(int i = 0 ; i < sasiedzi.size() ; i++)
			if( ( kolor != sasiedzi.get(i).getColor() ) || ( id != sasiedzi.get(i).getID() ) )
				wynik++;
		return wynik;
	}
	private Komorka min_ener(Komorka actual, List<Komorka> sasiedzi)				
	{
		int energy = actual.getEnergia_wew();
		Color kolor = losuj_kolor();
		int temp_e = sprawdz_ener(kolor, actual.getID(), sasiedzi);
		if(temp_e <= energy)
		{
			actual.setEnergia_wew(temp_e);
			actual.setColor(kolor);
		}	
		return actual;
	}
	private void zmien_spr()
	{
		for(int i = 0 ; i < width ; i++)
			for(int j = 0 ; j < height ; j++)
				tab1[i][j].sprawdzona(false);
	}
	private void monte_carlo()
	{
		for(int i = 0 ; i < width ; i ++)
			for(int j = 0 ; j < height ; j++)
			{
				if(tab1[i][j].isSprawdzona())
					continue;
				else
				{
					List<Komorka> sasiedzi = daj_sasiadow(i, j);
					tab1[i][j] = min_ener(tab1[i][j], sasiedzi);
					tab1[i][j].sprawdzona(true);
				}
	
			}
	}	
	public Panel()
	{
		super();
		this.set_borders = true;
		this.IDk = 0;
		for(int i = 0 ; i < rozmiar ; i++)
			for(int j = 0 ; j < rozmiar ; j++)
				tab1[i][j] = new Komorka(Color.WHITE);
		
		comboBox = new JComboBox<String>();
		comboBox.setBounds(613, 11, 127, 20);

		inclusionSelect = new JComboBox<String>();
		inclusionSelect.setBounds(720, 330, 127, 20);
		inclusionSelect.addItem("Rectangle");
		inclusionSelect.addItem("Circle");

		importSelect = new JComboBox<String>();
		importSelect.setBounds(720, 390, 127, 20);
		importSelect.addItem("BitMap");
		importSelect.addItem("Text");


		heightArea = new JTextArea();
		widthArea = new JTextArea();
		numberOfColorsArea = new JTextArea();
		numberOfInclusionsArea = new JTextArea();
		inclusionRadiusArea = new JTextArea();
		heightLabel = new JLabel("Height");
		widthLabel = new JLabel("Width");
		numberOfColorsLabel = new JLabel("Number of Grains (max 8)");
		numberOfInclusionsLabel = new JLabel("Number of Inclusions)");
		inclusionRadiusLabel = new JLabel("Radius of Inclusion");

		
		comboBox.addItem("Moore");
		comboBox.addItem("Moore Further");
		comboBox.addItem("Von Neumann");
		comboBox.addItem("Pentagonalne");
		comboBox.addItem("heksagonalne lewe");
		comboBox.addItem("heksagonalne prawe");
		comboBox.addItem("heksagonalne losowe");
		
		comboBox2 = new JComboBox<String>();
		comboBox2.setBounds(613, 40, 127, 20);
		comboBox2.addItem("losowe");
		comboBox2.addItem("rownomierne");
		comboBoxSpace = new JComboBox<String>();
		comboBoxSpace.setBounds(613, 71, 127, 20);
		comboBoxSpace.addItem("10");
		comboBoxSpace.addItem("20");
		comboBoxSpace.addItem("30");
		comboBoxSpace.addItem("40");
		comboBoxSpace.addItem("50");
		comboBoxSpace.addItem("60");
		comboBoxSpace.addItem("70");
		comboBoxSpace.addItem("80");
		comboBoxSpace.addItem("90");
		comboBoxSpace.addItem("100");

		/*heightArea.addComponentListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				heightArea.getText();

			}});*/
		
		okButton = new JButton("START");
		okButton.setBounds(613, 270, 87, 23);
		okButton.addActionListener(new ActionListener() 
		{
			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				height = Integer.parseInt(heightArea.getText());
				width = Integer.parseInt(widthArea.getText());
				numberOfColors = Integer.parseInt(numberOfColorsArea.getText());
				compute();
			}
		});
		clearButton = new JButton("RESET");
		clearButton.setBounds(613, 300, 87, 23);
		clearButton.addActionListener(new ActionListener() 
		{
			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				clear();
			}
		});
		showButton = new JButton("INCLUSION");
		showButton.setBounds(613, 330, 100, 23);
		showButton.addActionListener(new ActionListener() 
		{
			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				//set_begining();
				//height = Integer.parseInt(heightArea.getText());
				//width = Integer.parseInt(widthArea.getText());
				numberOfInclusions = numberOfInclusionsArea.getText().equalsIgnoreCase("") ?  5 : Integer.parseInt(numberOfInclusionsArea.getText());
				inclusionRadius = inclusionRadiusArea.getText().equalsIgnoreCase("") ?  10 : Integer.parseInt(inclusionRadiusArea.getText());
				set_begining_inclusion();
			}
		});


		eksportButton = new JButton("EKSPORT");
		eksportButton.setBounds(613, 360, 100, 23);
		eksportButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
				System.out.println("Eksport");
				for(int i = 0; i < width; i++){
					for(int j = 0; j < height; j++) {
						img.setRGB(i, j, tab1[i][j].getColor().getRGB());
					}
				}
				savePNG(img, "results.bmp");
				eksportToTextFile(tab1);
			}
		});

		importButton = new JButton("IMPORT");
		importButton.setBounds(613, 390, 100, 23);
		importButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				clear_panel();
				importImage();
			}
		});

		boundriesColoringButton = new JButton("KOLORUJ GRANICE");
		boundriesColoringButton.setBounds(613, 95, 150, 23);
		boundriesColoringButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				clear_panel();
				boundriesColoring();
			}
		});

		startWithInclusions = new JButton("START WITH INCLUSIONS");
		startWithInclusions.setBounds(613, 70, 150, 23);
		startWithInclusions.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				height = Integer.parseInt(heightArea.getText());
				width = Integer.parseInt(widthArea.getText());
				numberOfColors = Integer.parseInt(numberOfColorsArea.getText());
				numberOfInclusions = numberOfInclusionsArea.getText().equalsIgnoreCase("") ?  5 : Integer.parseInt(numberOfInclusionsArea.getText());
				inclusionRadius = inclusionRadiusArea.getText().equalsIgnoreCase("") ?  10 : Integer.parseInt(inclusionRadiusArea.getText());
				compute();
			}
		});

		lpd = new JCheckBox("Losowanie podczas dzialania");
		lpd.setSelected(true);
		lpd.setBounds(613, 102, 211, 23);
		promien = new JCheckBox("Z promieniem");
		promien.setBounds(613, 128, 127, 23);
		rekrystalizacja = new JCheckBox("Rekrystalizacja");
		rekrystalizacja.setBounds(613, 156, 127, 23);
		monte_carlo = new JCheckBox("Monte Carlo");
		monte_carlo.setBounds(613, 182, 127, 23);
		heightArea.setBounds(613, 210, 150,25);
		heightArea.setFont(new Font("pb", 1, 11));
		widthArea.setBounds(613, 240, 150,25);
		widthArea.setFont(new Font("pb", 1, 11));
		heightLabel.setBounds(800, 192, 50, 50);
		heightLabel.setText("Height");
		widthLabel.setBounds(800, 226, 50, 50);
		widthLabel.setText("Width");
		numberOfColorsArea.setBounds(613, 140,75,25);
		numberOfColorsArea.setFont(new Font("pb", 1, 11));
		numberOfColorsLabel.setBounds(697, 140, 150, 25);
		numberOfColorsLabel.setText("Number of Grains");
		numberOfInclusionsArea.setBounds(613, 170,75,25);
		numberOfInclusionsArea.setFont(new Font("pb", 1, 11));
		numberOfInclusionsLabel.setBounds(697, 170, 185, 25);
		numberOfInclusionsLabel.setText("Number of Inclusions (default 5)");
		inclusionRadiusArea.setBounds(613, 420,75,25);   //613, 360, 100, 23
		inclusionRadiusArea.setFont(new Font("pb", 1, 11));
		inclusionRadiusLabel.setBounds(697, 420, 200, 25);
		inclusionRadiusLabel.setText("Radius of Inclusion (default 10)");

		setLayout(null);
		add(clearButton);
		add(okButton);
		add(widthArea);
		add(heightArea);
		add(heightLabel);
		add(widthLabel);
		add(numberOfColorsArea);
		add(numberOfColorsLabel);
		add(numberOfInclusionsArea);
		add(numberOfInclusionsLabel);
		add(inclusionRadiusArea);
		add(inclusionRadiusLabel);
		add(comboBox);
		add(startWithInclusions);
		//add(comboBox2);
		//add(comboBoxSpace);
		add(showButton);
		add(eksportButton);
		add(importButton);
		add(inclusionSelect);
		add(importSelect);
		add(boundriesColoringButton);
		//add(lpd);                   //temporary remevd from GUI
		//add(promien);
		//add(rekrystalizacja);
		//add(monte_carlo);
		addMouseListener(this);
		
		lblRodzajSsiedztwa = new JLabel("Rodzaj S\u0105siedztwa");
		lblRodzajSsiedztwa.setBounds(750, 14, 107, 14);
		add(lblRodzajSsiedztwa);
		
		lblRodzajRozmieszczenia = new JLabel("Rodzaj Rozmieszczenia");
		lblRodzajRozmieszczenia.setBounds(750, 43, 147, 14);
		//add(lblRodzajRozmieszczenia);                                           // label rodzaj rozmieszczenia
		
		lblOdstp = new JLabel("Odst\u0119p");
		lblOdstp.setBounds(750, 74, 93, 14);
		//add(lblOdstp);                                                          // label odstep
		
	}
	protected Color rand_color()
	{
		Color kolor;
		
		//int x = rand.nextInt(200);
		int x = rand.nextInt(numberOfColors);

		kolor = Color.WHITE;
		switch(x)
		{
		case 0:
			kolor = Color.GREEN;
			break;
		case 1:
			kolor = Color.BLUE;
			break;
		case 2:
			kolor = Color.RED;
			break;
		case 3:
			kolor = Color.ORANGE;
			break;
		case 4:
			kolor = Color.PINK;
			break;
		case 5:
			kolor = Color.YELLOW;
			break;
		case 6:
			kolor = Color.MAGENTA;
			break;
		case 7:
			kolor = Color.CYAN;
			break;
		case 8:
			kolor = Color.GRAY;
			break;
		}

		return kolor;
	}

	protected void importImage(){

		//clear_panel();

		String importType = (String) importSelect.getSelectedItem();

		if(importType.equalsIgnoreCase("bitmap")) {

			File bmpFile = new File("fileToImport.bmp");
			BufferedImage image;
			try {
				image = ImageIO.read(bmpFile);
				int importWidth = image.getWidth();
				int importHeight = image.getHeight();
				width = importWidth;
				height = importHeight;

				for (int i = 0; i < importWidth; i++) {
					for (int j = 0; j < importHeight; j++) {
						tab1[i][j].setColor(Color.decode(String.valueOf(image.getRGB(i, j))));
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else if(importType.equalsIgnoreCase("text")) {
			importFromTextFile();
		}
	}

	private void importFromTextFile(){

		Path pathToTextFile = Paths.get("textFileToImport.txt");
		try {

			 Files.lines(pathToTextFile).map(line ->  line.split(" ")).forEach(column ->{
				 tab1[Integer.parseInt(column[0])][Integer.parseInt(column[1])].setColor(Color.decode(column[3]));
			 });

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected int rand_id(){
		//int rand_id = rand.nextInt(200);
		int rand_id = rand.nextInt(numberOfColors);
		return rand_id;           //TODO changes in new id
	}


	protected Color generateRandColor(){

		int r = ThreadLocalRandom.current().nextInt(0, 250 + 1);
		int g = ThreadLocalRandom.current().nextInt(0, 250 + 1);
		int b = ThreadLocalRandom.current().nextInt(0, 250 + 1);

		Color kolor = new Color(r,g,b);
		return kolor;
	}

	protected void set_begining_inclusion(){

		String inclusionType = (String) inclusionSelect.getSelectedItem();

		if(inclusionType.equalsIgnoreCase("rectangle")) {
			for (int i = 0; i < numberOfInclusions; i++) {

				int x_rand = rand.nextInt(width);
				int y_rand = rand.nextInt(height);
				drawRectangle(x_rand, y_rand, inclusionRadius);
			}

		}else if(inclusionType.equalsIgnoreCase("circle")) {

			for (int i = 0; i < numberOfInclusions; i++) {

				int x_rand = rand.nextInt(width);
				int y_rand = rand.nextInt(height);
				//TODO if only border check is set then...
				if (tab1[x_rand][y_rand].isNa_granicy()) {
					tab1[x_rand][y_rand].setColor(Color.BLACK);
					drawCircle(x_rand, y_rand, inclusionRadius);
				}
			}
		}
	}

	protected void set_beging_iclusion_start(){

		for (int i = 0; i < numberOfInclusions; i++) {

			int x_rand = rand.nextInt(width);
			int y_rand = rand.nextInt(height);
			drawRectangle(x_rand, y_rand, inclusionRadius);
		}


		for(int i = 0; i < numberOfColors; i++){
			int xrw = rand.nextInt(width);
			int yrw = rand.nextInt(height);
			if(!tab1[xrw][yrw].isZywa()) {
				Color kolor;
				int new_id;
				do {
					//kolor = rand_color();
					kolor = generateRandColor();                  // TO REMOVE !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
					new_id = rand_id();                     //TODO test new ids
				}
				//while(kolor == Color.WHITE);
				while (kolor.equals(Color.WHITE));
				tab1[xrw][yrw].setColor(kolor);
				tab1[xrw][yrw].setID(new_id);                 //TODO test new ids
				tab1[xrw][yrw].setZywa(false);
				//tab1[xrw][yrw].setZywa(true);

			}

		}


	}


	void drawCircle(int x, int y, int r){

		for(int x1 = -r; x1 < r; x1++){
			int heightTemp = (int)Math.sqrt(r * r - x1 * x1);
			for (int y1 = -heightTemp; y1 < heightTemp; y1++){
				if ((x + x1 > 0 && x + x1 < height) && (y + y1 > 0 && y + y1 < width) )
					tab1[x + x1][y + y1].setColor(Color.BLACK);
			}
		}
	}

	void drawRectangle(int x, int y, int diameter){

		int half = diameter / 2;

		int beg_x = x - half;
		int beg_y = y - half;

		double side = (double)diameter / Math.sqrt(2);
		int side_int = (int)side;

		for(int i = beg_x; i < beg_x + side_int; i++){
			for(int j = beg_y; j < beg_y + side_int; j++){
				tab1[i][j].setColor(Color.BLACK);
			}
		}
	}

	protected void clear_panel(){
		for(int i = 0; i < 400; i++){
			for(int j = 0; j < 400; j++){
				tab1[i][j].setColor(Color.WHITE);
			}
		}

	}

	private void boundriesColoring(){

		for(int i = 0; i < width; i++){
			for(int j = 0; j < height; j++){
				if(tab1[i][j].isNa_granicy())
					tab1[i][j].setColor(Color.BLACK);
				else
					tab1[i][j].setColor(Color.WHITE);
			}
		}

	}


	protected void set_begining()
	{
		clear();
		String losowanie = (String) comboBox2.getSelectedItem();
		
		if(losowanie.equalsIgnoreCase("losowe"))
		{
			for(int i = 0 ; i < width ; i++)
			{
				for(int j = 0 ; j < height ; j++)
				{	
					tab1[i][j].setColor(rand_color());
					//tab1[i][j].setColor(generateRandColor());                                  // TO REMOVE !!!!!!!!!!!!!!!!!!!!
					if(tab1[i][j].getColor() == Color.WHITE)
						tab1[i][j].setZywa(false);
					else
					{
						this.IDk++;
						tab1[i][j].setID(IDk);
						tab1[i][j].setZywa(true);
						if(promien.isSelected())
						{
							if(tab1[i][j].isDostepna())
							{
								tab1[i][j].setDostepna(false);
								String os = (String) comboBoxSpace.getSelectedItem();
								Integer oi = Integer.valueOf(os);
								int ix;
								int jx;
								int ni;
								int nj;
								if(i - oi >= 0)
									ix = i - oi;
								else
									ix = 0;
								
								if(j - oi >= 0)
									jx = j - oi;
								else
									jx = 0;
								
								if(i + oi <= width)
									ni = i + oi;
								else
									ni = width;
								
								if(j + oi <= height)
									nj = j + oi;
								else
									nj = height;
								for(int ii = ix; ii < ni ; ii++)
									for(int jj = jx; jj < nj ; jj++)
										if(Math.sqrt( Math.pow( i - ii, 2) + Math.pow( j - jj, 2) ) <= oi)
											tab1[ii][jj].setDostepna(false);
							}
							else
							{
								tab1[i][j].setColor(Color.WHITE);
								tab1[i][j].setZywa(false);
							}
						}
					}	
				}
			}
		}
		if(losowanie.equalsIgnoreCase("rownomierne"))
		{
			String os = (String) comboBoxSpace.getSelectedItem();
			Integer oi = Integer.valueOf(os);
			for(int i = 0 ; i < rozmiar ; i+=oi)
				for(int j = 0 ; j < rozmiar ; j+=oi)
				{
					Color kolor;
					do
					{
						//kolor = rand_color();
						kolor = generateRandColor();                // TO REMOVE !!!!!!!!
					}
					while(kolor == Color.WHITE);
					this.IDk++;
					tab1[i][j].setID(IDk);
					tab1[i][j].setZywa(true);
					tab1[i][j].setDostepna(false);
					tab1[i][j].setColor(kolor);
				}
		}
		repaint();
	}


	/*private static BufferedImage map( int sizeX, int sizeY, Color color ,int x, int y, BufferedImage res ){
		res.setRGB(x, y, color.getRGB());
		return res;
	}*/

	private static void savePNG(final BufferedImage bi, final String path ){
		try {
			RenderedImage rendImage = bi;
			ImageIO.write(rendImage, "bmp", new File(path));
		} catch ( IOException e) {
			e.printStackTrace();
		}
	}

	protected void clear()
	{
		this.set_borders = true;
		this.t = 0.0f;
		for(int i = 0 ; i < width ; i++)
			for(int j = 0 ; j < height ; j++)
			{
				this.IDk = 0;
				tab1[i][j].setID(0);
				tab1[i][j].setZywa(false);
				tab1[i][j].setDostepna(true);
				tab1[i][j].setColor(Color.WHITE);
				tab1[i][j].setNa_granicy(false);
				tab1[i][j].setDyslokacja(0);
				tab1[i][j].setZrekrystalizowana(false);
				tab1[i][j].setNext(false);
				tab1[i][j].setPrev(false);
				tab1[i][j].doNext();
				tab1[i][j].doPrev();
			}
		repaint();
	}

	protected void set_begining3(){

		for(int i = 0; i < numberOfColors; i++){
			int xrw = rand.nextInt(width);
			int yrw = rand.nextInt(height);
			if(!tab1[xrw][yrw].isZywa()) {
				Color kolor;
				int new_id;
				do {
					//kolor = rand_color();
					kolor = generateRandColor();                  // TO REMOVE !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
					new_id = rand_id();                     //TODO test new ids
				}
				//while(kolor == Color.WHITE);
				while (kolor.equals(Color.WHITE));
				tab1[xrw][yrw].setColor(kolor);
				tab1[xrw][yrw].setID(new_id);                 //TODO test new ids
				tab1[xrw][yrw].setZywa(false);
				//tab1[xrw][yrw].setZywa(true);

			}

		}

	}


	protected void rysowanie() 
	{

			/*if (lpd.isSelected()) {
				licznikWywolan++;
				System.out.println("licznik " + licznikWywolan);
				System.out.println("lpd is selected");
				int xrw = rand.nextInt(width);
				int yrw = rand.nextInt(height);

				if (!tab1[xrw][yrw].isZywa()) {
					Color kolor;
					int new_id;
					do {
						kolor = rand_color();
						//kolor = generateRandColor();                  // TO REMOVE !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
						new_id = rand_id();                     //TODO test new ids
					}
					//while(kolor == Color.WHITE);
					while (kolor.equals(Color.WHITE));
					tab1[xrw][yrw].setColor(kolor);
					tab1[xrw][yrw].setID(new_id);                 //TODO test new ids
					tab1[xrw][yrw].setZywa(false);

				}
			}*/

		/*int xrw = rand.nextInt(width);
		int yrw = rand.nextInt(height);
		if(!tab1[xrw][yrw].isZywa()) {
			int new_id;
			do
			{
				tab1[xrw][yrw].setColor(generateRandColor());
				new_id = rand_id();
			}while(tab1[xrw][yrw].getColor().equals(Color.WHITE));
			tab1[xrw][yrw].setZywa(false);
		}*/


		int id;
		Color color;
		for(int i = 0; i < width; i++)
			for(int j = 0; j < height; j++)
			{
				List<Komorka> sasiedzi = daj_sasiadow(i, j);
				//color = zmienStan(tab1[i][j], sasiedzi);
				color = zmienStan2(tab1[i][j], sasiedzi);
				System.out.println("new color " + color);

				//if(Color.WHITE == color) {
				if(color.equals(Color.WHITE)) {
					tab2[i][j] = new Komorka(color);
					tab2[i][j].setZywa(false);
				}else
				{
					tab2[i][j] = new Komorka(color, true);
					for(int k = 0 ; k < sasiedzi.size() ; k++)
						if( color == sasiedzi.get(k).getColor() )
						{
							id = sasiedzi.get(k).getID();
							tab2[i][j].setID(id);
							break;
						}
				}
			}
		
		for(int i = 0; i < width; i++)
			for(int j = 0; j < height; j++)
				tab1[i][j] = tab2[i][j];

	}


private List<Komorka> daj_sasiadow(int x , int y)					
	{
		List<Komorka> sasiedzi = new ArrayList<Komorka>();
		
		int x1;
		int x2;
		int y1;
		int y2;
		int rodzaj;
		String sasiedztwo = (String) comboBox.getSelectedItem();
		
		if(x == width - 1)
		{
			x1 = x - 1;
			x2 = 0;
		} 
		else if(x == 0)
		{
			x1 = width - 1;
			x2 = x + 1;
		} 
		else
		{
			x1 = x - 1;
			x2 = x + 1;
		}
		
		if(y == height - 1)
		{
			y1 = y - 1;
			y2 = 0;
		} 
		else if(y == 0)
		{
			y1 = height - 1;
			y2 = y + 1;
		} 
		else
		{
			y1 = y - 1;
			y2 = y + 1;
		}
		
		if(sasiedztwo.equalsIgnoreCase("Moore"))
		{
			sasiedzi.add(tab1[x1][y1]);
			sasiedzi.add(tab1[x1][y]);
			sasiedzi.add(tab1[x1][y2]);
			sasiedzi.add(tab1[x][y2]);
			sasiedzi.add(tab1[x2][y2]);
			sasiedzi.add(tab1[x2][y]);
			sasiedzi.add(tab1[x2][y1]);
			sasiedzi.add(tab1[x][y1]);
		}
		else if(sasiedztwo.equalsIgnoreCase("Moore Further"))
		{
			sasiedzi.add(tab1[x1][y2]);
			sasiedzi.add(tab1[x2][y2]);
			sasiedzi.add(tab1[x1][y1]);
			sasiedzi.add(tab1[x2][y1]);
		}
		else if( sasiedztwo.equalsIgnoreCase("Von Neumann"))
		{
			sasiedzi.add(tab1[x1][y]);
			sasiedzi.add(tab1[x][y2]);
			sasiedzi.add(tab1[x2][y]);
			sasiedzi.add(tab1[x][y1]);
		} 
		else if(sasiedztwo.equalsIgnoreCase("pentagonalne"))
		{
			rodzaj = rand.nextInt(4);
			if(rodzaj == 0)
			{
				sasiedzi.add(tab1[x1][y1]);
				sasiedzi.add(tab1[x1][y]);
				sasiedzi.add(tab1[x1][y2]);
				sasiedzi.add(tab1[x][y2]);
				sasiedzi.add(tab1[x][y1]);
			}
			if(rodzaj == 1)
			{
				sasiedzi.add(tab1[x][y2]);
				sasiedzi.add(tab1[x2][y2]);
				sasiedzi.add(tab1[x2][y]);
				sasiedzi.add(tab1[x2][y1]);
				sasiedzi.add(tab1[x][y1]);
			}
			if(rodzaj == 2)
			{
				sasiedzi.add(tab1[x1][y1]);
				sasiedzi.add(tab1[x1][y]);
				sasiedzi.add(tab1[x2][y]);
				sasiedzi.add(tab1[x2][y1]);
				sasiedzi.add(tab1[x][y1]);
			} if(rodzaj == 3)
			{
				sasiedzi.add(tab1[x1][y]);
				sasiedzi.add(tab1[x1][y2]);
				sasiedzi.add(tab1[x][y2]);
				sasiedzi.add(tab1[x2][y2]);
				sasiedzi.add(tab1[x2][y]);
			}
		} 
		else if(sasiedztwo.equalsIgnoreCase("heksagonalne prawe"))
		{
			sasiedzi.add(tab1[x1][y]);
			sasiedzi.add(tab1[x1][y2]);
			sasiedzi.add(tab1[x][y2]);
			sasiedzi.add(tab1[x2][y]);
			sasiedzi.add(tab1[x2][y1]);
			sasiedzi.add(tab1[x][y1]);
		} 
		else if(sasiedztwo.equalsIgnoreCase("heksagonalne lewe"))
		{
			sasiedzi.add(tab1[x1][y1]);
			sasiedzi.add(tab1[x1][y]);
			sasiedzi.add(tab1[x][y2]);
			sasiedzi.add(tab1[x2][y2]);
			sasiedzi.add(tab1[x2][y]);
			sasiedzi.add(tab1[x][y1]);
		} 
		else if(sasiedztwo.equalsIgnoreCase("heksagonalne losowe"))
		{
			int rodzaj1 = rand.nextInt(2);
			if(rodzaj1 == 0)
			{
				sasiedzi.add(tab1[x1][y1]);
				sasiedzi.add(tab1[x1][y]);
				sasiedzi.add(tab1[x][y2]);
				sasiedzi.add(tab1[x2][y2]);
				sasiedzi.add(tab1[x2][y]);
				sasiedzi.add(tab1[x][y1]);
			} 
			else if(rodzaj1 == 1)
			{
				sasiedzi.add(tab1[x1][y]);
				sasiedzi.add(tab1[x1][y2]);
				sasiedzi.add(tab1[x][y2]);
				sasiedzi.add(tab1[x2][y]);
				sasiedzi.add(tab1[x2][y1]);
				sasiedzi.add(tab1[x][y1]);
			}
		}
		return sasiedzi;
	}
	private void compute()
	{
		th = new Thread(run);
		th.start();
	}
	private Color zmienStan(Komorka komorka, List<Komorka> sasiedzi)			
	{
		if(komorka.isZywa())
			return komorka.getColor();
		
		int green = 0;
		int red = 0;
		int blue = 0;
		int orange = 0;
		int pink = 0;
		int yellow = 0;
		int cyan = 0;
		int gray = 0;
		int magenta = 0;
		
		for(Komorka sasiad: sasiedzi)
		{
			if(sasiad.getColor() == Color.GREEN)
				green++;
			else if(sasiad.getColor() == Color.RED)
				red++;
			else if(sasiad.getColor() == Color.ORANGE)
				orange++;
			else if(sasiad.getColor() == Color.BLUE)
				blue++;
			else if(sasiad.getColor() == Color.PINK)
			{
				pink++;
			} 
			else if(sasiad.getColor() == Color.YELLOW)
				yellow++; 
			else if(sasiad.getColor() == Color.CYAN)
				cyan++;
			else if(sasiad.getColor() == Color.GRAY)
				gray++; 
			else if(sasiad.getColor() == Color.MAGENTA)
				magenta++; 
		}
		if(green == 0 && red == 0 && orange == 0 && blue == 0 && pink == 0 && yellow == 0 && cyan == 0 && gray == 0 && magenta == 0)
			return Color.WHITE;
		
		int [] tabk = new int [9];
		tabk[0] = green;
		tabk[1] = red;
		tabk[2] = orange;
		tabk[3] = blue;
		tabk[4] = pink;
		tabk[5] = yellow;
		tabk[6] = magenta;
		tabk[7] = cyan;
		tabk[8] = gray;
		int max = Math.max(tabk[0], tabk[1]);
		
		for(int i = 2 ; i < 9 ; i++)
			max = Math.max(max, tabk[i]);

		
		if(max == orange)
			return Color.ORANGE;
		else 
			if(max == blue)
			return Color.BLUE; 
		else 
			if(max == red)
			return Color.RED; 
		else 
			if(max == green)
			return Color.GREEN;
		else 
			if(max == pink)
			return Color.PINK;
		else 
			if(max == yellow)
			return Color.YELLOW;
		else 
			if(max == cyan)
			return Color.CYAN;
		else 
			if(max == gray)
			return Color.GRAY;
		else 
			if(max == magenta)
			return Color.MAGENTA;
		else
			return Color.GREEN;
	}

	protected Color zmienStan2(Komorka komorka, List<Komorka> sasiedzi){

		if(komorka.isZywa())
			return komorka.getColor();

		Color colorResult;

		List<Color> listOfNeighbourColors = new ArrayList<>();
		for(Komorka sasiad: sasiedzi) {
			listOfNeighbourColors.add(sasiad.getColor());
		}

		Map<Color, Long> occurrences =
				listOfNeighbourColors.stream().collect(Collectors.groupingBy(w -> w, Collectors.counting()));

		occurrences.remove(Color.WHITE);
		occurrences.remove(Color.BLACK);
		if(occurrences.size() == 0)
			occurrences.put(komorka.getColor(),1l);
		long max = Collections.max(occurrences.values());

		colorResult = getKeysByValue(occurrences, max);
		return colorResult;
	}

	public <T, E> T getKeysByValue(Map<T, E> map, E value) {
		for (Map.Entry<T, E> entry : map.entrySet()) {
			if (Objects.equals(value, entry.getValue())) {
				return entry.getKey();
			}
		}
		return null;
	}

    synchronized public void paint(Graphics g)
    {
    	super.paint(g);
		for(int i = 0; i < width; i++)
			for(int j = 0; j < height; j++)
			{
				if(rekrystalizacja.isSelected())
				{
					if(tab1[i][j].isZrekrystalizowana())
						g.setColor(Color.BLACK);
					else
						g.setColor(Color.RED);
				}
				else
					g.setColor(tab1[i][j].getColor());
				g.fillRect(i * 2, j * 2, 2, 2);
			}
    }
	/*@Override
	public void mouseClicked(MouseEvent arg0) 
	{
		int x = arg0.getX();
		int y = arg0.getY()+105;
		if(x > 0 && x < width * 2 && y > 105 && y < height * 2 )
		{
			x = x / 2;
			y = (y - 105) / 2; 
			
			Color kolor;
			do
				kolor = rand_color();
			while(kolor == Color.WHITE);
			
			tab1[x][y].setColor(kolor);
			tab1[x][y].setZywa(true);
			if(promien.isSelected())
			{
				if(tab1[x][y].isDostepna())
				{
					tab1[x][y].setDostepna(false);
					String os = (String) comboBoxSpace.getSelectedItem();
					Integer oi = Integer.valueOf(os);
					int ix;
					int jx;
					int ni;
					int nj;
					if(x - oi >= 0)
						ix = x - oi;
					else
						ix = 0;
					
					if(y - oi >= 0)
						jx = y - oi;
					else
						jx = 0;
					
					if(x + oi <= width)
						ni = x + oi;
					else
						ni = width;
					
					if(y + oi <= height)
						nj = y + oi;
					else
						nj = height;
					for(int ii = ix; ii < ni ; ii++)
						for(int jj = jx; jj < nj ; jj++)
							if(Math.sqrt( Math.pow( x - ii, 2) + Math.pow( y - jj, 2) ) <= oi)
								tab1[ii][jj].setDostepna(false);

				}
				else
				{
					tab1[x][y].setColor(Color.WHITE);
					tab1[x][y].setZywa(false);
				}
			}
			
			Panel.this.repaint();
		}
	}*/
	@Override
	public void mouseClicked(MouseEvent arg0)
	{
		int x = arg0.getX();
		int y = arg0.getY()+105;
		//int y = arg0.getY();
		Color chosenColorByClick;
		System.out.println("klikniecie " + x + " " + y);
		if(x > 0 && x < width  && y > 105 && y < height  ){
			System.out.println("klikniecie w panelu");
			chosenColorByClick = tab1[x][y].getColor();
			System.out.println("kolor chosen " + chosenColorByClick);

		}
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseEntered(MouseEvent arg0)
	{}
	@Override
	public void mouseExited(MouseEvent arg0) 
	{
		// TODO Auto-generated method stub
	}
	@Override
	public void mousePressed(MouseEvent arg0) 
	{
		// TODO Auto-generated method stub
	}
	@Override
	public void mouseReleased(MouseEvent arg0) 
	{
		// TODO Auto-generated method stub
	}

	void eksportToTextFile(Komorka[][] array){

		try (PrintWriter out = new PrintWriter("results.txt")) {
			for(int i=0; i < width; i++){
				for(int j=0; j < height; j++){
					int id = array[i][j].getID();
					int color = array[i][j].getColor().getRGB();
					out.write(i + " " + j + " " + id + " " + color + "\n");
				}
			}
		}catch (IOException e){
			System.out.println("Blad zapisu do pliku");
		}
	}

}