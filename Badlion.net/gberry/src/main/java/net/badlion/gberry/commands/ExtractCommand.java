package net.badlion.gberry.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.joda.time.DateTime;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class ExtractCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

		String directory = args[0];

		getTimeZone(directory);
		sender.sendMessage("Got all timezones from the files in the directory.");
		return true;
	}

	public Set<DateTime> getTimeZone(final String directory) {
		File dFile = new File(directory);

		if (!dFile.exists()) {
			System.err.println("The original directory doesn't exist!");
			return null;
		}


		File[] oldFiles = dFile.listFiles();

		Set<DateTime> times = new HashSet<>();

		for (int i = 0; i < oldFiles.length; i++) {
			File file = oldFiles[i];
			times.add(new DateTime(file.lastModified()));
		}
		return times;
	}
}