package entities;

import java.awt.Color;
import java.awt.Graphics;

import main.SimpleMapLoader;
import main.interfaces.tickRender;
import world.Camera;
import world.Tile;
import world.World;

public class Player extends Entity implements tickRender {

	public Player(int x, int y, int z) {
		super(x, y, z);
	}

	public void tick() {

		super.tick();
		updateCamera();
	}

	public void updateCamera() {
		Camera.x = Camera.clamp(x - SimpleMapLoader.windowWidth / 2, 0,
				World.WIDTH * SimpleMapLoader.TileSize - SimpleMapLoader.windowWidth);
		Camera.y = Camera.clamp(y - SimpleMapLoader.windowHEIGHT / 2, 0,
				World.HEIGHT * SimpleMapLoader.TileSize - SimpleMapLoader.windowHEIGHT);

	}

	public void render(Graphics g) {
		g.setColor(Color.WHITE);
		g.fillRect(x - Camera.x, y - Camera.y, SimpleMapLoader.TileSize, SimpleMapLoader.TileSize);

		if (sqm_alvo != null) {
			g.setColor(new Color(175, 75, 50, 50));
			g.fillRect(
					sqm_alvo.getX() - Camera.x
							- (sqm_alvo.getZ() - SimpleMapLoader.player.getZ()) * SimpleMapLoader.TileSize,
					sqm_alvo.getY() - Camera.y
							- (sqm_alvo.getZ() - SimpleMapLoader.player.getZ()) * SimpleMapLoader.TileSize,
					SimpleMapLoader.TileSize, SimpleMapLoader.TileSize);
		}
	}

	public void camada(int acao) {
		int fz = z;
		if (acao > 0) {
			if (++fz >= World.HIGH) {
				fz = 0;
			}
		} else if (acao < 0) {
			if (--fz < 0) {
				fz = World.HIGH - 1;
			}
		}
		Tile lTile = World.pegar_chao(x, y, fz);
		if (lTile == null || !lTile.Solid()) {
			z = fz;
		}

	}
}
