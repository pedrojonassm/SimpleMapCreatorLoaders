package world;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
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

	public World(File prFile) {
		ready = false;
		tiles_index = tiles_animation_time = 0;
		max_tiles_animation_time = 15;
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

		int xfinal = xstart + (SimpleMapLoader.windowWidth >> log_ts) + 1;
		int yfinal = ystart + (SimpleMapLoader.windowHEIGHT >> log_ts) + 1;

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

		int xfinal = xstart + (SimpleMapLoader.windowWidth >> log_ts) + 1;
		int yfinal = ystart + (SimpleMapLoader.windowHEIGHT >> log_ts) + 1;

		if ((xstart -= (SimpleMapLoader.player.getZ() + 1)) < 0)
			xstart = 0;
		if ((ystart -= (SimpleMapLoader.player.getZ() + 1)) < 0)
			ystart = 0;

		Tile t;
		maxRenderingZ = HIGH;
		for (int i = 1; i < HIGH - SimpleMapLoader.player.getZ(); i++) {
			t = pegar_chao(SimpleMapLoader.player.getX() + SimpleMapLoader.TileSize,
					SimpleMapLoader.player.getY() + SimpleMapLoader.TileSize, SimpleMapLoader.player.getZ() + 1);

			if (t != null && t.tem_sprites()) {
				maxRenderingZ = t.getZ();
				break;
			}
		}

		for (int xx = xstart; xx <= xfinal; xx++)
			for (int yy = ystart; yy <= yfinal; yy++)
				for (int zz = 0; zz < maxRenderingZ; zz++) {
					if (xx < 0 || yy < 0 || xx >= WIDTH || yy >= HEIGHT) {
						continue;
					}

					Tile lTile = tiles[(xx + (yy * WIDTH)) * HIGH + zz];
					if (lTile != null)
						lTile.render(g);
				}
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
