package graficos.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import main.SimpleMapLoader;
import main.interfaces.tickRender;
import world.World;

public class MiniJanela implements tickRender {

	Rectangle aPosicao;

	int aTamanhoAtual, aTamanhoX;
	boolean aMostrar;

	int aLastConteudo;
	String[] aCoConteudo;

	public MiniJanela() {
		aPosicao = new Rectangle(50, SimpleMapLoader.windowHEIGHT - 240 - 10, 240, 240);
		aTamanhoAtual = 0;
		aMostrar = false;
		aLastConteudo = -1;
	}

	@Override
	public void tick() {
		if (aMostrar && aTamanhoAtual < aPosicao.height) {
			aTamanhoAtual += 6;
		} else if (!aMostrar && aTamanhoAtual > 0) {
			aTamanhoAtual -= 6;
		}

	}

	@Override
	public void render(Graphics prGraphics) {
		if (aTamanhoAtual > 0) {
			if (aCoConteudo != null && aTamanhoX == 0) {
				int max = 240, metrics = 0;
				for (String s : aCoConteudo) {
					metrics = prGraphics.getFontMetrics().stringWidth(s);
					if (metrics > max)
						max = metrics + 10;
				}
				aTamanhoX = max;

			}
			prGraphics.setColor(Color.RED);
			prGraphics.fillRect(aPosicao.x - 20, aPosicao.y - 10, aTamanhoX + 40, aTamanhoAtual + 20);
			prGraphics.setColor(Color.black);
			prGraphics.fillRect(aPosicao.x - 10, aPosicao.y, aTamanhoX + 20, aTamanhoAtual);
			prGraphics.setColor(Color.white);

			if (aCoConteudo != null && aCoConteudo.length > 0) {
				for (int i = 0; i < aCoConteudo.length; i++) {
					int lDesenhar = aPosicao.height * (i + 1) / (aCoConteudo.length + 1)
							+ prGraphics.getFont().getSize();
					if (lDesenhar > aTamanhoAtual)
						break;

					prGraphics.drawString(aCoConteudo[i], aPosicao.x, aPosicao.y + lDesenhar);
				}
			}

		}
	}

	public void ativarDesativarJanela(int prPos) {
		if (prPos == aLastConteudo && aMostrar) {
			aMostrar = false;
		} else if (aLastConteudo != prPos || !aMostrar) {
			aMostrar = true;
			aLastConteudo = prPos;
		}
		if (aMostrar) {
			aTamanhoX = 0;
			aCoConteudo = World.tiles[prPos].getConteudo();
		}
	}

	public void setaCoConteudo(String[] aCoConteudo) {
		this.aCoConteudo = aCoConteudo;
	}

}
