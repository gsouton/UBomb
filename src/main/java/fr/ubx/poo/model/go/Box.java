package fr.ubx.poo.model.go;

import fr.ubx.poo.game.Direction;
import fr.ubx.poo.game.Game;
import fr.ubx.poo.game.Position;
import fr.ubx.poo.model.Movable;

/**
 * GameObject representing a box in the Game
 */
public class Box extends GameObject implements Movable{

	private final int level;

	/**
	 * Create a Box GameObject
	 * @param game Game
	 * @param position Position to instantiate to
	 * @param level level associated
	 */
	public Box(Game game, Position position, int level) {
		super(game, position);
		this.level = level;
		game.getWorld(level).addGameObject(this);

	}


	/**
	 * Check if a box can move in a given direction
	 * @param direction Direction
	 * @return boolean
	 */
	@Override
	public boolean canMove(Direction direction) {
		Position nextPos = direction.nextPosition(getPosition());
		return game.getWorld(level).isInside(nextPos) &&
				!game.getWorld(level).isGameObject(nextPos) &&
				game.getWorld(level).isEmpty(nextPos);
	}

	/**
	 * Move the box in a given direction
	 * @param direction Direction
	 */
	@Override
    public void doMove(Direction direction) {
		if(canMove(direction)) {
			Position nextPos = direction.nextPosition(getPosition());
			setPosition(nextPos);
			if(game.getWorld(level).isExplosion(nextPos))
				destroy();
		}
    }

	/**
	 * Check it's possible to walk on the box (it's walkable if it can move)
	 * @return boolean
	 */
	@Override
	public boolean walkAble() {
		if(canMove(game.getPlayer().getDirection())) {
			doMove(game.getPlayer().getDirection());
			return true;
		}
		return false;

	}

	@Override
	public String toString() {
		return "Box";
	}

	@Override
	public boolean isBox() {
		return true;
	}

	/**
	 * Destroy the box in the game
	 */
	public void destroy(){
		this.alive = false;
		game.getWorld(level).clearGameObject(this);
	}

}
