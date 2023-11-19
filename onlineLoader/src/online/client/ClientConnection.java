package online.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import files.SalvarCarregar;
import main.OnlineMapLoader;
import main.Uteis;
import online.client.entities.ExEntity;
import online.servidor.Server.KDOqFoiEnviado;

public class ClientConnection implements Runnable {
    private Socket socket;
    private static DataOutputStream out;
    private static DataInputStream in;

    public void conectar(String prIp, int prPorta) throws Exception {
        socket = new Socket(prIp, prPorta);
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());

    }

    public static void send(KDOqFoiEnviado prKDOqFoiEnviado, Object prEnvio) {
        if (prKDOqFoiEnviado == null || out == null)
            return;
        try {
            out.writeInt(prKDOqFoiEnviado.ordinal());
            if (prEnvio != null) {
                String lConteudo = SalvarCarregar.toJSON(prEnvio);
                out.writeInt(lConteudo.getBytes(StandardCharsets.UTF_8).length);
                out.write(lConteudo.getBytes(StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void send(KDOqFoiEnviado prKDOqFoiEnviado) {
        send(prKDOqFoiEnviado, null);
    }

    @Override
    public void run() {
        Boolean lIsRunning = true;
        boolean fechar = false;
        int lDadoRecebido;
        KDOqFoiEnviado lKDOqFoiEnviado;
        byte[] lDados;
        String lDadosStr;
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
                        in.read(lDados);
                        lDadosStr = new String(lDados, StandardCharsets.UTF_8);
                        OnlineMapLoader.player.entrarNoServidor((ExEntity) SalvarCarregar.fromJson(lDadosStr, ExEntity.class));

                    }
                    break;
                case kdDesconectar:
                    OnlineMapLoader.retirarEntidade(in.readInt());
                    break;

                case kdAtualizarPlayer:
                    lDados = new byte[in.readInt()];
                    in.read(lDados);
                    lDadosStr = new String(lDados, StandardCharsets.UTF_8);
                    OnlineMapLoader.atualizarOuInserirEntidade((ExEntity) SalvarCarregar.fromJson(lDadosStr, ExEntity.class));

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
