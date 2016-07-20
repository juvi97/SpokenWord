package snorri.entities;

import snorri.animations.Animation;
import snorri.events.CollisionEvent;
import snorri.inventory.Droppable;
import snorri.main.GameWindow;
import snorri.main.Main;
import snorri.world.Vector;

public class Drop extends Detector {
	
	private static final long serialVersionUID = 1L;
	private final Droppable prize;
	
	public Drop(Vector pos, Droppable prize) {
		super(pos, 15);
		this.prize = prize;
		treeMember = true;
		age = -1;
		ignoreCollisions = true;
		animation = new Animation(Animation.SPARKLE);
	}
	
	public Drop(Vector pos, String prize) {
		this(pos, Droppable.fromString(prize));
	}
	
	public Droppable getPrize() {
		return prize;
	}
	
	@Override
	public void onCollision(CollisionEvent e) {
		if (e.getTarget() instanceof Player) {
			if (Main.getWindow() instanceof GameWindow) {
				((GameWindow) Main.getWindow()).showDialog(getPrize() + " acquired!");
			}
			((Player) e.getTarget()).getFullInventory().add(getPrize());
			e.getWorld().delete(this);
		}
	}

}
