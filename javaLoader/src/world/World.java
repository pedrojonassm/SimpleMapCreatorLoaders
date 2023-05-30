package world;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import files.SalvarCarregar;
import graficos.Spritesheet;
import main.SimpleMapLoader;
import main.Uteis;
import main.configs.ExConfig;
import main.configs.ExSpriteSheet;

public class World {

	public static Tile[] tiles;
	public static int WIDTH, HEIGHT, HIGH;
	public static HashMap<String, BufferedImage[]> spritesCarregados;

	public static int log_ts, tiles_index, tiles_animation_time, max_tiles_animation_time, maxRenderingZ;
	public static boolean ready, ok;
	public static File aArquivo;

	private static HashMap<Integer, HashMap<Integer, ArrayList<Runnable>>> renderizarDepois;

	public World(File prFile) {
		ready = false;
		tiles_index = tiles_animation_time = 0;
		max_tiles_animation_time = 15;
		renderizarDepois = new HashMap<Integer, HashMap<Integer, ArrayList<Runnable>>>();
		try {
			if (prFile == null) {
				prFile = new File(SalvarCarregar.aArquivoMundos, SalvarCarregar.startWorldName);
			}

			if (prFile != null && prFile.exists()) {
				tiles = SalvarCarregar.carregarMundo(prFile);
				WIDTH = SimpleMapLoader.aConfig.getWorldWidth();
				HEIGHT = SimpleMapLoader.aConfig.getWorldHeight();
				HIGH = SimpleMapLoader.aConfig.getWorldHigh();
				aArquivo = prFile;
				ok = true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void carregarSprites() {
		File lFileImagens = new File(aArquivo, SalvarCarregar.nameFolderImagens);
		if (lFileImagens.exists())
			SalvarCarregar.carregarSprites(lFileImagens);

	}

	public static void adicionarSprites(File prFile, ExSpriteSheet prExSpriteSheet) {
		Spritesheet lSpritesheet = new Spritesheet(prFile, prExSpriteSheet);
		spritesCarregados.put(lSpritesheet.getNome(), lSpritesheet.get_x_sprites(lSpritesheet.getTotalSprites()));
	}

	public static Tile pegarAdicionarTileMundo(int prPos) {
		Tile lRetorno = World.pegar_chao(prPos);
		if (lRetorno == null && prPos >= 0 && prPos < tiles.length) {
			int[] lPosXY = Uteis.calcularPosicaoSemAltura(prPos);
			lRetorno = new Tile(lPosXY[0] + Camera.x, lPosXY[1] + Camera.y, lPosXY[2]);
			tiles[prPos] = lRetorno;
		}
		return lRetorno;
	}

	public static Tile pegarAdicionarTileMundo(int x, int y, int z) {
		int lPos = World.calcular_pos(x, y, z);
		Tile lRetorno = World.pegar_chao(lPos);
		if (lRetorno == null && lPos >= 0 && lPos < tiles.length) {
			int[] lPosXY = Uteis.calcularPosicaoSemAltura(lPos);
			lRetorno = new Tile(lPosXY[0] + Camera.x, lPosXY[1] + Camera.y, z);
			tiles[lPos] = lRetorno;
		}
		return lRetorno;
	}

	public static Tile pegar_chao(int pos) {
		if (pos >= tiles.length || pos < 0) {
			return null;
		}
		return tiles[pos];
	}

	public static Tile pegar_chao(int mx, int my, int mz) {
		return pegar_chao(calcular_pos(mx, my, mz));
	}

	public static int calcular_pos(int mx, int my, int mz) {
		return ((mx >> log_ts) + (my >> log_ts) * World.WIDTH) * World.HIGH + mz;
	}

	public void tick() {
		if (++tiles_animation_time >= max_tiles_animation_time) {
			tiles_animation_time = 0;
			if (++tiles_index >= 100) {
				tiles_index = 0;
			}
		}

		int xstart = Camera.x >> log_ts;
		int ystart = Camera.y >> log_ts;

		int xfinal = xstart + (SimpleMapLoader.windowWidth >> log_ts) + 2;
		int yfinal = ystart + (SimpleMapLoader.windowHEIGHT >> log_ts) + 2;

		if ((xstart -= (SimpleMapLoader.player.getZ() + 1)) < 0)
			xstart = 0;
		if ((ystart -= (SimpleMapLoader.player.getZ() + 1)) < 0)
			ystart = 0;

		for (int xx = xstart; xx <= xfinal; xx++)
			for (int yy = ystart; yy <= yfinal; yy++) {
				if (xx < 0 || yy < 0 || xx >= WIDTH || yy >= HEIGHT) {
					continue;
				}

				Tile lTile = tiles[(xx + (yy * WIDTH)) * HIGH + SimpleMapLoader.player.getZ()];
				if (lTile != null)
					lTile.tick();
			}

	}

	public void render(Graphics g) {
		int xstart = Camera.x >> log_ts;
		int ystart = Camera.y >> log_ts;

		int xfinal = xstart + (SimpleMapLoader.windowWidth >> log_ts) + 2;
		int yfinal = ystart + (SimpleMapLoader.windowHEIGHT >> log_ts) + 2;

		if ((xstart -= (SimpleMapLoader.player.getZ() + 1)) < 0)
			xstart = 0;
		if ((ystart -= (SimpleMapLoader.player.getZ() + 1)) < 0)
			ystart = 0;

		Tile lTile;
		maxRenderingZ = HIGH;

		boolean lBreak = false;

		for (int xx = SimpleMapLoader.player.getX() >> log_ts + 1; xx <= xfinal && !lBreak; xx++)
			for (int yy = SimpleMapLoader.player.getY() >> log_ts + 1; yy <= yfinal && !lBreak; yy++)
				for (int zz = 1; zz < HIGH - SimpleMapLoader.player.getZ() - 1 && !lBreak; zz++) {
					if (xx < 0 || yy < 0 || xx >= WIDTH || yy >= HEIGHT) {
						continue;
					}
					lTile = tiles[(xx + (yy * WIDTH)) * HIGH + zz];

					if (lTile != null && lTile.checkMaxRendering()) {
						maxRenderingZ = lTile.getZ();
						lBreak = true;
					}
				}

		for (int yy = ystart; yy <= yfinal; yy++) {
			for (int xx = xstart; xx <= xfinal; xx++) {
				for (int zz = 0; zz < maxRenderingZ; zz++) {
					if (xx < 0 || yy < 0 || xx >= WIDTH || yy >= HEIGHT) {
						continue;
					}

					lTile = tiles[(xx + (yy * WIDTH)) * HIGH + zz];
					if (lTile != null) {
						lTile.render(g);
						if (lTile.getaPos() == SimpleMapLoader.player.aPosAtual
								|| lTile.getaPos() == SimpleMapLoader.player.aPosAlvo)
							SimpleMapLoader.player.render(g);
					}
				}
				if (renderizarDepois.get(xx) != null && renderizarDepois.get(xx).get(yy) != null) {
					while (renderizarDepois.get(xx).get(yy).size() > 0) {
						renderizarDepois.get(xx).get(yy).get(0).run();
						renderizarDepois.get(xx).get(yy).remove(0);
					}
				}
			}
		}
		renderizarDepois.clear();
	}

	public static void renderizarImagemDepois(int prXX, int prYY, Graphics prGraphics, BufferedImage image, int prPosX,
			int prPosY) {
		if (!renderizarDepois.containsKey(prXX))
			renderizarDepois.put(prXX, new HashMap<Integer, ArrayList<Runnable>>());
		if (!renderizarDepois.get(prXX).containsKey(prYY))
			renderizarDepois.get(prXX).put(prYY, new ArrayList<Runnable>());
		renderizarDepois.get(prXX).get(prYY).add(() -> prGraphics.drawImage(image, prPosX, prPosY, null));
	}

	public static void novo_mundo(File file) {
		ready = false;
		SimpleMapLoader.aConfig = new ExConfig();
		SimpleMapLoader.world = new World(file);
		SimpleMapLoader.instance.startGerador();
	}

	public static void carregar_mundo(String prNomeMundo) {
		novo_mundo(new File(SalvarCarregar.aArquivoMundos, prNomeMundo));
	}

	public static BufferedImage PegarSprite(String Key, int posicao) {
		return spritesCarregados.get(Key)[posicao];
	}

}
