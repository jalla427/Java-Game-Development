package tmp;

import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.Rectangle;

public abstract class GameObject {
		
		protected ID id;
		protected float x, y;
		protected float velX, velY;
		protected int width, height;
		protected int xDirection;
		protected int yDirection;
		protected boolean grounded;
		
		public GameObject(float x, float y, ID id) {
			this.x = x;
			this.y = y;
			this.id = id;
			
			//0 = left/up, 1 = right/down, 2 = neutral
			this.xDirection = 2;
			this.yDirection = 2;
		}

		public abstract void tick();
		public abstract void render(Graphics g);
		public abstract Polygon getBounds();

		//Get/Set
		public void setX(int x) {
			this.x = x;
		}
		public void setY(int y) {
			this.y = y;
		}
		public void setID(ID id) {
			this.id = id;
		}
		public void setVelX(int velX) {
			this.velX = velX;
		}
		public void setVelY(int velY) {
			this.velY = velY;
		}
		public void setxDirection(int xDirection) {
			this.xDirection = xDirection;
		}
		public void setyDirection(int yDirection) {
			this.yDirection = yDirection;
		}
		public void setGrounded(boolean grounded) {
			this.grounded = grounded;
		}
		
		public float getX() {
			return x;
		}
		public float getY() {
			return y;
		}
		public ID getID() {
			return id;
		}
		public float getVelX() {
			return velX;
		}
		public float getVelY() {
			return velY;
		}
		public int getxDirection() {
			return xDirection;
		}
		public int getyDirection() {
			return yDirection;
		}
		public boolean isGrounded() {
			return grounded;
		}
}
