package entities.allies.npc;

import java.awt.Graphics;

import entities.Entity;
import main.SimpleMapLoader;
import main.Uteis;
import world.Tile;

public class NPC extends Entity {

	Tile aOrigem;
	String aNome;

	protected String aFala;

	protected boolean aSleep;

	int ticksDesdeUltimaMovimentacao;

	public NPC(Tile prTile, String prNome) {
		super(prTile.getX(), prTile.getY(), prTile.getZ());

		aOrigem = prTile;
		aNome = prNome;
		ticksDesdeUltimaMovimentacao = 0;
		maxAnimationTime = 5;
		aSleep = false;
	}

	@Override
	public void tick() {
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
				left = right = up = down = false;
			}
		}
	}

	public boolean isSleep() {
		return aSleep;
	}

	public void setSleep(boolean prSleep) {
		this.aSleep = prSleep;
	}

	public String getaFala() {
		return aFala;
	}

	public void setaFala(String aFala) {
		this.aFala = aFala;
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

	public int obterPosOrigem() {
		return aOrigem.getaPos();
	}

	@Override
	public void render(Graphics prGraphics) {
		if (!aSleep || Uteis.distancia(x, aOrigem.getX(), y, aOrigem.getY()) > 0)
			super.render(prGraphics);
	}

}
