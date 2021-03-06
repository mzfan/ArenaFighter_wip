package animtest;

import java.awt.Frame;
import java.awt.Image;
import java.util.ArrayList;

public class Animation { ///////// WORK IN PROGRESS /////////////
	
	private ArrayList frames;
	private ArrayList endTimes;
	private int sequences;
	private long animTime;
	private long frameTime;
	private long animTotalTime;
	
	private Image spritemap;
	
	private int frameSeries, totalFrames, frameWidth, frameHeight;
	
	private int srcx1,srcy1,srcx2,srcy2;
			
	// ANIM CONSTRUCTOR
	public Animation(){
		frames = new ArrayList();
		endTimes = new ArrayList();
		animTotalTime=0;
		totalFrames=4;
		frameWidth=32;
		frameHeight=64;
		frameSeries=1;
		restart();
	}
	
	// public getters for source coordinates
	public synchronized int getSRCX1(){
		return srcx1;
	}
	public synchronized int getSRCY1(){
		return srcy1;
	}
	public synchronized int getSRCX2(){
		return srcx2;
	}
	public synchronized int getSRCY2(){
		return srcy2;
	}
	
	//SPRITE MAP SETTING VARIABLES, GET HEIGHT WIDTH
	public synchronized void setFrameSeries(int num){
		this.frameSeries = num;
	}
	
	public synchronized void setTotalFrames(int num){
		this.totalFrames = num;
	}
	
	public synchronized void setFrameWidth(int num){
		this.frameWidth = num;
	}
	
	public synchronized void setFrameHeight(int num){
		this.frameHeight = num;
	}
	public synchronized int getFrameWidth(){
		return frameWidth;
	}
	
	public synchronized int getFrameHeight(){
		return frameHeight;
	}	
	
	//debug
	public synchronized int getSequence(){
		return sequences;
	}
	public synchronized long getAnimTime(){
		return animTime;
	}
	public synchronized int getFrameSize(){
		return frames.size();
	}
	
	// Sets up Array of endTimes for each frame
	public synchronized void setEndTimes(long t){
		endTimes.clear();
		frameTime=t;
		for (int i=1;i<=totalFrames;i++){
			endTimes.add(t * i);
		}
	}
	
	// Adds frame to ArrayList, sets animation time
	public synchronized void addFrame(Image i, long t){
		frameTime = t;
		animTotalTime += t;
		spritemap = i;
		setEndTimes(t);
	}
	
	// Start Animation from beginning
	public synchronized void restart(){
		animTime=0;
		sequences=0;
	}
	
	// change frame
	public synchronized void update(long timePassed){
		
		//set animTotalTime based on total frames
		animTotalTime=frameTime * totalFrames;		
		
		// source y1,y2 set
		srcy1 = frameSeries * frameHeight;
		srcy2 = srcy1 + frameHeight;
		
		if (endTimes.size()>0){
			animTime += timePassed; // time anim has been running
			if(animTime >= animTotalTime){ // reset frames
				animTime=0;
				sequences=0;
			}
			while(animTime > ((Long) endTimes.get(sequences))){ // looping frame sequences
				//System.out.println(sequences+" "+totalFrames);
				
				sequences++;
								
				//System.out.println(animTime+" "+((Long) endTimes.get(sequences))+" "+sequences);
				
			}
			srcx1 = sequences * frameWidth;
			srcx2 = srcx1 + frameWidth;
		}
	}
	
	public synchronized void manualUpdate(int seq){ //manually set the x sequence on the source map
		sequences = seq;
		
		// source y1,y2 set
		srcy1 = frameSeries * frameHeight;
		srcy2 = srcy1 + frameHeight;
		
		srcx1 = sequences * frameWidth;
		srcx2 = srcx1 + frameWidth;
	}
	
	//lazycode, manual update for jumping
	public synchronized void manualUpdate(Player player, int firstFrame){
				
		// source y1,y2 set
		srcy1 = frameSeries * frameHeight;
		srcy2 = srcy1 + frameHeight;
		
		if (firstFrame<5){
			sequences = 0;
		}else{
			if (player.isJumping())
				sequences=1;
			else if (player.isFalling())
				sequences=2;
		}
		
		srcx1 = sequences * frameWidth;
		srcx2 = srcx1 + frameWidth;
		
	}
	
	// Get current frame as Image object
	public synchronized Image getImage(){
		
		return spritemap;
		
		/*if(frames.size()==0){
			return null;
		}else{
			return getFrame(sequences).pic;
		}*/
	}
	/*
	// Get frame
	private OneFrame getFrame(int x){
		return (OneFrame)frames.get(x);
	}

////// PRIVATE SUBCLASS OneFrame ///////////
	
	private class OneFrame{
		Image pic;
		long endTime;
		
		public OneFrame(Image pic, long endTime){
			this.pic=pic;
			this.endTime=endTime;
		}
	}*/
}
