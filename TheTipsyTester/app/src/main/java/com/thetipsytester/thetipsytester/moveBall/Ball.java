package com.thetipsytester.thetipsytester.moveBall;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import com.thetipsytester.thetipsytester.moveBall.Entity;

public class Ball extends Entity{
	
	public static float RADIUS = 15;
	private Vector2D velocity;

	public Ball(float x, float y) {
		super(x, y);
		velocity = Vector2D.ZERO;
	}
	
	public void accelerate(Vector2D v){
		velocity = velocity.add(v);
	}

	public Vector2D getVelocity() {
		return velocity;
	}

	public void setVelocity(Vector2D velocity) {
		this.velocity = velocity;
	}
	
	public void move(){
		this.x += this.velocity.getX();
		this.y += this.velocity.getY();
	}

	public void setX(int x) {
		this.x = x;
	}
	
	public void setY(int y) {
		this.y = y;
	}

}
