package com.satsukirin.RinDeath;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import com.satsukirin.RinDeath.RDConfig.TipType;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class RDSecondThread extends BukkitRunnable{
	
	private static RDSecondThread INSTANCE;
	private RDConfig config;
	
	private Map<UUID, Integer> deadPlayers;
	
	public RDSecondThread() {
		if (INSTANCE!=null) return;
		deadPlayers=new HashMap<UUID, Integer>();
		config=RinDeath.getInstance().getRDConfig();
		INSTANCE=this;
	}
	public static RDSecondThread getInstance() {
		return INSTANCE;
	}
	public void addPlayer(UUID uuid) {
		deadPlayers.put(uuid,config.getRespawnTime());
	}
	public void addPlayer(Player player) {
		addPlayer(player.getUniqueId());
	}
	public void removePlayer(UUID uuid,boolean respawn) {
		
		removePlayer(RinDeath.getInstance().getServer().getPlayer(uuid),respawn);
	}
	public void removePlayer(Player player,boolean respawn) {
		if (player==null)return;
		if (player.isOnline())
		if (!deadPlayers.containsKey(player.getUniqueId())) {
			player.sendMessage("你还未死亡, 无法重生/复活");
			return;
		}
		player.getPersistentDataContainer().remove(new NamespacedKey(RinDeath.getInstance(), "respawntime"));
		UUID asuuid=UUID.fromString(player.getPersistentDataContainer().get(new NamespacedKey(RinDeath.getInstance(), "graveuuid"), PersistentDataType.STRING));
		ArmorStand as = (ArmorStand) RinDeath.getInstance().getServer().getEntity(asuuid);
		player.setGameMode(GameMode.valueOf(player.getPersistentDataContainer().get(new NamespacedKey(RinDeath.getInstance(), "gamemode"), PersistentDataType.STRING)));
		if (respawn) {
			player.teleport(player.getBedSpawnLocation()==null?player.getWorld().getSpawnLocation():player.getBedSpawnLocation());
		}else {
			player.teleport(as.getLocation().add(0,2,0));
		}
		as.remove();
		player.getPersistentDataContainer().remove(new NamespacedKey(RinDeath.getInstance(), "graveuuid"));
		player.getPersistentDataContainer().remove(new NamespacedKey(RinDeath.getInstance(), "gamemode"));
		if (config.getTiptype().equals(TipType.bossbar)) {
			RinDeath.getInstance().getServer().getBossBar(new NamespacedKey(RinDeath.getInstance(), "bb_"+player.getName())).removeAll();
			RinDeath.getInstance().getServer().removeBossBar(new NamespacedKey(RinDeath.getInstance(), "bb_"+player.getName()));
		}
		deadPlayers.remove(player.getUniqueId());
	}
	public void removeAll(boolean respawn) {
		for(Entry<UUID, Integer> players : deadPlayers.entrySet()) {
			removePlayer(players.getKey(), respawn);
		}
	}
	
	
	
	@Override
	public void run() {
			for (Entry<UUID, Integer> entry : deadPlayers.entrySet()) {
				if (entry.getValue()==0) {
					removePlayer(entry.getKey(),true);
					continue;
				}
				deadPlayers.put(entry.getKey(),entry.getValue()-1);
				Player player = RinDeath.getInstance().getServer().getPlayer(entry.getKey());
				if (config.getTiptype().equals(TipType.actionbar)) {
					player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.YELLOW+"剩余复活时间: "+entry.getValue()+" 秒"));
				}
				if (config.getTiptype().equals(TipType.bossbar)) {
					BossBar bb = RinDeath.getInstance().getServer().getBossBar(new NamespacedKey(RinDeath.getInstance(), "bb_"+player.getName()));
					if (bb==null) {
						bb=RinDeath.getInstance().getServer().createBossBar(new NamespacedKey(RinDeath.getInstance(), "bb_"+player.getName()), "剩余复活时间: "+entry.getValue()+" 秒", BarColor.WHITE, BarStyle.SOLID);
						bb.addPlayer(player);
					}
					bb.setProgress(1.0*entry.getValue()/config.getRespawnTime());
					bb.setTitle("剩余复活时间: "+entry.getValue()+" 秒");
				}
				if (config.getTiptype().equals(TipType.title)) {
					player.sendTitle("力竭倒下", "剩余复活时间: "+entry.getValue()+" 秒", 3, 14, 3);
				}
			}
		
	}

}
