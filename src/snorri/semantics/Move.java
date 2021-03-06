package snorri.semantics;

import snorri.entities.Entity;

public class Move extends VerbDef {

	public Move() {
		super(true);
	}

	@Override
	public boolean exec(Object obj) {
		
		if (!e.getWorld().getLevel().isContextPathable(e.getDestination())) {
			return false;
		}
		
		if (obj instanceof Entity) {
			e.getWorld().getEntityTree().move((Entity) obj, e.getDestination());
			return true;
		}
		return false;
	}

	@Override
	public boolean eval(Object subj, Object obj) {
		return false;
	}
	
	@Override
	public boolean altersMovement() {
		return true;
	}

	@Override
	public String toString() {
		return "teleport";
	}

}
