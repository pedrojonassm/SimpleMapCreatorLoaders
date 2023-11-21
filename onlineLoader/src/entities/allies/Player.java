package entities.allies;

import java.awt.Color;
import java.awt.Graphics;

import entities.Entity;
import main.OnlineMapLoader;
import main.interfaces.tickRender;
import online.client.ClientConnection;
import online.client.entities.ExEntity;
import online.servidor.Server.KDOqFoiEnviado;
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
        int lX = x, lY = y, lZ = z;
        super.tick();
        updateCamera();
        if (x != lX || lY != y || lZ != z)
            sendTToServer(KDOqFoiEnviado.kdAtualizarPlayer);

    }

    public void entrarNoServidor(ExEntity prEntity) {
        aExEntity = prEntity;
        x = aExEntity.getX();
        y = aExEntity.getY();
        z = aExEntity.getZ();
        OnlineMapLoader.aIsOnline = true;
    }

    public void updateCamera() {
        Camera.x = Camera.clamp(x - OnlineMapLoader.windowWidth / 2, 0,
                World.WIDTH * OnlineMapLoader.TileSize - OnlineMapLoader.windowWidth);
        Camera.y = Camera.clamp(y - OnlineMapLoader.windowHEIGHT / 2, 0,
                World.HEIGHT * OnlineMapLoader.TileSize - OnlineMapLoader.windowHEIGHT);
    }

    public void render(Graphics g) {
        super.render(g);

        if (sqm_alvo != null) {
            g.setColor(new Color(175, 75, 50, 50));
            g.fillRect(sqm_alvo.getX() - Camera.x - (sqm_alvo.getZ() - OnlineMapLoader.player.getZ()) * OnlineMapLoader.TileSize,
                    sqm_alvo.getY() - Camera.y - (sqm_alvo.getZ() - OnlineMapLoader.player.getZ()) * OnlineMapLoader.TileSize,
                    OnlineMapLoader.TileSize, OnlineMapLoader.TileSize);
        }

        if (aCaminho != null) {
            for (Tile iTile : aCaminho) {
                if (iTile.getZ() != z)
                    continue;
                g.setColor(new Color(175, 75, 50, 80));
                g.fillRect(iTile.getX() - Camera.x - (iTile.getZ() - OnlineMapLoader.player.getZ()) * OnlineMapLoader.TileSize,
                        iTile.getY() - Camera.y - (iTile.getZ() - OnlineMapLoader.player.getZ()) * OnlineMapLoader.TileSize,
                        OnlineMapLoader.TileSize, OnlineMapLoader.TileSize);
            }
        }
    }

    @Override
    protected void iniciarNovaMovimentacaoSQM() {
        super.iniciarNovaMovimentacaoSQM();

        if (sqm_alvo != null)
            ClientConnection.sendObject(KDOqFoiEnviado.kdPedirTiles, World.pegarPosicoesDaBordaAoRedor(sqm_alvo.getaPos()));

    }

    @Override
    protected void encerrarMovimentacaoSQM() {
        super.encerrarMovimentacaoSQM();

        OnlineMapLoader.player.aPosAtual = OnlineMapLoader.player.aPosAlvo;
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
