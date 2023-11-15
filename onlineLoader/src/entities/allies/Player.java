package entities.allies;

import java.awt.Color;
import java.awt.Graphics;

import entities.Entity;
import files.SalvarCarregar;
import main.OnlineMapLoader;
import main.interfaces.tickRender;
import world.Camera;
import world.Tile;
import world.World;

public class Player extends Entity implements tickRender {

	public int aPosAtual, aPosAlvo;

	public Player(int x, int y, int z) {
		super(x, y, z);

		aPosAtual = aPosAlvo = -1;
	}

	public void tick() {

		super.tick();
		colidindoTransporteParaOutroMundo();
		updateCamera();
	}

	public void updateCamera() {
		Camera.x = Camera.clamp(x - OnlineMapLoader.windowWidth / 2, 0,
				World.WIDTH * OnlineMapLoader.TileSize - OnlineMapLoader.windowWidth);
		Camera.y = Camera.clamp(y - OnlineMapLoader.windowHEIGHT / 2, 0,
				World.HEIGHT * OnlineMapLoader.TileSize - OnlineMapLoader.windowHEIGHT);
	}

	private void colidindoTransporteParaOutroMundo() {
		Tile lTile = World.pegar_chao(x + OnlineMapLoader.TileSize / 2, y + OnlineMapLoader.TileSize / 2, z);

		if (lTile != null && lTile.getPropriedade("ToOtherWorld") != null) {
			Tile lPosicao = World.pegar_chao(lTile.getX() + OnlineMapLoader.TileSize * horizontal * -1,
					lTile.getY() + OnlineMapLoader.TileSize * vertical * -1, lTile.getZ());
			setX(lPosicao.getX());
			setY(lPosicao.getY());
			setZ(lPosicao.getZ());
			SalvarCarregar.salvar_mundo();
			SalvarCarregar.toOtherWorld(lTile.getPropriedade("ToOtherWorld").toString());

		}
	}

	public void render(Graphics g) {
		super.render(g);

		if (sqm_alvo != null) {
			g.setColor(new Color(175, 75, 50, 50));
			g.fillRect(
					sqm_alvo.getX() - Camera.x
							- (sqm_alvo.getZ() - OnlineMapLoader.player.getZ()) * OnlineMapLoader.TileSize,
					sqm_alvo.getY() - Camera.y
							- (sqm_alvo.getZ() - OnlineMapLoader.player.getZ()) * OnlineMapLoader.TileSize,
					OnlineMapLoader.TileSize, OnlineMapLoader.TileSize);
		}

		if (aCaminho != null) {
			for (Tile iTile : aCaminho) {
				if (iTile.getZ() != z)
					continue;
				g.setColor(new Color(175, 75, 50, 80));
				g.fillRect(
						iTile.getX() - Camera.x
								- (iTile.getZ() - OnlineMapLoader.player.getZ()) * OnlineMapLoader.TileSize,
						iTile.getY() - Camera.y
								- (iTile.getZ() - OnlineMapLoader.player.getZ()) * OnlineMapLoader.TileSize,
						OnlineMapLoader.TileSize, OnlineMapLoader.TileSize);
			}
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
