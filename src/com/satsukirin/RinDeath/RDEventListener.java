package com.satsukirin.RinDeath;

import java.util.UUID;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.ArmorStand.LockType;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.EulerAngle;

public class RDEventListener implements Listener{
	private RDConfig config;
	public RDEventListener() {
		config=RDConfig.getInstance();
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		if (!config.isEnable())return;
		Player player = e.getEntity();
		if (player.getGameMode().equals(GameMode.SPECTATOR))return;
		ArmorStand as = (ArmorStand) player.getWorld().spawnEntity(player.getLocation().add(0, -1.3,0 ),EntityType.ARMOR_STAND);
		ItemStack graveItem = new ItemStack(Material.PURPUR_BLOCK);
		ItemMeta itemMeta = graveItem.getItemMeta();
		itemMeta.setCustomModelData(config.getGravemodel());
		graveItem.setItemMeta(itemMeta);
		as.getEquipment().setItemInMainHand(graveItem);
		ItemStack headItem = new ItemStack(Material.PLAYER_HEAD);
		SkullMeta headMeta = (SkullMeta) headItem.getItemMeta();
		headMeta.setOwningPlayer(player);
		headItem.setItemMeta(headMeta);
		as.getEquipment().setHelmet(headItem);
		as.addEquipmentLock(EquipmentSlot.CHEST, LockType.ADDING_OR_CHANGING);
		as.addEquipmentLock(EquipmentSlot.LEGS, LockType.ADDING_OR_CHANGING);
		as.addEquipmentLock(EquipmentSlot.FEET, LockType.ADDING_OR_CHANGING);
		as.addEquipmentLock(EquipmentSlot.OFF_HAND, LockType.ADDING_OR_CHANGING);
		as.addEquipmentLock(EquipmentSlot.HAND, LockType.REMOVING_OR_CHANGING);
		as.addEquipmentLock(EquipmentSlot.HEAD, LockType.REMOVING_OR_CHANGING);
		as.setGravity(false);
		as.setInvisible(true);
		as.setInvulnerable(true);
		as.setRightArmPose(EulerAngle.ZERO);
		as.setCustomName(player.getName()+" 的坟墓");
		as.setCustomNameVisible(true);
		as.getPersistentDataContainer().set(new NamespacedKey(RinDeath.getInstance(), "playeruuid"), PersistentDataType.STRING, player.getUniqueId().toString());
		player.getPersistentDataContainer().set(new NamespacedKey(RinDeath.getInstance(), "respawntime"), PersistentDataType.INTEGER, config.getRespawnTime());
		player.getPersistentDataContainer().set(new NamespacedKey(RinDeath.getInstance(), "graveuuid"), PersistentDataType.STRING, as.getUniqueId().toString());
		player.getPersistentDataContainer().set(new NamespacedKey(RinDeath.getInstance(), "gamemode"), PersistentDataType.STRING, player.getGameMode().toString());
		player.setGameMode(GameMode.SPECTATOR);
		RDSecondThread.getInstance().addPlayer(player);
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		Player player = e.getPlayer();
		if (player.getGameMode().equals(GameMode.SPECTATOR)) {
			if (player.getPersistentDataContainer().has(new NamespacedKey(RinDeath.getInstance(), "graveuuid"), PersistentDataType.STRING)) {
				Entity as = RinDeath.getInstance().getServer().getEntity(UUID.fromString(player.getPersistentDataContainer().get(new NamespacedKey(RinDeath.getInstance(), "graveuuid"), PersistentDataType.STRING)));
				if (as.getLocation().distance(player.getLocation())>RinDeath.getInstance().getRDConfig().getRange()) {
					e.setTo(as.getLocation().add(0, 2, 0));
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractAtEntityEvent e) {
		if(!config.isEnable())return;

		if(!(e.getRightClicked() instanceof ArmorStand))return;
		if(e.getPlayer().getGameMode().equals(GameMode.SPECTATOR))return;
		ArmorStand as = (ArmorStand)e.getRightClicked();
		if(!as.getPersistentDataContainer().has(new NamespacedKey(RinDeath.getInstance(),"playeruuid"), PersistentDataType.STRING)) return;
		Player player = e.getPlayer();
		ItemStack handitem = player.getEquipment().getItemInMainHand();

		if (handitem.getType().equals(Material.AIR)) {
			player.sendMessage("您手中没有复活物品!");
			return;
		}
		NamespacedKey key = new NamespacedKey(RinDeath.getInstance(), "revive");
		ItemMeta meta=handitem.getItemMeta();
		if (meta.getPersistentDataContainer().has(key, PersistentDataType.INTEGER)) {
			if (meta.getPersistentDataContainer().get(key, PersistentDataType.INTEGER)>0) {
				meta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, meta.getPersistentDataContainer().get(key, PersistentDataType.INTEGER)-1);
			}			
			handitem.setItemMeta(meta);
			RDSecondThread.getInstance().removePlayer(UUID.fromString(as.getPersistentDataContainer().get(new NamespacedKey(RinDeath.getInstance(), "playeruuid"), PersistentDataType.STRING)), false);
			if (meta.getPersistentDataContainer().get(key, PersistentDataType.INTEGER)==0) {
				handitem.setAmount(handitem.getAmount()-1);
			}
		}

	}
	
}
