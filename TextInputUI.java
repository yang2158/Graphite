import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.*;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class TextInputUI extends JPanel implements ActionListener, KeyListener,MouseWheelListener , MouseMotionListener {
	private double scroll = 0;
	public int jslotcounter = 0;
	public JTextField[]textlist = new JTextField[100];
	int x =0;
	public final int heightBox = 100;
	final double scrollSpeed = 40;
	public TextInputUI() {
		addKeyListener(this);
		setFocusable(true);
		addMouseWheelListener(this);
		addMouseMotionListener(this);
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);

		for(int i = 0 ; i < jslotcounter;i++) {
			if( (i*heightBox+ i * 10 +10 - scrollOffsetR) <this.getHeight() && (i*heightBox+ i * 10 +10 - scrollOffsetR) >-100)
				g.setColor(Solution.colorS[i%(Solution.colorS.length)]);
			g.fillRect(0 ,  (int) (i*heightBox+ i * 10  - scrollOffsetR)   , this.getWidth() ,10);
		}
	}
	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub

		if(e.getKeyCode()==KeyEvent.VK_ENTER) {
			addTextField();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	Action action = new AbstractAction()
	{
	    @Override
	    public void actionPerformed(ActionEvent e)
	    {
	    	
	    	addTextField();
	    }
	};
	public void addTextField()
	{
		if(jslotcounter ==99)
		{
			return;
		}
		else
		{
			int target = 0;
			for( target =jslotcounter-1 ; target >=0;target--) {

				if(textlist[target].isFocusOwner()) {
					break;
				}
			}
			if((target<0)) {
				textlist[jslotcounter] = new JTextField(30);
				textlist[jslotcounter].setPreferredSize(new Dimension(350,70));
				textlist[jslotcounter].addActionListener(action);
				this.add(textlist[jslotcounter]);
				textlist[jslotcounter].requestFocus();
				Solution.current.refreshFrame();
			}else {
				target+=1;
				for(int i =jslotcounter ; i >target;i--) {
					textlist[i]=textlist[i-1] ;
					
				}
				textlist[target] = new JTextField(30);
				textlist[target].setPreferredSize(new Dimension(350,70));
				textlist[target].addActionListener(action);
				this.add(textlist[target]);
				textlist[target].requestFocus();
				Solution.current.refreshFrame();
				
			}
			jslotcounter++;
			
		}
		
		
	}
	public static double lerp(double a, double b, double f) {

		return a + f * (b - a);
	}
	double scrollOffset = 0;
	double scrollOffsetR = 0;
	public void refreshButtonBounds() {
		scrollOffset= scroll*scrollSpeed;
		scrollOffsetR=lerp (scrollOffsetR ,scrollOffset , 0.1 );
		for(int i = 0 ; i < jslotcounter;i++) {
			textlist[i].setBounds(0 ,  (int) (i*heightBox+ i * 10 +10 - scrollOffsetR)   , this.getWidth() ,heightBox);
		}
	}
	public String[] getText() {
		if(jslotcounter ==0)return new String[0];
		String[] temp = new String[jslotcounter];
		try {

			for(int i = 0 ; i < jslotcounter;i++) {
				temp[i]= textlist[i].getText();

				if(textlist[i].isFocusOwner()) {
				}
			}
		}catch(Exception e) {
			System.out.println(jslotcounter);
			
		}
		return temp;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		// TODO Auto-generated method stub
		if(x>0 &&x<this.getWidth())
		scroll+=e.getPreciseWheelRotation();
		scroll = clamp(scroll , 0 , 100*heightBox/scrollSpeed);
		
	}
	public double clamp (double n , double min , double max) {
		return Math.min(max, Math.max(min, n));
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		x=e.getX();
	}
}
