package net.badlion.uhc.commands.handlers;

import net.badlion.gberry.Gberry;
import net.badlion.uhc.BadlionUHC;
import net.badlion.uhc.UHCTeam;
import net.badlion.uhc.events.RulesEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RulesCommandHandler {

	public static List<String> rules = new ArrayList<>();
	public static List<String> spanishRules = new ArrayList<>();
	private static Iterator<String> it;

	public static void initialize() {
		if (BadlionUHC.getInstance().getGameType() == UHCTeam.GameType.SOLO) {
			RulesCommandHandler.rules.add("The use of any illegal client/mod, cave/chest finder or x-ray texture pack will result in a permanent ban.");
			RulesCommandHandler.rules.add("Using F3+a, F5 under lava, mining to entities or doing anything that gives you an unfair advantage will result in a permanent ban.");
			RulesCommandHandler.rules.add("Placing lava and/or water to ruin other players gameplay (lag machines) will result in a 14-30 day ban depending on the severity.");
			RulesCommandHandler.rules.add("All temporary bans for the following offenses are scaling: Excessive stalking, camping, iPvP, sacrificing, forming teams larger than specified team size, spoiling a disguise name, spoiling a player’s location, portal trapping and all forms of illegal mining listed below. First offense is a 7 day ban, second offense is a 30 day ban, and the third offense is a permanent ban.");
			RulesCommandHandler.rules.add("No stripmining or pokeholes below y32. You may mine to sounds and player tags at any level.");
			RulesCommandHandler.rules.add("You may mine down without going up first if you’re at the end of a cave or ravine, even if it’s below y32.");
			RulesCommandHandler.rules.add("Rollercoastering (staircasing up and down) is only allowed if you go from y32 to bedrock each time.");
			RulesCommandHandler.rules.add("Camping is NOT allowed once the border is 100x100. You may wall up to heal or loot, but you must move on quickly.");
			RulesCommandHandler.rules.add("Portal trapping is a 3 day ban. Portal camping is allowed.");
			RulesCommandHandler.rules.add("Spoiling a player’s disguised name while they’re alive is a temp ban.");
			RulesCommandHandler.rules.add("Spoiling someone else's location in global chat is not allowed.");
			RulesCommandHandler.rules.add("Joining a game with the intent to mine and give away your items (including your head) to a specific player (sacrificing) is not allowed. Receiver will be punished only if they keep the items.");
			RulesCommandHandler.rules.add("iPvP during grace period is not allowed. (Attempting to, or causing damage to a player before PvP).");
			RulesCommandHandler.rules.add("Stalking (following someone) and stealing is allowed, as long as it's not excessive.");
			RulesCommandHandler.rules.add("Excessive stalking means you cannot prevent a player from mining, crafting or placing blocks.  Also included is continuously stealing/breaking furnaces/crafting tables.");
			RulesCommandHandler.rules.add("If you are being iPvPd or stalked excessively, use /report or /helpop to alert a staff member.");
			RulesCommandHandler.rules.add("Do not stalk/follow Famous/Twitch during rank during grace period. We reserve the right to TP you away if you do.");
			RulesCommandHandler.rules.add("This is a FFA (free-for-all). Teaming/ trucing will result in a 3 day ban.");
			RulesCommandHandler.rules.add("At start of game everyone will be healed, fed, and given 10 starter food.");
			RulesCommandHandler.rules.add("At 10 minutes there will be a final heal. There is no feed at this time.");
			RulesCommandHandler.rules.add("The grace period is 20 minutes long. During this time, iPvP is not allowed.");
			RulesCommandHandler.rules.add("You may relog during the game. You have 10 minutes to get back in. A zombie (which can be killed if PvP is enabled) will spawn in your place until you return. If you don’t return in time, the zombie dies.");
			RulesCommandHandler.rules.add("If you relog during a heal, you will not receive it.");
			RulesCommandHandler.rules.add("Dinnerbone and Grumm horses are allowed.");
			RulesCommandHandler.rules.add("No respawns or heals due to block glitches or lag.");
			RulesCommandHandler.rules.add("If there is video evidence of you dying to a hacker that was banned, you will get a respawn if you request it.");
			RulesCommandHandler.rules.add("Hackusating in game chat or in spec chat will lead to a mute.");
			RulesCommandHandler.rules.add("Do not go underground after 100x100 shrink. If you follow another player underground and get a kill, you will be given a minute to loot.");
			RulesCommandHandler.rules.add(" No sky bases within 100x100 at any time during the game. You will be TPd if you don’t get down.");
			RulesCommandHandler.rules.add("TPs out of the nether are automatic at the 500x500 scatter.");
			RulesCommandHandler.rules.add("The world border will teleport you inside the new border if you are outside it.");
			RulesCommandHandler.rules.add("The 500 and 100 shrinks are random scatter.");
			RulesCommandHandler.rules.add("Use /explain and /config to see game modes and config options.");
			RulesCommandHandler.rules.add("If you have questions during global mute, use /helpop.");
			RulesCommandHandler.rules.add("If you believe someone is cheating, use /report so that all UHC staff members can see it.");
			RulesCommandHandler.spanishRules.add("Usar hacks, mods que no están permitidos o textures packs de x-ray resultará en un ban permanente.");
			RulesCommandHandler.spanishRules.add("Hacer f3 + a, usar F5 para ver debajo de la lava, minar a las entidades o hacer algo que te de algún tipo de ventaja injusta, seras baneado permanentemente.");
			RulesCommandHandler.spanishRules.add("Poner cubetas de lava o de agua para arruinar el juego para otros jugadores (maquinas de lag) resultará en un ban de 14-30 días.\n");
			RulesCommandHandler.spanishRules.add("Todos los bans temporales por las siguientes cosas son bans que duran más dependiendo de cuantos tenias antes: Excessive Stalking (seguir excesivamente a un jugador), iPvP, Campear, Darle tus cosas a un jugador (Sacrificing), Hacer teams en FFA’s, Hacer teams que sean más de lo que el host específico, Decir un nombre de algún jugador en disguise, Decir las coordenadas de algún jugador, Hacer trampas en portales, cual tipo de mineo illegal que está abajo. La primer ofensa es de 3 días, la segunda de 7 días y la tercera es de 30 días.");
			RulesCommandHandler.spanishRules.add("No puedes hacer stripmining o pokeholing debajo de y:32. Si puedes minar a cualquier sonido que escuches y a cualquier jugador que veas a cualquier nivel de altura.");
			RulesCommandHandler.spanishRules.add("Puedes minar hacia abajo sin tener que subir tu nivel de altura, pero solamente si estas al final de una cueva o una ravine.");
			RulesCommandHandler.spanishRules.add("Rollercoasting (minar en forma de escalera hacia arriba y abajo repetidamente) está permitido si empiezas en Y:32 y bajas hacia la bedrock (piedra madre).");
			RulesCommandHandler.spanishRules.add("Campear no está permitido cuando el mundo está a 100x100. Puedes hacer muros para regenerar o agarrar items.");
			RulesCommandHandler.spanishRules.add("Atrapar portales con trampas para que jugadores mueran de inmediato no está permitido. Campear los portales si está permitido.");
			RulesCommandHandler.spanishRules.add("Decir el nombre de algún jugador que está en disguise mientras esté vivo no está permitido y resultará en un ban temporal.");
			RulesCommandHandler.spanishRules.add("Decir las coordenadas de cualquier jugador en el chat no está permitido.");
			RulesCommandHandler.spanishRules.add("Unirse a un juego con la pura intención de conseguir cosas y dárselas a un jugador (esto incluye tu cabeza) no está permitido. El jugador que reciba las cosas será baneado si se las queda.");
			RulesCommandHandler.spanishRules.add("Durante el periodo de gracia no puedes hacer iPvP (hacer que el jugador tome daño de caída o de lava o cualquier cosas que le haga perder vida)");
			RulesCommandHandler.spanishRules.add("Seguir a alguien y robarles las cosas está permitido, mientras no lo hagas de una forma excesiva.");
			RulesCommandHandler.spanishRules.add("Hacerlo excesivamente significa que no dejes que el jugador disfrute su juego esto incluye: “Prevenir que el jugador use su mesa de crafteo, romperle hornos, mesas de crafteo y impedir que el jugar ropa bloques”");
			RulesCommandHandler.spanishRules.add("Si te están haciendo iPvP o te están siguiendo de una forma excesiva haz /report o /helpop para que un staff te pueda ayudar.");
			RulesCommandHandler.spanishRules.add("No sigas a personas con el rango de Famous/Twitch durante el periodo de gracia. Hacerlo resultara en TP inmediato.");
			RulesCommandHandler.spanishRules.add("Esto es un juego que se juega sin equipos, esto significa que no puedes hacer las paces con un jugador o hacer equipos. Hacerlo resultara en un ban temporal.");
			RulesCommandHandler.spanishRules.add("Al inicio del juego recibirás 10 de comida, te regeneraremos la vida y también el hambre");
			RulesCommandHandler.spanishRules.add("10 minutos en el juego recibirás la última regeneración de vida instantánea.");
			RulesCommandHandler.spanishRules.add("El periodo de gracia dura por 20 minutos. Durante este tiempo no puedes hacer iPvP");
			RulesCommandHandler.spanishRules.add("Te puedes desconectar y volver a conectar durante el juego. Solamente tienes 10 minutos para re-conectar antes de ser descalificado. Un zombie (que los jugadores podrán matar cuando inicie el Pvp) aparecerá en tu lugar hasta que regreses. si no regresas a tiempo el zombie morirá\n");
			RulesCommandHandler.spanishRules.add("Si te desconectas y te vuelvas a re-conectar durante la regeneración instantánea no la recibirás.");
			RulesCommandHandler.spanishRules.add("Caballos con el nombre de Dinnerbone y Grumm están permitidos.");
			RulesCommandHandler.spanishRules.add("Si mueres por algún glitch/lag no te regresaremos a la vida. Si pierdes vida por algo asi no te daremos la vida que perdiste.");
			RulesCommandHandler.spanishRules.add("Si hay evidencia en forma de video que moriste contra algún hacker que fue baneado, te regresaremos a la vida si lo pides.");
			RulesCommandHandler.spanishRules.add("Decir que alguien está hackeando en chat en no está permitido y resultará en un mute si lo haces.");
			RulesCommandHandler.spanishRules.add("No te vayas bajo tierra cuando el mapa esté a 100x100. Si sigues a alguien que va bajo tierra lo puedes seguir, una vez lo mates tendrás un minuto para conseguir las cosas.");
			RulesCommandHandler.spanishRules.add("No puedes hacer Sky Bases (bases en el cielo) dentro de las coordenadas de 100x100. Te haremos un TP si no te bajas.");
			RulesCommandHandler.spanishRules.add("TP’s afuera del nether son automáticos una vez el mapa llegue a 500x500");
			RulesCommandHandler.spanishRules.add("La barrera del mundo te tele-transporta dentro de la barrera si te sales/intentas salir.");
			RulesCommandHandler.spanishRules.add("Una vez el mapa llegue a 500x500 y a 100x100 las tele-transportaciones serán al azar a cualquier lugar que estén dentro de esas coordenadas.");
			RulesCommandHandler.spanishRules.add("Haz /explain y /config  para saber las modalidades de este juego y su configuración");
			RulesCommandHandler.spanishRules.add("Para enviar coordenadas a tu equipo haz /sc");
			RulesCommandHandler.spanishRules.add("Si crees que alguien está usando hacks haz /report para que todo los staff de UHC sepan y alguien lo vaya a ver.");
		} else {
			RulesCommandHandler.rules.add("The use of any illegal client/mod, cave/chest finder or x-ray texture pack will result in a permanent ban.");
			RulesCommandHandler.rules.add("Using F3+a, F5 under lava, mining to entities or doing anything that gives you an unfair advantage will result in a permanent ban.");
			RulesCommandHandler.rules.add("Placing lava and/or water to ruin other players gameplay (lag machines) will result in a 14-30 day ban depending on the severity.");
			RulesCommandHandler.rules.add("All temporary bans for the following offenses are scaling: Excessive stalking, camping, iPvP, sacrificing, forming teams larger than specified team size, spoiling a disguise name, spoiling a player’s location, portal trapping and all forms of illegal mining listed below. First offense is a 7 day ban, second offense is a 30 day ban, and the third offense is a permanent ban.");
			RulesCommandHandler.rules.add("No stripmining or pokeholes below y32. You may mine to sounds and player tags at any level.");
			RulesCommandHandler.rules.add("You may mine down without going up first if you’re at the end of a cave or ravine, even if it’s below y32.");
			RulesCommandHandler.rules.add("Rollercoastering (staircasing up and down) is only allowed if you go from y32 to bedrock each time.");
			RulesCommandHandler.rules.add("Camping is NOT allowed once the border is 100x100. You may wall up to heal or loot, but you must move on quickly.");
			RulesCommandHandler.rules.add("Portal trapping is a 3 day ban. Portal camping is allowed.");
			RulesCommandHandler.rules.add("Spoiling a player’s disguised name while they’re alive is a temp ban.");
			RulesCommandHandler.rules.add("Spoiling someone else's location in global chat is not allowed.");
			RulesCommandHandler.rules.add("Joining a game with the intent to mine and give away your items (including your head) to a specific player (sacrificing) is not allowed. Receiver will be punished only if they keep the items.");
			RulesCommandHandler.rules.add("iPvP during grace period is not allowed. (Attempting to, or causing damage to a player before PvP).");
			RulesCommandHandler.rules.add("Stalking (following someone) and stealing is allowed, as long as it's not excessive.");
			RulesCommandHandler.rules.add("Excessive stalking means you cannot prevent a player from mining, crafting or placing blocks.  Also included is continuously stealing/breaking furnaces/crafting tables.");
			RulesCommandHandler.rules.add("If you are being iPvPd or stalked excessively, use /report or /helpop to alert a staff member.");
			RulesCommandHandler.rules.add("Do not stalk/follow Famous/Twitch during rank during grace period. We reserve the right to TP you away if you do.");
			RulesCommandHandler.rules.add("Do not form teams larger than the specified team size. This is also a 3 day ban.");
			RulesCommandHandler.rules.add("At start of game everyone will be healed, fed, and given 10 starter food.");
			RulesCommandHandler.rules.add("At 10 minutes there will be a final heal. There is no feed at this time.");
			RulesCommandHandler.rules.add("The grace period is 20 minutes long. During this time, iPvP is not allowed.");
			RulesCommandHandler.rules.add("You may relog during the game. You have 10 minutes to get back in. A zombie (which can be killed if PvP is enabled) will spawn in your place until you return. If you don’t return in time, the zombie dies.");
			RulesCommandHandler.rules.add("If you relog during a heal, you will not receive it.");
			RulesCommandHandler.rules.add("Dinnerbone and Grumm horses are allowed.");
			RulesCommandHandler.rules.add("No respawns or heals due to block glitches or lag.");
			RulesCommandHandler.rules.add("If there is video evidence of you dying to a hacker that was banned, you will get a respawn if you request it.");
			RulesCommandHandler.rules.add("Hackusating in game chat or in spec chat will lead to a mute.");
			RulesCommandHandler.rules.add("Do not go underground after 100x100 shrink. If you follow another player underground and get a kill, you will be given a minute to loot.");
			RulesCommandHandler.rules.add("No sky bases within 100x100 at any time during the game. You will be TPd if you don’t get down.");
			RulesCommandHandler.rules.add("TPs out of the nether are automatic at the 500x500 scatter.");
			RulesCommandHandler.rules.add("The world border will teleport you inside the new border if you are outside it.");
			RulesCommandHandler.rules.add("The 500 and 100 shrinks are random scatter.");
			RulesCommandHandler.rules.add("Use /explain and /config to see game modes and config options.");
			RulesCommandHandler.rules.add("If you have questions during global mute, use /helpop.");
			RulesCommandHandler.rules.add("This is a team game. Cross-teaming is allowed.");
			RulesCommandHandler.rules.add("Friendly fire is on and team betrayal is allowed.");
			RulesCommandHandler.rules.add("To send coordinates to a teammate, use /sc.");
			RulesCommandHandler.rules.add("If you believe someone is cheating, use /report so that all UHC staff members can see it.");
			RulesCommandHandler.spanishRules.add("Usar hacks, mods que no están permitidos o textures packs de x-ray resultará en un ban permanente.");
			RulesCommandHandler.spanishRules.add("Hacer f3 + a, usar F5 para ver debajo de la lava, minar a las entidades o hacer algo que te de algún tipo de ventaja injusta, seras baneado permanentemente.");
			RulesCommandHandler.spanishRules.add("Poner cubetas de lava o de agua para arruinar el juego para otros jugadores (maquinas de lag) resultará en un ban de 14-30 días.\n");
			RulesCommandHandler.spanishRules.add("Todos los bans temporales por las siguientes cosas son bans que duran más dependiendo de cuantos tenias antes: Excessive Stalking (seguir excesivamente a un jugador), iPvP, Campear, Darle tus cosas a un jugador (Sacrificing), Hacer teams en FFA’s, Hacer teams que sean más de lo que el host específico, Decir un nombre de algún jugador en disguise, Decir las coordenadas de algún jugador, Hacer trampas en portales, cual tipo de mineo illegal que está abajo. La primer ofensa es de 3 días, la segunda de 7 días y la tercera es de 30 días.");
			RulesCommandHandler.spanishRules.add("No puedes hacer stripmining o pokeholing debajo de y:32. Si puedes minar a cualquier sonido que escuches y a cualquier jugador que veas a cualquier nivel de altura.");
			RulesCommandHandler.spanishRules.add("Puedes minar hacia abajo sin tener que subir tu nivel de altura, pero solamente si estas al final de una cueva o una ravine.");
			RulesCommandHandler.spanishRules.add("Rollercoasting (minar en forma de escalera hacia arriba y abajo repetidamente) está permitido si empiezas en Y:32 y bajas hacia la bedrock (piedra madre).");
			RulesCommandHandler.spanishRules.add("Campear no está permitido cuando el mundo está a 100x100. Puedes hacer muros para regenerar o agarrar items.");
			RulesCommandHandler.spanishRules.add("Atrapar portales con trampas para que jugadores mueran de inmediato no está permitido. Campear los portales si está permitido.");
			RulesCommandHandler.spanishRules.add("Decir el nombre de algún jugador que está en disguise mientras esté vivo no está permitido y resultará en un ban temporal.");
			RulesCommandHandler.spanishRules.add("Decir las coordenadas de cualquier jugador en el chat no está permitido.");
			RulesCommandHandler.spanishRules.add("Unirse a un juego con la pura intención de conseguir cosas y dárselas a un jugador (esto incluye tu cabeza) no está permitido. El jugador que reciba las cosas será baneado si se las queda.");
			RulesCommandHandler.spanishRules.add("Durante el periodo de gracia no puedes hacer iPvP (hacer que el jugador tome daño de caída o de lava o cualquier cosas que le haga perder vida)");
			RulesCommandHandler.spanishRules.add("Seguir a alguien y robarles las cosas está permitido, mientras no lo hagas de una forma excesiva.");
			RulesCommandHandler.spanishRules.add("Hacerlo excesivamente significa que no dejes que el jugador disfrute su juego esto incluye: “Prevenir que el jugador use su mesa de crafteo, romperle hornos, mesas de crafteo y impedir que el jugar ropa bloques”");
			RulesCommandHandler.spanishRules.add("Si te están haciendo iPvP o te están siguiendo de una forma excesiva haz /report o /helpop para que un staff te pueda ayudar.");
			RulesCommandHandler.spanishRules.add("No sigas a personas con el rango de Famous/Twitch durante el periodo de gracia. Hacerlo resultara en TP inmediato.");
			RulesCommandHandler.spanishRules.add("Al inicio del juego recibirás 10 de comida, te regeneraremos la vida y también el hambre");
			RulesCommandHandler.spanishRules.add("10 minutos en el juego recibirás la última regeneración de vida instantánea.");
			RulesCommandHandler.spanishRules.add("El periodo de gracia dura por 20 minutos. Durante este tiempo no puedes hacer iPvP");
			RulesCommandHandler.spanishRules.add("Te puedes desconectar y volver a conectar durante el juego. Solamente tienes 10 minutos para re-conectar antes de ser descalificado. Un zombie (que los jugadores podrán matar cuando inicie el Pvp) aparecerá en tu lugar hasta que regreses. si no regresas a tiempo el zombie morirá\n");
			RulesCommandHandler.spanishRules.add("Si te desconectas y te vuelvas a re-conectar durante la regeneración instantánea no la recibirás.");
			RulesCommandHandler.spanishRules.add("Caballos con el nombre de Dinnerbone y Grumm están permitidos.");
			RulesCommandHandler.spanishRules.add("Si mueres por algún glitch/lag no te regresaremos a la vida. Si pierdes vida por algo asi no te daremos la vida que perdiste.");
			RulesCommandHandler.spanishRules.add("Si hay evidencia en forma de video que moriste contra algún hacker que fue baneado, te regresaremos a la vida si lo pides.");
			RulesCommandHandler.spanishRules.add("Decir que alguien está hackeando en chat en no está permitido y resultará en un mute si lo haces.");
			RulesCommandHandler.spanishRules.add("No te vayas bajo tierra cuando el mapa esté a 100x100. Si sigues a alguien que va bajo tierra lo puedes seguir, una vez lo mates tendrás un minuto para conseguir las cosas.");
			RulesCommandHandler.spanishRules.add("No puedes hacer Sky Bases (bases en el cielo) dentro de las coordenadas de 100x100. Te haremos un TP si no te bajas.");
			RulesCommandHandler.spanishRules.add("TP’s afuera del nether son automáticos una vez el mapa llegue a 500x500");
			RulesCommandHandler.spanishRules.add("La barrera del mundo te tele-transporta dentro de la barrera si te sales/intentas salir.");
			RulesCommandHandler.spanishRules.add("Una vez el mapa llegue a 500x500 y a 100x100 las tele-transportaciones serán al azar a cualquier lugar que estén dentro de esas coordenadas.");
			RulesCommandHandler.spanishRules.add("Haz /explain y /config  para saber las modalidades de este juego y su configuración");
			RulesCommandHandler.spanishRules.add("Para enviar coordenadas a tu equipo haz /sc");
			RulesCommandHandler.spanishRules.add("Si crees que alguien está usando hacks haz /report para que todo los staff de UHC sepan y alguien lo vaya a ver.");
			RulesCommandHandler.spanishRules.add("Puedes hacer Cross teaming (hacer equipo con miembros de otro equipo) mientras que el equipo no sea más grande de lo que el host específico.");
			RulesCommandHandler.spanishRules.add("No hagas equipos que sean más grandes que los que el host haya especificado.");
			RulesCommandHandler.spanishRules.add("Le puedes pegar a tu equipo y traicionarlo está permitido.");
		}

		RulesCommandHandler.rules.add("You may relog during the game but you only have 10 minutes to get back in.");
		RulesCommandHandler.rules.add("At start of game everyone will be healed, fed, and given " + BadlionUHC.getInstance().getConfigurator().getOption(BadlionUHC.CONFIG_OPTIONS.FOOD.name()).getValue() + " starter food.");
		RulesCommandHandler.spanishRules.add("Te puedes desconectar mientras estés jugando, pero solamente tienes 10 minutos para volver conectarte.");
		RulesCommandHandler.spanishRules.add("Al inicio del juego todos conseguirán un heal, también les regeneraremos el hambre y por ultimo les daremos " + BadlionUHC.getInstance().getConfigurator().getOption(BadlionUHC.CONFIG_OPTIONS.FOOD.name()).getValue() + " de comida.");

		int finalHealTime = (int) BadlionUHC.getInstance().getConfigurator().getOption(BadlionUHC.CONFIG_OPTIONS.HEALTIME.name()).getValue();
		if (finalHealTime == 0) {
			RulesCommandHandler.rules.add("THERE IS NO FINAL HEAL THIS GAME!");
			RulesCommandHandler.spanishRules.add("NO TE DAREMOS UN HEAL EN ESTE JUEGO!");
		} else {
			RulesCommandHandler.rules.add("The final heal is at " + finalHealTime + " minutes. There is no final feed.");
			RulesCommandHandler.spanishRules.add("El heal será a los " + finalHealTime + " minutos. No te regeneraremos el hambre.");
		}

		RulesCommandHandler.rules.add("Type /config to see what is enabled and what is not.");
		RulesCommandHandler.rules.add("Type /scenarios to learn about the game scenarios.");
		RulesCommandHandler.rules.add("Lying to the host in-order to get an advantage will result in a 1 day ban.");
		RulesCommandHandler.rules.add("If you have any questions not answered by /config, /scenarios or rules, ask in chat.");
		RulesCommandHandler.rules.add("");
		RulesCommandHandler.rules.add("Have fun and don’t forget to thank your host!");

		RulesCommandHandler.spanishRules.add("Haz /config para ver la configuración del juego.");
		RulesCommandHandler.spanishRules.add("Haz /scenarios para aprender sobre los gamemodes de este juego ");
		RulesCommandHandler.spanishRules.add("Mentirle al host para conseguir una ventaja resulta en un ban de 1 día");
		RulesCommandHandler.spanishRules.add("Si tienes alguna pregunta que no fue respondida en /config, /scenarios, o en las reglas por favor pregunta en el chat");
		RulesCommandHandler.spanishRules.add("");
		RulesCommandHandler.spanishRules.add("Que te diviertas y no olvides agradecerle al host!");

		if (Gberry.serverName.startsWith("sa")) {
			RulesCommandHandler.it = RulesCommandHandler.spanishRules.iterator();
		} else {
			RulesCommandHandler.it = RulesCommandHandler.rules.iterator();
		}
	}

	public static void handleRulesCommand() {
		// Skip rules
		if (!BadlionUHC.getInstance().getConfig().getBoolean("read-rules", true)) {
			return;
		}

		RulesEvent event = new RulesEvent();
		BadlionUHC.getInstance().getServer().getPluginManager().callEvent(event);

		if (event.isCancelled()) {
			return;
		}

		if (BadlionUHC.getInstance().isMiniUHC()) {
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "gm");
		}

		Gberry.broadcastMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "[" + ChatColor.RESET + ChatColor.DARK_AQUA + "BadlionUHC" + ChatColor.GOLD + "" + ChatColor.BOLD + "] " + ChatColor.WHITE + "=============Rules=============");

		new BukkitRunnable() {

			@Override
			public void run() {
				if (RulesCommandHandler.it.hasNext()) {
					Gberry.broadcastMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "[" + ChatColor.RESET + ChatColor.DARK_AQUA + "BadlionUHC" + ChatColor.GOLD + "" + ChatColor.BOLD + "] " + ChatColor.WHITE + it.next());
				} else {
					this.cancel();

					if (BadlionUHC.getInstance().isMiniUHC()) {
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "gm");
					}
				}
			}

		}.runTaskTimer(BadlionUHC.getInstance(), 40L, 100L);
	}

}
