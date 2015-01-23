package animtest;

import java.awt.Image;

public class Sprite {
	
	private Image image;
	private boolean visible;
	protected boolean dying;
	protected int x, y, dx, dy;
	
	//CONSTRUCTOR
	public Sprite(){
		visible = true;
	}
	
	public void die(){
		visible = false;
	}
	
	public void setDying(boolean dying){
		this.dying=dying;
	}
	
	public boolean isDying(){
		return dying;
	}
	
	public boolean isVisible(){
		return visible;
	}
	
	public void setVisible(boolean visible){
		this.visible=visible;
	}
	
	public void setImage(Image image){
		this.image=image;
	}
	
	public Image getImage(){
		return image;
	}
	
	public int getX(){
		return x;
	}
	public int getY(){
		return y;
	}
	public void setX(int x){
		this.x=x;
	}
	public void setY(int y){
		this.y=y;
	}
	public int getDX(){
		return dx;
	}
	public void setDX(int dx){
		this.dx=dx;
	}
	

}
