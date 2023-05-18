package entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

import main.SimpleMapLoader;
import main.Uteis;
import main.interfaces.tickRender;
import world.Camera;
import world.Tile;
import world.World;

public class Entity implements tickRender {

	protected int x, y, z, tile_speed, salto, saltoMaximo, sinalSalto;
	public boolean left, right, up, down, aBloqueadoMovimentacao;

	protected BufferedImage[] sprites;
	protected int horizontal, vertical, speed, spriteADesenhar, animationTime, maxAnimationTime, minSpriteAnimation,
			maxSpriteAnimation;

	protected boolean forceRenderSize;

	protected Tile sqm_alvo = null;

	protected ArrayList<Tile> aCaminho;

	public Entity(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.horizontal = z;
		tile_speed = 0;
		left = right = up = down = aBloqueadoMovimentacao = false;
		spriteADesenhar = 0;
		saltoMaximo = 10;
		sinalSalto = 0;
		salto = 0;
		speed = 4;
		horizontal = vertical = 0;
		maxAnimationTime = animationTime = minSpriteAnimation = maxSpriteAnimation = 0;
		forceRenderSize = false;
		aCaminho = new ArrayList<>();
	}

	@Override
	public void tick() {
		if (sqm_alvo != null && sqm_alvo.getZ() == z
				&& Uteis.distancia(sqm_alvo.getX(), x, sqm_alvo.getY(), y) <= Uteis.modulo(speed + tile_speed)) {
			x = sqm_alvo.getX();
			y = sqm_alvo.getY();
			int k = sqm_alvo.ModificadorVelocidade();
			horizontal = vertical = 0;
			if (k > 0)
				tile_speed = k;
			else
				tile_speed = k;
			if (tile_speed == speed)
				tile_speed--;
			if (aCaminho.size() > 0)
				aCaminho.remove(0);

			sqm_alvo = null;
		} else if (sqm_alvo == null) {
			if (aCaminho.size() > 0) {
				sqm_alvo = aCaminho.get(0);
				if (sqm_alvo.getX() > getX())
					horizontal = 1;
				else if (sqm_alvo.getX() < getX())
					horizontal = -1;

				if (sqm_alvo.getY() > getY())
					vertical = 1;
				else if (sqm_alvo.getY() < getY())
					vertical = -1;

			} else {
				if (SimpleMapLoader.podeNovaMovimentacao) {
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
				}
				if (horizontal == 0 && vertical == 0) {
					maxSpriteAnimation = (minSpriteAnimation + maxSpriteAnimation) / 2;
					minSpriteAnimation = maxSpriteAnimation;
				}
			}
			changeAnimation();
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
			if (spriteADesenhar >= minSpriteAnimation && spriteADesenhar <= maxSpriteAnimation) {
				x += (speed + tile_speed) * horizontal;
				y += (speed + tile_speed) * vertical;
			}
		}

		if (++animationTime >= maxAnimationTime) {
			if (++spriteADesenhar > maxSpriteAnimation)
				spriteADesenhar = minSpriteAnimation;
			animationTime = 0;
		}

		salto += sinalSalto;
		if (salto == 0)
			sinalSalto = 0;
		else if (salto >= saltoMaximo)
			sinalSalto = -1;

		colidindo_com_escada();
	}

	public boolean isMoving() {
		return sqm_alvo != null || aCaminho.size() > 0;
	}

	public void changeAnimation() {
		if (vertical == 1) {
			minSpriteAnimation = 6;
			maxSpriteAnimation = 8;
		} else if (vertical == -1) {
			minSpriteAnimation = 0;
			maxSpriteAnimation = 2;
		}
		if (horizontal == 1) {
			minSpriteAnimation = 3;
			maxSpriteAnimation = 5;
		} else if (horizontal == -1) {
			minSpriteAnimation = 9;
			maxSpriteAnimation = 11;
		}
	}

	public void setaCaminho(ArrayList<Tile> prCaminho) {
		this.aCaminho = prCaminho;
	}

	public void saltar() {
		sinalSalto = 1;
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

	public void utilizarEscada(Tile prTile) {
		Tile lTile = prTile.utilizarEscada();
		boolean lUtilizar = false;
		if (lTile != null) {
			if (aCaminho == null || aCaminho.size() == 0)
				lUtilizar = true;
			else {
				if (aCaminho.size() > 1 && aCaminho.get(1).getaPos() == lTile.getaPos())
					lUtilizar = true;
			}
		}
		if (lUtilizar) {
			x = lTile.getX();
			y = lTile.getY();
			z = lTile.getZ();

			sqm_alvo = lTile;
		}
	}

	@Override
	public void render(Graphics prGraphics) {
		Tile lTileAcima = World.pegar_chao(x, y, z + 1);
		if (z == SimpleMapLoader.player.getZ()
				&& (lTileAcima == null || lTileAcima.getZ() >= World.maxRenderingZ || !lTileAcima.tem_sprites())) {
			if (sprites == null || spriteADesenhar >= sprites.length) {
				prGraphics.setColor(Color.WHITE);
				prGraphics.fillRect(x - Camera.x - salto, y - Camera.y - salto, SimpleMapLoader.TileSize,
						SimpleMapLoader.TileSize);
			} else {
				prGraphics.setColor(Color.WHITE);
				if (forceRenderSize)
					prGraphics.drawImage(sprites[spriteADesenhar], x - Camera.x - salto, y - Camera.y - salto,
							SimpleMapLoader.TileSize, SimpleMapLoader.TileSize, null);
				else
					prGraphics.drawImage(sprites[spriteADesenhar], x - Camera.x - salto, y - Camera.y - salto, null);
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

	public int getSpeed() {
		return speed;
	}

}
