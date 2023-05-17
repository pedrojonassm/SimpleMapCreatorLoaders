package entities.ia;

import world.Tile;

public class Path {
	Path from;
	Tile lTile;

	public Path(Path prFrom, Tile prTile) {
		from = prFrom;
		lTile = prTile;
	}

	public Path getFrom() {
		return from;
	}

	public void setFrom(Path from) {
		this.from = from;
	}

	public Tile getlTile() {
		return lTile;
	}

	public void setlTile(Tile lTile) {
		this.lTile = lTile;
	}

}
