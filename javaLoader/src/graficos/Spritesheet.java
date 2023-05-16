package graficos;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import main.configs.ExSpriteSheet;

public class Spritesheet extends ExSpriteSheet {

	private BufferedImage spritesheet;
	private int quadradosX, quadradosY, tamanhoX, tamanhoY;

	public Spritesheet(File prArquivo, ExSpriteSheet prSpriteSheet) {
		totalSprites = prSpriteSheet.getTotalSprites();
		tamanho = prSpriteSheet.getTamanho();
		nome = prSpriteSheet.getNome();
		tamanhoX = tamanhoY = tamanho;
		try {
			spritesheet = ImageIO.read(prArquivo);
			quadradosX = spritesheet.getWidth() / tamanhoX;
			quadradosY = spritesheet.getHeight() / tamanhoY;
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public Spritesheet(File prArquivo, int prTotalSprites, int prTamanhoX, int prTamanhoY, String prNome) {
		totalSprites = prTotalSprites;
		tamanho = prTamanhoX;
		tamanhoX = prTamanhoX;
		tamanhoY = prTamanhoY;
		nome = prNome;
		try {
			spritesheet = ImageIO.read(prArquivo);
			quadradosX = spritesheet.getWidth() / tamanhoX;
			quadradosY = spritesheet.getHeight() / tamanhoY;
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public BufferedImage getAsset(int x, int y) {
		return getAsset(x + y * tamanho);
	}

	public BufferedImage getAsset(int position) {
		return spritesheet.getSubimage((position % quadradosX) * tamanhoX, (position / quadradosX) * tamanhoY, tamanhoX,
				tamanhoY);
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