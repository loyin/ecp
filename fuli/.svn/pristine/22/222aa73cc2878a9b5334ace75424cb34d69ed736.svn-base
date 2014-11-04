package com.gson.plugin;

import java.util.HashMap;

public class PluginContainer extends HashMap<String, PluginInstance> {
	private static final long serialVersionUID = 1L;

	@Override
	public PluginInstance put(String key, PluginInstance value) {
		if(super.containsKey(key)){
			throw new RuntimeException("插件实例【"+key+"】已存在！");
		}
		return super.put(key, value);
	}
}
