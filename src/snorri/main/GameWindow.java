package snorri.main;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Queue;

import javax.swing.UIManager;

import snorri.dialog.DropMessage;
import snorri.dialog.Message;
import snorri.dialog.Objective;
import snorri.entities.Desk;
import snorri.entities.Entity;
import snorri.entities.Player;
import snorri.entities.Unit;
import snorri.inventory.Droppable;
import snorri.keyboard.Key;
import snorri.overlay.DeathScreen;
import snorri.triggers.Trigger.TriggerType;
import snorri.world.Playable;
import snorri.world.World;

public class GameWindow extends FocusedWindow {
		
	/**
	 * Main game window
	 */
	private static final long serialVersionUID = 1L;
	
	private Playable universe;
	private Player focus;
	private Queue<Message> messageQ;
	private boolean hasDied;
	private long lastTime;
	
	private Objective objective;
		
	public GameWindow(Playable universe, Player focus) {
		super();
		this.universe = universe;
		this.focus = focus;
		messageQ = new LinkedList<>();
		lastTime = getTimestamp();
		hasDied = false;
	}
	
	public GameWindow(Playable universe) {
		this(universe, universe.computeFocus());
	}
	
	@Override
	protected void onStart() {
		while (true) {
			if (TriggerType.TIMELINE.activate("start")) {
				break;
			}
		}
	}
	
	@Override
	protected void onFrame() {
				
		long time = getTimestamp();
		double deltaTime = (time - lastTime) / 1000000000d;
		lastTime = time;
		
		if (deltaTime > 0.2) { //this is shitty
			Main.log("high delta time detected (" + deltaTime + " sec)");
		}
		
		if (messageQ != null && messageQ.peek() != null && messageQ.peek().update(deltaTime)) {
			messageQ.poll();
		}
				
		if (isPaused()) {
			return;
		}
		
		if (!hasDied && focus != null && focus.isDead()) {
			TriggerType.TIMELINE.activate("death");
			hasDied = true;
			Main.setOverlay(new DeathScreen());
		}
		
		if (universe == null || universe.getCurrentWorld() == null) {
			return;
		}
		
//		SwingUtilities.invokeLater(new Runnable() {
//
//			@Override
//			public void run() {
//				universe.getCurrentWorld().update(deltaTime);
//			}
//			
//		});
		
		universe.getCurrentWorld().update(deltaTime);
		repaint();
				
	}
	
	@Override
	public void paintComponent(Graphics g){
				
		if (focus == null) {
			return;
		}
		
		long time = getTimestamp();
		double deltaTime = (time - lastRenderTime) / 1000000000d;
		lastRenderTime = time;
		
		super.paintComponent(g);
		
		universe.getCurrentWorld().render(this, g, deltaTime, true);
		focus.getInventory().render(this, g);
		focus.renderHealthBar(g);
		
		g.setFont(UIManager.getFont("Label.font"));
		if (objective != null) {
			objective.render(g, this);
		}
		
		int xTrans = 0;
		List<Message> reverse = new ArrayList<>(messageQ);
		for (ListIterator<Message> iter = reverse.listIterator(reverse.size()); iter.hasPrevious();) {
			xTrans += iter.previous().render(this, g, xTrans);
		}
		
	}
	
	public Player getFocus() {
		return focus;
	}
	
	public void showMessage(Droppable drop) {
		showMessage(new DropMessage(drop));
	}
	
	public void showMessage(Message m) {
		Main.log(m.toString());
		messageQ.add(m);
	}
	
	@Override
	public World getWorld() {
		return universe.getCurrentWorld();
	}
	
	@Override
	public Playable getUniverse() {
		return universe;
	}
	
	public void setObjective(Objective objective) {
		this.objective = objective;
	}
	
	public String getObjectiveInfo() {
		return objective.longDesc;
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		
		super.keyPressed(e);
		
		if (Key.ESC.isPressed(e)) {
			pause();
		}
		
		if (focus == null || focus.isDead() || isPaused()) {
			return;
		}
		
		if (Key.SPACE.isPressed(e)) {
			Entity interactRegion = new Entity(getFocus().getPos(), Unit.RADIUS + Desk.INTERACT_RANGE);
			for (Entity entity : universe.getCurrentWorld().getEntityTree().getAllCollisions(interactRegion)) {
				if (entity instanceof Desk) {
					openInventory();
					return;
				}
			}
		}
		
		focus.getInventory().checkKeys(e);
		
	}
	
	public void openInventory() {
		openInventory(focus.getInventory());
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}
	
	@Override
	public void unpause() {
		super.unpause();
		lastTime = getTimestamp();
	}
	
}
