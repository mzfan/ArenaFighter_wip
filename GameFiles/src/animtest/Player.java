package animtest;

import java.awt.Rectangle;
import java.awt.event.*;

public class Player extends Sprite implements Commons, KeyListener{
	
	private Animation animation;
	
	private final int START_X = 100;
	private final int START_Y = 200;
	private final int MAX_JUMP = 20;
	
	private int condition; //0 - standing, 1 - walking, 2 - jumping, 3 - attacking, 4 - projectile attack, 5 - damaged
	private boolean flipped, grounded, jumping, falling, autoattacking, specialattacking; //if flipped yes, facing right
	private boolean left, right, up, zKey, xKey; // booleans for if the key is down or not
	private int attQueue;
	private int HEALTHPOINTS;
	private boolean damaged,recoiling;
	private long idleTime;
	
	private long invincTime;
	
	public Player() {
		damaged=false;
		invincTime=0;
		recoiling=false;
		idleTime=0;
		HEALTHPOINTS=100;
		//addKeyListener(this);
		setX(START_X);
		setY(START_Y);
		attQueue=0;
		condition=0;
		flipped = true;
		autoattacking = specialattacking = false;
		grounded=true;
		jumping = falling = false;
	}
	public void setRecoiling(boolean recoiling){
		this.recoiling=recoiling;
	}
	public boolean isRecoiling(){
		return recoiling;
	}
	public boolean isDamaged(){
		return damaged;
	}
	public void setDamaged(boolean dmged){
		this.damaged=dmged;
	}
	
	public void damageHealth(int dmg){
		this.HEALTHPOINTS-=dmg;
	}
	public int getHealth(){
		return HEALTHPOINTS;
	}
	public void setHealth(int hp){
		this.HEALTHPOINTS=hp;
	}
	public boolean isGrounded(){
		return grounded;
	}
	public boolean isJumping(){
		return jumping;
	}
	public boolean isFalling(){
		return falling;
	}
	public boolean isAutoAttacking(){
		return autoattacking;
	}
	public boolean isSpecialAttacking(){
		return specialattacking;
	}
	public void setSpecialAttacking(boolean sa){
		this.specialattacking=sa;
	}
	public void setAutoAttacking(boolean aa){
		this.autoattacking=aa;
	}
	public int getAttQueue(){
		return attQueue;
	}
	public void setAttQueue(int attQueue){
		this.attQueue=attQueue;
	}
	public int getCondition(){
		return condition;
	}
	public synchronized void setCondition(int cond){
		this.condition=cond;
	}
	public boolean isFlipped(){
		return flipped;
	}
	
	public void setAnimation(Animation animation){
		this.animation=animation;
	}
	
	//HIT BOX
	public Rectangle getHitBox(){
		if (condition==1){ // running hitbox
			if (flipped)
				return new Rectangle(x+(64-12-40)*2,y+10*2,40*2,54*2);
			else
				return new Rectangle(x+12*2, y+10*2, 40*2, 54*2);
		}else if (condition==0){ // standing hitbox
			if (flipped)
				return new Rectangle(x+(32-8-20)*2,y+8*2,20*2,54*2);
			else
				return new Rectangle(x+8*2,y+8*2,20*2,54*2);
		}else if (condition==2){ // jumping hitbox
			if (flipped)
				return new Rectangle(x+(36-8-24)*2,y,24*2,58*2);
			else
				return new Rectangle(x+8*2,y,24*2,58*2);
		}else if (condition==3){ // attacking hitbox
			
			if(flipped){
				return new Rectangle(x+(64-48)*2,y,48*2,64*2);
			}else{
				return new Rectangle(x,y,48*2,64*2);
			}			
		}else if (condition==4){ // projectile hitbox
			if (flipped){
				return new Rectangle(x+(64-20-44)*2,y,44*2,64*2);
			}else{
				return new Rectangle(x+20*2,y,44*2,64*2);
			}
		}
		System.out.println("NullHitBox.");
		return new Rectangle();
	}
	
	//ATTACK BOX
	public Rectangle getAttackBox(){
		if (condition==3){
			if (flipped){
				return new Rectangle(x+(64-48)*2,y,48*2,64*2);
			}else{
				return new Rectangle(x,y,48*2,64*2);
			}
		}
		System.out.println("NullAttackBox.");
		return new Rectangle();
	}
	
	public void fall(){ // APPLY GRAVITY

		dy += GRAVITY;
		if (dy > TERMINAL_VELOCITY){
			dy = TERMINAL_VELOCITY;
	    }
		y += dy;
		if (y >= FLOOR_Y){
			y=FLOOR_Y;
			dy = 0;
			grounded=true;
			if (left || right)
				condition = 1;
			else if (!left && !right)
				condition = 0;
			falling = false;
		}
		
	}
	
	public void jump(){ // JUMPING
		 
		if (y <= 125){
			dy += GRAVITY;
		}
		y += dy;
		if (y<=MAX_JUMP){
			y=MAX_JUMP;
			dy = 0;
			jumping = false;
			falling = true;
		}
	}
	
	public void act(long timePassed){ // MOVE X, Y POS
		//System.out.println(x+" "+y+" "+dx+" "+dy+" grounded:"+grounded+" jumping:"+jumping+" falling:"+falling);
		
		// if don't attack again within 80 ms, attQueue resets to 0;
		if (attQueue>0){
			idleTime+=timePassed;
			if (idleTime>1000){
				attQueue=0;
				idleTime=0;
			}
		}
		if (damaged){
			invincTime+=timePassed;
			if (invincTime>500){
				if (left || right)
					condition = 1;
				else if (!left && !right)
					condition = 0;
				recoiling=false;
			}else if (recoiling){
				dx=0;
				condition=5; //damaged, recoiling
			}
			if (invincTime>=2000){
				damaged=false;
				invincTime=0;
			}				
				
		}
		
		x += dx;
		if (jumping)
			jump();
		else if (falling)
			fall();
		if (x <= 8)
			x = 8;
		if (x >= BOARD_WIDTH - 128)
			x = BOARD_WIDTH - 128;
	}
	
	public void keyPressed (KeyEvent e){
		int key = e.getKeyCode();
		//System.out.println(key);
		if (key == KeyEvent.VK_UP){
			up = true;
			if (grounded && !specialattacking && !autoattacking && !recoiling){
				dy = -15;
				condition = 2; //jumping
				grounded = false;
				jumping = true;
			}
		}
		if (key == KeyEvent.VK_LEFT){
			left = true;
			if (!specialattacking && !autoattacking && !recoiling){
				dx = -4;
				if (grounded)
					condition = 1; // walking
				flipped = false; //facing left
			}
		}
		if (key == KeyEvent.VK_RIGHT){
			right = true;
			if (!specialattacking && !autoattacking && !recoiling){
				dx = 4;
				if (grounded)
					condition = 1; //walking
				flipped = true; //facing right
			}
		}
	
		if (key== KeyEvent.VK_Z){ // ATTACK KEY
			zKey=true;
			if (grounded && !specialattacking && !recoiling){
				idleTime=0;
				autoattacking=true;
				attQueue++;
				if (attQueue>3){//max number of attacks for Zkey
					attQueue=3;
				}
				dx=0;
				condition = 3;
			}
		}
		
		if (key==KeyEvent.VK_X){ //projectile shot
			xKey=true;
			if (grounded && !autoattacking && !recoiling){
				specialattacking=true;
				dx=0;
				condition=4;
			}
		}
	
	}
	public void keyReleased (KeyEvent e){
		int key = e.getKeyCode();
		
		if (key== KeyEvent.VK_Z){
			zKey=false;
		}
		if (key==KeyEvent.VK_X){
			xKey=false;
		}
		if (key == KeyEvent.VK_UP){
			up = false;
		}
		
		if (key == KeyEvent.VK_LEFT){
			left = false;
			dx = 0;
			if (grounded && !recoiling)
				condition = 0; //standing
		}
		if (key == KeyEvent.VK_RIGHT){
			right = false;
			dx = 0;
			if (grounded && !recoiling)
				condition = 0; //standing
		}
	}
	public void keyTyped (KeyEvent e){
		
	}


}
