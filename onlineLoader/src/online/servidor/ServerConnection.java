package online.servidor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import files.SalvarCarregar;
import main.Uteis;
import online.client.entities.ExEntity;
import online.servidor.Server.KDOqFoiEnviado;

public class ServerConnection implements Runnable {
    public DataOutputStream out;
    public DataInputStream in;
    int id;
    private ExEntity aJogador;
    Thread thread = null;

    public ServerConnection(DataOutputStream out, DataInputStream in) {
        this.out = out;
        this.in = in;
        aJogador = new ExEntity();
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }

    public ExEntity getaJogador() {
        return aJogador;
    }

    public void run() {
        Boolean lIsRunning = true;
        boolean fechar = false;
        int lDadoRecebido;
        KDOqFoiEnviado lKDOqFoiEnviado;
        byte[] lDados;
        String lDadosStr = null;
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
                        aJogador.setIdentificadorServidor(pos);
                        Server.entrar_servidor(this, pos);
                        lDadosStr = SalvarCarregar.toJSON(aJogador);
                        lDados = lDadosStr.getBytes(StandardCharsets.UTF_8);
                        out.writeInt(lDados.length);
                        out.write(lDados);
                        Server.filaEspera.remove(this);

                        for (ServerConnection iServerConnection : Server.playersConectados) {
                            if (iServerConnection == null || iServerConnection.getPlayerid() == aJogador.getIdentificadorServidor())
                                continue;

                            iServerConnection.sendObject(KDOqFoiEnviado.kdAtualizarPlayer, aJogador);

                            sendObject(KDOqFoiEnviado.kdAtualizarPlayer, iServerConnection.getaJogador());

                        }
                    } else
                        out.writeBoolean(false);

                    break;
                case kdDesconectar:
                    lIsRunning = false;
                    if (aJogador.getIdentificadorServidor() == null)
                        break;
                    Server.playersConectados[aJogador.getIdentificadorServidor()] = null;
                    Server.sendIntToEveryOneExceptMe(lKDOqFoiEnviado, aJogador.getIdentificadorServidor(),
                            aJogador.getIdentificadorServidor());

                    break;

                case kdAtualizarPlayer:
                    lDados = new byte[in.readInt()];
                    in.read(lDados);
                    lDadosStr = new String(lDados, StandardCharsets.UTF_8);
                    aJogador = (ExEntity) SalvarCarregar.fromJson(lDadosStr, ExEntity.class);
                    Server.sendByteArrayToEveryOneExceptMe(KDOqFoiEnviado.kdAtualizarPlayer, lDados, aJogador.getIdentificadorServidor());

                    break;

                default:
                    break;
                }
                if (fechar) {
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
                if (aJogador.getIdentificadorServidor() != null)
                    Server.playersConectados[aJogador.getIdentificadorServidor()] = null;
                if (!Server.isClosed())
                    Server.sendIntToEveryOneExceptMe(KDOqFoiEnviado.kdDesconectar, aJogador.getIdentificadorServidor(),
                            aJogador.getIdentificadorServidor());
                break;
            }
        }
    }

    public Integer getPlayerid() {
        return aJogador.getIdentificadorServidor();
    }

    public void sendObject(KDOqFoiEnviado prKDOqFoiEnviado, Object prEnvio) {
        String lJson = SalvarCarregar.toJSON(prEnvio);
        sendByteArray(prKDOqFoiEnviado, lJson.getBytes(StandardCharsets.UTF_8));
    }

    public void sendByteArray(KDOqFoiEnviado prKDOqFoiEnviado, byte[] prEnvio) {
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

    public void sendInt(KDOqFoiEnviado prKDOqFoiEnviado, int prEnvio) {
        if (prKDOqFoiEnviado == null)
            return;
        try {
            out.writeInt(prKDOqFoiEnviado.ordinal());
            out.writeInt(prEnvio);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendAction(KDOqFoiEnviado prKDOqFoiEnviado) {
        if (prKDOqFoiEnviado == null)
            return;
        try {
            out.writeInt(prKDOqFoiEnviado.ordinal());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
