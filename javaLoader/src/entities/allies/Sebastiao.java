package entities.allies;

import java.io.File;

import entities.ia.Astar;
import files.SalvarCarregar;
import graficos.Spritesheet;
import graficos.Talks;
import main.SimpleMapLoader;
import world.Tile;
import world.World;

public class Sebastiao extends NPC {

	public Sebastiao(Tile prTile) {
		super(prTile);
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
	protected void finishTalk(String prTalk) {
		if (Talks.namePorcoQuebrou.contentEquals(prTalk)) {
			// aqui ele vai dormir
			aSleep = true;
			aCaminho = Astar.findPath(World.pegar_chao(x, y, z), aOrigem);
		} else if (Talks.nameMexeuAnemona.contentEquals(prTalk)) {
			// SSinceramente nada, então só ddeixa passar
		}
		SimpleMapLoader.podeNovaMovimentacao = true;
	}
}
