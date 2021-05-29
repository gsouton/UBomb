package fr.ubx.poo.ai;

import fr.ubx.poo.game.Direction;
import fr.ubx.poo.game.Position;

import java.util.Objects;

/**
 * Node that represent each position of the world, with distances and direction to specified targets
 *
 */
public class Node {
    private final Position position;
    private double distanceToEnd;
    private double distanceToBegin;
    private double totalCost;
    private Node parent;
    private final Direction direction;

    /**
     * Create a Node with a Position, a source position, a target position and a direction
     * @param position Position of the node
     * @param startPos Position of the source
     * @param endPos Position of the target
     * @param direction direction
     */
    public Node(Position position, Position startPos, Position endPos, Direction direction){
        this.position = position;
        distanceToEnd = this.position.distance(endPos);
        distanceToBegin = this.position.distance(startPos);
        this.totalCost = distanceToEnd + distanceToBegin;
        this.parent = this;
        this.direction = direction;

    }

    /**
     * Give the position of the node
     * @return Position
     */
    public Position getPosition() {
        return position;
    }

    /**
     * Total cost to move in the direction
     * @return double
     */
    public double getTotalCost() {
        return totalCost;
    }

    /**
     * Distance to the target Node
     * @return double
     */
    public double getDistanceToEnd() {
        return distanceToEnd;
    }

    /**
     * Distance to the source node
     * @return double
     */
    public double getDistanceToBegin() {
        return distanceToBegin;
    }

    /**
     * Set distance to the source node
     * @param distanceToBegin double
     */
    public void setDistanceToBegin(double distanceToBegin) {
        this.distanceToBegin = distanceToBegin;
    }

    /**
     * Set the distance to the target node
     * @param distanceToEnd double
     */
    public void setDistanceToEnd(double distanceToEnd) {
        this.distanceToEnd = distanceToEnd;
    }

    /**
     * Set the total cost
     * @param totalCost double
     */
    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    /**
     * Set the parent of this node
     * @param parent parent Node
     */
    public void setParent(Node parent) {
        this.parent = parent;
    }

    /**
     * Get the parent Node for this node
     * @return Node
     */
    public Node getParent() {
        return parent;
    }

    /**
     * Get the coming direction of this node
     * @return Direction
     */
    public Direction getDirection() {
        return direction;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return Objects.equals(position, node.position);
    }

    @Override
    public int hashCode() {
        return Objects.hash(position, distanceToEnd, distanceToBegin, totalCost);
    }

    @Override
    public String toString() {
        return "Node{" +
                "position=" + position +
                ", direction=" + direction +
                /*", distanceToEnd=" + distanceToEnd +
                ", distanceToBegin=" + distanceToBegin +
                ", totalCost=" + totalCost +
                ", parent=" + parent +*/
                '}';
    }
}
