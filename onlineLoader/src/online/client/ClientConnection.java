package online.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

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
        if (prKDOqFoiEnviado == null || prEnvio == null)
            return;
        try {
            String lConteudo = SalvarCarregar.toJSON(prEnvio);
            out.writeInt(prKDOqFoiEnviado.ordinal());
            out.writeInt(lConteudo.getBytes().length);
            out.write(lConteudo.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void send(KDOqFoiEnviado prKDOqFoiEnviado) {
        if (prKDOqFoiEnviado == null)
            return;
        try {
            out.writeInt(prKDOqFoiEnviado.ordinal());
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                        OnlineMapLoader.player.definirIdentificadorServidor(in.readInt());
                    }
                    break;
                case kdDesconectar:
                    break;

                case kdAtualizarPlayer:
                    lDados = new byte[in.readInt()];
                    in.read(lDados);
                    lDadosStr = new String(lDados);
                    OnlineMapLoader.atualizarEntidade((ExEntity) SalvarCarregar.fromJson(lDadosStr, ExEntity.class));

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
