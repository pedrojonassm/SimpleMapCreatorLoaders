package files;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import main.SimpleMapLoader;
import main.configs.ExConfig;
import main.configs.ExSpriteSheet;
import world.Tile;
import world.World;

public class SalvarCarregar {

	public static File aArquivoMundos;

	private ArrayList<String> mundosDisponiveis;

	public static final String nameFileWorld = "world.world", nameFileWorldconfig = "world.config",
			startWorldName = "MundoInicial", nameFolderImagens = "imagens", nameFileImageData = "data.config",
			nameImagem = "image.png";

	public SalvarCarregar() {
		try {
			aArquivoMundos = new File(URLDecoder.decode(getClass().getResource("/mundos").getFile(), "UTF-8"));
			if (aArquivoMundos.exists()) {
				mundosDisponiveis = listFoldersInFolder(aArquivoMundos);

			} else {
				mundosDisponiveis = new ArrayList<>();
			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

	}

	public ArrayList<String> getMundosDisponiveis() {
		return mundosDisponiveis;
	}

	public static ArrayList<String> listFilesForFolder(final File folder) {
		ArrayList<String> retorno = new ArrayList<String>();
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				for (String nome : listFilesForFolder(fileEntry)) {
					retorno.add(folder.getName() + "/" + nome);
				}
			} else {
				retorno.add(folder.getName() + "/" + fileEntry.getName());
			}
		}
		return retorno;
	}

	public static void salvarConfiguracoesMundo() throws IOException {
		File lFileworld = new File(World.aArquivo, nameFileWorldconfig);
		BufferedWriter writer = new BufferedWriter(new FileWriter(lFileworld));

		SimpleMapLoader.aConfig.atualizarAntesSalvar();

		writer.write(toJSON(SimpleMapLoader.aConfig));
		writer.flush();
		writer.close();

	}

	public static void salvar_mundo() {

		String salvar = "";
		salvar += SalvarCarregar.toJSON(World.tiles);
		try {
			salvarConfiguracoesMundo();
			File lFileworld = new File(World.aArquivo, nameFileWorld);
			if (!lFileworld.exists())
				lFileworld.createNewFile();
			BufferedWriter writer = new BufferedWriter(new FileWriter(lFileworld));
			writer.write(salvar);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static ArrayList<String> listFoldersInFolder(final File folder) {
		ArrayList<String> retorno = new ArrayList<String>();
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				retorno.add(fileEntry.getName());
			}
		}
		return retorno;
	}

	public static ExConfig carregarConfiguracoesMundo(File lFileConfig) throws Exception {
		ExConfig lExConfig = new ExConfig();
		if (lFileConfig.exists()) {
			BufferedReader reader = new BufferedReader(new FileReader(lFileConfig));
			String singleLine = null;
			String lFile = "";
			while ((singleLine = reader.readLine()) != null) {
				lFile += singleLine;
			}
			reader.close();
			lExConfig = (ExConfig) fromJson(lFile, ExConfig.class);

		}
		return lExConfig;
	}

	public static String toJSON(final Object prObj) {
		String lJSON = "";

		ObjectMapper lObjectMapper = new ObjectMapper();

		try {
			lObjectMapper.configure(SerializationFeature.WRITE_ENUMS_USING_INDEX, true);
			lJSON = lObjectMapper.writeValueAsString(prObj) + "\n";
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return lJSON;
	}

	public static <T> Object fromJson(String prJson, Class<T> prClass) {
		ObjectMapper lObjectMapper = new ObjectMapper();
		try {
			lObjectMapper.configure(SerializationFeature.WRITE_ENUMS_USING_INDEX, true);
			return lObjectMapper.readValue(prJson, prClass);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Tile[] carregarMundo(File prfile) throws Exception {
		SimpleMapLoader.aConfig = carregarConfiguracoesMundo(new File(prfile + "/" + nameFileWorldconfig));
		BufferedReader reader = new BufferedReader(new FileReader(new File(prfile, nameFileWorld)));
		String singleLine = null, lFile = "";
		while ((singleLine = reader.readLine()) != null) {
			lFile += singleLine;
		}
		reader.close();
		return (Tile[]) SalvarCarregar.fromJson(lFile, Tile[].class);

	}

	public static void carregarSprites(File lFileImagens) {
		World.spritesCarregados = new HashMap<String, BufferedImage[]>();
		File lFile;
		for (String lImagem : listFoldersInFolder(lFileImagens)) {
			lFile = new File(lFileImagens, lImagem);
			if (lFile.exists()) {
				BufferedReader reader;
				try {
					reader = new BufferedReader(new FileReader(new File(lFile, nameFileImageData)));
					String singleLine, lConteudo = "";
					while ((singleLine = reader.readLine()) != null && !singleLine.isBlank()) {
						lConteudo += singleLine;
					}
					ExSpriteSheet lExSpriteSheet = (ExSpriteSheet) fromJson(lConteudo, ExSpriteSheet.class);
					World.adicionarSprites(new File(lFile, nameImagem), lExSpriteSheet);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}
	}

	public static void toOtherWorld(String prNomeMundo) {
		File lPastaMundo = new File(aArquivoMundos, prNomeMundo), lConfigs, lArquivoMundo;
		if (lPastaMundo.exists() && lPastaMundo.isDirectory()) {
			lConfigs = new File(lPastaMundo, nameFileWorld);
			lArquivoMundo = new File(lPastaMundo, nameFileWorld);
			if (lConfigs.exists() && lArquivoMundo.exists())
				World.novo_mundo(lPastaMundo);
		}
	}
}
