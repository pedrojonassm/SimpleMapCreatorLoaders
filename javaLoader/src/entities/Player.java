package entities;

import java.awt.Color;
import java.awt.Graphics;
import java.util.HashMap;
import java.util.List;

import main.SimpleMapLoader;
import main.Uteis;
import main.interfaces.tickRender;
import world.Camera;
import world.Tile;
import world.World;

public class Player implements tickRender {
	private int x, y, z, tile_speed;
	private int horizontal, vertical, speed;
	public boolean left, right, up, down, aBloqueadoMovimentacao;
	Tile sqm_alvo = null;

	public Player(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.horizontal = z;
		tile_speed = 0;
		left = right = up = down = aBloqueadoMovimentacao = false;

		speed = 4;
		horizontal = vertical = 0;
	}

	public int getSpeed() {
		return speed;
	}

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
			boolean mover = false;
			if (left) {
				horizontal = -1;
				mover = true;
			} else if (right) {
				horizontal = 1;
				mover = true;
			} else {
				horizontal = 0;
			}
			if (up) {
				vertical = -1;
				mover = true;
			} else if (down) {
				vertical = 1;
				mover = true;
			} else {
				vertical = 0;
			}
			if (mover && !aBloqueadoMovimentacao) {
				boolean lInverteuVelocidade = false;
				if (speed + tile_speed < 0) {
					lInverteuVelocidade = true;
					horizontal *= -1;
					vertical *= -1;
				}

				sqm_alvo = World.pegar_chao(
						World.calcular_pos(x + SimpleMapLoader.TileSize * horizontal, y + SimpleMapLoader.TileSize * vertical, z));

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
		updateCamera();
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

	public void updateCamera() {
		Camera.x = Camera.clamp(x - SimpleMapLoader.windowWidth / 2, 0, World.WIDTH * SimpleMapLoader.TileSize - SimpleMapLoader.windowWidth);
		Camera.y = Camera.clamp(y - SimpleMapLoader.windowHEIGHT / 2, 0,
				World.HEIGHT * SimpleMapLoader.TileSize - SimpleMapLoader.windowHEIGHT);

	}

	public void render(Graphics g) {
		g.setColor(Color.WHITE);
		g.fillRect(x - Camera.x, y - Camera.y, SimpleMapLoader.TileSize, SimpleMapLoader.TileSize);

		if (sqm_alvo != null) {
			g.setColor(new Color(175, 75, 50, 50));
			g.fillRect(sqm_alvo.getX() - Camera.x - (sqm_alvo.getZ() - SimpleMapLoader.player.getZ()) * SimpleMapLoader.TileSize,
					sqm_alvo.getY() - Camera.y - (sqm_alvo.getZ() - SimpleMapLoader.player.getZ()) * SimpleMapLoader.TileSize,
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
