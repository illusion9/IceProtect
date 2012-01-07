package eu.icecraft.iceprotect;

import org.bukkit.entity.Player;

import com.iConomy.iConomy;
import com.iConomy.system.Holdings;

public class Economy {
	public static double costPerBlock = 0.2; 

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
}
