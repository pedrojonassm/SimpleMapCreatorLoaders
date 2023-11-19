package graficos.telas;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JOptionPane;
import javax.swing.JTextField;

import main.OnlineMapLoader;
import main.configs.Constant;
import main.interfaces.Tela;
import online.client.ClientConnection;
import online.servidor.Server;
import online.servidor.Server.KDOqFoiEnviado;

public class TelaSelecionarServidor implements Tela {
    // Conectar a um servidor e/ou hostear

    private Rectangle aRectangleConectar, aRectangleHostear;

    private Font aFonteSelecionado;

    public TelaSelecionarServidor() {
        aRectangleConectar = new Rectangle();
        aRectangleHostear = new Rectangle();
        aFonteSelecionado = new Font("arial", Font.BOLD, OnlineMapLoader.instance.aFonte.getSize() + 10);
        posicionarRetangulos();
    }

    private void posicionarRetangulos() {
        aRectangleConectar.width = OnlineMapLoader.TileSize * 2;
        aRectangleConectar.height = (int) (OnlineMapLoader.TileSize / 1.5);
        aRectangleConectar.x = OnlineMapLoader.windowWidth / 2 - aRectangleConectar.width / 2;
        aRectangleConectar.y = OnlineMapLoader.windowHEIGHT / 2 - aRectangleConectar.height;
        aRectangleHostear.width = aRectangleConectar.width;
        aRectangleHostear.height = aRectangleConectar.height;
        aRectangleHostear.x = OnlineMapLoader.windowWidth / 2 - aRectangleHostear.width / 2;
        aRectangleHostear.y = OnlineMapLoader.windowHEIGHT / 2 + aRectangleHostear.width / 2;
    }

    @Override
    public void tick() {

    }

    @Override
    public void render(Graphics prGraphics) {
        int lAux = 0;
        if (!OnlineMapLoader.aIsConectado) {
            prGraphics.setColor(Color.gray);
            if (aRectangleConectar.contains(OnlineMapLoader.instance.aMouseX, OnlineMapLoader.instance.aMouseY)) {
                prGraphics.setFont(aFonteSelecionado);
                lAux = prGraphics.getFontMetrics().stringWidth(Constant.entrar);
                prGraphics.drawString(Constant.entrar, aRectangleConectar.x + aRectangleConectar.width / 2 - lAux / 2,
                        aRectangleConectar.y + aRectangleConectar.height / 2 + prGraphics.getFont().getSize() / 2);
                prGraphics.setFont(OnlineMapLoader.instance.aFonte);
            } else {
                lAux = prGraphics.getFontMetrics().stringWidth(Constant.entrar);
                prGraphics.drawString(Constant.entrar, aRectangleConectar.x + aRectangleConectar.width / 2 - lAux / 2,
                        aRectangleConectar.y + aRectangleConectar.height / 2 + prGraphics.getFont().getSize() / 2);
            }

            if (aRectangleHostear.contains(OnlineMapLoader.instance.aMouseX, OnlineMapLoader.instance.aMouseY)) {
                prGraphics.setFont(aFonteSelecionado);
                lAux = prGraphics.getFontMetrics().stringWidth(Constant.hostear);
                prGraphics.drawString(Constant.hostear, aRectangleHostear.x + aRectangleHostear.width / 2 - lAux / 2,
                        aRectangleHostear.y + aRectangleHostear.height / 2 + prGraphics.getFont().getSize() / 2);
                prGraphics.setFont(OnlineMapLoader.instance.aFonte);
            } else {
                lAux = prGraphics.getFontMetrics().stringWidth(Constant.hostear);
                prGraphics.drawString(Constant.hostear, aRectangleHostear.x + aRectangleHostear.width / 2 - lAux / 2,
                        aRectangleHostear.y + aRectangleHostear.height / 2 + prGraphics.getFont().getSize() / 2);
            }
        }

    }

    @Override
    public boolean clicou(int x, int y) {
        if (!OnlineMapLoader.aIsConectado && aRectangleConectar.contains(x, y)) {
            tentarConectar();
            return true;
        } else if (!OnlineMapLoader.aIsConectado && aRectangleHostear.contains(x, y)) {
            tentarHostear();
            return true;
        }
        return false;
    }

    @Override
    public boolean cliquedireito(int x, int y) {
        return false;
    }

    @Override
    public boolean trocar_pagina(int x, int y, int prRodinha) {
        return false;
    }

    private boolean conectar(String prIp, int prPorta) {
        try {
            OnlineMapLoader.aClientConnection.conectar(prIp, prPorta);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        OnlineMapLoader.aIsConectado = true;
        return true;
    }

    private void logar() {
        if (OnlineMapLoader.aIsConectado) {
            Thread lThread = new Thread(OnlineMapLoader.aClientConnection);
            lThread.start();
            ClientConnection.send(KDOqFoiEnviado.kdConectar);
        }
    }

    @Override
    public boolean interagivelAgora() {
        if (OnlineMapLoader.aIsOnline)
            return false;

        return true;
    }

    private Boolean tentarConectar() {
        JTextField lIp = new JTextField("localhost"), lPorta = new JTextField("7777");
        KeyListener l = new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
                if (!(e.getKeyChar() >= '0' && e.getKeyChar() <= '9')) {
                    e.consume();
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        };
        lPorta.addKeyListener(l);
        Object[] message = { Constant.ipServer, lIp, Constant.porta, lPorta };

        int option = JOptionPane.showConfirmDialog(null, message, Constant.conectarServidor, JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                String lIpText = lIp.getText();
                Integer lPort = Integer.parseInt(lPorta.getText());

                // TODO: ao conectar ele vai tentar automaticamente entrar. Mas possa ser que seja um server com vários personagens, então
                // teria um passo mais
                // Então como isso é uma base, deixaremos assim
                if (!conectar(lIpText, lPort))
                    JOptionPane.showMessageDialog(null, "Não foi possível realizar a conexão com o servidor.");
                else
                    logar();

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "alguns dados não foram inseridos ou foram inseridos incorretamente.");
            }

        }
        return false;
    }

    private Boolean tentarHostear() {
        JTextField lMaximoJogadores = new JTextField("10"), lPorta = new JTextField("7777");
        KeyListener l = new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
                if (!(e.getKeyChar() >= '0' && e.getKeyChar() <= '9')) {
                    e.consume();
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        };
        lMaximoJogadores.addKeyListener(l);
        lPorta.addKeyListener(l);
        Object[] message = { Constant.porta, lPorta, Constant.maximoJogadores, lMaximoJogadores };

        int option = JOptionPane.showConfirmDialog(null, message, Constant.hostearServidor, JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                Integer lMaximoJogadoresText = Integer.parseInt(lMaximoJogadores.getText());
                Integer lPort = Integer.parseInt(lPorta.getText());

                OnlineMapLoader.aServer = new Server();

                OnlineMapLoader.aServer.hostear(lMaximoJogadoresText, lPort);

                if (!conectar("localhost", lPort))
                    JOptionPane.showMessageDialog(null, "Não foi possível realizar a conexão com o servidor.");
                else
                    logar();

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "alguns dados não foram inseridos ou foram inseridos incorretamente.");
            }

        }
        return false;
    }

}
