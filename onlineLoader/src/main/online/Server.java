package main.online;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JOptionPane;

import entities.allies.Player;
import world.World;

public class Server {
	private static Server server;
	static ServerSocket serverSocket;
	public Socket socket;
	public DataOutputStream out;
	public DataInputStream in;
	public static int port;
	public static Player[] players;
	public static int jogadores_conectados = -1, maximoJogadores = -1;
	private static final String txt_port = "porta", txt_players = "total de jogadores", splitter = ": ";
	public static World world;
	
	public static void main(String[] args) {
	    server = this;
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
		    String str_port = read.readLine().split(splitter)[1],
			str_max_players = read.readLine().split(splitter)[1];
			
		    Integer lPorta = null, lMaximoJogadores = null;
		    if (!tem_letras(str_max_players)) {
		        lMaximoJogadores = Integer.parseInt(str_max_players);
		    }
			
		    if (!tem_letras(str_port)) {
			lPorta = Integer.parseInt(str_port);
		    }
		    if (lPorta == null || lMaximoJogadores == null) {
			if (lPorta == null && lMaximoJogadores == null) {
			    JOptionPane.showMessageDialog(null, "Não foi possível ler a " + txt_port + "\nNão foi possível ler o " + txt_players);
			}
			else if (lPorta == null) {
			    JOptionPane.showMessageDialog(null, "Não foi possível ler a " + txt_port);
			}
			else if (lMaximoJogadores == null) {
			    JOptionPane.showMessageDialog(null, "Não foi possível ler o " + txt_players);
			}
		    }
		    read.close();
		    if (lPorta != null && lMaximoJogadores != null) {
		        JOptionPane.showMessageDialog(null, "server iniciado!\n" + txt_port + ":" + str_port + "\n"
	                            + txt_players + ": " + str_max_players);
		        server.hostear(lMaximoJogadores, lPorta);
		    }
		}
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	private void hostear(int prMaximoJogadores, int prPorta) {
	    maximoJogadores = prMaximoJogadores;
            port = prPorta; 
            players = new Player[maximoJogadores];
            serverSocket = new ServerSocket(port);
            while (true) {
                socket = serverSocket.accept();
                out = new DataOutputStream(socket.getOutputStream());
                in = new DataInputStream(socket.getInputStream());
                // System.out.println("Conexão feita com: "+socket.getInetAddress());
                Users novo = new Users(out, in);
                players.add(novo);
                Thread thread = new Thread(novo);
                novo.setThread(thread);
                out.writeInt(jogadores_conectados);
                thread.start();
            }
        }

	private static boolean tem_letras(String text) {
            char[] numeros = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
            boolean tem= false;
            for (char c : text.toCharArray()) {
                for (char n : numeros) {
                    if (n == c) {
                        //verifica se o caractere � um n�mero
                        tem = true;
                        break;
                        }
                    }
                if (!tem) {
                    // se n�o for, a fun��o retorna verdadeiro, j� que tem letras
                    return true;
                    }
            }
            // caso contr�rio, ela retorna falso, pois tem apenas n�meros
            return false;
        }

	public static int tem_espaco() {
            for (int i = 0; i < players.length; i++) {
                if (players[i] == null) {
                    return i;
                }
            }
            return -1;
        }

	public static void entrar_servidor(Users entrar, int pos) {
		jogadores_conectados++;
		entrar.setPlayerid(pos);
		user[pos] = entrar;
		players.remove(entrar);
	}
}
