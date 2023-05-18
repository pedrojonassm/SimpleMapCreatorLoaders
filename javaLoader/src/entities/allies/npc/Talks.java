package entities.allies.npc;

import java.util.HashMap;

public class Talks {
	public static final String sebastiao = "Sebastiao", monika = "Monika", player = "Player",
			namePorcoQuebrou = "porcoQuebrou", nameMatouAnemona = "matouAnemona", nameMexeuAlavanca = "mexeuAlavanca",
			nameMexeuAnemona = "mexeuAnemona", nameTentarSubirEscada = "tentarSubirEscada";
	public static final String[] porcoQuebrou = { sebastiao, "Meu porquinho...", player, "Foi sem querer", monika,
			"Tudo bem...", "Tenho certeza que ele nãoia durar muito mesmo", sebastiao, "...", "Vou dormir", player,
			"..." };
	public static final String[] matouAnemona = { player, "Ela é linda...", "Ela se move", "para...", "E volta", monika,
			"Não...", "Ela não para", "Muito menos volta" };
	public static final String[] mexeuAnemona = { player, "O que é isso?", "Parece uma anênoma...", "Mas fora do mar",
			sebastiao, "Não...", "Não é uma planta viva", "...", "É complicado de explicar" };

	public static final String[] mexeuAlavanca = { player, "O que isso faz?",
			"Parece ser algum tipo de mecanismo antigo...", "bem antigo", monika, "Não mecha nisso...",
			"Sob hipótise alguma", player, "...", "Ok!", "Perdão" };
	public static final String[] FinishGame = { player, "É", "Devia ter ouvido a mamãe",
			"E não ter entrado em lugares estranhos" };
	public static final String[] tentarSubirEscada = { monika, "Sobe lá não", "Fica aqui com a gente", player, "...",
			monika, "por favor?" };

	public static HashMap<String, String[]> generate() {

		HashMap<String, String[]> lHashmap = new HashMap<>();
		lHashmap.put(namePorcoQuebrou, porcoQuebrou);
		lHashmap.put(nameMatouAnemona, matouAnemona);
		lHashmap.put(nameMexeuAlavanca, mexeuAlavanca);
		lHashmap.put(nameMexeuAnemona, mexeuAnemona);
		lHashmap.put(nameTentarSubirEscada, tentarSubirEscada);
		return lHashmap;
	}

}
