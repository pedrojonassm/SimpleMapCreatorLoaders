package graficos;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import graficos.telas.Sprite;
import main.SimpleMapLoader;
import world.World;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ConjuntoSprites {
	private ArrayList<ArrayList<Sprite>> sprites;

	public ConjuntoSprites() {
		sprites = new ArrayList<ArrayList<Sprite>>();

		for (int i = 0; i < SimpleMapLoader.aConfig.getTotalLayers(); i++) {
			sprites.add(new ArrayList<Sprite>());
		}

	}

	public ArrayList<ArrayList<Sprite>> getSprites() {
		return sprites;
	}

	public void setSprites(ArrayList<ArrayList<Sprite>> sprites) {
		this.sprites = sprites;
	}

	public ArrayList<BufferedImage> obterSprite_atual() {
		ArrayList<BufferedImage> lDesenhoAtual = new ArrayList<BufferedImage>();
		for (ArrayList<Sprite> imagens : sprites) {
			if (imagens != null && imagens.size() > 0) {
				Sprite sprite = imagens.get(World.tiles_index % imagens.size());
				lDesenhoAtual.add(sprite.pegarImagem());
			}
		}

		return lDesenhoAtual;
	}

	public ConjuntoSprites clone() {
		ConjuntoSprites lConjuntoSprites = new ConjuntoSprites();
		ArrayList<ArrayList<Sprite>> lSpritesConjunto = new ArrayList<>();
		ArrayList<Sprite> lCoSprites;
		Sprite lSprite;
		for (int i = 0; i < getSprites().size(); i++) {
			lCoSprites = new ArrayList<>();
			for (int j = 0; j < getSprites().get(i).size(); j++) {
				lSprite = new Sprite();
				lSprite.setNome(getSprites().get(i).get(j).getNome());
				lSprite.setPosicao(getSprites().get(i).get(j).getPosicao());
				lCoSprites.add(lSprite);
			}
			lSpritesConjunto.add(lCoSprites);
		}
		lConjuntoSprites.setSprites(lSpritesConjunto);
		return lConjuntoSprites;
	}

}
