package graficos;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import main.configs.ExSpriteSheet;

public class Spritesheet extends ExSpriteSheet {

	private BufferedImage spritesheet;
	private int quadradosX, quadradosY;

	public Spritesheet(File prArquivo, ExSpriteSheet prSpriteSheet) {
		totalSprites = prSpriteSheet.getTotalSprites();
		tamanho = prSpriteSheet.getTamanho();
		nome = prSpriteSheet.getNome();
		try {
			spritesheet = ImageIO.read(prArquivo);
			quadradosX = spritesheet.getWidth() / tamanho;
			quadradosY = spritesheet.getHeight() / tamanho;
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public BufferedImage getAsset(int x, int y) {
		return getAsset(x + y * tamanho);
	}

	public BufferedImage getAsset(int position) {
		return spritesheet.getSubimage((position % quadradosX) * tamanho, (position / quadradosX) * tamanho, tamanho,
				tamanho);
	}

	public BufferedImage[] get_x_sprites(int total) {
		BufferedImage[] retorno = new BufferedImage[total];
		for (int i = 0; i < total; i++) {
			retorno[i] = getAsset(i);
		}
		return retorno;
	}

	public int getQuadradosX() {
		return quadradosX;
	}

	public int getQuadradosY() {
		return quadradosY;
	}

	public int getTamanho() {
		return tamanho;
	}

	public String getNome() {
		return nome;
	}

}