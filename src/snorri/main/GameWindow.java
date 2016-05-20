package snorri.main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.MouseInfo;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JPanel;
import javax.swing.SwingWorker;

import snorri.entities.Entity;
import snorri.entities.Projectile;
import snorri.keyboard.Key;
import snorri.keyboard.KeyStates;
import snorri.world.Vector;
import snorri.world.World;

public class GameWindow extends JPanel implements KeyListener {

	
	/**
	 * Main game window
	 */
	private static final long serialVersionUID = 1L;
	private static final int FRAME_DELTA = 50;
	
	private KeyStates states;
	
	private World world;
	private Entity focus;
	
	public GameWindow(World world, Entity focus) {
		this.world = world;
		this.focus = focus;
		states = new KeyStates();
		addKeyListener(this);
		setFocusable(true);
		startAnimation();
	}
	
	public void startAnimation() {
		SwingWorker<Object, Object> sw = new SwingWorker<Object, Object>() {
			@Override
			protected Object doInBackground() throws Exception {
				while (true) {
					onFrame();
					Thread.sleep(FRAME_DELTA);
				}
			}
		};

		sw.execute();
	}
	
	//TODO: pass a timeDelta argument to update()
	private void onFrame() {
		focus.walk(states.getMovementVector(), world.getEntityTree());
		world.update();
		repaint();
	}
	
	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		setBackground(Color.WHITE);
		world.render(this, g);
		//TODO: function which renders everything intersecting a circle inscribing the screen
	}
	
	public Entity getFocus() {
		return focus;
	}
	
	public Vector getMousePos() {
		Vector origin = new Vector(getLocationOnScreen());
		origin.add((new Vector(getBounds())).divide(2));		
		return (new Vector(MouseInfo.getPointerInfo().getLocation())).sub(origin);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		states.set(e.getKeyCode(), true);
		//Main.log(e.getKeyCode() + ": " + states.get(e.getKeyCode()));
	}

	@Override
	public void keyReleased(KeyEvent e) {
		states.set(e.getKeyCode(), false);
		//Main.log(e.getKeyCode() + ": " + states.get(e.getKeyCode()));
	}

	@Override
	public void keyTyped(KeyEvent e) {
		
		if (e.getKeyChar() == Key.SPACE.getChar()) {
			//TODO: put this stuff in a shoot function
			Vector dir = getMousePos().copy().normalize();
			
			if (dir.notInPlane()) {
				return;
			}
			
			world.add(new Projectile(focus, states.getMovementVector(), dir));
		}
		
	}
	
}
