package entities.allies.npc;

import java.io.File;

import files.SalvarCarregar;
import graficos.Spritesheet;
import world.Tile;

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

}
