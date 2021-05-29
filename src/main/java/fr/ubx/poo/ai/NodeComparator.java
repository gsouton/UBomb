package fr.ubx.poo.ai;

import java.util.Comparator;


public class NodeComparator implements Comparator<Node> {

    @Override
    public int compare(Node node, Node t1) {
        if( node.getTotalCost() > t1.getTotalCost() || (node.getTotalCost() == t1.getTotalCost() && node.getDistanceToEnd() < t1.getDistanceToEnd())){
            return 1;
        }else if(node.getTotalCost() < t1.getTotalCost() || (node.getTotalCost() == t1.getTotalCost() && node.getDistanceToEnd() > t1.getDistanceToEnd())){
            return -1;
        }
        return 0;
    }
}
