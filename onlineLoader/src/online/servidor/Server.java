package online.servidor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import files.SalvarCarregar;
import main.OnlineMapLoader;
import world.World;

public class Server implements Runnable {

    public enum KDOqFoiEnviado {
        kdConectar, kdDesconectar, kdFecharServidor, kdAtualizarPlayer, kdCarregarMapaAoRedor, kdAtualizarTile
    }

    private static Server server;
    static ServerSocket aServerSocket;
    public Socket socket;
    public DataOutputStream out;
    public DataInputStream in;
    public static int port;
    public static ServerConnection[] playersConectados;
    public static ArrayList<ServerConnection> filaEspera;
    public static int jogadores_conectados = 0, maximoJogadores = -1;
    private static final String txt_port = "porta", txt_players = "total de jogadores", splitter = ": ";
    public static World world;
    Boolean aIsServerRunning;

    public static void main(String[] args) {
        server = new Server();
        OnlineMapLoader.instance = new OnlineMapLoader(true);
        File file = new File("settings.txt");
        try {
            if (!file.exists()) {
                file.createNewFile();
                BufferedWriter write = null;
                write = new BufferedWriter(new FileWriter(file));
                write.write(txt_port + splitter + "7777");
                write.newLine();
                write.write(txt_players + splitter + "10");
                write.newLine();
                write.close();
                JOptionPane.showMessageDialog(null,
                        "Foi criado um arquivo \"settings.txt\" com as configurações padrão de porta e número de jogadores\n Caso necessário, edite-o");
            } else {
                BufferedReader read = new BufferedReader(new FileReader(file));
                String str_port = read.readLine().split(splitter)[1], str_max_players = read.readLine().split(splitter)[1];

                Integer lPorta = null, lMaximoJogadores = null;
                if (!tem_letras(str_max_players)) {
                    lMaximoJogadores = Integer.parseInt(str_max_players);
                }

                if (!tem_letras(str_port)) {
                    lPorta = Integer.parseInt(str_port);
                }
                if (lPorta == null || lMaximoJogadores == null) {
                    if (lPorta == null && lMaximoJogadores == null) {
                        JOptionPane.showMessageDialog(null,
                                "Não foi possível ler a " + txt_port + "\nNão foi possível ler o " + txt_players);
                    } else if (lPorta == null) {
                        JOptionPane.showMessageDialog(null, "Não foi possível ler a " + txt_port);
                    } else if (lMaximoJogadores == null) {
                        JOptionPane.showMessageDialog(null, "Não foi possível ler o " + txt_players);
                    }
                }
                read.close();
                if (lPorta != null && lMaximoJogadores != null) {
                    JOptionPane.showMessageDialog(null,
                            "server iniciado!\n" + txt_port + ":" + str_port + "\n" + txt_players + ": " + str_max_players);
                    server.hostear(lMaximoJogadores, lPorta);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isClosed() {
        return aServerSocket.isClosed();
    }

    public void hostear(int prMaximoJogadores, int prPorta) {
        maximoJogadores = prMaximoJogadores;
        port = prPorta;
        playersConectados = new ServerConnection[maximoJogadores];
        filaEspera = new ArrayList<>();
        aIsServerRunning = true;
        Thread lThread = new Thread(this);
        lThread.start();
    }

    private static boolean tem_letras(String prTexto) {
        char[] numeros = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
        boolean tem = false;
        for (char c : prTexto.toCharArray()) {
            for (char n : numeros) {
                if (n == c) {
                    // verifica se o caractere um n mero
                    tem = true;
                    break;
                }
            }
            if (!tem) {
                // se n o for, a fun o retorna verdadeiro, j que tem letras
                return true;
            }
        }
        // caso contr rio, ela retorna falso, pois tem apenas n meros
        return false;
    }

    public static int podeEntrar() {
        for (int i = 0; i < playersConectados.length; i++) {
            if (playersConectados[i] == null) {
                return i;
            }
        }
        return -1;
    }

    public static void entrar_servidor(ServerConnection entrar, int pos) {
        jogadores_conectados++;
        playersConectados[pos] = entrar;
    }

    public static void sair_servidor(ServerConnection entrar, int pos) {
        jogadores_conectados--;
        playersConectados[pos] = null;
        filaEspera.add(entrar);
    }

    @Override
    public void run() {
        try {
            aServerSocket = new ServerSocket(port);
            while (aIsServerRunning) {
                try {
                    socket = aServerSocket.accept();
                    out = new DataOutputStream(socket.getOutputStream());
                    in = new DataInputStream(socket.getInputStream());
                    // System.out.println("Conexão feita com: " + socket.getInetAddress());
                    ServerConnection novo = new ServerConnection(out, in);
                    filaEspera.add(novo);
                    Thread thread = new Thread(novo);
                    novo.setThread(thread);
                    thread.start();

                } catch (Exception e) {
                }
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public void fecharServidor() {
        sendActionToEveryOneExceptMe(KDOqFoiEnviado.kdFecharServidor, null);
        aIsServerRunning = false;
        try {
            if (!aServerSocket.isClosed())
                aServerSocket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendObjectToEveryOneExceptMe(KDOqFoiEnviado prOqFoiEnviado, Object prConteudo, Integer prMe) {
        sendByteArrayToEveryOneExceptMe(prOqFoiEnviado, SalvarCarregar.toBytes(prConteudo), prMe);
    }

    public static void sendByteArrayToEveryOneExceptMe(KDOqFoiEnviado prOqFoiEnviado, byte[] prConteudo, Integer prMe) {
        for (ServerConnection iServerConnection : playersConectados) {
            if (iServerConnection != null && (prMe == null || iServerConnection.getPlayerid() != prMe)) {
                iServerConnection.sendByteArray(prOqFoiEnviado, prConteudo);
            }
        }
    }

    public static void sendIntToEveryOneExceptMe(KDOqFoiEnviado prOqFoiEnviado, Integer prConteudo, Integer prMe) {
        for (ServerConnection iServerConnection : playersConectados) {
            if (iServerConnection != null && (prMe == null || iServerConnection.getPlayerid() != prMe)) {
                iServerConnection.sendInt(prOqFoiEnviado, prConteudo);
            }
        }
    }

    public static void sendActionToEveryOneExceptMe(KDOqFoiEnviado prOqFoiEnviado, Integer prMe) {
        for (ServerConnection iServerConnection : playersConectados) {
            if (iServerConnection != null && iServerConnection.getPlayerid() != prMe) {
                iServerConnection.sendAction(prOqFoiEnviado);
            }
        }
    }
}
