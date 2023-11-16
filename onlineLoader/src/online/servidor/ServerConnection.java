package online.servidor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import main.Uteis;
import online.servidor.Server.KDOqFoiEnviado;

public class ServerConnection implements Runnable {
    public DataOutputStream out;
    public DataInputStream in;
    int id;
    Integer playerid = null;
    Thread thread = null;

    public ServerConnection(DataOutputStream out, DataInputStream in) {
        this.out = out;
        this.in = in;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }

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
                    int pos = Server.podeEntrar();
                    out.writeInt(KDOqFoiEnviado.kdConectar.ordinal());
                    if (pos > -1) {
                        out.writeBoolean(true);
                        playerid = pos;
                        Server.entrar_servidor(this, playerid);
                        System.out.println("Enviado: " + playerid);
                        out.writeInt(playerid);
                    } else
                        out.writeBoolean(false);

                    break;
                case kdDesconectar:
                    lIsRunning = false;
                    if (playerid != null)
                        break;
                    Server.playersConectados[playerid] = null;
                    playerid = null;

                    break;

                case kdAtualizarPlayer:
                    lDados = new byte[in.readInt()];
                    in.read(lDados);
                    Server.sendToEveryOneExceptMe(KDOqFoiEnviado.kdAtualizarPlayer, lDados, playerid);

                    break;

                default:
                    break;
                }
                if (fechar) {
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
                if (playerid != null)
                    Server.playersConectados[playerid] = null;
                break;
            }
        }
    }

    public Integer getPlayerid() {
        return playerid;
    }

    public void send(KDOqFoiEnviado prKDOqFoiEnviado, byte[] prEnvio) {
        if (prKDOqFoiEnviado == null || prEnvio == null)
            return;
        try {
            out.writeInt(prKDOqFoiEnviado.ordinal());
            out.writeInt(prEnvio.length);
            out.write(prEnvio);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
