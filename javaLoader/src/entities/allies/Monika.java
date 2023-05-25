package entities.allies;

import java.io.File;

import entities.ia.Astar;
import files.SalvarCarregar;
import graficos.Spritesheet;
import graficos.Talks;
import main.SimpleMapLoader;
import world.Tile;
import world.World;

public class Monika extends NPC {
	public Monika(Tile prTile) {
		super(prTile);
		if (SalvarCarregar.aArquivoPersonagens != null && SalvarCarregar.aArquivoPersonagens.exists()) {
			File lImagem = new File(SalvarCarregar.aArquivoPersonagens, "monika.png");
			if (lImagem.exists()) {
				Spritesheet lSpritesheet = new Spritesheet(lImagem, 12, 32, 36, "Monika");
				sprites = lSpritesheet.get_x_sprites(lSpritesheet.getTotalSprites());
			}
			forceRenderSize = true;
			spriteADesenhar = 1;
		}
	}

	public void finishTalk(String prTalk) {
		if (Talks.nameMatouAnemona.contentEquals(prTalk)) {
			aCaminho = Astar.findPath(World.pegar_chao(x, y, z), aOrigem);
			aSleep = true;
		} else if (Talks.nameTentarSubirEscada.contentEquals(prTalk)) {
			// só voltar a podder se mexer mesmo
		} else if (Talks.nameMexeuAlavanca.contentEquals(prTalk)) {
			// só voltar a podder se mexer mesmo
		}
		SimpleMapLoader.podeNovaMovimentacao = true;
	}
}
