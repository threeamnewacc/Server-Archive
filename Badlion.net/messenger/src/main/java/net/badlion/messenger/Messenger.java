package net.badlion.messenger;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class Messenger extends JavaPlugin {
	
	private Random generator;
	private ArrayList<String> messages;
	
	public Messenger() {
		/*messages = new String [] {
			"Show your love for the server by donating at http://badlion.net and receive donator perks as a token of our gratitude.",
			"Have a suggestion, story, or video you want to share with the community? Post it on the Badlion Forums http://badlion.net/forum.",
			"Please do not accuse players of hacking in public chat. Use /report <name> <msg> to message the mods.",
			"Register on the Badlion Network /register [email]",
			//"Tournament coming up on December 21st, HCF Ladder.  Go to http://badlion.net to sign up!",
			"Type '/help' for information on our server's commands",
			"Type '/unranked' to get matched up with other players in 1v1's",
			"Vote for the Badlion Network on PMC!  Earn extra ranked matches for voting at http://badlion.net/vote"
		};*/
		this.messages = new ArrayList<String>();
		this.generator = new Random();
	}
	
	@Override
	public void onEnable() {
		@SuppressWarnings("unchecked")
		List<String> msgs = (List<String>) this.getConfig().getList("messenger.active_messages");
		
		for (String msg : msgs) {
			String color = this.getConfig().getString("messenger.messages." + msg + ".color");
			String message = this.getConfig().getString("messenger.messages." + msg + ".message");
			
			this.messages.add(ChatColor.RED + "[" + ChatColor.BLUE + "TIP" + ChatColor.RED + "] " + ChatColor.valueOf(color) + message);
		}
		
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new MessengerTask(this), 0, 20 * this.getConfig().getInt("messenger.time"));
	}
	
	@Override
	public void onDisable() {
	}

	public Random getGenerator() {
		return generator;
	}

	public void setGenerator(Random generator) {
		this.generator = generator;
	}

	public ArrayList<String> getMessages() {
		return messages;
	}

	public void setMessages(ArrayList<String> messages) {
		this.messages = messages;
	}
}
