package graficos;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import graficos.telas.BallonTalk;
import graficos.telas.Tela;

public class Ui implements Tela {
	public static boolean mostrar;

	public static ArrayList<Runnable> renderizarDepois;

	public static BallonTalk aBallonTalk;

	public boolean cliqueUi;

	public Ui() {
		cliqueUi = false;
		mostrar = true;
		renderizarDepois = new ArrayList<>();
		aBallonTalk = new BallonTalk();
	}

	public void tick() {
		aBallonTalk.tick();
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

		aBallonTalk.render(g);

		g.setColor(Color.white);
	}

	public boolean clicou(int x, int y) {
		if (aBallonTalk.contemFalas()) {
			aBallonTalk.next();
			return true;
		}

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