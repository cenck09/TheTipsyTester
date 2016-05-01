package com.thetipsytester.thetipsytester.moveBall;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.util.LinkedList;
import java.util.Random;

import com.thetipsytester.thetipsytester.moveBall.Entity;

public class Field {
	
	public static double FRICTION = 0.06;
	public static double WALL_BOUNCE = 0.75;
	public static double GRAVITY = 0.22;
	private Ball ball;
	private LinkedList<Hole> holes;
	private int height;
	private int width;
	
	public Field(int height, int width) {
		this.height = height;
		this.width = width;
		spawnEntities();
	}
	
	private void spawnEntities(){
		Random r = new Random();
		int margin = (int) (Ball.RADIUS + Hole.RADIUS);
		ball = new Ball(r.nextInt(height - (margin*2) + 1) + margin, r.nextInt(width - (margin*2) + 1) + margin);
		
		holes = new LinkedList<Hole>();
		for(int i = 0; i < 5; i++){
			Hole h = new Hole(r.nextInt(height - (margin*2) + 1) + margin, r.nextInt(width - (margin*2) + 1) + margin);
			do{
				h = new Hole(r.nextInt(height - (margin*2) + 1) + margin, r.nextInt(width - (margin*2) + 1) + margin);
			}while(distanceToBall(h) < (Hole.RADIUS * 3) || minDistanceToHole(h) < Hole.RADIUS * 2);
			holes.add(h);
		}
		
	}

	public Ball getBall() {
		return ball;
	}
	
	public LinkedList<Hole> getHoles() {
		return holes;
	}
	
	public void update(float x, float y){
		getBall().accelerate(new Vector2D(x * Field.GRAVITY, y * Field.GRAVITY));
		getBall().setVelocity(getBall().getVelocity().scalarMultiply(1-Field.FRICTION));
		
		//bounce top or bottom > negative x velocity
		if(getBall().getX() + Ball.RADIUS > height || getBall().getX() - Ball.RADIUS < 0){
			getBall().setVelocity(new Vector2D(-getBall().getVelocity().getX() * Field.WALL_BOUNCE, getBall().getVelocity().getY() * Field.WALL_BOUNCE));
			if(getBall().getX() + Ball.RADIUS > height){
				getBall().setX((int) (height - Ball.RADIUS));
			}else{
				getBall().setX((int) Ball.RADIUS);
			}
		}
		//bounce left or right > negative y velocity
		if(getBall().getY() + Ball.RADIUS > width || getBall().getY() - Ball.RADIUS < 0){
			getBall().setVelocity(new Vector2D(getBall().getVelocity().getX() * Field.WALL_BOUNCE, -getBall().getVelocity().getY() * Field.WALL_BOUNCE));
			if(getBall().getY() + Ball.RADIUS > width){
				getBall().setY((int) (width - Ball.RADIUS));
			}else{
				getBall().setY((int) Ball.RADIUS);
			}
		}
		
		//if the ball comes close to the hole it starts to fall towards it
		for(Hole h : holes){
			if(distanceToBall(h) < Hole.RADIUS){
				getBall().accelerate(new Vector2D((h.getX() - getBall().getX()) * Field.GRAVITY / 5, (h.getY() - getBall().getY()) * Field.GRAVITY / 5));
			}
		}
		
		getBall().move();
	}

	public double distanceToBall(Entity e) {
		return getBall().distanceBetween(e);
	}
	
	private double minDistanceToHole(Entity e) {
		double res = Double.MAX_VALUE;
		for(Hole h : holes){
			if(e.distanceBetween(h) < res)
				res = e.distanceBetween(h);
		}
		return res;
	}

	public int getHeight() {
		return height;
	}
	
	public int getWidth() {
		return width;
	}

}
