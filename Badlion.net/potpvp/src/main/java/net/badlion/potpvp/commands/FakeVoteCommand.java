package net.badlion.potpvp.commands;

import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;
import net.badlion.potpvp.PotPvP;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class FakeVoteCommand extends GCommandExecutor {

    public FakeVoteCommand() {
        super(0); // 0 args minimum
    }

	@Override
	public void onGroupCommand(Command command, String label, final String[] args) {
		Vote vote = new Vote();
		vote.setUsername(this.player.getName());
		PotPvP.getInstance().getServer().getPluginManager().callEvent(new VotifierEvent(vote));
	}

    @Override
    public void usage(CommandSender sender) {

    }

}
