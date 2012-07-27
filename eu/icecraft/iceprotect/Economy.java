package eu.icecraft.iceprotect;

import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.iConomy.iConomy;
import com.iConomy.system.Holdings;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.util.RegionUtil;

import eu.icecraft.iceprotect.configCompat.Configuration;

public class Economy {
	public static double costPerBlock = 0.1;
	public static int maxDonatorAllowedCost = 100000;
	public static int maxDonatorAllowedRegions = 60;

	private Configuration regionsForSale; 

	public Economy(Configuration regionsForSale) {
		this.regionsForSale = regionsForSale;
	}

	public double getCost(int volume) {
		return volume * costPerBlock;
	}

	public boolean chargePlayer(Player player, double amount) {
		Holdings balance = iConomy.getAccount(player.getName()).getHoldings();

		if(balance.hasEnough(amount)) {
			balance.subtract(amount);
			return true;
		} else return false;
	}

	public boolean isRegionForSale(String name) {
		return regionsForSale.getString("regions." + name) != null;
	}

	public void buyRegion(ProtectedRegion region, Player sender, RegionManager mgr) {
		double cost = regionsForSale.getDouble("regions." + region.getId() + ".price", 0D);

		if(cost == 0) {
			sender.sendMessage(ChatColor.RED + "(this shouldn't happen) You can't buy this region. Notify an admin!");
			return;
		}

		if(chargePlayer(sender, cost)) {

			mgr.removeRegion(region.getId());

			String id = "icp_" + sender.getName() + "_" + region.getId();

			int iter = 0;
			while(mgr.hasRegion(id)) {
				id += "_c";
				iter++;
				if(iter > 10) {
					System.err.println("[IceProtect] Failed saving region " + region.getId() + " for " + sender.getName() + " after 10 attempts. Tried name " + id);
					sender.sendMessage(ChatColor.RED + "Failed saving the region! Name collision, please notify an admin.");
					break;
				}
			}

			ProtectedRegion newRegion = null;
			if(region instanceof ProtectedPolygonalRegion) {
				ProtectedPolygonalRegion polyRegion = (ProtectedPolygonalRegion) region;
				int minY = polyRegion.getMinimumPoint().getBlockY();
				int maxY = polyRegion.getMaximumPoint().getBlockY();
				newRegion = new ProtectedPolygonalRegion(id, polyRegion.getPoints(), minY, maxY);
			} else {
				BlockVector min = region.getMinimumPoint();
				BlockVector max = region.getMaximumPoint();
				newRegion = new ProtectedCuboidRegion(id, min, max);
			}

			String[] names = new String[1];
			names[0] = sender.getName();
			newRegion.setOwners(RegionUtil.parseDomainString(names, 0));

			mgr.addRegion(newRegion);

			regionsForSale.removeProperty("regions." + region.getId());
			regionsForSale.save();

			try {
				mgr.save();
				sender.sendMessage(ChatColor.GREEN + "Region " + ChatColor.YELLOW + region.getId() + ChatColor.GREEN + " successfully bought for " + ChatColor.YELLOW + cost + "$");
			} catch (IOException e) {
				sender.sendMessage(ChatColor.RED + "(shouldn't happen) Failed to write regions file: " + e.getMessage());
				e.printStackTrace();
				return;
			}

		} else {
			sender.sendMessage(ChatColor.YELLOW + "You don't have sufficient money to perform this action!");
			sender.sendMessage(ChatColor.RED + "You need $" + Double.toString(cost) + ".");
			return;
		}
	}

}
