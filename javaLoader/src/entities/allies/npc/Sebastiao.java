package entities.allies.npc;

import java.awt.Graphics;
import java.io.File;

import entities.Entity;
import entities.ia.PathFinder;
import files.SalvarCarregar;
import graficos.Spritesheet;
import graficos.Ui;
import graficos.telas.BallonTalk;
import main.SimpleMapLoader;
import main.Uteis;
import world.Tile;
import world.World;

public class Sebastiao extends NPC {

	public Sebastiao(Tile prTile, String prNome) {
		super(prTile, prNome);
		if (SalvarCarregar.aArquivoPersonagens != null && SalvarCarregar.aArquivoPersonagens.exists()) {
			File lImagem = new File(SalvarCarregar.aArquivoPersonagens, "Sebastiao.png");
			if (lImagem.exists()) {
				Spritesheet lSpritesheet = new Spritesheet(lImagem, 12, 32, 36, "Sebastiao");
				sprites = lSpritesheet.get_x_sprites(lSpritesheet.getTotalSprites());
			}
			forceRenderSize = true;
			spriteADesenhar = 1;
		}
	}

	@Override
	public void tick() {
		if (aFala != null && !aSleep && Uteis.distancia(x, aOrigem.getX(), y, aOrigem.getY()) > 0) {
			boolean canTalk = true;
			for (Entity iEntity : SimpleMapLoader.entities) {
				if (iEntity.isMoving()) {
					canTalk = false;
					break;
				}
			}
			if (canTalk) {
				String lString = aFala;
				Ui.aBallonTalk.setFalas(BallonTalk.allTalks.get(aFala));
				Ui.aBallonTalk.next();
				Ui.aBallonTalk.adicionarAcaoAposFalar(() -> finishTalk(lString));
				aFala = null;
			}

		} else if (aSleep && x == aOrigem.getX() && y == aOrigem.getY() && aOrigem.getPosicao_Conjunto() == 0) {
			aOrigem.setPosicao_Conjunto(1);
		}
		super.tick();
	}

	public void finishTalk(String prTalk) {
		if (Talks.namePorcoQuebrou.contentEquals(prTalk)) {
			aSleep = true;
			aCaminho = PathFinder.point(World.pegar_chao(x, y, z), aOrigem);
			SimpleMapLoader.podeNovaMovimentacao = true;
		} else if (Talks.nameMexeuAnemona.contentEquals(prTalk)) {
			SimpleMapLoader.podeNovaMovimentacao = true;
		}
	}

	@Override
	public void render(Graphics prGraphics) {
		super.render(prGraphics);
	}

}
