package entities.allies.npc;

import entities.Entity;
import main.SimpleMapLoader;
import world.Tile;

public class NPC extends Entity {

	Tile aOrigem;
	String aNome;

	int ticksDesdeUltimaMovimentacao;

	public NPC(Tile prTile, String prNome) {
		super(prTile.getX(), prTile.getY(), prTile.getZ());

		aOrigem = prTile;
		aNome = prNome;
		ticksDesdeUltimaMovimentacao = 0;
		maxAnimationTime = 5;
	}

	@Override
	public void tick() {
		ticksDesdeUltimaMovimentacao++;
		if (ticksDesdeUltimaMovimentacao > 180) {
			ticksDesdeUltimaMovimentacao = 0;
			randomMoviment();
			changeAnimation();
		}
		super.tick();
		if (sqm_alvo == null) {
			left = right = up = down = false;
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

	public int obterPosOrigem() {
		return aOrigem.getaPos();
	}

}
