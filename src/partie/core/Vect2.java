package partie.core;

import java.io.Serializable;

public class Vect2 implements Serializable {
	
	public float x, y;
	
	public Vect2() {
		this.x = 0;
		this.y = 0;
	}
	public Vect2(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public void setPos(float x, float y) {
		this.x = x;
		this.y = y;
	}
	public void setPos(Vect2 pos) {
		this.x = pos.x;
		this.y = pos.y;
	}
	public void setPosX(float x) {
		this.x = x;
	}
	public void setPosY(float y) {
		this.y = y;
	}

}
