package animtest;

import java.awt.*;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class AnimBoard extends JFrame implements Runnable, Commons{

	private Image bg;
	private Image pic;
	private boolean loaded;
	public Animation playerAnim;
	public Animation playerAttAnim;
	private Thread animator;
	private JPanel boardPanel;
	private final int DELAY = 20;
	
	private Player player;
	private Wolf wolf;
	private long attRunTime;
	private int attCounter; // for attacking
	
	private int seq,  timesLooped;
	private CatProjectile flyingcat;
	private boolean projectileloaded;
	
	private boolean playerDmged, wolfDmged;
	private boolean ingame;
	// RUN METHOD
	public AnimBoard(){
		ingame=true;
		projectileloaded = false;
		playerDmged = wolfDmged = false;
		attRunTime=0;
		//addKeyListener(new TAdapter());
		player = new Player();
		attCounter=0;
		seq=0;
		timesLooped=0;
		wolf = new Wolf();
		loaded=false;
		try{
			loadImage();
		}catch(Exception e){
			System.out.println("LoadImage error.");
		}
		boardPanel = new JPanel(){
			
			public void paintComponent(Graphics g){
				super.paintComponent(g);
				int x2,y2;
				int wolfx2,wolfy2;
				Graphics2D g2d = (Graphics2D)g;
				g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				
				if (loaded){
					
					//background draw
					g2d.drawImage(bg,0,0,this);
					// Scaling of wolf and player
					if (player.isAutoAttacking()){
						x2 = (int)Math.round( player.getX()+playerAttAnim.getFrameWidth() *2f);
						y2 = (int)Math.round( player.getY()+playerAttAnim.getFrameHeight() *2f);
					}else if (player.isSpecialAttacking()){
						x2 = (int)Math.round( player.getX()+playerAttAnim.getFrameWidth() *2f);
						y2 = (int)Math.round( player.getY()+playerAttAnim.getFrameHeight() *2f);
					}else{
						x2 = (int)Math.round( player.getX()+playerAnim.getFrameWidth() *2f);
						y2 = (int)Math.round( player.getY()+playerAnim.getFrameHeight() *2f);
					}
					wolfx2 = (int)Math.round( wolf.getX()+wolf.getFrameWidth() *2f);
					wolfy2 = (int)Math.round( wolf.getY()+wolf.getFrameHeight() *2f);
					
					//drawing player and wolf
					if (player.isAutoAttacking()){
						if (player.isFlipped()){
							g2d.drawImage(playerAttAnim.getImage(), x2, player.getY(), player.getX(), y2, playerAttAnim.getSRCX1(), playerAttAnim.getSRCY1(), playerAttAnim.getSRCX2(), playerAttAnim.getSRCY2(), this);
						}else if (!player.isFlipped()){
							g2d.drawImage(playerAttAnim.getImage(), player.getX(), player.getY(), x2, y2, playerAttAnim.getSRCX1(), playerAttAnim.getSRCY1(), playerAttAnim.getSRCX2(), playerAttAnim.getSRCY2(), this);
						}
					}else if (player.isSpecialAttacking()){
						if (player.isFlipped()){
							g2d.drawImage(playerAttAnim.getImage(), x2, player.getY(), player.getX(), y2, playerAttAnim.getSRCX1(), playerAttAnim.getSRCY1(), playerAttAnim.getSRCX2(), playerAttAnim.getSRCY2(), this);
						}else if (!player.isFlipped()){
							g2d.drawImage(playerAttAnim.getImage(), player.getX(), player.getY(), x2, y2, playerAttAnim.getSRCX1(), playerAttAnim.getSRCY1(), playerAttAnim.getSRCX2(), playerAttAnim.getSRCY2(), this);
						}
						if (flyingcat.isFlipped()){
							g2d.drawImage(flyingcat.getMap(), flyingcat.getX()+40*2, flyingcat.getY(), flyingcat.getX(), flyingcat.getY()+32*2, flyingcat.getSRCX1(), flyingcat.getSRCY1(), flyingcat.getSRCX2(), flyingcat.getSRCY2(), this);
						}else if (!flyingcat.isFlipped()){
							g2d.drawImage(flyingcat.getMap(), flyingcat.getX(), flyingcat.getY(), flyingcat.getX()+40*2, flyingcat.getY()+32*2, flyingcat.getSRCX1(), flyingcat.getSRCY1(), flyingcat.getSRCX2(), flyingcat.getSRCY2(), this);
						}
					}else{
						if (player.isFlipped()){
							g2d.drawImage(playerAnim.getImage(), x2, player.getY(), player.getX(), y2, playerAnim.getSRCX1(), playerAnim.getSRCY1(), playerAnim.getSRCX2(), playerAnim.getSRCY2(), this);
						}else if (!player.isFlipped()){
							g2d.drawImage(playerAnim.getImage(), player.getX(), player.getY(), x2, y2, playerAnim.getSRCX1(), playerAnim.getSRCY1(), playerAnim.getSRCX2(), playerAnim.getSRCY2(), this);
						}
					}
					if (wolf.isFlipped()){
						g2d.drawImage(wolf.getSpritemap(), wolfx2, wolf.getY(), wolf.getX(), wolfy2, wolf.getSRCX1(), wolf.getSRCY1(), wolf.getSRCX2(), wolf.getSRCY2(), this);
					}else if (!wolf.isFlipped()){
						g2d.drawImage(wolf.getSpritemap(), wolf.getX(), wolf.getY(), wolfx2, wolfy2, wolf.getSRCX1(), wolf.getSRCY1(), wolf.getSRCX2(), wolf.getSRCY2(), this);
					}
					
					g2d.drawString("Player HP: "+player.getHealth(), 4, 20);
					g2d.drawString("Mob HP: "+wolf.getHealth(), 560, 20);
					
					if (!ingame){ // end game screen
						g2d.drawRect(0, 0, 720, 480);
						g2d.setColor(Color.black);
						g2d.fillRect(0, 0, 720, 480);
						g2d.setColor(Color.white);
						g2d.setFont(new Font("Arial",Font.PLAIN, 48));
						if (player.getHealth()<=0){
							g2d.drawString("YOU DIED", 180, 240);
						}else if(wolf.getHealth()<=0){
							g2d.drawString("YOU WON", 180, 240);
						}
					}
					
					// Housekeeping
					Toolkit.getDefaultToolkit().sync();
					g.dispose();
					
				}
			}
		};
        getContentPane().add(boardPanel);
        addKeyListener(player);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(BOARD_WIDTH, BOARD_HEIGHT);
        setLocationRelativeTo(null);
        setTitle("TWEWY: Arena Mode");
        setResizable(false);
        setVisible(true);
        		
		boardPanel.addKeyListener(player);
		boardPanel.setBackground(Color.BLACK);
		boardPanel.setForeground(Color.RED);
		boardPanel.setDoubleBuffered(true);
		boardPanel.setFont(new Font("Arial",Font.PLAIN, 24));
	
	}
	
	//addNotify THREAD
	public void addNotify(){
		super.addNotify();
		animator = new Thread(this);
		animator.start();
	}
	
	//load image resources
	public void loadImage(){
		
		bg = new ImageIcon(getClass().getResource("towarecords.png")).getImage();
		Image spritemap = new ImageIcon(getClass().getResource("shikisprite.gif")).getImage();
		Image attspritemap = new ImageIcon(getClass().getResource("shikisprite_attack.gif")).getImage();
		playerAttAnim= new Animation();
		playerAttAnim.addFrame(attspritemap, PLAYER_FRAME_SPEED-150);
		playerAnim = new Animation();
		playerAnim.addFrame(spritemap, PLAYER_FRAME_SPEED);
		
		loaded = true;
	}
	// HANDLES THE PROJECTILE ATTACK OF THE PLAYER
	public synchronized void playerSpecHandle(){
		
		playerAttAnim.manualUpdate(1);		
		player.setDX(0);
		if (!projectileloaded){
			flyingcat = new CatProjectile(player.getX()+32,player.getY()+32, player.isFlipped());
			projectileloaded=true;
		}
		flyingcat.move();
		if (flyingcat.getX()<-80 || flyingcat.getX()>BOARD_WIDTH){
			flyingcat.die();
			flyingcat.setX(720);
			flyingcat.setY(480);
			projectileloaded=false;
			player.setSpecialAttacking(false);
		}
	}
	
	// METHOD FOR MANAGING ATTACKS AND HEALTH DEDUCTIONS
	public void damageCheck(){
		// checking player's autoattack
		if (player.isAutoAttacking() && !wolf.isDamaged()){
			Rectangle playerattackbox = player.getAttackBox();
			Rectangle wolfhitbox = wolf.getHitBox();
			if (playerattackbox.intersects(wolfhitbox)){
				wolf.damageHealth(5);
				if (wolf.getHealth()<0){
					wolf.setHealth(0);
				}
				wolf.setDamaged(true);
				wolf.setRecoiling(true);
			}
		}
		//checking player's ranged projectile
		if (player.isSpecialAttacking() && !wolf.isDamaged()){
			Rectangle playerattackbox = flyingcat.getAttackBox();
			Rectangle wolfhitbox = wolf.getHitBox();
			if (playerattackbox.intersects(wolfhitbox)){
				wolf.damageHealth(15);
				if (wolf.getHealth()<0){
					wolf.setHealth(0);
				}
				wolf.setDamaged(true);
				wolf.setRecoiling(true);
			}
		}
		//	checking wolf's attack 
		if (wolf.getCondition()==2 && !player.isDamaged()){
			Rectangle playerhitbox = player.getHitBox();
			Rectangle wolfattackbox = wolf.getAttackBox();
			if (playerhitbox.intersects(wolfattackbox)){
				player.damageHealth(15);
				if (player.getHealth()<0){
					player.setHealth(0);
				}
				player.setCondition(5);
				player.setDamaged(true);
				player.setRecoiling(true);
			}
		}
		
	}
	
	public void playerAttHandle(long timePassed){ // handles the player's non-special attack animation frames

		if (attCounter<4 && attCounter!=0){
			if(timesLooped<player.getAttQueue()){
				attRunTime+=timePassed;
				if (attRunTime >=80){
					seq++;
					attRunTime=0;
				}
				if (seq>1){
					seq=0;
					timesLooped++;
				}else
					playerAttAnim.manualUpdate(seq);
			}else if (player.getAttQueue()<4){
				player.setAttQueue(0);
				timesLooped=0;
				player.setAutoAttacking(false);
				
			}
		}
//		}else if (attCounter==4){  -- DOESNT WORK, NOT BEING USED.
//			uppercutRunTime+=timePassed;
//			if (uppercutRunTime >=80){
//				uppercutseq++;
//				uppercutRunTime=0;
//			}
//			if (uppercutseq>5){
//				uppercutseq=2;
//				timesLooped=0;
//				player.setAutoAttacking(false);
//			}else
//				playerAttAnim.manualUpdate(uppercutseq);
//		}
		
		// resetting the attacks when it reaches full loop
		if (attCounter > player.getAttQueue()){
			attCounter =0;
		}else{ //attack # increases
			attCounter++;
		}
	}
	
	// MAIN GAME LOOP
	public void run(){ 
		long beforeTime, timeDiff, sleep; //threading variables
		int firstFrame = 0; // for the jump animation
		int currentCond=player.getCondition(),oldCond=currentCond; //for reseting animation frames
		beforeTime = System.currentTimeMillis();
		
		while (ingame){
			
			// CHECK FOR PLAYER CONDITION AND RESETS THE ANIMATION FRAMES TO INITIAL.
			currentCond = player.getCondition();
			if (currentCond!=oldCond  && !player.isGrounded()){
				// jumping special case
				playerAnim.setFrameSeries(2);
				playerAnim.setTotalFrames(3);
				playerAnim.setFrameHeight(64);
				playerAnim.setFrameWidth(36);
				firstFrame=0;
			}else if (currentCond!=oldCond && player.isGrounded()){
				//Frame Series check
				if ( player.getCondition()==0){ // standing
					playerAnim.setFrameSeries(1);
					playerAnim.setTotalFrames(4);
					playerAnim.setFrameHeight(64);
					playerAnim.setFrameWidth(32);
					playerAnim.setEndTimes(PLAYER_FRAME_SPEED);
					
				}else if ( player.getCondition()==1){ // running/walking
					playerAnim.setFrameSeries(0);
					playerAnim.setTotalFrames(8);
					playerAnim.setFrameHeight(64);
					playerAnim.setFrameWidth(64);
					playerAnim.setEndTimes(PLAYER_FRAME_SPEED-150);
					
				}else if (player.getCondition()==3){
					//attacking special case
					playerAttAnim.setFrameSeries(0);
					playerAttAnim.setTotalFrames(6);
					playerAttAnim.setFrameHeight(64);
					playerAttAnim.setFrameWidth(64);
					playerAttAnim.restart();
				}else if (player.getCondition()==4){
					//projectile attack special case
					playerAttAnim.setFrameSeries(1);
					playerAttAnim.setTotalFrames(2);
					playerAttAnim.setFrameHeight(64);
					playerAttAnim.setFrameWidth(64);
					playerAttAnim.restart();
				}else if(player.getCondition()==5){
					// damaged, recoil frame
					playerAnim.setFrameSeries(3);
					playerAnim.setTotalFrames(1);
					playerAnim.setFrameHeight(64);
					playerAnim.setFrameWidth(64);
				}
				playerAnim.restart();
				
			}
			oldCond=currentCond;
			
			//movement and repainting
			wolf.move();
			boardPanel.repaint();			
			
			//threading
			timeDiff = System.currentTimeMillis() - beforeTime;
            sleep = DELAY - timeDiff;
            if (sleep < 0)
                sleep = 2;
            
            //WOLF AI + ANIMATION UPDATING
            player.act(sleep);
            wolf.act(sleep, player.getX(), player.getY(), playerDmged);
            if (player.isGrounded() && !player.isRecoiling()){ // ATTACKING AND MOVEMENT ANIM HANDLING
            	if (player.isAutoAttacking())
            		playerAttHandle(sleep);
            	else if (player.isSpecialAttacking())
            		playerSpecHandle();
            	else
            		playerAnim.update(sleep);
            	
            }else if(player.isRecoiling()){
            	playerAnim.manualUpdate(0);
            }else{ // JUMPING ANIMATION HANDLING
            	if (firstFrame<5){
            		playerAnim.manualUpdate(player,firstFrame);
            		firstFrame++;
            	}else{
            		playerAnim.manualUpdate(player,firstFrame);
            	}            	
            }
            damageCheck();
            if (player.getHealth()<=0 || wolf.getHealth()<=0){
            	ingame=false;
            }
            //threading --constant fps maintenance--
            try {
                Thread.sleep(sleep);
            } catch (InterruptedException e) {
                System.out.println("interrupted");
            }
            
            beforeTime = System.currentTimeMillis();
				
		}
		repaint();
		
	}
	/*
	// PRIVATE TAdapter -- HANDLES PLAYER INPUT
	private class TAdapter extends KeyAdapter{
	
		public void keyReleased (KeyEvent e){
			player.keyReleased(e);
		}
		
		public void keyPressed (KeyEvent e){
			player.keyPressed(e);
			System.out.println(e.getKeyCode());
		}
		
		public void keyTyped (KeyEvent e){
			player.keyTyped(e);
		}
	}
	*/
}