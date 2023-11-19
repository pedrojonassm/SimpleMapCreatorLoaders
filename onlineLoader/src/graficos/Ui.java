package graficos;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import graficos.telas.TelaSelecionarServidor;
import main.interfaces.Tela;

public class Ui implements Tela {
    public static boolean mostrar;

    private static ArrayList<Runnable> renderizarDepois;

    public static ArrayList<Tela> aCoTelas;

    public boolean cliqueUi;

    public Ui() {
        cliqueUi = false;
        mostrar = true;
        renderizarDepois = new ArrayList<>();
        aCoTelas = new ArrayList<>();
        aCoTelas.add(new TelaSelecionarServidor());
    }

    public void tick() {
        if (mostrar)
            for (Tela iTela : aCoTelas) {
                if (iTela.interagivelAgora())
                    iTela.tick();
            }
    }

    public static void renderizarImagemDepois(Graphics prGraphics, BufferedImage image, int prPosX, int prPosY) {
        renderizarDepois.add(() -> prGraphics.drawImage(image, prPosX, prPosY, null));
    }

    public static void renderizarEscritaDepois(Graphics prGraphics, String prString, int prPosX, int prPosY) {
        Ui.renderizarDepois.add(() -> prGraphics.drawString(prString, prPosX, prPosY));
    }

    public static void renderizarDesenharQuadradoDepois(Graphics prGraphics, int prPosX, int prPosY, int prWidth, int prHeight) {
        Ui.renderizarDepois.add(() -> prGraphics.drawRect(prPosX, prPosY, prWidth, prHeight));
    }

    public static void renderizarDesenharArcoDepois(Graphics prGraphics, int prPosX, int prPosY, int prWidth, int prHeight,
            int prStartAngle, int prArcAngle) {
        Ui.renderizarDepois.add(() -> prGraphics.drawArc(prPosX, prPosY, prWidth, prHeight, prStartAngle, prArcAngle));
    }

    public void render(Graphics g) {
        g.setColor(Color.white);

        if (renderizarDepois.size() > 0) {

            for (Runnable iRunnable : renderizarDepois) {
                try {
                    iRunnable.run();
                } catch (Exception e) {
                }

            }

            renderizarDepois.clear();
        }

        if (mostrar)
            for (Tela iTela : aCoTelas) {
                if (iTela.interagivelAgora())
                    iTela.render(g);
            }

        g.setColor(Color.white);
    }

    public boolean clicou(int x, int y) {
        if (!mostrar)
            return false;

        for (Tela iTela : aCoTelas) {
            if (iTela.interagivelAgora())
                iTela.clicou(x, y);
        }

        return false;
    }

    public boolean cliquedireito(int x, int y) {
        if (!mostrar)
            return false;

        for (Tela iTela : aCoTelas) {
            if (iTela.interagivelAgora())
                iTela.cliquedireito(x, y);
        }

        return false;
    }

    public boolean trocar_pagina(int x, int y, int prRodinha) {
        if (!mostrar)
            return false;

        for (Tela iTela : aCoTelas) {
            if (iTela.interagivelAgora())
                iTela.trocar_pagina(x, y, prRodinha);
        }

        return false;
    }

    @Override
    public boolean interagivelAgora() {
        return true;
    }

}