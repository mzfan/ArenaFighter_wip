package animtest;

import java.awt.Image;
import java.awt.Rectangle;

import javax.swing.ImageIcon;

public class CatProjectile extends Sprite implements Commons{
		
	private Image flyingcat;
	private boolean flipped=true;
	public CatProjectile(int x, int y, boolean flipped){
		
		setX(x);
		setY(y);
		this.flipped = flipped;
		try{
			flyingcat = new ImageIcon(getClass().getResource("shikisprite_attack.gif")).getImage();
			
		}catch (Exception e){
			System.out.println("load cat sprite error.");
		}
		if (flipped)
			dx = 8;
		else
			dx = -8;
	}
	
	public void move(){
		x += dx;
	}
	
	public boolean isFlipped(){
		return flipped;
	}
	
	public Image getMap(){
		return flyingcat;
	}
	
	public synchronized Rectangle getAttackBox(){
		return new Rectangle(getX(),getY(),40 * 2,32 * 2);
	}
	
	// HARD SOURCED SPRITE LOCATION
	public int getSRCX1(){
		return 0;
	}
	public int getSRCY1(){
		return 128;
	}
	public int getSRCX2(){
		return 40;
	}
	public int getSRCY2(){
		return 128+32;
	}

}
