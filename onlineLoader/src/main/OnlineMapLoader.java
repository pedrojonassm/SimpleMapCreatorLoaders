package main;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JFrame;

import entities.Entity;
import entities.allies.Player;
import entities.ia.Astar;
import files.SalvarCarregar;
import graficos.Ui;
import main.configs.ExConfig;
import online.client.ClientConnection;
import online.client.entities.ExEntity;
import online.servidor.Server;
import world.Camera;
import world.Tile;
import world.World;

public class OnlineMapLoader extends Canvas implements Runnable, KeyListener, MouseListener, MouseMotionListener, MouseWheelListener {

    private static final long serialVersionUID = 1L;
    public static JFrame frame;
    private Thread thread;
    private boolean isRunning = true, aTrocouPosicao;
    public static int windowWidth = 1240, windowHEIGHT = 720, TileSize, FPS;
    private BufferedImage image;

    public static World world;

    public static Player player;
    public static ArrayList<Entity> entities;
    public SalvarCarregar memoria;

    private int aPos, aPosOld, aCliqueMouse;
    public int aMouseX, aMouseY;

    private boolean clique_no_mapa;
    private int arcoInicial, arcoFinal;

    public static boolean control, shift;
    public static Random random;
    public static Ui ui;

    public static ExConfig aConfig;

    public static OnlineMapLoader instance;

    public static boolean podeNovaMovimentacao;

    public static ClientConnection aClientConnection;

    public static Boolean aIsConectado, aIsOnline;

    public Font aFonte;

    public static Server aServer = null;

    public OnlineMapLoader(Boolean prIsServer) {
        instance = this;
        arcoInicial = arcoFinal = 0;
        aIsConectado = aIsOnline = false;
        aConfig = new ExConfig();
        memoria = new SalvarCarregar();
        world = new World(null);
        podeNovaMovimentacao = true;
        aFonte = new Font("arial", Font.PLAIN, 20);
        if (World.ok) {
            startGerador();
            if (!prIsServer) {
                initFrame();
                aClientConnection = new ClientConnection();
            }

        }

    }

    public void startGerador() {
        entities = new ArrayList<>();
        OnlineMapLoader.TileSize = aConfig.getTileSize();
        World.log_ts = Uteis.log(OnlineMapLoader.TileSize, 2);
        random = new Random();
    }

    public void initFrame() {
        player = new Player(aConfig.getPlayerX(), aConfig.getPlayerY(), 0);
        ui = new Ui();
        World.carregarSprites();
        control = shift = clique_no_mapa = false;
        image = new BufferedImage(windowWidth, windowHEIGHT, BufferedImage.TYPE_INT_RGB);
        World.ready = true;
        addKeyListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {

                windowWidth = e.getComponent().getWidth();
                windowHEIGHT = e.getComponent().getHeight();

                super.componentResized(e);
            }
        });

        frame = new JFrame("java Loader");
        setPreferredSize(new Dimension(windowWidth, windowHEIGHT));
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (aServer != null) {
                    aServer.fecharServidor();
                }

                super.windowClosing(e);
            }
        });
        frame.add(this);
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public synchronized void start() {
        thread = new Thread(this);
        isRunning = true;
        thread.start();
    }

    public synchronized void stop() {
        isRunning = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {
        OnlineMapLoader gerador = new OnlineMapLoader(false);
        if (World.ok)
            gerador.start();
    }

    public void tick() {
        if (clique_no_mapa && aTrocouPosicao) {
            aTrocouPosicao = false;
            if (aCliqueMouse == 1) { // LMB
                if (!control) {
                    clique_no_mapa = false;
                }
                if (shift) {

                }
            } else if (aCliqueMouse == 3) { // RMB
                if (!control) {
                    clique_no_mapa = false;
                }
                if (shift) {

                }
            }
        }
        for (Entity iEntity : entities)
            iEntity.tick();
        player.tick();
        world.tick();
        ui.tick();
    }

    public static void atualizarOuInserirEntidade(ExEntity prExEntity) {
        Boolean lAtualizou = false;

        for (Entity iEntity : entities) {
            if (iEntity.getIdentificadorServidor() == prExEntity.getIdentificadorServidor()) {
                lAtualizou = true;
                iEntity.setX(prExEntity.getX());
                iEntity.setY(prExEntity.getY());
                iEntity.setZ(prExEntity.getZ());
            }
        }

        if (!lAtualizou) {
            Entity lEntity = new Entity(prExEntity.getX(), prExEntity.getY(), prExEntity.getZ());
            lEntity.definirIdentificadorServidor(prExEntity.getIdentificadorServidor());
            entities.add(lEntity);
        }

    }

    public static void retirarEntidade(int prIdentificador) {
        for (int i = 0; i < entities.size(); i++) {
            Entity iEntity = entities.get(i);
            if (iEntity.getIdentificadorServidor() == prIdentificador) {
                entities.remove(iEntity);
                break;
            }
        }
    }

    public void render() {
        BufferStrategy bs = this.getBufferStrategy();
        if (bs == null) {
            this.createBufferStrategy(3);
            return;
        }

        Graphics g = image.getGraphics();
        g.setFont(aFonte);
        g.setColor(new Color(0, 0, 0));
        g.fillRect(0, 0, windowWidth + TileSize, windowHEIGHT + TileSize);
        if (World.ready && World.ok && aIsOnline) {
            world.render(g);

            g.setColor(Color.red);
            int[] localDesenho = Uteis.calcularPosicaoSemAlturaRelativoACamera(aPos);
            g.drawRect(localDesenho[0], localDesenho[1], TileSize, TileSize);
            for (Entity iEntity : entities)
                iEntity.render(g);

            if (player.aPosAtual < 0 || World.tiles[player.aPosAtual] == null)
                player.render(g);
        } else if (aIsConectado) {
            g.setColor(Color.white);
            arcoInicial += 5;
            if (arcoInicial >= 360) {
                arcoInicial = 30;
            }

            arcoFinal += 3;
            if (arcoFinal >= 360)
                arcoFinal = 0;

            g.drawArc(windowWidth / 2 - TileSize / 2, windowHEIGHT / 2 - TileSize / 2, TileSize, TileSize, arcoInicial, arcoFinal);
        }
        ui.render(g);
        g.dispose();
        g = bs.getDrawGraphics();
        g.drawImage(image, 0, 0, windowWidth, windowHEIGHT, null);
        bs.show();
    }

    public void run() {
        long lastTime = System.nanoTime();
        double ns = 1000000000 / 60;
        double delta = 0;
        int frames = 0;
        double timer = System.currentTimeMillis();
        requestFocus();
        while (isRunning) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            if (delta >= 1) {
                try {
                    if (World.ready && World.ok)
                        tick();
                    render();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                frames++;
                delta = 0;
            }

            if (System.currentTimeMillis() - timer >= 1000) {
                FPS = frames;
                frames = 0;
                timer += 1000;
            }
        }
        stop();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            if (!player.right)
                player.aBloqueadoMovimentacao = false;
            player.right = true;
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            if (!player.left)
                player.aBloqueadoMovimentacao = false;
            player.left = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            if (!player.up)
                player.aBloqueadoMovimentacao = false;
            player.up = true;
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            if (!player.down)
                player.aBloqueadoMovimentacao = false;
            player.down = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_CONTROL)
            control = true;
        if (e.getKeyCode() == KeyEvent.VK_SHIFT)
            shift = true;
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            // Ui.mostrar = !Ui.mostrar;
            return;
        }
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            return;
        }

    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_RIGHT)
            player.right = false;
        if (e.getKeyCode() == KeyEvent.VK_LEFT)
            player.left = false;
        if (e.getKeyCode() == KeyEvent.VK_UP)
            player.up = false;
        if (e.getKeyCode() == KeyEvent.VK_DOWN)
            player.down = false;
        if (e.getKeyCode() == KeyEvent.VK_CONTROL)
            control = false;
        if (e.getKeyCode() == KeyEvent.VK_SHIFT)
            shift = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent arg0) {
    }

    @Override
    public void mouseEntered(MouseEvent arg0) {

    }

    @Override
    public void mouseExited(MouseEvent arg0) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        aMouseX = e.getX();
        aMouseY = e.getY();
        Tile lEscolhido = World.tiles[aPos];
        if (e.getButton() == MouseEvent.BUTTON1) {
            if ((!Ui.mostrar || !ui.clicou(e.getX(), e.getY())) && aIsOnline) {
                clique_no_mapa = true;
                aCliqueMouse = 1;
                aTrocouPosicao = true;
                player.setaCaminho(
                        Astar.findPath(World.pegar_chao(player.getX(), player.getY(), player.getZ()), World.pegar_chao(aPos), true));
                return;
            } else {
                ui.cliqueUi = true;
            }
        } else if (e.getButton() == MouseEvent.BUTTON2) {
            int[] teste = Uteis.calcularPosicaoSemAlturaRelativoACamera(aPos);
            System.out.println("cx: " + Camera.x + " cy: " + Camera.y);
            System.out.println("pos: " + aPos);
            System.out.println("tem tile: " + (lEscolhido != null));
            System.out.println("tx: " + teste[0] + " ty: " + teste[1] + " tz: " + teste[2] + "\n");
            return;
        } else if (e.getButton() == MouseEvent.BUTTON3) {
            if (ui.cliquedireito(e.getX(), e.getY()) || !aIsOnline)
                return;
            else {
                Tile lTile = World.tiles[aPos];
                if (lTile != null) {
                    if (lTile.getaPropriedades() != null) {
                        lTile.dispararEventos();
                    }
                }
                clique_no_mapa = true;
                aCliqueMouse = 3;
            }
        }
    }

    public int getaPos() {
        return aPos;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        aMouseX = e.getX();
        aMouseY = e.getY();
        clique_no_mapa = false;
        if (e.getButton() == MouseEvent.BUTTON1)
            ui.cliqueUi = false;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        aMouseX = e.getX();
        aMouseY = e.getY();
        aPos = World.calcular_pos(e.getX() + Camera.x, e.getY() + Camera.y, player.getZ());
        if (aPosOld != aPos) {
            aPosOld = aPos;
            aTrocouPosicao = true;
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        aMouseX = e.getX();
        aMouseY = e.getY();
        aPos = World.calcular_pos(e.getX() + Camera.x, e.getY() + Camera.y, player.getZ());
        if (aPosOld != aPos) {
            aPosOld = aPos;
            aTrocouPosicao = true;
        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        aMouseX = e.getX();
        aMouseY = e.getY();
        if (ui.trocar_pagina(e.getX(), e.getY(), (e.getWheelRotation() > 0) ? 1 : -1) || !aIsOnline)
            return;
        else {
            if (control) {
                player.camada(e.getWheelRotation());
                aPos = World.calcular_pos(e.getX() + Camera.x, e.getY() + Camera.y, player.getZ());
            }
        }
    }

}
