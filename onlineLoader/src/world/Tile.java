package world;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import com.fasterxml.jackson.annotation.JsonProperty;

import graficos.ConjuntoSprites;
import graficos.Ui;
import main.OnlineMapLoader;
import main.interfaces.tickRender;
import online.client.ClientConnection;
import online.servidor.Server.KDOqFoiEnviado;

public class Tile implements tickRender {
    private ArrayList<ConjuntoSprites> CoConjuntoSprites;
    private int x, y, z, aPos, posicao_Conjunto;

    private HashMap<String, Object> aPropriedades;

    public Tile(@JsonProperty("x") int x, @JsonProperty("y") int y, @JsonProperty("z") int z) {
        posicao_Conjunto = 0;
        this.x = x;
        this.y = y;
        this.z = z;
        CoConjuntoSprites = new ArrayList<>();
        CoConjuntoSprites.add(new ConjuntoSprites());
        aPos = World.calcular_pos(x, y, z);
    }

    public int ModificadorVelocidade() {
        if (aPropriedades != null && aPropriedades.get("Speed") != null)
            try {
                return Integer.parseInt(aPropriedades.get("Speed").toString());
            } catch (Exception e) {
            }
        return 0;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public int getPosicao_Conjunto() {
        return posicao_Conjunto;
    }

    public void setPosicao_Conjunto(int posicao_Conjunto) {
        this.posicao_Conjunto = posicao_Conjunto;
    }

    public int getaPos() {
        return aPos;
    }

    public void setaPos(int aPos) {
        this.aPos = aPos;
    }

    public ArrayList<BufferedImage> obterSprite_atual() {
        return CoConjuntoSprites.get(posicao_Conjunto).obterSprite_atual();
    }

    @Override
    public void tick() {
    }

    public boolean isTileEmCima(int prX, int prY, int prZ) {
        if (z > prZ) {
            int dx, dy, maxWidth = 0, maxHeight = 0;
            if (posicao_Conjunto < CoConjuntoSprites.size() && CoConjuntoSprites.get(posicao_Conjunto) != null)
                for (ArrayList<Sprite> imagens : CoConjuntoSprites.get(posicao_Conjunto).getSprites()) {
                    if (imagens != null && imagens.size() > 0) {
                        Sprite sprite = imagens.get(World.tiles_index % imagens.size());

                        BufferedImage image = sprite.pegarImagem();

                        if (image.getWidth() > maxHeight) {
                            maxWidth = image.getWidth();
                        }
                        if (image.getHeight() > maxHeight) {
                            maxHeight = image.getHeight();
                        }

                    }
                }

            if (maxWidth > OnlineMapLoader.TileSize || maxHeight > OnlineMapLoader.TileSize) {
                dx = x - OnlineMapLoader.TileSize * ((maxWidth / OnlineMapLoader.TileSize) - 1);
                dy = y - OnlineMapLoader.TileSize * ((maxHeight / OnlineMapLoader.TileSize) - 1);
            } else {
                dx = x;
                dy = y;
            }
            dx -= (z - prZ) * OnlineMapLoader.TileSize;
            dy -= (z - prZ) * OnlineMapLoader.TileSize;

            if (new Rectangle(dx, dy, maxWidth, maxHeight).intersects(new Rectangle(OnlineMapLoader.player.getX(),
                    OnlineMapLoader.player.getY(), OnlineMapLoader.TileSize, OnlineMapLoader.TileSize)))
                return true;

        }
        return false;

    }

    private boolean temTileAcima() {
        Tile lTile;
        for (int zz = z + 1; zz < World.maxRenderingZ; zz++) {
            lTile = World.pegar_chao(x + OnlineMapLoader.TileSize * (zz - z), y + OnlineMapLoader.TileSize * (zz - z), zz);
            if (lTile != null && lTile.isTileEmCima(x, y, z))
                return true;
        }
        return false;
    }

    @Override
    public void render(Graphics g) {

        if (posicao_Conjunto < CoConjuntoSprites.size() && CoConjuntoSprites.get(posicao_Conjunto) != null)
            for (int iLayer = 0; iLayer < CoConjuntoSprites.get(posicao_Conjunto).getSprites().size(); iLayer++) {
                ArrayList<Sprite> imagens = CoConjuntoSprites.get(posicao_Conjunto).getSprites().get(iLayer);
                if (imagens != null && imagens.size() > 0) {
                    Sprite sprite = imagens.get(World.tiles_index % imagens.size());
                    int dx, dy;
                    BufferedImage image = sprite.pegarImagem();
                    if (image.getWidth() > OnlineMapLoader.TileSize || image.getHeight() > OnlineMapLoader.TileSize) {
                        dx = x - Camera.x - OnlineMapLoader.TileSize;
                        dy = y - Camera.y - OnlineMapLoader.TileSize;
                    } else {
                        dx = x - Camera.x;
                        dy = y - Camera.y;
                    }
                    dx -= (z - OnlineMapLoader.player.getZ()) * OnlineMapLoader.TileSize;
                    dy -= (z - OnlineMapLoader.player.getZ()) * OnlineMapLoader.TileSize;
                    if (z == OnlineMapLoader.player.getZ() && !temTileAcima()) {
                        if (getPropriedade("renderLayerPosWorldRender") != null
                                && getPropriedade("renderLayerPosWorldRender").toString().contentEquals((iLayer + 1) + ""))
                            Ui.renderizarImagemDepois(g, image, dx, dy);
                        else {
                            if (getPropriedade("renderLayerPosWorldRenderHorizontal") != null
                                    && getPropriedade("renderLayerPosWorldRenderHorizontal").toString().contentEquals((iLayer + 1) + ""))
                                World.renderizarImagemDepois((x >> World.log_ts) + 1, (y >> World.log_ts), g, image, dx, dy);
                            if (getPropriedade("renderLayerPosWorldRenderVertical") != null
                                    && getPropriedade("renderLayerPosWorldRenderVertical").toString().contentEquals((iLayer + 1) + ""))
                                World.renderizarImagemDepois((x >> World.log_ts), (y >> World.log_ts) + 1, g, image, dx, dy);
                        }

                    }
                    g.drawImage(image, dx, dy, null);
                }
            }

    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setZ(int z) {
        this.z = z;
    }

    @SuppressWarnings("unchecked")
    public Tile utilizarEscada() {
        HashMap<String, Object> lHashMap = (HashMap<String, Object>) getPropriedade("TRANSPORT");
        if (lHashMap == null || lHashMap.get("DESTINY") == null)
            return null;

        return World.pegarAdicionarTileMundo(Tile.pegarPosicaoRelativa(getX(), getY(), getZ(), (List<Integer>) lHashMap.get("DESTINY")));

    }

    @SuppressWarnings("unchecked")
    public boolean isEscada() {
        HashMap<String, Object> lHashMap = (HashMap<String, Object>) getPropriedade("TRANSPORT");
        if (lHashMap == null || lHashMap.get("DESTINY") == null)
            return false;
        return true;
    }

    public boolean tem_sprites() {
        for (ArrayList<Sprite> spr : CoConjuntoSprites.get(posicao_Conjunto).getSprites()) {
            if (spr.size() > 0) {
                return true;
            }
        }
        return false;
    }

    public boolean estaVazio() {
        if (aPropriedades != null && !aPropriedades.isEmpty())
            return false;

        if (tem_sprites())
            return false;
        return true;
    }

    public void addPropriedades(HashMap<String, Object> prPropriedades) {
        if (aPropriedades == null)
            aPropriedades = new HashMap<>();
        aPropriedades.putAll(prPropriedades);
    }

    public void addPropriedade(String prKey, Object prValor) {
        if (prKey == null)
            return;
        if (aPropriedades == null)
            aPropriedades = new HashMap<>();
        if (aPropriedades.get(prKey) != null)
            aPropriedades.remove(prKey);
        if (prValor != null && !prValor.toString().isBlank())
            aPropriedades.put(prKey, prValor);
    }

    public Object getPropriedade(String prKey) {
        if (aPropriedades == null)
            return null;
        return aPropriedades.get(prKey);
    }

    public void removePropriedade(String prKey) {
        if (aPropriedades == null)
            return;
        aPropriedades.remove(prKey);
    }

    public boolean trocar_Sprite(int x, int y, int prRodinha) {
        posicao_Conjunto += prRodinha;
        if (posicao_Conjunto >= CoConjuntoSprites.size())
            posicao_Conjunto = 0;
        else if (posicao_Conjunto < 0)
            posicao_Conjunto = CoConjuntoSprites.size() - 1;

        return true;
    }

    public boolean Solid() {
        if (aPropriedades == null || getPropriedade("Solid") == null)
            return false;
        try {
            if (getPropriedade("Solid").toString().startsWith("ConjuntoNot=")) {
                if (getPosicao_Conjunto() != Integer.parseInt(getPropriedade("Solid").toString().split("=")[1]))
                    return true;
            } else if (getPropriedade("Solid").toString().startsWith("Conjunto=")) {
                if (getPosicao_Conjunto() == Integer.parseInt(getPropriedade("Solid").toString().split("=")[1]))
                    return true;
            } else if (Boolean.valueOf(getPropriedade("Solid").toString()) || Integer.parseInt(getPropriedade("Solid").toString()) == 1)
                return true;

        } catch (Exception e) {
        }
        return false;
    }

    public boolean playerSolid() {
        if (aPropriedades == null || getPropriedade("Solid") == null)
            return false;
        if (Solid())
            return true;
        return false;
    }

    public ArrayList<ConjuntoSprites> getCoConjuntoSprites() {
        return CoConjuntoSprites;
    }

    public void setCoConjuntoSprites(ArrayList<ConjuntoSprites> aCoConjuntoSprites) {
        this.CoConjuntoSprites = aCoConjuntoSprites;
    }

    public HashMap<String, Object> getaPropriedades() {
        return aPropriedades;
    }

    public void setaPropriedades(HashMap<String, Object> aPropriedades) {
        this.aPropriedades = aPropriedades;
    }

    public static int pegarPosicaoRelativa(int prFromX, int prFromY, int prFromZ, List<Integer> prPosicaoRelativa) {
        return World.calcular_pos(prFromX + (prPosicaoRelativa.get(0) << World.log_ts),
                prFromY + (prPosicaoRelativa.get(1) << World.log_ts), prFromZ + prPosicaoRelativa.get(2));
    }

    public void dispararEventos() {
        Boolean lEnviar = false;
        lEnviar = true;
        for (Entry<String, Object> iEntrySet : aPropriedades.entrySet()) {
            System.out.println(iEntrySet.getKey() + " = " + iEntrySet.getValue());
        }

        if (lEnviar)
            ClientConnection.sendObject(KDOqFoiEnviado.kdTileAtualizado, this);
    }

    public void atualizar(Tile prTile) {
        setaPropriedades(prTile.getaPropriedades());
        setPosicao_Conjunto(prTile.getPosicao_Conjunto());
        setCoConjuntoSprites(prTile.getCoConjuntoSprites());
    }

}
