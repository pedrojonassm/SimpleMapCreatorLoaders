package entities.ai;

import java.util.ArrayList;
import java.util.HashMap;

import main.SimpleMapLoader;
import world.Tile;
import world.World;

public class PathFinder {

	public static ArrayList<Tile> point(Tile prInicio, Tile prDestino) {
		if (prDestino.Solid())
			return new ArrayList<>();
		HashMap<Integer, ArrayList<Path>> lCaminho = new HashMap<>();
		ArrayList<Path> lCaminhoAuxiliar = new ArrayList<>();
		ArrayList<Tile> lRetorno = new ArrayList<>();
		lCaminhoAuxiliar.add(new Path(null, prInicio));
		lCaminho.put(0, lCaminhoAuxiliar);
		ArrayList<Integer> lVerificados = new ArrayList<>();
		lVerificados.add(prInicio.getaPos());
		int lContador = 1;
		Tile lTo = prInicio;
		boolean lBreak = false;
		Path lPath = new Path(null, prInicio);
		while (!lBreak) {
			lCaminhoAuxiliar = new ArrayList<>();

			for (Path iPath : lCaminho.get(lContador - 1)) {
				Tile iTile = iPath.getlTile();
				if (iTile.isEscada())
					continue;
				if (lBreak)
					break;

				for (int horizontal = -1; horizontal <= 1 && !lBreak; horizontal++)
					for (int vertical = -1; vertical <= 1 && !lBreak; vertical++) {
						lTo = World.pegar_chao(World.calcular_pos(iTile.getX() + SimpleMapLoader.TileSize * horizontal,
								iTile.getY() + SimpleMapLoader.TileSize * vertical, iTile.getZ()));
						lBreak = verificarAdicionarLista(lVerificados, lCaminhoAuxiliar, prDestino, iPath, lTo);
					}
			}

			lCaminho.put(lContador, lCaminhoAuxiliar);

			if (lBreak)
				break;
			lContador++;

		}

		lPath = lCaminhoAuxiliar.get(lCaminhoAuxiliar.size() - 1);
		do {
			lRetorno.add(0, lPath.getlTile());
			lPath = lPath.getFrom();
		} while (lPath != null);

		return lRetorno;
	}

	private static boolean verificarAdicionarLista(ArrayList<Integer> prVerificados, ArrayList<Path> prCaminhos,
			Tile prDestino, Path prFrom, Tile prTo) {
		if (prTo == null || prVerificados.contains(prTo.getaPos()) || prTo.Solid())
			return false;

		prVerificados.add(prTo.getaPos());

		Path lFrom = new Path(prFrom, prTo);
		prCaminhos.add(lFrom);

		if (prTo.getaPos() == prDestino.getaPos())
			return true;

		return verificarAdicionarLista(prVerificados, prCaminhos, prDestino, lFrom, prTo.utilizarEscada());
	}
}
