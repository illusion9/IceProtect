package eu.icecraft.iceprotect;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

public class IceProtect extends JavaPlugin {

	WorldEditPlugin we;
	WorldGuardPlugin wg;
	Economy econ;
	Commands cmd;

	@Override
	public void onDisable() {
		System.out.println("[IceProtect] Disabled.");
	}

	@Override
	public void onEnable() {

		Plugin weRaw = this.getServer().getPluginManager().getPlugin("WorldEdit");	
		if(weRaw != null && weRaw.isEnabled()) {
			we = (WorldEditPlugin) weRaw;
			System.out.println("[IceProtect] Hooked into WorldEdit.");
		}

		Plugin wgRaw = this.getServer().getPluginManager().getPlugin("WorldGuard");	
		if(wgRaw != null && wgRaw.isEnabled()) {
			wg = (WorldGuardPlugin) wgRaw;
			System.out.println("[IceProtect] Hooked into WorldGuard.");
		}

		Plugin iConomy = this.getServer().getPluginManager().getPlugin("iConomy");
		if(iConomy != null) {
			System.out.println("[IceProtect] Hooked into iConomy.");
		}

		econ = new Economy();
		cmd = new Commands(wg, we, econ);

		System.out.println("[IceProtect] Enabled.");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage("You need to be a player!");
			return true;
		}
		Player player = (Player) sender;

		if(label.equalsIgnoreCase("protect") || label.equalsIgnoreCase("pr")) {
			if(args.length == 0 || args[0].equalsIgnoreCase("help")) {
				cmd.help(player, args);
				return true;
			}

			if(args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("define") || args[0].equalsIgnoreCase("def")) {
				cmd.setRegion(player, args);
				return true;
			}

			if(args[0].equalsIgnoreCase("allow")) {
				cmd.allowUser(player, args);
				return true;
			}

			if(args[0].equalsIgnoreCase("deny")) {
				cmd.denyUser(player, args);
				return true;
			}

			if(args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("del")) {
				cmd.deleteRegion(player, args);
				return true;
			}

			if(args[0].equalsIgnoreCase("list") || args[0].equalsIgnoreCase("ls")) {
				cmd.listRegions(player, args);
				return true;
			}
			// fun
			if(args[0].equalsIgnoreCase("yes") || args[0].equalsIgnoreCase("yeah") || args[0].equalsIgnoreCase("yup") || args[0].equalsIgnoreCase("ya") || args[0].equalsIgnoreCase("yea")) {
				cmd.yes(player, args);
				return true;
			}
			if(args[0].equalsIgnoreCase("no") || args[0].equalsIgnoreCase("nah") || args[0].equalsIgnoreCase("nope") || args[0].equalsIgnoreCase("nu")) {
				cmd.no(player, args);
				return true;
			}

			if(args[0].equalsIgnoreCase("price") || args[0].equalsIgnoreCase("cost")) {
				cmd.regionPrice(player, args);
				return true;
			}

			if(args[0].equalsIgnoreCase("f") || args[0].equalsIgnoreCase("flag") || args[0].equalsIgnoreCase("flags")) { 
				cmd.setFlags(player, args);
				return true;
			}
		}

		cmd.help(player, args);
		return true;
	}
}