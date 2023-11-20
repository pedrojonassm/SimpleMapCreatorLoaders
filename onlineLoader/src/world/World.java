package world;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import files.SalvarCarregar;
import graficos.Spritesheet;
import main.OnlineMapLoader;
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
                WIDTH = OnlineMapLoader.aConfig.getWorldWidth();
                HEIGHT = OnlineMapLoader.aConfig.getWorldHeight();
                HIGH = OnlineMapLoader.aConfig.getWorldHigh();
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
            int[] lPosXY = Uteis.calcularPosicaoSemAlturaRelativoACamera(prPos);
            lRetorno = new Tile(lPosXY[0] + Camera.x, lPosXY[1] + Camera.y, lPosXY[2]);
            tiles[prPos] = lRetorno;
        }
        return lRetorno;
    }

    public static Tile pegarAdicionarTileMundo(int x, int y, int z) {
        int lPos = World.calcular_pos(x, y, z);
        Tile lRetorno = World.pegar_chao(lPos);
        if (lRetorno == null && lPos >= 0 && lPos < tiles.length) {
            int[] lPosXY = Uteis.calcularPosicaoSemAlturaRelativoACamera(lPos);
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

    public static Tile pegar_chao_sem_shift(int mx, int my, int mz) {
        return pegar_chao(calcular_pos_sem_shift(mx, my, mz));
    }

    public static int calcular_pos_sem_shift(int mx, int my, int mz) {
        return (mx + my * World.WIDTH) * World.HIGH + mz;
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

        int xfinal = xstart + (OnlineMapLoader.windowWidth >> log_ts) + 2;
        int yfinal = ystart + (OnlineMapLoader.windowHEIGHT >> log_ts) + 2;

        if ((xstart -= (OnlineMapLoader.player.getZ() + 1)) < 0)
            xstart = 0;
        if ((ystart -= (OnlineMapLoader.player.getZ() + 1)) < 0)
            ystart = 0;

        for (int xx = xstart; xx <= xfinal; xx++)
            for (int yy = ystart; yy <= yfinal; yy++) {
                if (xx < 0 || yy < 0 || xx >= WIDTH || yy >= HEIGHT) {
                    continue;
                }

                Tile lTile = tiles[(xx + (yy * WIDTH)) * HIGH + OnlineMapLoader.player.getZ()];
                if (lTile != null)
                    lTile.tick();
            }

    }

    public void render(Graphics g) {
        int xstart = Camera.x >> log_ts;
        int ystart = Camera.y >> log_ts;

        int xfinal = xstart + (OnlineMapLoader.windowWidth >> log_ts) + 2;
        int yfinal = ystart + (OnlineMapLoader.windowHEIGHT >> log_ts) + 2;

        if ((xstart -= (OnlineMapLoader.player.getZ() + 1)) < 0)
            xstart = 0;
        if ((ystart -= (OnlineMapLoader.player.getZ() + 1)) < 0)
            ystart = 0;

        Tile lTile;
        maxRenderingZ = HIGH;

        boolean lBreak = false;

        for (int xx = OnlineMapLoader.player.getX() >> log_ts + 1; xx <= xfinal && !lBreak; xx++)
            for (int yy = OnlineMapLoader.player.getY() >> log_ts + 1; yy <= yfinal && !lBreak; yy++)
                for (int zz = 1; zz < HIGH - OnlineMapLoader.player.getZ() - 1 && !lBreak; zz++) {
                    if (xx < 0 || yy < 0 || xx >= WIDTH || yy >= HEIGHT) {
                        continue;
                    }
                    lTile = tiles[(xx + (yy * WIDTH)) * HIGH + zz];

                    if (lTile != null && lTile.isTileEmCima(OnlineMapLoader.player.getX(), OnlineMapLoader.player.getY(),
                            OnlineMapLoader.player.getZ())) {
                        maxRenderingZ = lTile.getZ();
                        lBreak = true;
                    }
                }

        for (int xx = xstart; xx <= xfinal; xx++) {
            for (int yy = ystart; yy <= yfinal; yy++) {
                for (int zz = 0; zz < maxRenderingZ; zz++) {
                    if (xx < 0 || yy < 0 || xx >= WIDTH || yy >= HEIGHT) {
                        continue;
                    }

                    lTile = tiles[(xx + (yy * WIDTH)) * HIGH + zz];
                    if (lTile != null) {
                        lTile.render(g);
                        if (lTile.getaPos() == OnlineMapLoader.player.aPosAtual || lTile.getaPos() == OnlineMapLoader.player.aPosAlvo)
                            OnlineMapLoader.player.render(g);
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

    }

    public static void renderizarImagemDepois(int prXX, int prYY, Graphics prGraphics, BufferedImage image, int prPosX, int prPosY) {
        if (!renderizarDepois.containsKey(prXX))
            renderizarDepois.put(prXX, new HashMap<Integer, ArrayList<Runnable>>());
        if (!renderizarDepois.get(prXX).containsKey(prYY))
            renderizarDepois.get(prXX).put(prYY, new ArrayList<Runnable>());
        renderizarDepois.get(prXX).get(prYY).add(() -> prGraphics.drawImage(image, prPosX, prPosY, null));
    }

    public static void novo_mundo(File file) {
        ready = false;
        OnlineMapLoader.aConfig = new ExConfig();
        OnlineMapLoader.world = new World(file);
        OnlineMapLoader.instance.startGerador();
    }

    public static void carregar_mundo(String prNomeMundo) {
        novo_mundo(new File(SalvarCarregar.aArquivoMundos, prNomeMundo));
    }

    public static BufferedImage PegarSprite(String Key, int posicao) {
        return spritesCarregados.get(Key)[posicao];
    }

    public static ArrayList<Tile> pegarTiles(ArrayList<Integer> prPosicoes) {
        ArrayList<Tile> lCoTiles = new ArrayList<>();
        for (int iPosicao : prPosicoes) {
            if (iPosicao >= 0 && iPosicao < tiles.length)
                lCoTiles.add(tiles[iPosicao]);
        }
        return lCoTiles;
    }

    public static ArrayList<Tile> pegarTilesAoRedor(int prPos) {
        ArrayList<Tile> lCoTiles = new ArrayList<>();
        Tile lTile = World.pegar_chao(prPos);
        Tile lTileMinima = pegar_chao(lTile.getX() - 8 * OnlineMapLoader.TileSize, lTile.getY() - 7 * OnlineMapLoader.TileSize, 0),
                lTileMaxima = pegar_chao(lTile.getX() + 8 * OnlineMapLoader.TileSize, lTile.getY() + 7 * OnlineMapLoader.TileSize, 0);
        int lPosMinimo = 0, lPosMaximo = tiles.length - 1;
        if (lTileMinima != null)
            lPosMinimo = lTileMinima.getaPos();
        if (lTileMaxima != null)
            lPosMaximo = lTileMaxima.getaPos();
        for (int i = lPosMinimo; i < lPosMaximo; i++) {
            if (tiles[i] != null && !tiles[i].estaVazio())
                lCoTiles.add(tiles[i]);
        }
        return lCoTiles;
    }

    public static ArrayList<Integer> pegarPosicoesTilesAoRedor(int prPos) {
        ArrayList<Tile> lCoTiles = pegarTilesAoRedor(prPos);
        ArrayList<Integer> lCoRetorno = new ArrayList<>();
        for (Tile iTile : lCoTiles)
            lCoRetorno.add(iTile.getaPos());
        return lCoRetorno;
    }

    public static void atualizarTiles(ArrayList<Tile> prCoTiles) {
        for (Tile iTile : prCoTiles) {
            atualizarTile(iTile);
        }
    }

    public static void atualizarTile(Tile prTile) {
        if (tiles[prTile.getaPos()] != null)
            tiles[prTile.getaPos()].atualizar(prTile);
        else
            tiles[prTile.getaPos()] = prTile;
    }
}
