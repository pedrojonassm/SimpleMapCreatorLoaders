package entities.ia;

public class Node {
	public Vector2i tile;
	public Node parent;
	public double fCost, gCost;

	public Node(Vector2i tile, Node parent, double gCoste) {
		this.tile = tile;
		this.parent = parent;
		this.gCost = gCoste;
	}
}
