package graficos.telas;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashMap;

import entities.Entity;
import entities.allies.Monika;
import entities.allies.Player;
import entities.allies.Sebastiao;
import graficos.Talks;
import main.SimpleMapLoader;
import main.interfaces.tickRender;
import world.Camera;

public class BallonTalk implements tickRender {

	Entity falando; // quem está falando
	String[] falas; // Conversa
	int fala; // falas[fala] = fala atual
	public ArrayList<Runnable> executarAposFalar;
	public static HashMap<String, String[]> allTalks;

	int ticksPorximaFala;

	public BallonTalk() {
		allTalks = Talks.generate();

		ticksPorximaFala = 0;
		fala = -1; // -1 simboliza que n tem falas
		executarAposFalar = new ArrayList<>();
	}

	@Override
	public void tick() {
		if (falas != null && falas.length > fala) {
			if (++ticksPorximaFala > 300) { // 300/60 = 5 segundos
				next();

			}
		}

	}

	@Override
	public void render(Graphics prGraphics) {
		if (falas != null && falas.length > 0) {
			prGraphics.setFont(prGraphics.getFont().deriveFont(22f));
			if (falando instanceof Monika)
				prGraphics.setColor(Color.PINK);
			if (falando instanceof Sebastiao)
				prGraphics.setColor(Color.GREEN);
			if (falando instanceof Player)
				prGraphics.setColor(Color.WHITE);
			int stringWidth = prGraphics.getFontMetrics().stringWidth(falas[fala]);
			prGraphics.drawString(falas[fala],
					falando.getX() + SimpleMapLoader.TileSize / 2 - Camera.x - stringWidth / 2,
					falando.getY() - Camera.y);
		}
	}

	public String[] getFalas() {
		return falas;
	}

	public void setFalas(String[] falas) {
		this.falas = falas;
	}

	public void adicionarAcaoAposFalar(Runnable prRunnable) {
		executarAposFalar.add(prRunnable);
	}

	public void next() {
		ticksPorximaFala = 0;
		fala++;
		if (fala >= falas.length) {
			fala = -1;
			ticksPorximaFala = 0;
			falas = null;
			while (executarAposFalar.size() > 0) {
				executarAposFalar.get(0).run();
				executarAposFalar.remove(0);
			}
		} else {
			if (Talks.sebastiao.contentEquals(falas[fala])) {
				falando = SimpleMapLoader.sebastiao;
				next();
			} else if (Talks.monika.contentEquals(falas[fala])) {
				falando = SimpleMapLoader.monika;
				next();
			} else if (Talks.player.contentEquals(falas[fala])) {
				falando = SimpleMapLoader.player;
				next();
			}
		}

	}

	public boolean contemFalas() {
		if (falas != null && fala < falas.length)
			return true;

		return false;
	}

}