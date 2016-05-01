package com.thetipsytester.thetipsytester.moveBall;


public abstract class Entity {
	
	protected float x;
	protected float y;

	public Entity(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public float getX(){
		return x;
	}
	
	public float getY(){
		return y;
	}
	
	public double distanceBetween(Entity e) {
		return Math.sqrt(Math.pow(e.getX() - this.getX(), 2) + Math.pow(e.getY() - this.getY(), 2));
	}
	
}
