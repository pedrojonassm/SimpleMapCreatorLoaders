package graficos;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import graficos.telas.Tela;
import main.SimpleMapLoader;

public class Ui implements Tela {
	public static boolean mostrar;

	public static ArrayList<Runnable> renderizarDepois;

	public boolean cliqueUi;

	public Ui() {
		cliqueUi = false;
		mostrar = false;
		renderizarDepois = new ArrayList<>();

	}

	public void tick() {

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
		if (mostrar) {
			int w1 = g.getFontMetrics().stringWidth("Ui aberta!");
			g.drawString("Ui aberta!", SimpleMapLoader.windowWidth / 2 - w1 / 2, SimpleMapLoader.windowHEIGHT - 20);
		}
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