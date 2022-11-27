package com.satsukirin.RinDeath;

import java.io.File;

import org.bukkit.plugin.java.JavaPlugin;

public class RinDeath extends JavaPlugin {
	
	public static RinDeath INSTANCE;
	private RDSecondThread secondthread;
	
	@Override
	public void onEnable() {
		INSTANCE=this;
		
		File configFile = new File(getDataFolder(),"config.yml");
		if (!configFile.exists()) {
			saveDefaultConfig();
		}
		new RDConfig(configFile);
		getServer().getPluginManager().registerEvents(new RDEventListener(), this);
		RDCommand command = new RDCommand();
		getServer().getPluginCommand("rind").setExecutor(command);
		getServer().getPluginCommand("rindeath").setExecutor(command);
		secondthread=new RDSecondThread();
		secondthread.runTaskTimer(this, 20, 20);
		getLogger().info("RinDeath load finish!");
	}
	
	
	@Override
	public void onDisable() {
		secondthread.removeAll(true);
		secondthread.cancel();
	}

	public RDConfig getRDConfig() {
		return RDConfig.getInstance();
	}
	public static RinDeath getInstance() {
		return INSTANCE;
	}
}
