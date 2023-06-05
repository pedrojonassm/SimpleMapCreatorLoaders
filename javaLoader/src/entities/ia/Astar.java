package entities.ia;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import main.SimpleMapLoader;
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
		return findPath(start, end, false);
	}

	public static ArrayList<Tile> findPath(Tile start, Tile end, boolean isPlayer) {

		List<Node> lCoNodes = findPath(new Vector2i(start.getX(), start.getY(), start.getZ()),
				new Vector2i(end.getX(), end.getY(), end.getZ()), isPlayer);
		if (lCoNodes == null || lCoNodes.size() == 0)
			return new ArrayList<>();
		else {
			ArrayList<Tile> lRetorno = new ArrayList<>();
			Node lNode = lCoNodes.get(0);
			Tile lTile = null;
			while (lNode != null) {
				lTile = World.pegar_chao(lNode.tile.x, lNode.tile.y, lNode.tile.z);
				lRetorno.add(0, lTile);
				lNode = lNode.parent;
			}
			return lRetorno;
		}
	}

	public static List<Node> findPath(Vector2i start, Vector2i end, boolean isPlayer) {
		lastTime = System.currentTimeMillis();
		List<Node> openList = new ArrayList<Node>();
		List<Node> closedList = new ArrayList<Node>();

		Node current = new Node(start, null, 0), atual;
		openList.add(current);
		int x, y, z;
		Tile tile;
		Vector2i a;
		Node node;
		double gCost = 0;

		Node lObjetivo = null;

		while (openList.size() > 0) {
			Collections.sort(openList, nodeSorter);
			current = openList.get(0);

			if (lObjetivo != null && lObjetivo.gCost < gCost) {
				closedList.add(current);

				openList.remove(current);
				continue;
			}

			if (current.tile.equals(end) && (lObjetivo == null || lObjetivo.gCost > current.gCost))
				lObjetivo = current;

			openList.remove(current);
			if (NodeInList(closedList, current)) {
				continue;
			}
			closedList.add(current);

			atual = current;

			for (int horizontal = -1; horizontal <= 1; horizontal++)
				for (int vertical = -1; vertical <= 1; vertical++) {
					if (horizontal == vertical && vertical == 0)
						continue;

					current = atual;

					x = current.tile.x;
					y = current.tile.y;
					z = current.tile.z;
					tile = World.pegar_chao(x + horizontal * SimpleMapLoader.TileSize,
							y + vertical * SimpleMapLoader.TileSize, z);
					if (tile == null || ((!isPlayer
							&& (end.x != tile.getX() || end.y != tile.getY() || end.z != tile.getZ()) && tile.Solid())
							|| (isPlayer && (end.x != tile.getX() || end.y != tile.getY() || end.z != tile.getZ())
									&& tile.playerSolid()))) {
						if (!isPlayer && tile != null) {
							// se pode abrir a porta
							tile.dispararEventoUnico("ProximoConjuntoAoInteragir");
							if (tile.Solid()) {
								tile.dispararEventoUnico("ProximoConjuntoAoInteragir");
								continue;
							}
							tile.dispararEventoUnico("ProximoConjuntoAoInteragir");

						} else
							continue;
					}

					do {

						a = new Vector2i(tile.getX(), tile.getY(), tile.getZ());
						gCost = current.gCost - tile.ModificadorVelocidade()
								+ ((vertical != 0 && horizontal != 0) ? 3 : 1) + (tile.isEscada() ? 5 : 0);

						if (gCost < 0)
							gCost = ((vertical != 0 && horizontal != 0) ? 3 : 1) + (tile.isEscada() ? 5 : 0);

						node = new Node(a, current, gCost);

						if (node.fCost < 0)
							node.fCost = 0;

						if (NodeInList(closedList, node) && gCost >= current.gCost) {
							break;
						}

						if (!NodeInList(openList, node)) {
							openList.add(node);
						} else if (gCost < current.gCost + current.fCost) {
							openList.remove(current);
							openList.add(node);
						}

						if (tile.isEscada()) {
							current = node;
						}

					} while ((tile = tile.utilizarEscada()) != null);

				}

		}

		if (lObjetivo != null && lObjetivo.gCost < gCost) {
			List<Node> path = new ArrayList<Node>();
			while (lObjetivo.parent != null) {
				path.add(lObjetivo);
				lObjetivo = lObjetivo.parent;
			}
			openList.clear();
			closedList.clear();
			return path;
		}
		closedList.clear();
		return null;
	}

	private static boolean NodeInList(List<Node> list, Node prCurrent) {
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).tile.equals(prCurrent.tile)) {
				if (list.get(i).fCost <= prCurrent.fCost)
					return true;
				list.remove(i);
				break;
			}
		}
		return false;
	}

}
