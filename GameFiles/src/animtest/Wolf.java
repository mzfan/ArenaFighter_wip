package animtest;

import java.awt.Image;
import java.awt.Rectangle;

import javax.swing.ImageIcon;

public class Wolf extends Sprite implements Commons{
	
	private final int START_X = 620;
	private final int START_Y = 192;
	
	private int condition, randomAct, currentCond, oldCond; // current animation series, 0-standing,1-running,2-attacking,3-damaged
	private boolean flipped;
	private boolean acting, idling;
	
	private long runTime, maxTimeIdle, maxTimeMove;
	private Animation wolfAnim;
	private Image wolfmap;
	
	private int attackSeq;
	
	private int HEALTHPOINTS;
	private boolean damaged, recoiling;
	
	private long invincTime;
	
	public Wolf(){
		HEALTHPOINTS=100;
		damaged=false;
		recoiling=false;
		invincTime=0;
		try{
			wolfmap = new ImageIcon(getClass().getResource("wolfsprite.gif")).getImage();
			wolfAnim = new Animation();
			wolfAnim.addFrame(wolfmap, PLAYER_FRAME_SPEED);
		}catch (Exception e){
			System.out.println("load wolf sprite error.");
		}
		setX(START_X);
		setY(START_Y);
		randomAct = 0;
		attackSeq=0;
		condition = 0;
		wolfAnim.setFrameSeries(0);
		wolfAnim.setTotalFrames(6);
		wolfAnim.setFrameHeight(72);
		wolfAnim.setFrameWidth(72);
		wolfAnim.setEndTimes(PLAYER_FRAME_SPEED);
		oldCond= -1;
		dx = dy = 0;
		maxTimeIdle=1000 + (long)(Math.random()*1000);
		flipped = false;
		acting = true; // shows that mob is currently doing something
	}
	
	public Image getSpritemap(){
		return wolfmap;
	}
	public void setRecoiling(boolean recoiling){
		this.recoiling=recoiling;
	}
	public boolean isDamaged(){
		return damaged;
	}
	public void setDamaged(boolean dmged){
		this.damaged=dmged;
	}
	public int getCondition(){
		return condition;
	}
	public boolean isFlipped(){
		return flipped;
	}
	
	public int getSRCX1(){
		return wolfAnim.getSRCX1();
	}
	public int getSRCY1(){
		return wolfAnim.getSRCY1();
	}
	public int getSRCX2(){
		return wolfAnim.getSRCX2();
	}
	public int getSRCY2(){
		return wolfAnim.getSRCY2();
	}
	
	public int getFrameWidth(){
		return wolfAnim.getFrameWidth();
	}
	public int getFrameHeight(){
		return wolfAnim.getFrameHeight();
	}
	
	public int getHealth(){
		return HEALTHPOINTS;
	}
	public void damageHealth(int dmg){
		this.HEALTHPOINTS-=dmg;
	}
	public void setHealth(int hp){
		this.HEALTHPOINTS=hp;
	}
	
	//HITBOXES, ATTACKBOXES
	public Rectangle getHitBox(){
		if (condition==1){ // running hitbox
			if (flipped)
				return new Rectangle(x+(106-86)*2,y+30*2,86*2,30*2);
			else
				return new Rectangle(x, y+30*2, 86*2, 30*2);
		
		}else if (condition==0){ // standing hitbox
			if (flipped)
				return new Rectangle(x+(72-22-44)*2,y+30*2,44*2,42*2);
			else
				return new Rectangle(x+22*2,y+30*2,44*2,42*2);
		
		}
		System.out.println("NullHitBox.");
		return new Rectangle();
	}
	
	//ATTACK BOX
	public Rectangle getAttackBox(){
		if (condition==2){
			if (flipped){
				return new Rectangle(x+(150-90)*2,y+34*2,90*2,32*2);
			}else{
				return new Rectangle(x,y+34*2,90*2,32*2);
			}
		}
		System.out.println("NullAttackBox.");
		return new Rectangle();
	}	
	
	public void move(){ //movement handling
		x += dx;
		y += dy;
		if (x <= 8){
			if (condition==1){
				dx = -dx;
				flipped = true;
			}else 
				x = 8;
		}else if (x >= BOARD_WIDTH - 128){
			if (condition==1){
				dx = -dx;
				flipped = false;
			}else
				x =BOARD_WIDTH - 128;
		}
	}
	
	
	// WOLF MOB AI HANDLING
	public void act(long timePassed, int playerXpos, int playerYpos, boolean dmged){
				
		currentCond = condition;
		if (currentCond!=oldCond){
			//Frame Series check
			if (condition==0){ // standing
				wolfAnim.setFrameSeries(0);
				wolfAnim.setTotalFrames(6);
				wolfAnim.setFrameHeight(72);
				wolfAnim.setFrameWidth(72);
				wolfAnim.setEndTimes(PLAYER_FRAME_SPEED);
				
			}else if (condition==1){ // running/walking
				wolfAnim.setFrameSeries(1);
				wolfAnim.setTotalFrames(5);
				wolfAnim.setFrameHeight(72);
				wolfAnim.setFrameWidth(106);
				wolfAnim.setEndTimes(PLAYER_FRAME_SPEED-150);
				
			}else if (condition==2){ // attacking (special case)
				wolfAnim.setFrameSeries(2);
				wolfAnim.setTotalFrames(5);
				wolfAnim.setFrameHeight(72);
				wolfAnim.setFrameWidth(150);
			}else if(condition==3){ // damaged
				wolfAnim.setFrameSeries(3);
				wolfAnim.setTotalFrames(1);
				wolfAnim.setFrameHeight(72);
				wolfAnim.setFrameWidth(72);
			}
			wolfAnim.restart();
		}
		oldCond=currentCond;
		
		if(damaged){
			
			invincTime+=timePassed;
			if (invincTime>500 && invincTime<999){
				invincTime=1000;
				condition=1;
				randomAct=1;
				recoiling=false;
				acting = false;
			}else if (recoiling){
				condition=3;
				acting =true;
				randomAct=-1;
				dx=0;
				wolfAnim.manualUpdate(0);
			}
			if (invincTime>=2500){
				damaged=false;
				invincTime=0;
			}
						
		}	
			
		if (condition!=2 && !recoiling)
			wolfAnim.update(timePassed);
		else if(condition==3 && recoiling){
			wolfAnim.manualUpdate(0);
		}
		
		if (randomAct==0){ //random act idle
			condition = 0; // stand
			acting = true;
			runTime += timePassed;
			if (runTime >= maxTimeIdle){
				runTime = 0;
				acting = false;
				
			}
			dx = 0;
		
		}else if(randomAct>=1 && randomAct<=2){ // random act chase player
			if (!acting){
				if (x-playerXpos >= 0){ // move towards the player
					dx = -5;
					flipped = false;
				}else{
					dx = 5;		
					flipped = true;
				}
			}
			acting = true;
			condition = 1; // move
			runTime += timePassed;
			if (runTime >= maxTimeMove){
				runTime = 0;
				acting = false;
			}
			
		}else if(randomAct==3){ // attack (Special case)
			if (!acting){
				if (x-playerXpos >= 0){ // move towards the player
					dx = -10;
					flipped = false;
				}else{
					dx = 10;		
					flipped = true;
				}
			}
			condition = 2; // attack
			acting = true;
			if (attackSeq<2){
				wolfAnim.manualUpdate(attackSeq);
				runTime += timePassed;
				if (runTime>=100){
					runTime=0;
					attackSeq++;
				}
			}else if(attackSeq>=2 && attackSeq<=5){
				wolfAnim.manualUpdate(attackSeq);
				if (x >= BOARD_WIDTH - 128 || x<=8){
					runTime += timePassed;
					if (runTime>=100){
						runTime=0;
						attackSeq++;
					}
				}
			}else if(attackSeq>5){
				runTime=0;
				if (x>=BOARD_WIDTH -128)
					flipped = false;
				else if (x<=8)
					flipped = true;
				condition=0;
				randomAct=0;
				attackSeq=0;
			}
		}
		
		if (!acting){ // if not doing something, generate something to do
			randomAct = (int)(Math.random()*4); // 0 is stand, 1-2 move, 3 attack
			maxTimeIdle=1000 + (long)(Math.random()*1000);
			maxTimeMove=1000 + (long)(Math.random()*1000);
		}
	
	}
	
}
