import java.io.*;
import java.util.*;
import java.util.List;
import java.awt.*;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.Border;

import java.awt.event.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.awt.geom.AffineTransform;
import javax.imageio.ImageIO;



public class Solution extends JPanel implements ActionListener, MouseWheelListener, MouseListener,MouseMotionListener, KeyListener, Border {


	/**
	 * 
	 */
	private static final long serialVersionUID = 7732570952062313116L;

	String[] specialfunctions= {
			"plusminus",
			"asin",
			"acos",
			"atan",
			"sin",
			"cos",
			"tan",
			"floor",
			"sum",
			"abs"




	};
	String[] storedFunction = new String[100];
	ArrayList<point>[] storedData = new ArrayList[100];
	String[] verSpecial= {//Very special functions that skip a bunch
			"plusminus",
			"floor",
			"sum"




	};
	private static final DecimalFormat df = new DecimalFormat("0.00000000");
	private static final DecimalFormat lbf = new DecimalFormat("0.00");
	private double zoomFactor =250;
	public String equation = new String();;
	public JLabel equationLabel;
	public JTextField graphFunctionBox;
	public JTextField graphFunctionBox1;
	public JTextField graphFunctionBox2;
	public JButton zoomInButton;
	public JButton zoomOutButton;
	public JButton submitButton;
	public JButton calcButton;
	public boolean onComponent = false;
	public Color[] colors= {Color.BLACK, Color.red, Color.blue, Color.ORANGE, Color.magenta, Color.YELLOW, Color.LIGHT_GRAY};
	public static Color[] colorS= {Color.BLACK, Color.red, Color.blue, Color.ORANGE, Color.magenta, Color.YELLOW, Color.LIGHT_GRAY};
 	public JPanel menu;
	public JFrame graph;
	public static Solution current;
	private Point mouse;
	private double targetZoom ;
	private double xOffset, yOffset=0;
	public TextInputUI textInput = new TextInputUI();
	public float[] dataPoints ; // DataPoints on Screen with length of w * n   N being how precise the data is.
	private Timer time = new Timer(10,this);
	public Image animation;
		public long starttime;

public long getElapsedTime()
    {
        return System.currentTimeMillis()-starttime;
    }
	public void refreshFrame() {
		
		graph.revalidate();
		graph.repaint();
	}
	public static void refreshFrameS() {
		
		current.graph.revalidate();
		current.graph.repaint();
	}
	public Solution(){ 
		for(int i = 0; i < 100; i++) {
			storedData[i]= new ArrayList<point>();
			storedFunction[i] = "";
		}
		starttime = System.currentTimeMillis();
		 menu = new JPanel();
		JPanel UI = new JPanel();
		UI.setBounds(0,0,400,150);;
		graph = new JFrame();
		graph.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		try {
			calcButton = new JButton(new ImageIcon(new URL("https://cdn.discordapp.com/attachments/619967192384929804/1018291265512673301/calculator.png")));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		zoomInButton = new JButton("+");
		 zoomInButton.setForeground(Color.BLUE);
		 zoomOutButton = new JButton("_");
		 zoomOutButton.setForeground(Color.RED);
		calcButton.setSize(getMaximumSize());
		

try {
            animation = ImageIO.read(new URL("https://cdn.discordapp.com/attachments/619967192384929804/1018301577578156042/graphitephoto.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
		setFocusable(true);
		setBounds(0, 20, 400, 200);
		graphFunctionBox = new JTextField(30);
		graphFunctionBox1 = new JTextField(30);
		graphFunctionBox2 = new JTextField(30);
		graph.addMouseWheelListener(this);
		graph.setTitle("Graphite");
		graph.setSize(300,300);
		menu.setLayout((LayoutManager) new BoxLayout(menu,BoxLayout.Y_AXIS));
		add(calcButton);
		add(zoomOutButton);
		add(zoomInButton);
	      Border blackline = BorderFactory.createLineBorder(Color.black);
		textInput.setBorder(blackline);
		graph.add(textInput);
		graph.add(this);
		this.add(UI);
		repaint();
		addMouseListener(this);
		addMouseMotionListener(this);
		targetZoom = zoomFactor;
		addMouseWheelListener(this);
		addKeyListener(this);
		grabFocus();
		calcButton.addActionListener(this);
		zoomInButton.addActionListener(this);
		zoomOutButton.addActionListener(this);
		graph.setVisible(true);
		setFocusable(true);
		time.start();
	}
	public void paintComponent(Graphics g){
		if(textInput.jslotcounter==0) {
			textInput.addTextField();
		}
		
		calcButton.setBounds(this.getWidth()-90, 20 , 70,70);
		zoomOutButton.setBounds(this.getWidth()-70, this.getHeight()-70 , 50,50);
		zoomInButton.setBounds(this.getWidth()-140, this.getHeight()-70 , 50,50);
		textInput.setBounds(0,0,350,graph.getHeight());
		this.setBounds(350,7,graph.getWidth()-350, graph.getHeight()-40);
		textInput.refreshButtonBounds();
		zoomFactor =lerp(zoomFactor ,targetZoom , 0.3 );
		if(targetZoom> zoomFactor) {

			if(targetZoom - zoomFactor < Math.abs(500 *Math.pow(1.1,zoom-1) -targetZoom)/10) {
				zoomFactor = targetZoom;
			}
		}else {
			if(zoomFactor-targetZoom  < Math.abs(500 *Math.pow(1.1,zoom+1) -targetZoom)/10) {
				zoomFactor = targetZoom;
			}
		}
		super.paintComponent(g);
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		
		if(getElapsedTime()<3000)
		        {

					textInput.setBounds(0,0,0,0);

					calcButton.setBounds(0,0,0,0);
					zoomOutButton.setBounds(0,0,0,0);
					zoomInButton.setBounds(0,0,0,0);
		            this.setBounds(0,0,graph.getWidth(),graph.getHeight());
		            g.drawImage(animation,getWidth()/2-getHeight()/8*6 , getHeight()/4,getHeight()/4*6   ,getHeight()/2, null);
		            return;
		        }
		if(mouse==null) {
			mouse = new Point(0,0);
		}
		Equation mouseX =new Equation(graphFunctionBox.getText().trim() ,Double.parseDouble(lbf.format((mouse.x -xOffset*-zoomFactor   )/zoomFactor))) ;
		g.setColor(Color.BLACK);
		int at =0;
		g.drawString("x :" +  lbf.format((mouse.x +xOffset*zoomFactor   )/zoomFactor ), 200, 200);
		if(mouse!=null)

			for(Map.Entry<Double, Boolean> entry : mouseX.value.entrySet()) {
				at++;
				g.drawString("y :" + entry.getKey() , 200, 200+at*20);

			}

		g.setColor(Color.pink);
		try{

			g.setColor(Color.BLACK);
			//y axis
			g.drawLine( (int)(xOffset*-zoomFactor) , this.getHeight(),(int)(xOffset*-zoomFactor) , 0   );
			//x axis
			g.drawLine( 0,this.getHeight()+ (int)(yOffset*-zoomFactor) , this.getWidth() ,  this.getHeight()+(int)(yOffset*-zoomFactor));
			//double[][] data =loadDataPoints( graphFunctionBox.getText()  , xOffset , xOffset + (this.getWidth())*(1/zoomFactor) , 1/zoomFactor);
			double y2;

			double a =((0 - (yOffset*-zoomFactor)-(this.getHeight() ))/(- zoomFactor))- ((0 - (yOffset*-zoomFactor) ))/(- zoomFactor) ;
			double scale= Math.pow(10,(double)((int)(Math.log10(a)-1)));
			int incre =  this.getHeight()/scale/zoomFactor >10? 5:1 ;
			for(int i = 0 ; scale*i *zoomFactor< this.getHeight()-zoomFactor*yOffset ; i+= incre) {
				g.drawString( lbf.format(scale * i) , (int)(xOffset*-zoomFactor)  + 20 , (int)(this.getHeight() - i*scale* zoomFactor+ (int)(yOffset*-zoomFactor)));
			}
			for(int i = 0 ; -scale*i *zoomFactor< this.getHeight()-zoomFactor*yOffset ; i-= incre) {
				g.drawString( lbf.format(scale * i) , (int)(xOffset*-zoomFactor)  + 20 , (int)(this.getHeight() - i*scale* zoomFactor+ (int)(yOffset*-zoomFactor)));
			}


			for(int i = 0 ; scale*i *zoomFactor< this.getWidth() -zoomFactor*xOffset; i+= incre) {
				g.drawString( lbf.format(scale * i) , (int)(i*scale* zoomFactor+ (int)(xOffset*-zoomFactor)) ,this.getHeight()+(int)(yOffset*-zoomFactor) +20 ) ;
			}
			for(int i = 0 ; -scale*i *zoomFactor< this.getWidth() -zoomFactor*xOffset  ; i-= incre) {
				g.drawString( lbf.format(scale * i) , (int)(i*scale* zoomFactor+ (int)(xOffset*-zoomFactor)) ,this.getHeight()+(int)(yOffset*-zoomFactor) +20 ) ;
			}
			String [] text = textInput.getText();
			for(int i = 0 ; i < text.length; i++ )
				drawFunction(g,text[i] ,colors[i%colors.length] , i);

		}catch(Exception e ){
			e.printStackTrace();
			// Equation Provided is Invalid
		}

	}
	public void drawFunction(Graphics g , String function, Color color, int id) {
		function = function.trim();
		if(function.length() == 0) return;
		int commas = 0;
		int com = 0;
		int brack = 0;
		int start =0;
		if(function.charAt(0) == '(')
			for(int i =0 ; i < function.length();i++) {
				if( i == -1) {
					break;
				}
				if(function.charAt(i) == '(') {
					if(brack ==0) {
						start=i;
					}
					brack++;
				}if(function.charAt(i) == ')') {
					brack--;
				}
				if(brack == 1 &&function.charAt(i)  == ',' ) {
					commas++;
					com =i;

				}
				if(brack ==0 && commas==1) {
					if(function.indexOf("{" ) != -1) {
						try {
							drawPoints((Graphics2D) g , function.substring(start +1 , com) , function.substring(com+1,i) , Double.parseDouble(function.substring(function.indexOf("{",i )+1 , function.indexOf(",",i ) )   ) , Double.parseDouble(function.substring(  function.indexOf(",",i )+1 ,function.indexOf("}",i ))    ) , 0.01 ,id );
						}catch(Exception e) {
							drawPoints((Graphics2D) g , function.substring(start +1 , com) , function.substring(com+1,i) , 0 , 1 , 0.2 ,id);
						}
					}else {
						drawPoints((Graphics2D) g , function.substring(start +1 , com) , function.substring(com+1,i) , 0 , 1 , 0.2 ,id);

					}

					return;
				}
			}
		Color initColor = g.getColor();
		g.setColor(color);

		functionDraw(g, function , id );	




		g.setColor(initColor);

	}
	private class point{
		double x,y;
		public point(double tx , double ty) {
			x= tx ;
			y= ty;
		}
	}

	public void drawPoints(Graphics2D g2,String functionA, String functionB , double start , double end , double inc , int id) {

		g2.setStroke(new  BasicStroke(2));
		if(storedFunction[id].equals(functionA +";"+functionB+ ";"+start+";"+end+ ";"+inc)) {
			for(int i = 0 ; i < storedData[id].size()-1 ; i++) {
				point p1 =storedData[id].get(i);
				point p2 =storedData[id].get(i+1);
				double x1 =xToScreen(p1.x);
				double x =xToScreen(p2.x);
				double y1 =yToScreen(p1.y);
				double y =yToScreen(p2.y);				
				if( p1.x /p2.x < 0 && Math.abs(x1-x) > this.getWidth()){ // if signs changes ie -1 to 1

					g2.drawOval((int)x1-2, (int)y1-2, 4, 4);
				}else if( p1.y /p2.y < 0 && Math.abs(y1-y) > this.getHeight()){ // if signs changes ie -1 to 1

					g2.drawOval((int)x1-2, (int)y1-2, 4, 4);
				}else

					g2.drawLine((int)x1,(int) y1,(int) x, (int)y);
			}
			return;
		}
		storedFunction[id] = functionA +";"+functionB+ ";"+start+";"+end+ ";"+inc;
		storedData[id].clear();
		double x =0,y = 0;
		Equation temp , temp1;
		temp  = new Equation (functionA , start);
		temp1 = new Equation (functionB , start);

		boolean answer = false;
		for(Map.Entry<Double, Boolean> entryA : temp.value.entrySet()) {

			for(Map.Entry<Double, Boolean> entryB : temp1.value.entrySet()) {

				y =((int)(this.getHeight() - entryB.getKey()*zoomFactor + (int)(yOffset*-zoomFactor)));
				x = xToScreen(entryA.getKey());
				g2.fillOval( (int)x-2, (int)y-2, 4, 4);


				break;
			}
			break;
		}
		boolean locaAns= false;
		if(functionA.contains(specialfunctions[0] )||functionB.contains(specialfunctions[0] ))return;
		for( start = start ; start < end ; start+=inc) {
			temp  = new Equation (functionA , start);
			temp1 = new Equation (functionB , start);

			locaAns=false;
			for(Map.Entry<Double, Boolean> entryA : temp.value.entrySet()) {

				for(Map.Entry<Double, Boolean> entryB : temp1.value.entrySet()) {

					double y1 =yToScreen( entryB.getKey());
					double x1 = xToScreen(entryA.getKey());
					storedData[id].add(new point(entryA.getKey(),entryB.getKey()));
					if(answer) {

						if( Math.abs(x1 -x) > this.getWidth()){ // if signs changes ie -1 to 1

							g2.drawOval((int)x1-2, (int)y1-2, 4, 4);
						}else if( Math.abs(y1 -y) > this.getHeight()){ // if signs changes ie -1 to 1

							g2.drawOval((int)x1-2, (int)y1-2, 4, 4);
						}else

							g2.drawLine((int)x1,(int) y1,(int) x, (int)y);
					}
					else
						g2.drawOval((int)x1-2, (int)y1-2, 4, 4);

					x= x1;
					y=y1;
					locaAns= true;

					answer = true;
					break;
				}
				break;
			}
			answer =locaAns;
		}
	}
	public double xToScreen(double n) {
		return zoomFactor *( n-xOffset ) ;
	}
	public double yToScreen(double n) {
		return ((int)(this.getHeight() - n*zoomFactor + (int)(yOffset*-zoomFactor)));
	}
	public void functionDraw(Graphics g , String function , int id) {

		
		Graphics2D g2 =((Graphics2D) g);
		boolean special = false;
		for(int i = 0 ; i < verSpecial.length; i++) {
			if(function.contains(verSpecial[i])) {
				special = true;
				break;
			}
		}
		if(storedFunction[id].equals(function+ ";"+xOffset+";"+zoomFactor)) {
			
			
			if(function.contains("plusminus")) {


				for(int i = 0 ; i < storedData[id].size() ; i++) {
					point data = storedData[id].get(i);
					g2.fillOval((int)data.x-2 , (int)data.y-2, 4, 4);
				}
			}else if(!special ) {
				g2.setStroke(new  BasicStroke(3));
				for(int i = 0 ; i < storedData[id].size()-1 ; i++) {
					point data = storedData[id].get(i);
					point data1 = storedData[id].get(i+1);
					g2.drawLine((int)data.x , (int)data.y, (int)data1.x , (int)data1.y);
				}
			}
			else {

				for(int i = 0 ; i < storedData[id].size() ; i++) {
					point data = storedData[id].get(i);
					g2.fillOval((int)data.x-2 , (int)data.y-2, 4, 4);
				}
			}
			
			
			return;
		}
		storedFunction[id] = function+ ";"+xOffset+";"+zoomFactor;
		storedData[id].clear();
		if(function.contains("plusminus")) {

			for(int x = 0 ; x < this.getWidth(); x++){
				Equation x2 =new Equation(function,round2Interval((x -xOffset*-zoomFactor   )/zoomFactor ,0.005)) ;
				for(Map.Entry<Double, Boolean> entry : x2.value.entrySet()) {
					double y2 =entry.getKey();

					double y =((int)(this.getHeight() - y2*zoomFactor + (int)(yOffset*-zoomFactor)));
					if( y > 0 && y < this.getHeight() ) {

						g.fillOval(x-2, (int) (y-2), 4, 4);
						storedData[id].add(new point(x , y));

					}
				}

			}
		}else if(!special ) {

			g2.setStroke(new  BasicStroke(3));
			double py=0;
			boolean init =false;
			for(int x = 0 ; x < this.getWidth(); x++){
				Equation x2 =new Equation(function,round2Interval((x -xOffset*-zoomFactor   )/zoomFactor ,0.005)) ;

				for(Map.Entry<Double, Boolean> entry : x2.value.entrySet()) {
					double y2 =entry.getKey();


					double y =clamp(((int)(this.getHeight() - y2*zoomFactor + (int)(yOffset*-zoomFactor))),-100 , this.getHeight()+100);

					if(!init) {
						py = y;
						init = true;
					}
						g2.drawLine(x-1,  (int)py,x,(int) y);

						storedData[id].add(new point(x , y));
					
					py=y;
					break;
				}

			}
		}
		else {
			double py=0;
			for(int x = 0 ; x < this.getWidth(); x++){
				Equation x2 =new Equation(function,round2Interval((x -xOffset*-zoomFactor   )/zoomFactor ,0.005)) ;

				for(Map.Entry<Double, Boolean> entry : x2.value.entrySet()) {
					double y2 =entry.getKey();
					if(x==0) {
						py = y2;
					}


					double y =((int)(this.getHeight() - y2*zoomFactor + (int)(yOffset*-zoomFactor)));
					if(Math.abs(py-y)>5) {
						precisedraw(g,function, (double)x, 0.4 ,id);
					}

					py=y;
					if( y > 0 && y < this.getHeight() ) {

						g.fillOval(x-2, (int) (y-2), 4, 4);
						storedData[id].add(new point(x , y));

					}
					break;
				}

			}
		}
	}
	public void precisedraw(Graphics g , String function, double x, double inc ,int id ) {
		double end = x+1;
		for( x = x+inc ; x < end; x+=inc){
			Equation x2 =new Equation(function,round2Interval((x -xOffset*-zoomFactor   )/zoomFactor ,0.005)) ;
			for(Map.Entry<Double, Boolean> entry : x2.value.entrySet()) {
				double y2 =entry.getKey();



				double y =((int)(this.getHeight() - y2*zoomFactor + (int)(yOffset*-zoomFactor)));
				if( y > 0 && y < this.getHeight() ) {

					g.fillOval((int) (x-2), (int) (y-2), 4, 4);
					storedData[id].add(new point(x , y));

				}
			}

		}
	}
	
	public double clamp (double n , double min , double max) {
		return Math.min(max, Math.max(min, n));
	}
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() ==time)
			repaint();

if(e.getSource()==zoomInButton)
        {
            zoom += 1;
            targetZoom =500 *Math.pow(1.1,zoom);
        }
        if(e.getSource()==zoomOutButton)
        {
            zoom -= 1;
            targetZoom =500*Math.pow(1.1,zoom);
        }
		if(e.getSource()==calcButton)
		{
			CalcGUI s1 = new CalcGUI();
		}
	}

	public static void main(String[]args)
	{
		current = new Solution();


	}
	public void mousePressed(MouseEvent e) {
	}
	public void mouseReleased(MouseEvent e)
	{
		p1 = null;
	}
	Point p1 = null;
	public void mouseMoved(MouseEvent e)
	{
		mouse = e.getPoint();
	}
	double zoom =0;
	public void mouseWheelMoved(MouseWheelEvent e)
	{
		if(onComponent) {
			zoom += e.getPreciseWheelRotation();
			targetZoom =500 *Math.pow(1.1,zoom);
			
		}
	}
	static String previous = " ";
	public static double lerp(double a, double b, double f) {

		return a + f * (b - a);
	}
	public class Equation{
		HashMap<Double, Boolean> value =new HashMap<Double,Boolean>();
		String a = "";
		double valStore;

		public Equation(String string,double xVal) {
			valStore = xVal;
			a = string;
			a= string.replaceAll(" ", ""); 
			for(int i = 0 ; i < a.length(); i++ ) {
				if( a.charAt(i)=='(' || a.charAt(i)=='x') {
					if(i !=0) {
						if(isNum(a.charAt(i-1)) || a.charAt(i-1) == ')'){
							a =a.substring(0, i) + "*" +a.substring( i) ;
						}


					}
				}
				if( a.charAt(i)==')'||a.charAt(i)=='x') {
					if(i !=a.length()-1) {
						if(isNum(a.charAt(i+1)) || a.charAt(i+1) == '(' ){
							a =a.substring(0, i+1) + "*" +a.substring( i+1) ;
						}


					}
				}
			}



			if(xVal!= Double.NaN)
				a= a.replaceAll("x", "("+xVal+ ")");

			try {
				sove(xVal);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
		}
		public Equation(String string,double xVal,boolean auto) {
			a = string;
			a= string.replaceAll(" ", ""); 
			for(int i = 0 ; i < a.length(); i++ ) {
				if( a.charAt(i)=='(' || a.charAt(i)=='x') {
					if(i !=0) {
						if(isNum(a.charAt(i-1)) || a.charAt(i-1) == ')'){
							a =a.substring(0, i) + "*" +a.substring( i) ;
						}


					}
				}
				if( a.charAt(i)==')'||a.charAt(i)=='x') {
					if(i !=a.length()-1) {
						if(isNum(a.charAt(i+1)) || a.charAt(i+1) == '(' ){
							a =a.substring(0, i+1) + "*" +a.substring( i+1) ;
						}


					}
				}
			}



			if(xVal!= Double.NaN)
				a= a.replaceAll("x", "("+xVal+ ")");
		}
		public void addVar( String var, String val) {
			for(int i = 0 ; i <a.length() - var.length()+1; i++) {
				if(a.substring(i , i + var.length()).equals(var)) {
					if(i==0||isBasicOperator( a.charAt(i -1))  ||a.charAt(i -1)==')') {
						if(i==a.length() - var.length()||isBasicOperator( a.charAt(i +var.length()))  ||a.charAt(i +var.length() )=='(') {

							a=a.substring(0 , i ) + (i!=0 && a.charAt(i -1)==')' ? "*":"" ) + val +(i!=a.length() - var.length() &&a.charAt(i +var.length())=='('? "*":"" )+ a.substring( i + var.length());
						}
					}else
						a=a.substring(0 , i ) +"*"+ val + a.substring( i + var.length());
				}
			}


		}
		public void sub(double n) {
			if(value.size()==0) {
				value.put(-n,true);
				return;
			}
			HashMap<Double, Boolean> newVal =new HashMap<Double,Boolean>();
			for(Map.Entry<Double, Boolean> entry : value.entrySet()) {
				newVal.put(entry.getKey() - n, true);
			}
			value = newVal;

		}
		public void add(double n) {
			if(value.size()==0) {
				value.put(n,true);
				return;
			}
			HashMap<Double, Boolean> newVal =new HashMap<Double,Boolean>();
			for(Map.Entry<Double, Boolean> entry : value.entrySet()) {
				newVal.put(entry.getKey() + n, true);
			}
			value = newVal;

		}
		public HashMap<Double, Boolean> function(String function, String[] args) {
			Equation temp;
			HashMap<Double, Boolean> newVal = new HashMap<Double, Boolean>();
			switch(function) {


			case "acos":
				temp = new Equation(args[0],Double.NaN);


				for(Map.Entry<Double, Boolean> entry : temp.value.entrySet()) {
					Double key = entry.getKey();
					newVal.put(Math.acos(key), null);


				}
				return newVal;
			case "asin":
				temp = new Equation(args[0],Double.NaN);


				for(Map.Entry<Double, Boolean> entry : temp.value.entrySet()) {
					Double key = entry.getKey();
					newVal.put(Math.asin(key), null);


				}
				return newVal;
			case "atan":
				temp = new Equation(args[0],Double.NaN);


				for(Map.Entry<Double, Boolean> entry : temp.value.entrySet()) {
					Double key = entry.getKey();
					newVal.put(Math.atan(key), null);
				}
				return newVal;
			case "plusminus":
				temp = new Equation(args[0],Double.NaN);


				for(Map.Entry<Double, Boolean> entry : temp.value.entrySet()) {
					Double key = entry.getKey();
					newVal.put(key, null);
					newVal.put(-key, null);


				}
				return newVal;
			case "sin":
				temp= new Equation(args[0],Double.NaN);


				for(Map.Entry<Double, Boolean> entry : temp.value.entrySet()) {
					Double key = entry.getKey();
					newVal.put(Math.sin(key), null);


				}
				return newVal;
			case "cos":
				temp= new Equation(args[0],Double.NaN);


				for(Map.Entry<Double, Boolean> entry : temp.value.entrySet()) {
					Double key = entry.getKey();
					newVal.put(Math.cos(key), null);


				}
				return newVal;
			case "tan":
				temp= new Equation(args[0],Double.NaN);


				for(Map.Entry<Double, Boolean> entry : temp.value.entrySet()) {
					Double key = entry.getKey();
					newVal.put(Math.tan(key), null);


				}
				return newVal;


			case "floor":
				temp = new Equation(args[0],Double.NaN);


				for(Map.Entry<Double, Boolean> entry : temp.value.entrySet()) {
					Double key = entry.getKey();
					newVal.put(Math.floor(key), null);
				}
				return newVal;
			case "sum":
				try {
					int target = Integer.parseInt(args[1] );
					double sum =0;
					for(int n= Integer.parseInt(args[0] ); n <= target; n++) {

						temp = new Equation(args[2],Double.NaN,false);
						temp.addVar("n", ""+n);
						temp.sove(valStore);

						for(Map.Entry<Double, Boolean> entry : temp.value.entrySet()) {
							sum+= entry.getKey();
							break;
						}
					}
					newVal.put(sum, null);
				}catch(Exception e) {
				}
				return newVal;
			case "abs":
				temp = new Equation(args[0],Double.NaN);


				for(Map.Entry<Double, Boolean> entry : temp.value.entrySet()) {
					Double key = entry.getKey();
					newVal.put(Math.abs(key), null);
				}
				return newVal;
			}
			return newVal;
		}
		public void clipper() {

		}

		public void sove(double xVal) throws Exception{
			/**( 2 )
	        |
	        V

	 	1  + 1
	  /          \
	x+1          x+1
			 */

			int brack = 0;
			for(int i = 0 ; i < a.length();i++) {
				if(a.charAt(i) == '(') {
					brack++;
				}if(a.charAt(i) == ')') {
					brack--;
				}
			}
			if(brack!= 0 ) {
				return;
			}




			//TODO : Special Functions here


			for(int i = 0 ; i < specialfunctions.length ; i++) {
				String cur = specialfunctions[i];
				while(a.contains(cur)) {
					int index = a.indexOf(cur)+ cur.length();
					if(a.charAt(index) != '(') {

						// invalid formatting
						return ;

					}

					brack = 0;
					int i1 = 0;
					for( i1=index; i1 < a.length();i1++) {
						if(a.charAt(i1) == '(') {
							brack++;
						}if(a.charAt(i1) == ')') {
							brack--;
						}
						if(brack==0) {
							break;
						}
					}
					String[] args = a.substring(index+1,i1).split(",");
					HashMap<Double, Boolean> output = function(cur, args);
					for(Map.Entry<Double, Boolean> entry : output.entrySet()) {
						Double key = entry.getKey();

						String newstring =a.substring(0, index- cur.length() )  
								+df.format( key)
								+a.substring(i1+1);

						Equation temp= new Equation(newstring , xVal);

						for(Map.Entry<Double, Boolean> childValues : temp.value.entrySet()) {
							if(!Double.isNaN(childValues.getKey()))
								value.put(childValues.getKey(), true);
						}
						// do what you have to do here
						// In your case, another loop.
					}
					return;


				}
			}



			while(a.contains("(") &&a.contains(")")) {

				int num =0;
				int start=0;

				for(int i = a.indexOf("(") ; i < a.length() ; i++ ) {
					if( a.charAt(i) == '(') {
						if(num == 0)
						{
							start = i;
						}
						num ++;

					}else if( a.charAt(i) == ')') {
						if(num == 1)
						{

							Equation b = new Equation(a.substring(start+1,i ) ,Double.NaN);
							if(b.value.size()==0) {

								return;
							}
							if(i+2 < a.length()) {
								if(a.charAt(i+1) == '^' ) {
									if(a.charAt(i+2)!='(') {

										int s ;

										for(s =i +2 ; s < a.length() ; s++) {
											if(a.charAt(s-1) == 'E') 
												continue;
											if(a.charAt(s) == '-'&& s==i+2 )continue;
											if(isBasicOperator(a.charAt(s)))break;
										}
										double expo= Double.parseDouble(a.substring(i+2, s));
										if(!hasTwoAnswers(expo)) {


											for(Map.Entry<Double, Boolean> entry : b.value.entrySet()) {
												Double key = entry.getKey();

												String newstring =a.substring(0, start )  
														+df.format( pow(key, expo) )
														+a.substring(s);
												Equation temp= new Equation(newstring , xVal);

												for(Map.Entry<Double, Boolean> childValues : temp.value.entrySet()) {
													if(!Double.isNaN(childValues.getKey()))
														value.put(childValues.getKey(), true);
												}
												// do what you have to do here
												// In your case, another loop.
											}
											return ;

										}else {

											for(Map.Entry<Double, Boolean> entry : b.value.entrySet()) {
												Double key = entry.getKey();

												String newstring =a.substring(0, start )  
														+df.format( pow(key, expo) )
														+a.substring(s);
												Equation temp= new Equation(newstring , xVal);
												for(Map.Entry<Double, Boolean> childValues : temp.value.entrySet()) {
													if(!Double.isNaN(childValues.getKey()))
														value.put(childValues.getKey(), true);
												}
												// do what you have to do here
												// In your case, another loop.
											}
											for(Map.Entry<Double, Boolean> entry : b.value.entrySet()) {
												Double key = entry.getKey();

												String newstring =a.substring(0, start )  + "(-"
														+df.format( pow(key, expo) )+")"
														+a.substring(s);
												Equation temp= new Equation(newstring , xVal);
												for(Map.Entry<Double, Boolean> childValues : temp.value.entrySet()) {
													value.put(childValues.getKey(), true);
												}
												// do what you have to do here
												// In your case, another loop.
											}
											return ;
										}
									}else {

									}
								}
							}
							for(Map.Entry<Double, Boolean> entry : b.value.entrySet()) {

								Equation temp = new Equation(a.substring(0, start )  
										+df.format(entry.getKey() )
										+a.substring(i+1), xVal);

								for(Map.Entry<Double, Boolean> childValues : temp.value.entrySet()) {
									if(!Double.isNaN(childValues.getKey()))
										value.put(childValues.getKey(), true);
								}
								return;
							}
							break;
						}
						num --;

					}
				}

			}
			while(a.contains("^")) {
				int inde = a.indexOf("^");

				int e ;
				for(e=inde-1 ; e >=0 ; e --) {

					if(isBasicOperator(a.charAt(e)))break;

				}
				int s ;

				for(s =inde +1 ; s < a.length() ; s++) {
					if(a.charAt(s-1) == 'E') 
						continue;
					if(a.charAt(s) == '-'&& s==inde+1 )continue;
					if(isBasicOperator(a.charAt(s)))break;
				}
				double expo =Double.parseDouble(a.substring(inde+1, s));
				if(hasTwoAnswers(expo)) {// If it has two answers then it would create an two equations exactly the same as itself but with +- and solve it and return it
					String newstring =a.substring(0, e+1 )  
							+df.format(pow(Double.parseDouble(a.substring(e+1, inde))
									, expo)) 
							+a.substring(s);

					Equation temp= new Equation(newstring , xVal);
					for(Map.Entry<Double, Boolean> childValues : temp.value.entrySet()) {
						value.put(childValues.getKey(), true);
					}

					newstring =a.substring(0, e+1 )  + "(-"
							+df.format(pow(Double.parseDouble(a.substring(e+1, inde))
									, expo)) +")"
									+a.substring(s);

					temp= new Equation(newstring , xVal);
					for(Map.Entry<Double, Boolean> childValues : temp.value.entrySet()) {
						value.put(childValues.getKey(), true);
					}
					return;
				}
				a = a.substring(0, e+1 )  
						+df.format(pow(Double.parseDouble(a.substring(e+1, inde))
								, expo)) 
						+a.substring(s);

			}

			while(a.contains("*")) {
				int inde = a.indexOf("*");

				int e ;
				for(e=inde-1 ; e >=0 ; e --) {


					if(a.charAt(e) == '-') {

						if(e!=0 &&a.charAt(e-1) == 'E') 
							continue;
						if(e-1 <0) {
							continue;
						}
						if( !isNum(a.charAt(e-1)) )
							continue;
					}
					if(isBasicOperator(a.charAt(e)))break;

				}
				int s ;

				for(s =inde +1 ; s < a.length() ; s++) {
					if(a.charAt(s-1) == 'E') 
						continue;
					if(a.charAt(s) == '-'&& s==inde+1 )continue;
					if(isBasicOperator(a.charAt(s)))break;
				}
				a = a.substring(0, e+1 )  
						+df.format(Double.parseDouble(a.substring(e+1, inde))
								* Double.parseDouble(a.substring(inde+1, s))) 
						+a.substring(s);
			}

			while(a.contains("/")) {
				int inde = a.indexOf("/");

				int e ;
				for(e=inde-1 ; e >=0 ; e --) {

					if(a.charAt(e) == '-') {


						if(e!=0 &&a.charAt(e-1) == 'E') 
							continue;
						if(e-1 <0) {
							continue;
						}
						if( !isNum(a.charAt(e-1)) )
							continue;
					}
					if(isBasicOperator(a.charAt(e)))break;

				}
				int s ;

				for(s =inde +1 ; s < a.length() ; s++) {
					if(a.charAt(s-1) == 'E') 
						continue;
					if(a.charAt(s) == '-'&& s==inde+1 )continue;
					if(isBasicOperator(a.charAt(s)))break;
				}

				a = a.substring(0, e+1 )  
						+df.format(Double.parseDouble(a.substring(e+1, inde))
								/ Double.parseDouble(a.substring(inde+1, s))) 
						+a.substring(s);

			}
			while(a.contains("+")) {
				int inde = a.indexOf("+");

				int e ;
				for(e=inde-1 ; e >=0 ; e --) {

					if(a.charAt(e) == '-') {

						if(e!=0 &&a.charAt(e-1) == 'E') 
							continue;
						if(e-1 <0) {
							continue;
						}
						if( !isNum(a.charAt(e-1)) )
							continue;
					}
					if(isBasicOperator(a.charAt(e)))break;

				}
				int s ;

				for(s =inde +1 ; s < a.length() ; s++) {
					if(a.charAt(s-1) == 'E') 
						continue;
					if(a.charAt(s) == '-'&& s==inde+1 )continue;
					if(isBasicOperator(a.charAt(s)))break;
				}

				a = a.substring(0, e+1 )  
						+df.format(Double.parseDouble(a.substring(e+1, inde))
								+ Double.parseDouble(a.substring(inde+1, s))) 
						+a.substring(s);

			}
			while(a.contains("-")) {
				int inde = a.indexOf("-");

				int e ;
				for(e=inde-1 ; e >=0 ; e --) {

					if(a.charAt(e) == '-') {
						if(e-1 <0) {
							continue;
						}
						if(e!=0 &&a.charAt(e-1) == 'E') 
							continue;

						if( !isNum(a.charAt(e-1)) )
							continue;
					}
					if(isBasicOperator(a.charAt(e)))break;

				}
				int s ;

				for(s =inde +1 ; s < a.length() ; s++) {
					if(a.charAt(s-1) == 'E') 
						continue;
					if(a.charAt(s) == '-'&& s==inde+1 )continue;
					if(isBasicOperator(a.charAt(s)))break;
				}
				if(inde ==0) {
					sub(Double.parseDouble(a.substring(1, s)))  ;
					a=a.substring(s);
					continue;
				}

				a = a.substring(0, e+1 )  
						+df.format(Double.parseDouble(a.substring(e+1, inde))
								- Double.parseDouble(a.substring(inde+1, s))) 
						+a.substring(s);

			}
			if(a.isEmpty()) {
				return ;
			}
			add( Double.parseDouble(a));
			return ;
		}

	}

	public static double[][] run(double min , double max , double inc) throws IOException {
		double[][] result = new double[2][ (int) ((max - min)/inc )];
		Runtime rt = Runtime.getRuntime();
		Process pr = rt.exec("java Snippet " +  min + " " + max + " " +inc );
		BufferedWriter pr_writer = new BufferedWriter(
				new OutputStreamWriter(pr.getOutputStream()));

		BufferedReader br = new BufferedReader(new InputStreamReader(pr.getInputStream()));
		String ch;
		int data =0;
		for(double i = min ; i <=max ; i+= inc) {

			if ((ch = br.readLine()) != null)
			{
				if(data >= result[0].length)break;
				String [] a = ch.trim().split("\\s");
				result[0][data] = Double.parseDouble(a[0]);
				result[1][data] = Double.parseDouble(a[1]);

				data++;
			}

		}
		br.close();
		pr_writer.close();
		return result;
	}
	public static boolean isBasicOperator(char a) {
		return (a == '^'||a == '+' || a== '-' || a =='%' || a == '*' || a=='/');
	}
	public static boolean isNum(char a) { // including Periods
		return(((int) a >=46 && (int)a<=57 && (int) a != 57 ) || a=='x' || a=='E') ;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		onComponent= true;
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		onComponent= true;
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		onComponent = false;
	}
	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub

		if(p1 == null) {
			p1 = e.getPoint();

		}
		else {
			xOffset -= (e.getPoint().getX() - p1.getX() )/ zoomFactor;
			yOffset -= (e.getPoint().getY() - p1.getY() )/ zoomFactor;

			p1 = e.getPoint();
		}

	}
	public boolean hasTwoAnswers(double a) {
		if(a==a)
			return false;
		a =round2NearestInterval(a,0.0001);
		a = a%1;
		if(a==0 )return false;
		a = 1/a;
		return getLastDigit(a)%2 ==0;

	}public double expoNeg(double a) {
		a =round2NearestInterval(a,0.0001);
		a = a%1;
		if(a==0 )return 0;
		a = 1/a;
		if(a %2 ==0) {

		}
		return 0;

	}
	DecimalFormat smdf = new DecimalFormat("0.00000");
	public double round2Interval(double n , double interval) {

		n -=n%interval;
		n=  Double.parseDouble(smdf.format(n));
		return n;
	}
	public double round2NearestInterval(double n , double interval) {
		return Double.parseDouble(df.format(n));

	}
	public double pow(double base , double expo) { // Because Math.pow failed me
		double result =0;
		if(base <0) {
			result = Math.pow(base,(int)-((-expo) - (-expo)%1));



			expo = -((-expo)%1 ) ;
			if(expo == 0) {
				return result;
			}

			int gcd =findGCD(expo);
			int b =(int) (1/( (double)(720720/gcd)*expo));
			if(b%2 ==0) {
				return Double.NaN;
			}
			int a = 720720/gcd;

			return result * Math.pow(-base, expo)  *Math.pow(-1, a)  ;

		}
		return  Math.pow(base, expo);
	}
	public int getLastDigit(double a) {
		a =round2NearestInterval(a,0.0001);
		String temp =(a+"");
		int c =temp.length()-1;
		while(c>=0) {
			if(temp.charAt(c) == '0' ||temp.charAt(c) == '.' ||temp.charAt(c) == '-' ) {
				c--;
				continue;
			}else {
				return Integer.parseInt(temp.charAt(c) +""); 
			}
		}
		return 0;

	}
	public int getLastDigitinc0(double a) {
		a =round2NearestInterval(a,0.0001);
		String temp =(a+"");
		int c =temp.length()-1;
		while(c>=0) {
			if(temp.charAt(c) == '0' ||temp.charAt(c) == '.' ||temp.charAt(c) == '-' ) {
				c--;
				continue;
			}else {
				return Integer.parseInt(temp.charAt(c) +""); 
			}
		}
		return 0;

	}
	public int findGCD(double dec) {
		dec = round2NearestInterval(dec ,0.001);
		int scale =(int)((double)720720 /dec) ;//720720 highly composite
		return findGCD(720720 , scale);

	}
	int findGCD(int a, int b)  
	{  
		while(b != 0)  
		{  
			if(a > b)  
			{  
				a = a - b;  
			}  
			else  
			{  
				b = b - a;  
			}  
		}  
		return a;  
	}
	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
	}
	@Override
	public void keyPressed(KeyEvent e) {
		System.out.println("e :"+e.getExtendedKeyCode());
	}
	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		System.out.println("e :"+e.getExtendedKeyCode());

	}
	@Override
	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public Insets getBorderInsets(Component c) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public boolean isBorderOpaque() {
		// TODO Auto-generated method stub
		return false;
	}  

}
