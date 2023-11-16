package graficos;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import graficos.telas.Tela;

public class Ui implements Tela {
    public static boolean mostrar;

    private static ArrayList<Runnable> renderizarDepois;

    public boolean cliqueUi;

    public Ui() {
        cliqueUi = false;
        mostrar = true;
        renderizarDepois = new ArrayList<>();
    }

    public void tick() {
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

        g.setColor(Color.white);
    }

    public boolean clicou(int x, int y) {
        if (!mostrar)
            return false;

        return false;
    }

    public boolean cliquedireito(int x, int y) {
        if (mostrar) {

        }

        return false;
    }

    public boolean trocar_pagina(int x, int y, int prRodinha) {
        if (mostrar) {

        }
        return false;
    }

}