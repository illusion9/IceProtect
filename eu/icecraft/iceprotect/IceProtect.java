package eu.icecraft.iceprotect;

import java.io.File;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import eu.icecraft.iceprotect.configCompat.Configuration;

public class IceProtect extends JavaPlugin {

	private WorldEditPlugin we;
	private WorldGuardPlugin wg;
	private Economy econ;
	private Commands cmd;
	private Configuration regionsForSale;

	@Override
	public void onDisable() {
		regionsForSale.save();
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

		if(!this.getDataFolder().exists()) this.getDataFolder().mkdir();

		File sellFile = new File(this.getDataFolder(), "sell.yml");
		regionsForSale = new Configuration(sellFile);
		if(!sellFile.exists()) regionsForSale.save();
		regionsForSale.load();

		econ = new Economy(regionsForSale);
		cmd = new Commands(wg, we, econ, regionsForSale);

		System.out.println("[IceProtect] Enabled.");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage("You need to be a player!");
			return true;
		}

		Player player = (Player) sender;

		if(label.equalsIgnoreCase("psell")) {
			cmd.sellRegion(player, args);
			return true;
		}

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

			if(args[0].equalsIgnoreCase("delete")) {
				cmd.deleteRegion(player, args);
				return true;
			}

			if(args[0].equalsIgnoreCase("list")) {
				cmd.listRegions(player, args);
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

			if(args[0].equalsIgnoreCase("buy")) {
				cmd.buyRegion(player, args);
				return true;
			}

			if(args[0].equalsIgnoreCase("forsale")) {
				cmd.checkRegion(player, args);
				return true;
			}
		}

		cmd.help(player, args);
		return true;
	}
}