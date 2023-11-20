package online.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import files.SalvarCarregar;
import main.OnlineMapLoader;
import main.Uteis;
import online.client.entities.ExEntity;
import online.servidor.Server.KDOqFoiEnviado;
import world.Tile;
import world.World;

public class ClientConnection implements Runnable {
    private Socket socket;
    private static DataOutputStream out;
    private static DataInputStream in;

    public void conectar(String prIp, int prPorta) throws Exception {
        socket = new Socket(prIp, prPorta);
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());

    }

    public static void sendObject(KDOqFoiEnviado prKDOqFoiEnviado, Object prEnvio) {
        if (prKDOqFoiEnviado == null || out == null)
            return;
        try {
            out.writeInt(prKDOqFoiEnviado.ordinal());
            if (prEnvio != null) {
                byte[] lConteudo = SalvarCarregar.toBytes(prEnvio);
                out.writeInt(lConteudo.length);
                out.write(lConteudo);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void send(KDOqFoiEnviado prKDOqFoiEnviado) {
        sendObject(prKDOqFoiEnviado, null);
    }

    @Override
    public void run() {
        Boolean lIsRunning = true;
        boolean fechar = false;
        int lDadoRecebido;
        KDOqFoiEnviado lKDOqFoiEnviado;
        byte[] lDados;
        while (lIsRunning) {
            try {

                lDadoRecebido = in.readInt();
                lKDOqFoiEnviado = null;
                if (Uteis.isEnumValueValid(lDadoRecebido, KDOqFoiEnviado.class))
                    lKDOqFoiEnviado = KDOqFoiEnviado.values()[lDadoRecebido];
                switch (lKDOqFoiEnviado) {
                case kdConectar:
                    if (in.readBoolean()) {
                        lDados = new byte[in.readInt()];
                        in.readFully(lDados);
                        OnlineMapLoader.player.entrarNoServidor((ExEntity) SalvarCarregar.fromByteArray(lDados, ExEntity.class));
                        // carregar mapa
                        sendObject(KDOqFoiEnviado.kdCarregarMapaAoRedor,
                                World.pegarPosicoesTilesAoRedor(World.calcular_pos(OnlineMapLoader.player.getX(),
                                        OnlineMapLoader.player.getY(), OnlineMapLoader.player.getZ())));

                    }
                    break;
                case kdDesconectar:
                    OnlineMapLoader.retirarEntidade(in.readInt());
                    break;

                case kdAtualizarPlayer:
                    lDados = new byte[in.readInt()];
                    in.readFully(lDados);
                    OnlineMapLoader.atualizarOuInserirEntidade((ExEntity) SalvarCarregar.fromByteArray(lDados, ExEntity.class));

                    break;

                case kdFecharServidor:
                    OnlineMapLoader.aIsOnline = false;
                    OnlineMapLoader.aIsConectado = false;
                    OnlineMapLoader.entities.clear();
                    lIsRunning = false;
                    in.close();
                    out.close();
                    socket.close();
                    break;

                case kdCarregarMapaAoRedor:
                    lDados = new byte[in.readInt()];
                    in.readFully(lDados);
                    ArrayList<Tile> lCoTiles = (ArrayList<Tile>) SalvarCarregar.fromByteArrayToList(lDados, Tile.class);
                    World.atualizarTiles(lCoTiles);
                    break;

                case kdAtualizarTile:
                    lDados = new byte[in.readInt()];
                    in.readFully(lDados);
                    World.atualizarTile((Tile) SalvarCarregar.fromByteArray(lDados, Tile.class));
                    break;

                default:
                    break;
                }
                if (fechar) {
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }
}
