package snorri.entities;

import java.util.ArrayList;
import java.util.List;

import snorri.animations.Animation;
import snorri.collisions.Collider;
import snorri.collisions.RectCollider;
import snorri.events.SpellEvent;
import snorri.modifiers.Modifier;
import snorri.pathfinding.Team;
import snorri.triggers.Trigger.TriggerType;
import snorri.world.Vector;
import snorri.world.World;

public abstract class Unit extends Entity {

	private static final long serialVersionUID = 1L;
	private static final int BASE_SPEED = 120;
	/**Dimensions for humanoid units*/
	public static final int RADIUS = 46, RADIUS_X = 21, RADIUS_Y = 40;
	protected static final double MAX_HEALTH = 100;
	
	protected List<Modifier<Unit>> modifiers = new ArrayList<>();
	
	protected int speed;
	private Team team;
	private double health;
	
	protected Animation walkingAnimation;
	protected Animation idleAnimation;
		
	protected Unit(Vector pos, Animation idle, Animation walking) {
		this(pos, new RectCollider(new Vector(2 * RADIUS_X, 2 * RADIUS_Y)));
		animation = idle;
		idleAnimation = idle;
		walkingAnimation = walking;
	}
		
	/**
	 * Use this constructor to build non-humanoid units
	 * @param pos
	 * 	Position to spawn the unit at
	 * @param c
	 * 	Collider for the unit
	 */
	protected Unit(Vector pos, Collider c) {
		super(pos, c);
		health = MAX_HEALTH;
		z = UNIT_LAYER;
	}

	@Override
	public void update(World world, double deltaTime) {
		
		speed = getBaseSpeed();
		
		if (modifiers == null) {
			modifiers = new ArrayList<>();
		}
		
		for (Object o : modifiers.toArray()) {
			@SuppressWarnings("unchecked")
			Modifier<Unit> m = (Modifier<Unit>) o;
			if (m.modify(this, deltaTime)) {
				modifiers.remove(m);
			}
		}
		
		if (isDead()) {
			world.delete(this);
			TriggerType.KILL.activate(tag);
		}
		
		super.update(world, deltaTime);
		
	}

	public void walk(World world, Vector direction, double deltaTime) {
		
		animation = (direction.magnitude() == 0) ? idleAnimation : walkingAnimation;
		if (direction.getX() != 0 && animation != null) {
			walkingAnimation.flip(direction.getX() > 0);
			idleAnimation.flip(direction.getX() > 0);
		}
		
		moveHard(world, direction.copy().normalize(), getSpeed() * deltaTime);
	}
	
	public void walkTo(World world, Vector target, double deltaTime) {
		walk(world, target.copy().sub(pos), deltaTime);
	}
	
	public double getHealth() {
		return health;
	}
	
	public void damage(double d) {
		health -= d;
	}
	
	public void damage(double d, SpellEvent e) {
		heal(e.modifyHealthInteraction(d));
	}
	
	public void heal(double d) {
		health += d;
		if (health > MAX_HEALTH) {
			health = MAX_HEALTH;
		}
	}
	
	public void heal(double d, SpellEvent e) {
		heal(e.modifyHealthInteraction(d));
	}
	
	public boolean isDead() {
		return health <= 0;
	}
	
	//override this for faster entities
	protected final int getSpeed() {
		return speed;
	}
	
	public int getBaseSpeed() {
		return BASE_SPEED;
	}
	
	public void setSpeed(int spd) {
		speed = spd;
	}
	
	public void modifySpeed(double factor) {
		speed = (int) (speed * factor);
	}
	
	@Override
	public Object get(World world, AbstractSemantics attr) {
		
		if (attr == AbstractSemantics.HEALTH) {
			return (int) health;
		}
		
		return super.get(world, attr);
		
	}
	
	public List<Modifier<Unit>> getModifiers() {
		return modifiers;
	}
	
	public boolean hasModifier(Class <? extends Modifier<Unit>> c) {
		for (Modifier<Unit> modifier : modifiers) {
			if (c.isInstance(modifier)) {
				return true;
			}
		}
		return false;
	}

	public void addModifier(Modifier<Unit> m) {
		modifiers.add(m);
	}
	
	public void removeModifier(Modifier<Unit> m){
		modifiers.remove(m);
	}
	
	public void setTeam(Team team) {
		this.team = team;
	}
	
	public Team getTeam() {
		if (team == null) {
			Team out = new Team();
			out.add(this);
			return out;
		}
		return team;
	}
	
}
