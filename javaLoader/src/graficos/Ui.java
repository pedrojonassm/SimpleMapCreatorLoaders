package graficos;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import files.SalvarCarregar;
import graficos.telas.Tela;
import main.SimpleMapLoader;

public class Ui implements Tela {
	public static boolean mostrar;

	private static ArrayList<Runnable> renderizarDepois;

	public boolean cliqueUi;

	BufferedImage[] aSlides;
	private int aSlide;

	public Ui() {
		cliqueUi = false;
		mostrar = true;
		renderizarDepois = new ArrayList<>();
		aSlide = 0;

		if (SalvarCarregar.aArquivosSlides.exists()) {
			File lSlide;
			aSlides = new BufferedImage[11];
			for (int i = 1; i <= 11; i++) {
				lSlide = new File(SalvarCarregar.aArquivosSlides, "slide-" + i + ".png");
				if (lSlide.exists())
					try {
						aSlides[i - 1] = ImageIO.read(lSlide);
					} catch (IOException e) {
						e.printStackTrace();
					}
			}
		}
	}

	public void tick() {
	}

	public static void renderizarImagemDepois(Graphics prGraphics, BufferedImage image, int prPosX, int prPosY) {
		renderizarDepois.add(() -> prGraphics.drawImage(image, prPosX, prPosY, null));
	}

	public static void renderizarEscritaDepois(Graphics prGraphics, String prString, int prPosX, int prPosY) {
		Ui.renderizarDepois.add(() -> prGraphics.drawString(prString, prPosX, prPosY));
	}

	public static void renderizarDesenharQuadradoDepois(Graphics prGraphics, int prPosX, int prPosY, int prWidth,
			int prHeight) {
		Ui.renderizarDepois.add(() -> prGraphics.drawRect(prPosX, prPosY, prWidth, prHeight));
	}

	public static void renderizarDesenharArcoDepois(Graphics prGraphics, int prPosX, int prPosY, int prWidth,
			int prHeight, int prStartAngle, int prArcAngle) {
		Ui.renderizarDepois.add(() -> prGraphics.drawArc(prPosX, prPosY, prWidth, prHeight, prStartAngle, prArcAngle));
	}

	public void render(Graphics g) {
		g.setColor(Color.white);

		if (Ui.mostrar) {
			if (aSlide >= 0 && aSlide < aSlides.length)
				g.drawImage(aSlides[aSlide], 0, 0, SimpleMapLoader.windowWidth, SimpleMapLoader.windowHEIGHT, null);
		}

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

	public void nextSlide(int prNext) {
		aSlide += prNext;
		if (aSlide < 0)
			aSlide = 0;
		else if (aSlide >= aSlides.length)
			aSlide = aSlides.length - 1;
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