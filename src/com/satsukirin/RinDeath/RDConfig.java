package com.satsukirin.RinDeath;

import java.io.File;

import org.bukkit.configuration.file.YamlConfiguration;

public class RDConfig {
	private boolean enable;
	private int respawnTime;
	private int range;
	private TipType tiptype;
	private static RDConfig INSTANCE;
	private int gravemodel;
	public static RDConfig getInstance() {
		return INSTANCE;
	}
	
	public RDConfig(File configFile) {
		YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
		this.enable=config.getBoolean("enable");
		this.respawnTime = config.getInt("respawnTime");
		this.range=config.getInt("range");
		this.tiptype = TipType.valueOf(config.getString("tipType"));
		this.gravemodel=config.getInt("graveModel");
		INSTANCE=this;
	}


	public TipType getTiptype() {
		return tiptype;
	}

	public int getGravemodel() {
		return gravemodel;
	}

	public boolean isEnable() {
		return enable;
	}


	public int getRespawnTime() {
		return respawnTime;
	}


	public int getRange() {
		return range;
	}


	enum TipType{
		actionbar,bossbar,title
	}
}
