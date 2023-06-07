package entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import entities.allies.NPC;
import entities.allies.Player;
import entities.ia.Astar;
import files.SalvarCarregar;
import graficos.Spritesheet;
import main.SimpleMapLoader;
import main.Uteis;
import main.interfaces.tickRender;
import world.Camera;
import world.Tile;
import world.World;

public class Entity implements tickRender {

	protected int x, y, z, tile_speed;
	public boolean left, right, up, down, aBloqueadoMovimentacao;

	protected int horizontal, vertical, speed, minSpriteAnimation, maxSpriteAnimation, animationTime, maxAnimationTime,
			spriteADesenhar;

	protected BufferedImage[] sprites;

	protected boolean forceRenderSize;

	protected Tile sqm_alvo = null;

	protected ArrayList<Tile> aCaminho;

	private ArrayList<Runnable> aExecutarAposMover;

	public Entity(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
		tile_speed = 0;
		left = right = up = down = aBloqueadoMovimentacao = false;
		speed = 4;
		horizontal = vertical = 0;
		forceRenderSize = false;
		aCaminho = new ArrayList<>();
		aExecutarAposMover = new ArrayList<>();
		maxAnimationTime = 8;
	}

	protected void randomSprites() {
		if (SalvarCarregar.aSpritesUtilizados.isEmpty())
			SalvarCarregar.aSpritesUtilizados.addAll(SalvarCarregar.aAllSprites);

		int lPosicaoImagem = SimpleMapLoader.random.nextInt(SalvarCarregar.aSpritesUtilizados.size());
		definirSprites(SalvarCarregar.aSpritesUtilizados.get(lPosicaoImagem));
		SalvarCarregar.aSpritesUtilizados.remove(lPosicaoImagem);
	}

	protected void definirSprites(String prImagem) {
		if (SalvarCarregar.aArquivoPersonagens != null && SalvarCarregar.aArquivoPersonagens.exists()) {

			File lImagem = new File(SalvarCarregar.aArquivoPersonagens, prImagem);
			if (lImagem.exists()) {
				Spritesheet lSpritesheet = new Spritesheet(lImagem, 12, 32, 36, "");
				sprites = lSpritesheet.get_x_sprites(lSpritesheet.getTotalSprites());
			}
			forceRenderSize = true;
			spriteADesenhar = 5;

		}
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

			minSpriteAnimation = (minSpriteAnimation + maxSpriteAnimation) / 2;
			maxSpriteAnimation = minSpriteAnimation;

			if (this instanceof Player)
				SimpleMapLoader.player.aPosAtual = SimpleMapLoader.player.aPosAlvo;

			sqm_alvo.addPropriedade("contemEntidade", true);

			for (int i = 0; i < aExecutarAposMover.size(); i++) {
				aExecutarAposMover.get(i).run();
				aExecutarAposMover.remove(0);
			}

			if (sqm_alvo.getPropriedade("portaNPC") != null) {
				if (World.irParaCasa && !sqm_alvo.Solid())
					sqm_alvo.dispararEventoUnico("ProximoConjuntoAoInteragir");
				else if (!World.irParaCasa && sqm_alvo.Solid())
					sqm_alvo.dispararEventoUnico("ProximoConjuntoAoInteragir");
			}

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

					if ((this instanceof Player) ? sqm_alvo.playerSolid() : sqm_alvo.Solid()) {
						Boolean lSolid = true;
						if (this instanceof NPC) {
							sqm_alvo.dispararEventoUnico("ProximoConjuntoAoInteragir");
							lSolid = sqm_alvo.Solid();
						}

						if (lSolid) {
							sqm_alvo = null;
							if (aCaminho.size() > 0) {
								aCaminho.remove(0);
								if (aCaminho.size() > 0) {
									Tile lTile = World.pegar_chao(x, y, z);
									ArrayList<Tile> lCoTile = Astar.findPath(lTile, aCaminho.get(0));
									if (lCoTile != null && lCoTile.size() > 0) {
										aCaminho.addAll(0, lCoTile);
										sqm_alvo = aCaminho.get(0);
									} else {
										// não é possível chegar em ponto A
										aCaminho.clear();
									}

								}

							}
							if (sqm_alvo == null) {
								minSpriteAnimation = (minSpriteAnimation + maxSpriteAnimation) / 2;
								maxSpriteAnimation = minSpriteAnimation;
							}
						} else {
							aExecutarAposMover.add(() -> sqm_alvo.dispararEventoUnico("ProximoConjuntoAoInteragir"));
						}

					} else if (Uteis.distancia(sqm_alvo.getX(), x, sqm_alvo.getY(), y) <= speed * 3
							+ Uteis.modulo(tile_speed) * 2)
						aBloqueadoMovimentacao = true;
				}

				if (lInverteuVelocidade) {
					horizontal *= -1;
					vertical *= -1;
				}
			}
			if (sqm_alvo != null) {
				Tile lTile = World.pegar_chao(x, y, z);
				if (lTile != null)
					lTile.removePropriedade("contemEntidade");
				if (this instanceof Player)
					SimpleMapLoader.player.aPosAlvo = sqm_alvo.getaPos();
			}
		} else {
			x += (speed + tile_speed) * horizontal;
			y += (speed + tile_speed) * vertical;
		}

		if (++animationTime >= maxAnimationTime) {
			animationTime = 0;
			if (spriteADesenhar < minSpriteAnimation)
				spriteADesenhar = minSpriteAnimation;
			else if (++spriteADesenhar >= maxSpriteAnimation)
				spriteADesenhar = minSpriteAnimation;
		}

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
			if (this instanceof Player)
				SimpleMapLoader.player.aPosAlvo = sqm_alvo.getaPos();
		}
	}

	@Override
	public void render(Graphics prGraphics) {
		Tile lTileAcima = World.pegar_chao(x, y, z + 1);
		if (z == SimpleMapLoader.player.getZ()
				&& (lTileAcima == null || lTileAcima.getZ() >= World.maxRenderingZ || !lTileAcima.tem_sprites())) {
			if (sprites == null || spriteADesenhar >= sprites.length) {
				prGraphics.setColor(Color.WHITE);
				prGraphics.fillRect(x - Camera.x, y - Camera.y, SimpleMapLoader.TileSize, SimpleMapLoader.TileSize);
			} else {
				prGraphics.setColor(Color.WHITE);
				if (forceRenderSize)
					prGraphics.drawImage(sprites[spriteADesenhar], x - Camera.x, y - Camera.y, SimpleMapLoader.TileSize,
							SimpleMapLoader.TileSize, null);
				else
					prGraphics.drawImage(sprites[spriteADesenhar], x - Camera.x, y - Camera.y, null);
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
