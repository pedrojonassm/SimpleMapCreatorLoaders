package entities.ia;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import main.SimpleMapLoader;
import main.Uteis;
import world.Tile;
import world.World;

public class Astar {
	public static double lastTime = System.currentTimeMillis();
	private static Comparator<Node> nodeSorter = new Comparator<Node>() {

		@Override
		public int compare(Node n0, Node n1) {
			if (n1.fCost < n0.fCost)
				return +1;
			if (n1.fCost > n0.fCost)
				return -1;
			return 0;
		}

	};

	public static boolean clear() {
		if (System.currentTimeMillis() - lastTime >= 1000) {
			return true;
		}
		return false;
	}

	public static ArrayList<Tile> findPath(Tile start, Tile end) {
		List<Node> lCoNodes = findPath(SimpleMapLoader.world, new Vector2i(start.getX(), start.getY(), start.getZ()),
				new Vector2i(end.getX(), end.getY(), end.getZ()));
		if (lCoNodes == null || lCoNodes.size() == 0)
			return new ArrayList<>();
		else {
			ArrayList<Tile> lRetorno = new ArrayList<>();
			Node lNode = lCoNodes.get(0);
			while (lNode != null) {
				lRetorno.add(0, World.pegar_chao(lNode.tile.x, lNode.tile.y, lNode.tile.z));
				lNode = lNode.parent;
			}
			return lRetorno;
		}
	}

	public static List<Node> findPath(World world, Vector2i start, Vector2i end) {
		lastTime = System.currentTimeMillis();
		List<Node> openList = new ArrayList<Node>();
		List<Node> closedList = new ArrayList<Node>();

		Node current = new Node(start, null, 0, nextCost(start, end));
		openList.add(current);
		while (openList.size() > 0) {
			Collections.sort(openList, nodeSorter);
			current = openList.get(0);
			if (current.tile.equals(end)) {
				List<Node> path = new ArrayList<Node>();
				while (current.parent != null) {
					path.add(current);
					current = current.parent;
				}
				openList.clear();
				closedList.clear();
				return path;
			}

			openList.remove(current);
			closedList.add(current);

			int x, y, z;

			for (int horizontal = -1; horizontal <= 1; horizontal++)
				for (int vertical = -1; vertical <= 1; vertical++) {
					if (horizontal == vertical && vertical == 0)
						continue;

					x = current.tile.x;
					y = current.tile.y;
					z = current.tile.z;
					Tile tile = World.pegar_chao(x + horizontal * SimpleMapLoader.TileSize,
							y + vertical * SimpleMapLoader.TileSize, z);
					if (tile == null || tile.Solid())
						continue;

					do {
						Vector2i a = new Vector2i(tile.getX(), tile.getY(), tile.getZ());
						double gCost = current.gCost + nextCost(current.tile, a)
								+ (tile.isEscada() ? SimpleMapLoader.TileSize * 2 : 0);
						double hCost = nextCost(a, end);

						Node node = new Node(a, current, gCost, hCost);

						if (vecInList(closedList, a) && gCost >= current.gCost) {
							break;
						}

						if (!vecInList(openList, a)) {
							openList.add(node);
						} else if (gCost < current.gCost) {
							openList.remove(current);
							openList.add(node);
						}

					} while ((tile = tile.utilizarEscada()) != null);

				}

		}
		closedList.clear();
		return null;
	}

	private static boolean vecInList(List<Node> list, Vector2i vector) {
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).tile.equals(vector)) {
				return true;
			}
		}
		return false;
	}

	private static double nextCost(Vector2i tile, Vector2i goal) {
		double dx = tile.x - goal.x;
		double dy = tile.y - goal.y;

		return Math.pow(Math.sqrt(dx * dx + dy * dy), Uteis.modulo(tile.z - goal.z) + 1);
	}
}
