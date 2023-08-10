package world;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import com.fasterxml.jackson.annotation.JsonProperty;

import entities.allies.NPC;
import graficos.ConjuntoSprites;
import graficos.telas.Sprite;
import graficos.ui.Ui;
import main.SimpleMapLoader;
import main.interfaces.tickRender;

public class Tile implements tickRender {
	private ArrayList<ConjuntoSprites> CoConjuntoSprites;
	private int x, y, z, aPos, posicao_Conjunto;

	private HashMap<String, Object> aPropriedades;

	public Tile(@JsonProperty("x") int x, @JsonProperty("y") int y, @JsonProperty("z") int z) {
		posicao_Conjunto = 0;
		this.x = x;
		this.y = y;
		this.z = z;
		CoConjuntoSprites = new ArrayList<>();
		CoConjuntoSprites.add(new ConjuntoSprites());
		aPos = World.calcular_pos(x, y, z);
	}

	public int ModificadorVelocidade() {
		if (aPropriedades != null && aPropriedades.get("Speed") != null)
			try {
				return Integer.parseInt(aPropriedades.get("Speed").toString());
			} catch (Exception e) {
			}
		return 0;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getZ() {
		return z;
	}

	public int getPosicao_Conjunto() {
		return posicao_Conjunto;
	}

	public void setPosicao_Conjunto(int posicao_Conjunto) {
		this.posicao_Conjunto = posicao_Conjunto;
	}

	public int getaPos() {
		return aPos;
	}

	public void setaPos(int aPos) {
		this.aPos = aPos;
	}

	public ArrayList<BufferedImage> obterSprite_atual() {
		return CoConjuntoSprites.get(posicao_Conjunto).obterSprite_atual();
	}

	@Override
	public void tick() {
		if (World.secondsCount != 0 && getPropriedade("trocarConjuntoDeTempo") != null) {
			if (getPropriedade("trocarConjuntoDeTempoLast") != null) {
				posicao_Conjunto = getPosicao_Conjunto();
			}
			if (getPropriedade("trocarConjuntoDeTempoLast") == null
					|| !getPropriedade("trocarConjuntoDeTempoLast").toString().contentEquals(World.secondsCount + "")) {
				try {
					if (World.secondsCount
							% Integer.parseInt(getPropriedade("trocarConjuntoDeTempo").toString()) == 0) {
						if (getPropriedade("ProximoConjuntoAleatorio") != null
								&& Boolean.valueOf(getPropriedade("ProximoConjuntoAleatorio").toString()))
							posicao_Conjunto = SimpleMapLoader.random.nextInt(CoConjuntoSprites.size());
						else
							posicao_Conjunto++;
						if (posicao_Conjunto >= CoConjuntoSprites.size())
							posicao_Conjunto = 0;
						addPropriedade("trocarConjuntoDeTempoLast", World.secondsCount + "");
					}

				} catch (Exception e) {

				}

			}
		}
	}

	public boolean isTileEmCima(int prX, int prY, int prZ) {
		if (z > prZ) {
			int dx, dy, maxWidth = 0, maxHeight = 0;
			if (posicao_Conjunto < CoConjuntoSprites.size() && CoConjuntoSprites.get(posicao_Conjunto) != null)
				for (ArrayList<Sprite> imagens : CoConjuntoSprites.get(posicao_Conjunto).getSprites()) {
					if (imagens != null && imagens.size() > 0) {
						Sprite sprite = imagens.get(World.tiles_index % imagens.size());

						BufferedImage image = sprite.pegarImagem();

						if (image.getWidth() > maxHeight) {
							maxWidth = image.getWidth();
						}
						if (image.getHeight() > maxHeight) {
							maxHeight = image.getHeight();
						}

					}
				}

			if (maxWidth > SimpleMapLoader.TileSize || maxHeight > SimpleMapLoader.TileSize) {
				dx = x - SimpleMapLoader.TileSize * ((maxWidth / SimpleMapLoader.TileSize) - 1);
				dy = y - SimpleMapLoader.TileSize * ((maxHeight / SimpleMapLoader.TileSize) - 1);
			} else {
				dx = x;
				dy = y;
			}
			dx -= (z - prZ) * SimpleMapLoader.TileSize;
			dy -= (z - prZ) * SimpleMapLoader.TileSize;

			if (new Rectangle(dx, dy, maxWidth, maxHeight).intersects(new Rectangle(SimpleMapLoader.player.getX(),
					SimpleMapLoader.player.getY(), SimpleMapLoader.TileSize, SimpleMapLoader.TileSize)))
				return true;

		}
		return false;

	}

	private boolean temTileAcima() {
		Tile lTile;
		for (int zz = z + 1; zz < World.maxRenderingZ; zz++) {
			lTile = World.pegar_chao(x + SimpleMapLoader.TileSize * (zz - z), y + SimpleMapLoader.TileSize * (zz - z),
					zz);
			if (lTile != null && lTile.isTileEmCima(x, y, z))
				return true;
		}
		return false;
	}

	@Override
	public void render(Graphics g) {

		if (posicao_Conjunto < CoConjuntoSprites.size() && CoConjuntoSprites.get(posicao_Conjunto) != null)
			for (int iLayer = 0; iLayer < CoConjuntoSprites.get(posicao_Conjunto).getSprites().size(); iLayer++) {
				ArrayList<Sprite> imagens = CoConjuntoSprites.get(posicao_Conjunto).getSprites().get(iLayer);
				if (imagens != null && imagens.size() > 0) {
					Sprite sprite = imagens.get(World.tiles_index % imagens.size());
					int dx, dy;
					BufferedImage image = sprite.pegarImagem();
					if (image.getWidth() > SimpleMapLoader.TileSize || image.getHeight() > SimpleMapLoader.TileSize) {
						dx = x - Camera.x - SimpleMapLoader.TileSize;
						dy = y - Camera.y - SimpleMapLoader.TileSize;
					} else {
						dx = x - Camera.x;
						dy = y - Camera.y;
					}
					dx -= (z - SimpleMapLoader.player.getZ()) * SimpleMapLoader.TileSize;
					dy -= (z - SimpleMapLoader.player.getZ()) * SimpleMapLoader.TileSize;
					if (z == SimpleMapLoader.player.getZ() && !temTileAcima()) {
						if (getPropriedade("renderLayerPosWorldRender") != null
								&& getPropriedade("renderLayerPosWorldRender").toString()
										.contentEquals((iLayer + 1) + ""))
							Ui.renderizarImagemDepois(g, image, dx, dy);
						else {
							if (getPropriedade("renderLayerPosWorldRenderHorizontal") != null
									&& getPropriedade("renderLayerPosWorldRenderHorizontal").toString()
											.contentEquals((iLayer + 1) + ""))
								World.renderizarImagemDepois((x >> World.log_ts) + 1, (y >> World.log_ts), g, image, dx,
										dy);
							if (getPropriedade("renderLayerPosWorldRenderVertical") != null
									&& getPropriedade("renderLayerPosWorldRenderVertical").toString()
											.contentEquals((iLayer + 1) + ""))
								World.renderizarImagemDepois((x >> World.log_ts), (y >> World.log_ts) + 1, g, image, dx,
										dy);
						}

					}
					g.drawImage(image, dx, dy, null);
				}
			}

	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public void setZ(int z) {
		this.z = z;
	}

	@SuppressWarnings("unchecked")
	public Tile utilizarEscada() {
		HashMap<String, Object> lHashMap = (HashMap<String, Object>) getPropriedade("TRANSPORT");
		if (lHashMap == null || lHashMap.get("DESTINY") == null)
			return null;

		return World.pegarAdicionarTileMundo(
				Tile.pegarPosicaoRelativa(getX(), getY(), getZ(), (List<Integer>) lHashMap.get("DESTINY")));

	}

	@SuppressWarnings("unchecked")
	public boolean isEscada() {
		HashMap<String, Object> lHashMap = (HashMap<String, Object>) getPropriedade("TRANSPORT");
		if (lHashMap == null || lHashMap.get("DESTINY") == null)
			return false;
		return true;
	}

	public boolean tem_sprites() {
		for (ArrayList<Sprite> spr : CoConjuntoSprites.get(posicao_Conjunto).getSprites()) {
			if (spr.size() > 0) {
				return true;
			}
		}
		return false;
	}

	public void addPropriedades(HashMap<String, Object> prPropriedades) {
		if (aPropriedades == null)
			aPropriedades = new HashMap<>();
		aPropriedades.putAll(prPropriedades);
	}

	public void addPropriedade(String prKey, Object prValor) {
		if (prKey == null)
			return;
		if (aPropriedades == null)
			aPropriedades = new HashMap<>();
		if (aPropriedades.get(prKey) != null)
			aPropriedades.remove(prKey);
		if (prValor != null && !prValor.toString().isBlank())
			aPropriedades.put(prKey, prValor);
	}

	public Object getPropriedade(String prKey) {
		if (aPropriedades == null)
			return null;
		return aPropriedades.get(prKey);
	}

	public void removePropriedade(String prKey) {
		if (aPropriedades == null)
			return;
		aPropriedades.remove(prKey);
	}

	public boolean trocar_Sprite(int x, int y, int prRodinha) {
		posicao_Conjunto += prRodinha;
		if (posicao_Conjunto >= CoConjuntoSprites.size())
			posicao_Conjunto = 0;
		else if (posicao_Conjunto < 0)
			posicao_Conjunto = CoConjuntoSprites.size() - 1;

		return true;
	}

	public boolean Solid() {
		if (aPropriedades == null || (getPropriedade("Solid") == null && getPropriedade("contemEntidade") == null))
			return false;
		try {
			if (getPropriedade("Solid") != null) {
				if (getPropriedade("Solid").toString().startsWith("ConjuntoNot=")) {
					if (getPosicao_Conjunto() != Integer.parseInt(getPropriedade("Solid").toString().split("=")[1]))
						return true;
				} else if (getPropriedade("Solid").toString().startsWith("Conjunto=")) {
					if (getPosicao_Conjunto() == Integer.parseInt(getPropriedade("Solid").toString().split("=")[1]))
						return true;
				} else if (Boolean.valueOf(getPropriedade("Solid").toString())
						|| Integer.parseInt(getPropriedade("Solid").toString()) == 1)
					return true;
			} else if (getPropriedade("contemEntidade") != null) {
				if (Boolean.valueOf(getPropriedade("contemEntidade").toString()))
					return true;
			}

		} catch (Exception e) {
		}
		return false;
	}

	public boolean playerSolid() {
		if (aPropriedades == null || (getPropriedade("Solid") == null && getPropriedade("contemEntidade") == null))
			return false;
		if (Solid())
			return true;
		return false;
	}

	public List<ConjuntoSprites> getCoConjuntoSprites() {
		return CoConjuntoSprites;
	}

	public void setCoConjuntoSprites(ArrayList<ConjuntoSprites> aCoConjuntoSprites) {
		this.CoConjuntoSprites = aCoConjuntoSprites;
	}

	public HashMap<String, Object> getaPropriedades() {
		return aPropriedades;
	}

	public void setaPropriedades(HashMap<String, Object> aPropriedades) {
		this.aPropriedades = aPropriedades;
	}

	public static int tileExisteLista(int prPos, ArrayList<Tile> prTilesList) {
		for (int i = 0; i < prTilesList.size(); i++) {
			Tile iTile = prTilesList.get(i);
			if (prPos == iTile.getaPos()) {
				return i;
			}
		}
		return -1;
	}

	public static int pegarPosicaoRelativa(int prFromX, int prFromY, int prFromZ, List<Integer> prPosicaoRelativa) {
		return World.calcular_pos(prFromX + (prPosicaoRelativa.get(0) << World.log_ts),
				prFromY + (prPosicaoRelativa.get(1) << World.log_ts), prFromZ + prPosicaoRelativa.get(2));
	}

	public void dispararEventoUnico(String prEvento) {
		switch (prEvento) {
		case "ProximoConjuntoAoInteragir":
			if (getPropriedade("ProximoConjuntoAoInteragir") != null) {
				try {
					posicao_Conjunto += Integer.parseInt(getPropriedade("ProximoConjuntoAoInteragir").toString());
				} catch (Exception e) {
					posicao_Conjunto++;
				}

				if (posicao_Conjunto >= CoConjuntoSprites.size())
					posicao_Conjunto = 0;
			}
			break;

		case "TrocarConjuntoDePara":
			if (getPropriedade("TrocarConjuntoDePara") != null) {
				try {
					String[] lSet = getPropriedade("TrocarConjuntoDePara").toString().split("=");
					Tile lTile = World.pegar_chao(Integer.parseInt(lSet[0]));
					if (lTile != null)
						lTile.setPosicao_Conjunto(Integer.parseInt(lSet[1]));
				} catch (Exception e) {
				}
			}
			break;

		case "TrocarConjuntoAoInteragirPara":
			if (getPropriedade("TrocarConjuntoAoInteragirPara") != null) {
				try {
					setPosicao_Conjunto(Integer.parseInt(getPropriedade("TrocarConjuntoAoInteragirPara").toString()));
				} catch (Exception e) {
				}
			}
			break;

		default:
		}

	}

	public void dispararEventos() {
		for (Entry<String, Object> iEntrySet : aPropriedades.entrySet()) {
			dispararEventoUnico(iEntrySet.getKey());
		}

	}

	public void eventosUnicos() {

		if (getPropriedade("NPC") != null) {
			SimpleMapLoader.entities.add(new NPC(this, getPropriedade("NPC").toString()));
		}
	}

	public String[] getConteudo() {
		String[] lCoString = null;
		if (aPropriedades != null) {
			lCoString = new String[aPropriedades.size() + 1];
			int i = 1;

			lCoString[0] = "Tile Nro. " + aPos;

			for (Entry<String, Object> iEntrySet : aPropriedades.entrySet()) {
				lCoString[i] = iEntrySet.getKey() + ": " + iEntrySet.getValue();
				i++;
			}

		} else {
			lCoString = new String[1];
			lCoString[0] = "Tile Nro. " + aPos;
		}
		return lCoString;

	}

}
