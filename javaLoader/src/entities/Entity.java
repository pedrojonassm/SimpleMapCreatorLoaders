package entities;

import java.awt.Graphics;
import java.util.HashMap;
import java.util.List;

import main.SimpleMapLoader;
import main.Uteis;
import main.interfaces.tickRender;
import world.Tile;
import world.World;

public class Entity implements tickRender {

	protected int x, y, z, tile_speed;
	public boolean left, right, up, down, aBloqueadoMovimentacao;

	protected int horizontal, vertical, speed;

	protected Tile sqm_alvo = null;

	public Entity(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.horizontal = z;
		tile_speed = 0;
		left = right = up = down = aBloqueadoMovimentacao = false;

		speed = 4;
		horizontal = vertical = 0;
	}

	@Override
	public void tick() {
		if (sqm_alvo != null
				&& Uteis.distancia(sqm_alvo.getX(), x, sqm_alvo.getY(), y) <= Uteis.modulo(speed + tile_speed)) {
			x = sqm_alvo.getX();
			y = sqm_alvo.getY();
			int k = sqm_alvo.ModificadorVelocidade();
			if (k > 0)
				tile_speed = k;
			else
				tile_speed = k;
			if (tile_speed == speed)
				tile_speed--;
			sqm_alvo = null;
		} else if (sqm_alvo == null) {
			if (left) {
				if (x - SimpleMapLoader.TileSize >= 0)
					horizontal = -1;

			} else if (right) {

				if (x + SimpleMapLoader.TileSize < World.WIDTH * SimpleMapLoader.TileSize)
					horizontal = 1;

			} else {
				horizontal = 0;
			}
			if (up) {

				if (y - SimpleMapLoader.TileSize >= 0)
					vertical = -1;

			} else if (down) {

				if (y + SimpleMapLoader.TileSize < World.HEIGHT * SimpleMapLoader.TileSize)
					vertical = 1;

			} else {
				vertical = 0;
			}
			if ((horizontal != 0 || vertical != 0) && !aBloqueadoMovimentacao) {
				boolean lInverteuVelocidade = false;
				if (speed + tile_speed < 0) {
					lInverteuVelocidade = true;
					horizontal *= -1;
					vertical *= -1;
				}

				sqm_alvo = World.pegar_chao(World.calcular_pos(x + SimpleMapLoader.TileSize * horizontal,
						y + SimpleMapLoader.TileSize * vertical, z));

				if (sqm_alvo != null) {
					if (sqm_alvo.Solid())
						sqm_alvo = null;

					else if (Uteis.distancia(sqm_alvo.getX(), x, sqm_alvo.getY(), y) <= speed * 3
							+ Uteis.modulo(tile_speed) * 2)
						aBloqueadoMovimentacao = true;
				}
				if (lInverteuVelocidade) {
					horizontal *= -1;
					vertical *= -1;
				}
			}
		} else {
			x += (speed + tile_speed) * horizontal;
			y += (speed + tile_speed) * vertical;
		}

		colidindo_com_escada();
	}

	private void colidindo_com_escada() {

		Tile lTile = World.pegar_chao(x + SimpleMapLoader.TileSize / 2, y + SimpleMapLoader.TileSize / 2, z);

		if (lTile != null && lTile.getPropriedade("TRANSPORT") != null) {
			@SuppressWarnings("unchecked")
			HashMap<String, Object> lHashmap = (HashMap<String, Object>) lTile.getPropriedade("TRANSPORT");
			try {
				if (lHashmap.get("TYPE") != null)
					switch (lHashmap.get("TYPE").toString()) {
					case "colisao":
						utilizarEscada(lTile);
						break;

					}

			} catch (Exception e) {
			}
		}

	}

	@SuppressWarnings("unchecked")
	public void utilizarEscada(Tile prTile) {
		HashMap<String, Object> lHashMap = (HashMap<String, Object>) prTile.getPropriedade("TRANSPORT");
		if (lHashMap == null || lHashMap.get("DESTINY") == null)
			return;

		Tile lTile = World.pegarAdicionarTileMundo(Tile.pegarPosicaoRelativa(prTile.getX(), prTile.getY(),
				prTile.getZ(), (List<Integer>) lHashMap.get("DESTINY")));

		x = lTile.getX();
		y = lTile.getY();
		z = lTile.getZ();
		sqm_alvo = lTile;

	}

	@Override
	public void render(Graphics prGraphics) {

	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getZ() {
		return z;
	}

	public void setZ(int z) {
		this.z = z;
	}

	public int getSpeed() {
		return speed;
	}

}
