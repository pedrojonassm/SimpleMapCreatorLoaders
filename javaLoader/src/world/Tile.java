package world;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import com.fasterxml.jackson.annotation.JsonProperty;

import entities.Entity;
import entities.allies.npc.Monika;
import entities.allies.npc.NPC;
import entities.allies.npc.Sebastiao;
import entities.allies.npc.Talks;
import entities.ia.Astar;
import graficos.ConjuntoSprites;
import graficos.Ui;
import graficos.telas.Sprite;
import main.SimpleMapLoader;
import main.Uteis;
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
		if (aPropriedades != null && aPropriedades.containsKey("NPC")) {
			boolean lSummon = true;
			for (Entity iEntity : SimpleMapLoader.entities) {
				if (iEntity instanceof NPC && ((NPC) iEntity).obterPosOrigem() == aPos) {
					lSummon = false;
					break;
				}
			}
			if (lSummon) {
				if ("Sebastiao".contentEquals(aPropriedades.get("NPC").toString())) {
					SimpleMapLoader.sebastiao = new Sebastiao(this, aPropriedades.get("NPC").toString());
					SimpleMapLoader.entities.add(SimpleMapLoader.sebastiao);
				} else if ("Monika".contentEquals(aPropriedades.get("NPC").toString())) {
					SimpleMapLoader.monika = new Monika(this, aPropriedades.get("NPC").toString());
					SimpleMapLoader.entities.add(SimpleMapLoader.monika);
				}
			}

		}
	}

	@Override
	public void render(Graphics g) {

		if (posicao_Conjunto < CoConjuntoSprites.size() && CoConjuntoSprites.get(posicao_Conjunto) != null)
			for (ArrayList<Sprite> imagens : CoConjuntoSprites.get(posicao_Conjunto).getSprites()) {
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
		if (aPropriedades == null || getPropriedade("Solid") == null)
			return false;
		try {
			if (getPropriedade("Solid").toString().startsWith("ConjuntoNot=")) {
				if (getPosicao_Conjunto() != Integer.parseInt(getPropriedade("Solid").toString().split("=")[1]))
					return true;
			} else if (getPropriedade("Solid").toString().startsWith("Conjunto=")) {
				if (getPosicao_Conjunto() == Integer.parseInt(getPropriedade("Solid").toString().split("=")[1]))
					return true;
			} else if (Boolean.valueOf(getPropriedade("Solid").toString())
					|| Integer.parseInt(getPropriedade("Solid").toString()) == 1)
				return true;

		} catch (Exception e) {
		}
		return false;
	}

	public boolean playerSolid() {
		if (aPropriedades == null || getPropriedade("Solid") == null)
			return false;
		if (Solid())
			return true;
		if ("Monika".contentEquals(getPropriedade("Solid").toString()) && !SimpleMapLoader.monika.isSleep())
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

	public void dispararEventos() {
		for (Entry<String, Object> iEntrySet : aPropriedades.entrySet()) {
			System.out.println(iEntrySet.getKey() + " = " + iEntrySet.getValue());
		}
		if (aPropriedades.containsKey("evento")) {
			if ("CallMonika".contentEquals(aPropriedades.get("evento").toString())) {

				if (Uteis.distancia(x + SimpleMapLoader.TileSize / 2,
						SimpleMapLoader.player.getX() + SimpleMapLoader.TileSize / 2, y + SimpleMapLoader.TileSize / 2,
						SimpleMapLoader.player.getY() + SimpleMapLoader.TileSize / 2) <= SimpleMapLoader.TileSize
								* 1.5) {
					if (!SimpleMapLoader.monika.isSleep()) {
						SimpleMapLoader.monika.saltar();
						ArrayList<Tile> lCoTile = Astar.findPath(
								World.pegar_chao(SimpleMapLoader.monika.getX(), SimpleMapLoader.monika.getY(),
										SimpleMapLoader.monika.getZ()),
								World.pegar_chao(SimpleMapLoader.player.getX(), SimpleMapLoader.player.getY(),
										SimpleMapLoader.player.getZ()));
						lCoTile.remove(lCoTile.size() - 1);
						SimpleMapLoader.monika.setaCaminho(lCoTile);
						if (aPropriedades.containsKey("monstro")) {
							SimpleMapLoader.monika.setaFala(Talks.nameMexeuAlavanca);
							SimpleMapLoader.podeNovaMovimentacao = false;
						}
					} else {
						posicao_Conjunto = 1;
						if (posicao_Conjunto >= CoConjuntoSprites.size())
							posicao_Conjunto = 0;
					}
				}
			} else if ("CallSebastiao".contentEquals(aPropriedades.get("evento").toString())) {
				if (Uteis.distancia(x + SimpleMapLoader.TileSize / 2,
						SimpleMapLoader.player.getX() + SimpleMapLoader.TileSize / 2, y + SimpleMapLoader.TileSize / 2,
						SimpleMapLoader.player.getY() + SimpleMapLoader.TileSize / 2) <= SimpleMapLoader.TileSize
								* 1.5) {

					if (!SimpleMapLoader.sebastiao.isSleep()) {

						SimpleMapLoader.sebastiao.saltar();
						ArrayList<Tile> lCoTile = Astar.findPath(
								World.pegar_chao(SimpleMapLoader.sebastiao.getX(), SimpleMapLoader.sebastiao.getY(),
										SimpleMapLoader.sebastiao.getZ()),
								World.pegar_chao(SimpleMapLoader.player.getX(), SimpleMapLoader.player.getY(),
										SimpleMapLoader.player.getZ()));
						lCoTile.remove(lCoTile.size() - 1);
						SimpleMapLoader.sebastiao.setaCaminho(lCoTile);

						SimpleMapLoader.sebastiao.setaFala(Talks.nameMexeuAnemona);
						SimpleMapLoader.podeNovaMovimentacao = false;
					} else {
						posicao_Conjunto = 1;
						if (posicao_Conjunto >= CoConjuntoSprites.size())
							posicao_Conjunto = 0;
						if (!SimpleMapLoader.monika.isSleep()) {
							SimpleMapLoader.monika.saltar();
							ArrayList<Tile> lCoTile = Astar.findPath(World.pegar_chao(SimpleMapLoader.monika.getX(),
									SimpleMapLoader.monika.getY(), SimpleMapLoader.monika.getZ()), this);
							lCoTile.remove(lCoTile.size() - 1);
							SimpleMapLoader.monika.setaCaminho(lCoTile);
							SimpleMapLoader.monika.setaFala(Talks.nameMatouAnemona);
							SimpleMapLoader.podeNovaMovimentacao = false;
						}
					}
				}
			} else if ("QuebrarPorco".contentEquals(aPropriedades.get("evento").toString())) {
				if (Uteis.distancia(x + SimpleMapLoader.TileSize / 2,
						SimpleMapLoader.player.getX() + SimpleMapLoader.TileSize / 2, y + SimpleMapLoader.TileSize / 2,
						SimpleMapLoader.player.getY() + SimpleMapLoader.TileSize / 2) <= SimpleMapLoader.TileSize
								* 1.5) {
					posicao_Conjunto = 1;
					if (posicao_Conjunto >= CoConjuntoSprites.size())
						posicao_Conjunto = 0;
					if (!SimpleMapLoader.sebastiao.isSleep()) {
						SimpleMapLoader.sebastiao.saltar();
						ArrayList<Tile> lCoTile = Astar
								.findPath(
										World.pegar_chao(SimpleMapLoader.sebastiao.getX(),
												SimpleMapLoader.sebastiao.getY(), SimpleMapLoader.sebastiao.getZ()),
										World.pegar_chao(x, y, z));
						lCoTile.remove(lCoTile.size() - 1);
						SimpleMapLoader.sebastiao.setaCaminho(lCoTile);

						SimpleMapLoader.sebastiao.setaFala(Talks.namePorcoQuebrou);

						SimpleMapLoader.monika.saltar();
						lCoTile = Astar.findPath(World.pegar_chao(SimpleMapLoader.monika.getX(),
								SimpleMapLoader.monika.getY(), SimpleMapLoader.monika.getZ()),
								World.pegar_chao(x, y, z));
						lCoTile.remove(lCoTile.size() - 1);
						SimpleMapLoader.monika.setaCaminho(lCoTile);

						SimpleMapLoader.podeNovaMovimentacao = false;
					}
				}
			} else if ("DestrancarPorta".contentEquals(aPropriedades.get("evento").toString())) {
				if (Uteis.distancia(x + SimpleMapLoader.TileSize / 2,
						SimpleMapLoader.player.getX() + SimpleMapLoader.TileSize / 2, y + SimpleMapLoader.TileSize / 2,
						SimpleMapLoader.player.getY() + SimpleMapLoader.TileSize / 2) <= SimpleMapLoader.TileSize
								* 1.5) {
					posicao_Conjunto = 1;
					if (posicao_Conjunto >= CoConjuntoSprites.size())
						posicao_Conjunto = 0;
					SimpleMapLoader.player.setContemChave(true);

				}
			} else if ("Porta".contentEquals(aPropriedades.get("evento").toString())) {
				if (SimpleMapLoader.player.isContemChave()) {
					posicao_Conjunto++;
					if (posicao_Conjunto >= CoConjuntoSprites.size())
						posicao_Conjunto = 0;
				}
			} else if ("Fujir".contentEquals(aPropriedades.get("evento").toString())) {
				if (SimpleMapLoader.player.isContemChave()) {
					Ui.aBallonTalk.setFalas(Talks.FinishGame);
					Ui.aBallonTalk.next();
					SimpleMapLoader.podeNovaMovimentacao = false;
					Ui.aBallonTalk.adicionarAcaoAposFalar(() -> SimpleMapLoader.podeNovaMovimentacao = true);
				}
			}
		}
	}

}
