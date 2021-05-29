package fr.ubx.poo.model.go.character;

import fr.ubx.poo.ai.Node;
import fr.ubx.poo.game.Direction;
import fr.ubx.poo.game.Game;
import fr.ubx.poo.game.Position;
import javafx.animation.AnimationTimer;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * GameObject representing a monster in the Game
 */
public class Monster extends Character{

    private final long speed; // move every milliseconds
    private AnimationTimer animationTimer;
    private long lastMove = 0;
    private Position playerPos;
    private final boolean smart;
    private boolean searchingForPlayer = false;
    private final ArrayList<Node> path = new ArrayList<>();


    public Monster(Game game, Position position, int level, boolean smart){
        super(game, position);
        this.monster = true;
        startUpdateLoop();
        this.level = level;
        game.getWorld(level).addGameObject(this);
        this.smart = smart;
        this.speed = (long) 1000/(this.level+1);
    }


    /**
     * Check if the monster is smart and will follow the player or not
     * @return boolean
     */
    public boolean isSmart() {
        return smart;
    }

    /**
     * Uses a pseudo A star Algorithm to find shortest path to player
     * This algorithm is closest to dijkstra than A*
     */
    private void shortestPathToPlayer(){
        path.clear();
        Node startNode = new Node(getPosition(), getPosition(), playerPos, getDirection());
        Node endNode = new Node(playerPos, getPosition(), playerPos, game.getPlayer().getDirection());

        //PriorityQueue<Node> openSet = new PriorityQueue<>(game.getHeightWorld(level) * game.getWidthWorld(level));
        ArrayList<Node> openSet = new ArrayList<>();
        Set<Node> closeSet = new HashSet<>();
        openSet.add(startNode); // add the starting node to path

        while(!openSet.isEmpty()){
            Node currentNode = openSet.get(0); // find the minimum cost movement from openSet
            openSet.remove(currentNode); // remove current from open
            closeSet.add(currentNode); // close the node by adding to the set

            if(currentNode.getPosition().equals(playerPos)){
                retracePath(closeSet);
                return;
            }

            List<Node> neighbors = getNeighbors(currentNode.getPosition()); // get the neighbors
            for(Node n : neighbors){
                if(isNodeContained(closeSet, n)) { // if neighbors is already visited
                    continue; // continue execution
                }
                double movementCost = currentNode.getDistanceToBegin() + distanceNode(currentNode, n); // calculate the cost of moving
                if(movementCost < n.getDistanceToBegin() || !isNodeContained(openSet, n)){ // if the cost if < than the distance to the being or is not in open set
                    n.setParent(currentNode); // set the parent of the node to
                    n.setDistanceToBegin(movementCost);
                    n.setDistanceToEnd(distanceNode(n, endNode));
                    if(!isNodeContained(openSet, n)){
                        openSet.add(n);
                    }
                }

            }

        }
    }

    /**
     * Check for a given Set of node if the node n is present or not
     * @param set Set to check from
     * @param n Node to search
     * @return boolean
     */
    private boolean isNodeContained(Collection<Node> set, Node n){
        for(Node node : set){
            if(node.getPosition().equals(n.getPosition()))
                return true;
        }
        return false;
    }

    /**
     * Retrace the path from target Node to source
     * @param closeSet CloseSet used in searchPathPlayer()
     */
    private void retracePath(Set<Node> closeSet){
        path.clear();
        Node start = findStartNode(closeSet);
        Node end = findEndNode(closeSet);
        if(start == null || end == null){
            System.out.println("Error cannot retrace path !");
            return;
        }
        Node currentNode = end;
        while(currentNode != start){
            path.add(currentNode);
            currentNode = currentNode.getParent();
        }
        Collections.reverse(path);
    }

    /**
     * Find the starting node from the closeSet
     * @param closeSet Set<>
     * @return null or an object Node
     */
    private Node findStartNode(Set<Node> closeSet){
        for(Node n : closeSet){
            if(n.getPosition().equals(getPosition()))
                return n;
        }
        return null;
    }

    /**
     * Find the end Node from closeSet
     * @param closeSet Set<>
     * @return null or an object Node
     */
    private Node findEndNode(Set<Node> closeSet){
        for(Node n : closeSet){
            if(n.getPosition().equals(playerPos))
                return n;
        }
        return null;
    }

    /**
     * Calculate distance between two nodes
     * @param s source node
     * @param t target node
     * @return double
     */
    private double distanceNode(Node s, Node t){
        return s.getPosition().distance(t.getPosition());
    }

    /**
     * Get the neighbors as Node for a given position
     * @param currentPos starting position to look for neighbors
     * @return ArrayList of Node
     */
    private ArrayList<Node> getNeighbors(Position currentPos){
        Direction[] directions = Direction.values();
        ArrayList<Node> neighbors = new ArrayList<>();
        for (Direction value : directions) {
            Position nextPos = value.nextPosition(currentPos);
            if (canWalk(nextPos)) {
                neighbors.add(new Node(nextPos, this.getPosition(), playerPos, value));
            }
        }
        return neighbors;
    }


    /**
     * Start the main update loop of the monster
     */
    private void startUpdateLoop(){
        animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if(alive) {
                    calculatePathToPlayer();
                    update(now);
                    move(now);
                }else{
                    animationTimer.stop();
                }
            }
        };
        animationTimer.start();
    }

    @Override
    public void update(long now) {
            super.update(now);
            if (playerExists()) { // if the player is initialize
                if (smart && !isPlayerPresent()) {  // if the monster is smart but the player is not in the same level
                    searchingForPlayer = false; // not searching for player
                    path.clear(); // clear the path
                }
                if (smart && isPlayerPresent()) {
                    searchingForPlayer = true;
                }
                if (isPlayerPresent() && getPosition().equals(game.getPlayer().getPosition())) {
                    game.getPlayer().requestDamage();
                }
            }

    }

    private void calculatePathToPlayer() {
        if(searchingForPlayer && smart && playerExists() && isPlayerPresent()) {
            if(playerPos != game.getPlayer().getPosition()) {
                playerPos = game.getPlayer().getPosition();
                shortestPathToPlayer();
            }
        }
    }

    private void move(long now){

        long currentTime = TimeUnit.NANOSECONDS.toMillis(now);
        if(lastMove == 0)
            lastMove = currentTime;
        if(currentTime - lastMove >= speed){
            if(!smart) {
                setDirection(Direction.random());
                requestMove(direction);
                lastMove = currentTime;
            }else if(playerExists() && isPlayerPresent()){
                searchingForPlayer = true;
                if(!path.isEmpty()){
                    Node n = path.get(0);
                    setDirection(n.getDirection());
                    requestMove(direction);
                    path.remove(n);
                    lastMove = currentTime;
                }
            }

        }

    }


    public void setDamage() {
        lives--;
        if(lives == 0){
            alive = false;
            game.getWorld(level).clearGameObject(this);
        }
    }




    @Override
    public boolean canMove(Direction direction) {
        Position n_pos = direction.nextPosition(this.getPosition());
        return game.getWorld(level).isInside(n_pos) &&
                (game.getWorld(level).isEmpty(n_pos) || game.getWorld(level).isCollectable(n_pos)) &&
                !game.getWorld(level).isGameObject(n_pos);
    }

    public boolean canWalk(Position position){
        return game.getWorld(level).isInside(position) &&
                (game.getWorld(level).isEmpty(position) || game.getWorld(level).isCollectable(position)) &&
                !game.getWorld(level).isGameObject(position);
    }



    @Override
    public void doMove(Direction direction) {
        Position nextPos = direction.nextPosition(getPosition());
        setPosition(nextPos);
        if(game.getLevel() == level && nextPos.equals(game.getPlayer().getPosition()))
            game.getPlayer().requestDamage();
    }

    @Override
    public String toString() {
        return "Monster";
    }

    /**
     * Check if the player is in the same level as the monster
     * @return boolean
     */
    private boolean isPlayerPresent(){
        return level == game.getLevel();
    }

    /**
     * Check if the player is instantiated
     * @return boolean
     */
    private boolean playerExists(){
        return game.getPlayer().getPosition() != null;
    }
}
