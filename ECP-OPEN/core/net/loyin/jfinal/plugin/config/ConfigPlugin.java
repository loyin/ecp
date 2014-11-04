package net.loyin.jfinal.plugin.config;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.plugin.IPlugin;

public class ConfigPlugin implements IPlugin {
	private final  List<String> resources = new ArrayList<String>();
	
	public ConfigPlugin() {
	}
	public ConfigPlugin(String resource) {
		this.resources.add(resource);
	}
	public void addResource(String resource) {
		this.resources.add(resource);
	}
	public void addResources(String resources){
		this.resources.add(resources);
	}
	public boolean start() {
		ConfigKit.init(resources);
		return true;
	}

	public boolean stop() {
		return true;
	}


}
