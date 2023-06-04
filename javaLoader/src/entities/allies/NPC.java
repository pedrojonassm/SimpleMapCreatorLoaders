package entities.allies;

import java.awt.Graphics;

import entities.Entity;
import main.SimpleMapLoader;
import main.Uteis;
import world.Tile;

public class NPC extends Entity {

	Tile aOrigem;

	String nome;

	protected boolean aSleep;

	int ticksDesdeUltimaMovimentacao;

	public NPC(Tile prTile, String prNome) {
		super(prTile.getX(), prTile.getY(), prTile.getZ());

		aOrigem = prTile;
		ticksDesdeUltimaMovimentacao = 0;
		aSleep = false;
		nome = prNome;
		randomSprites();

	}

	@Override
	public void tick() {
		// Definiremos aSleep = true quando ele for dormir,
		// mas ele tem que ir para a cama ainda
		if (!aSleep || Uteis.distancia(x, aOrigem.getX(), y, aOrigem.getY()) > 0) {
			ticksDesdeUltimaMovimentacao++;
			if (ticksDesdeUltimaMovimentacao > 180) {
				ticksDesdeUltimaMovimentacao = 0;
				if (SimpleMapLoader.podeNovaMovimentacao) {
					randomMoviment();
					changeAnimation();
				}
			}
			super.tick();
			if (sqm_alvo == null) {
				// Caso não tenhha pra onde ir, caso contrário ele se moverá infinitamente
				left = right = up = down = false;
			}
		} else if (aSleep && Uteis.distancia(x, aOrigem.getX(), y, aOrigem.getY()) <= SimpleMapLoader.TileSize) {
			sqm_alvo = null;
			aCaminho.clear();
			if (aOrigem.getPosicao_Conjunto() == 0)
				aOrigem.setPosicao_Conjunto(1);
		}
	}

	protected void randomMoviment() {
		switch (SimpleMapLoader.random.nextInt(8)) {
		case 0:
			up = true;
			break;
		case 1:
			right = true;
			up = true;
			break;
		case 2:
			right = true;
			break;
		case 3:
			right = true;
			down = true;
			break;
		case 4:
			down = true;
			break;
		case 5:
			down = true;
			left = true;
			break;
		case 6:
			left = true;
			break;
		case 7:
			left = true;
			up = true;
			break;

		}
	}

	@Override
	public void render(Graphics prGraphics) {
		if (!aSleep || Uteis.distancia(x, aOrigem.getX(), y, aOrigem.getY()) > 0)
			super.render(prGraphics);
	}

	public int obterPosOrigem() {
		return aOrigem.getaPos();
	}

	public boolean isSleep() {
		return aSleep;
	}

	public void setSleep(boolean prSleep) {
		this.aSleep = prSleep;
	}

}
