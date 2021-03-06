package snorri.entities;

import java.util.ArrayDeque;
import java.util.ArrayList;

import snorri.animations.Animation;
import snorri.inventory.Carrier;
import snorri.inventory.Inventory;
import snorri.inventory.Item;
import snorri.inventory.Item.ItemType;
import snorri.inventory.Orb;
import snorri.inventory.Weapon;
import snorri.main.Main;
import snorri.main.GameWindow;
import snorri.pathfinding.PathNode;
import snorri.pathfinding.Pathfinder;
import snorri.pathfinding.Pathfinding;
import snorri.pathfinding.Targetter;
import snorri.world.Vector;
import snorri.world.World;

public class Enemy extends Unit implements Pathfinder, Carrier, Targetter {

	private static final Animation MUMMY_IDLE = new Animation("/textures/animations/mummy/idle");
	
	private static final long serialVersionUID = 1L;
	private static final double APPROACH_MARGIN = 15;
	private static final double CHANGE_PATH_MARGIN = 350;
	
	private Vector lastSeenPos;
	private boolean recalculatingPath = false;
	
	protected double seekRange = 1000;
	protected double attackRange = 450;
	
	protected Inventory inventory;
	protected Entity target;
	
	private ArrayDeque<PathNode> path;
	
	public Enemy(Vector pos, Entity target) {
		super(pos, new Animation(MUMMY_IDLE), new Animation(MUMMY_IDLE));
		this.target = target;
		inventory = new Inventory(this);
		getInventory().add(Item.newItem(ItemType.PELLET));
		getInventory().add(Item.newItem(ItemType.SLOW_SLING));
	}
	
	public Enemy(Vector pos) {
		this(pos, null);
	}

	public void setTarget(Entity target) {
		this.target = target;
	}
	
	public void setWeapon(Weapon weapon) {
		inventory.setWeapon(weapon);
	}
	
	public void setOrb(Orb orb) {
		inventory.setOrb(0, orb);
	}
	
	/**
	 * does a grid raycast to detect unpathable terrain blocking the shot
	 * @param target
	 * 		the entity we're tryna cap
	 * @return
	 * 		whether terrain obstructs the shot
	 */
	public boolean canShootAt(World world, Entity target) {
		
		if (target.pos.distanceSquared(pos) > attackRange * attackRange) {
			return false;
		}
		
		Vector step = target.pos.copy().sub(pos).normalize();
		Vector tempPos = pos.copy();
		
		//I'm checking if pos and target.pos are both okay just in case we're in a wall
		while (tempPos.distanceSquared(pos) <= target.pos.distanceSquared(pos)) {	
		
			if (! world.getLevel().canShootOver(tempPos.copy().toGridPos())) {
				return false;
			}
			
			if (tempPos.distanceSquared(pos) > attackRange * attackRange) {
				return false;
			}
			
//			Entity col;
//			if ((col = world.getEntityTree().getFirstCollision(new Entity(tempPos))) != null
//					&& col != this && col != target) {
//				return false;
//			}
			
			tempPos.add(step);	
		}

		return true;
		
	}
	
	public void shootAt(World world, Entity e) {
		if (inventory == null) {
			return;
		}
		inventory.tryToShoot(world, this, Vector.ZERO.copy(), e.getPos().copy().sub(pos));
	}
	
	//TODO don't walk into each other
	
	@Override
	public void update(World world, double deltaTime) {
		
		inventory.update(deltaTime);
		super.update(world, deltaTime);
		
		if (target == null) {
			if (Main.getWindow() instanceof GameWindow) {
				setTarget(((GameWindow) Main.getWindow()).getFocus());
			} else {
				return;
			}
		}
							
		if (canShootAt(world, target)) {
			shootAt(world, target);
			return;
		}
				
		ArrayList<Vector> graph = world.getLevel().getGraph(this);
		ArrayList<Vector> targetGraph = world.getLevel().getGraph(target);
				
		if (path != null) {
			
			if (target.pos.distanceSquared(lastSeenPos) > CHANGE_PATH_MARGIN * CHANGE_PATH_MARGIN
					&& targetGraph == graph && graph != null) {
				stopPath();
			} else {
				follow(world, deltaTime);
			}

		} else if (!recalculatingPath && (pos.distanceSquared(target.pos) <= seekRange * seekRange)) {
			
			if (graph != null && graph == targetGraph) {
				startPath();
			}

		}
				
	}

	private void follow(World world, double deltaTime) {
		
		if (path.peek().getGlobalPos().distance(this.getPos()) < APPROACH_MARGIN) {
			path.pop();
		}
		
		if (path.isEmpty()) {
			stopPath();
			return;
		}
		
		walkTo(world, path.peek().getGlobalPos(), deltaTime);
		
	}

	@Override
	public void setPath(ArrayDeque<PathNode> stack) {
		recalculatingPath = false;
		if (stack != null) {
			path = stack;
		}
	}
	
	public void recalculatePath() {
		recalculatingPath = true;
	}
	
	public void startPath() {
		
		if (pos == null) {
			return;
		}
				
		Pathfinding.setPathAsync(pos.copy().toGridPos(), target.pos.copy().toGridPos(), this);
		lastSeenPos = target.pos.copy(); //don't put this in other thing
		recalculatingPath = true;
	}
	
	public void stopPath() {
		path = null;
		recalculatingPath = false;
	}

	@Override
	public Inventory getInventory() {
		return inventory;
	}

}
