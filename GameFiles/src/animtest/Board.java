package animtest;

import java.awt.*;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;

import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Board extends JPanel{

	private Image bg;
	private Image pic;
	private boolean loaded;
	
	// RUN METHOD
	public Board(){
		setBackground(Color.PINK);
		setForeground(Color.WHITE);
		setDoubleBuffered(true);
		setFont(new Font("Arial",Font.PLAIN, 24));
		loaded=false;
		
		try{
			loadImage();
		}catch(Exception e){}
	}
	
	//load images
	public void loadImage(){
		//pic = new ImageIcon(getClass().getResource("rawr_emot.jpg")).getImage();
		bg = new ImageIcon(getClass().getResource("towarecords.png")).getImage();
		loaded = true;
		repaint();
	}
	
	public void paint(Graphics g){
		if (g instanceof Graphics2D){
			Graphics2D g2 = (Graphics2D)g;
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		}
		if (loaded){
			g.drawImage(bg,0,0,null);
			//g.drawImage(pic,170,180,null);
		}
	}
}
