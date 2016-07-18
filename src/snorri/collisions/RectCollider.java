package snorri.collisions;

import java.awt.Graphics;
import java.awt.Rectangle;

import snorri.entities.Entity;
import snorri.main.FocusedWindow;
import snorri.world.Vector;

public class RectCollider extends Collider {

	private static final long serialVersionUID = 1L;
	private Vector dim;
	
	/**
	 * @param pos
	 * 	the center of the rectangle
	 * @param dim
	 * 	the dimensions of the rectangle
	 */
	public RectCollider(Vector pos, Vector dim) {
		super(pos);
		this.dim = dim;
	}
	
	public Vector getDimensions() {
		return dim;
	}
	
	public int getWidth() {
		return dim.getX() / 2;
	}
	
	public int getHeight() {
		return dim.getY() / 2;
	}
	
	public Vector getTopLeft() {
		return pos.copy().sub(dim.copy().divide(2));
	}
	
	public Vector getBottomRight() {
		return pos.copy().add(dim.copy().divide(2));
	}
	
	public Vector getTopRight() {
		return pos.copy().add(-dim.getX() / 2, dim.getY() / 2);
	}
	
	public Vector getBottomLeft() {
		return pos.copy().add(dim.getX() / 2, -dim.getY() / 2);
	}
	
	@Override
	public Rectangle getShape() {
		return new Rectangle(pos.getX() - dim.getX() / 2, pos.getY() - dim.getY() / 2, dim.getX(), dim.getY());
//		Vector top = getTopLeft();
//		return new Rectangle(top.getX(), top.getY(), dim.getX(), dim.getY());
	}

	@Override
	public void render(FocusedWindow g, Graphics gr) {
		if (pos == null || g.getFocus().getPos() == null) {
			return;
		}
		Vector rel = pos.copy().sub(g.getFocus().getPos());
		Rectangle rect = getShape();
		int x = (int) (rel.getX() + g.getCenter().getX() - rect.getWidth() / 2);
		int y = (int) (rel.getY() + g.getCenter().getY() - rect.getHeight() / 2);
		gr.drawRect(x, y, (int) rect.getWidth(), (int) rect.getHeight());
	}

	@Override
	public Collider cloneOnto(Entity root) {
		return new RectCollider(root.getPos(), dim.copy());
	}
	
	@Override
	public int getMaxRadius() {
		return (int) (Math.sqrt(getWidth() * getWidth() + getHeight() * getHeight()) + 1);
	}
	
	@Override
	public String toString() {
		return dim.getY() + "x" + dim.getX() + "(r" + getMaxRadius() + ")";
	}
	
	@Override
	public RectCollider copy() {
		return new RectCollider(pos, dim.copy());
	}

}
