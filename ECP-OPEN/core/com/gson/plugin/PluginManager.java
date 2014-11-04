/**
 * 微信公众平台开发模式(JAVA) SDK
 * (c) 2012-2014 ____′↘夏悸 <wmails@126.cn>, MIT Licensed
 * http://www.jeasyuicn.com/wechat
 */
package com.gson.plugin;

import com.gson.util.ConfKit;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 插件管理器
 *
 * @author ____′↘夏悸
 */
public class PluginManager {
    private static PluginManager pm = new PluginManager();
    private PluginContainer runingPluginContainer = new PluginContainer();
    private PluginContainer pluginContainer;
    private Properties pluginProperties;
    private String pluginDir = ConfKit.get("pluginDir");
    private final String PROPERTIES_FILE_NAME = "plugin.properties";

    private PluginManager() {
        try {
            pluginContainer = fetchAllPlugins();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取Manager对象
     *
     * @return
     */
    public static PluginManager getInstance() {
        return pm;
    }

    /**
     * 运行插件
     *
     * @param pluginName
     * @throws Exception
     */
    public void run(String pluginName, Object... obj) throws Exception {
        PluginInstance instance = getInstance(pluginName);
        if (instance == null || instance.getEntry() == null) {
            throw new Exception("[" + pluginName + "]插件不存在或尚未启动!");
        }
        Plugin ins = instance.getPluginInstance();
        ins.run(obj);
    }

    /**
     * 启动插件
     *
     * @param pluginName
     * @throws Exception
     */
    public void startPlugin(String pluginName) throws Exception {
        PluginInstance instance = pluginContainer.get(pluginName);
        Class<?> entry = Class.forName(instance.getEntryClass(), true, instance.getLoader());
        instance.setEntry(entry);
        this.runingPluginContainer.put(pluginName, instance);
    }

    /**
     * 安装插件
     *
     * @param pluginName
     * @return
     * @throws Exception
     */
    public Boolean installPlugin(String pluginName) throws Exception {
        Plugin pluginIns = getPlugin(pluginName);
        return pluginIns.install();
    }

    /**
     * 插件更新
     *
     * @param pluginName
     * @return
     * @throws Exception
     */
    public Boolean upgradePlugin(String pluginName) throws Exception {
        Plugin pluginIns = getPlugin(pluginName);
        return pluginIns.upgrade();
    }

    /**
     * 插件卸载
     *
     * @param pluginName
     * @return
     * @throws Exception
     */
    public Boolean uninstallPlugin(String pluginName) throws Exception {
        Plugin pluginIns = getPlugin(pluginName);
        unloadPlugin(pluginName);
        return pluginIns.uninstall();
    }

    /**
     * 插件是否已经加载
     *
     * @param pluginName
     * @return
     */
    public Boolean isRuning(String pluginName) {
        return this.runingPluginContainer.containsKey(pluginName);
    }

    /**
     * 获取所有插件信息
     *
     * @return
     * @throws IOException
     */
    public PluginContainer fetchAllPlugins() throws IOException {
        File dir = new File(pluginDir);
        PluginContainer container = new PluginContainer();
        if (!dir.exists()) {
           return container;// throw new RuntimeException("插件目录不存在!");
        }
        PluginClassLoader loader;

        File[] jars = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".jar");
            }
        });

        for (File file : jars) {
            JarFile jar = new JarFile(file);
            Enumeration<JarEntry> enumer = jar.entries();
            while (enumer.hasMoreElements()) {
                JarEntry jarEntry = (JarEntry) enumer.nextElement();
                if (jarEntry.getName().equals(PROPERTIES_FILE_NAME)) {
                    loader = new PluginClassLoader();
                    loader.addURLFile(new URL("jar:file:/" + file.getPath() + "!/"));
                    PluginInstance instance = new PluginInstance();
                    instance.setLoader(loader);
                    pluginProperties = new Properties();
                    pluginProperties.load(jar.getInputStream(jarEntry));
                    instance.setEntryClass(pluginProperties.getProperty("entryClass"));
                    instance.setAuthor(pluginProperties.getProperty("author"));
                    instance.setDescription(pluginProperties.getProperty("description"));
                    instance.setName(pluginProperties.getProperty("name"));
                    instance.setVersion(pluginProperties.getProperty("version"));
                    instance.setFileName(file.getName());
                    container.put(instance.getName(), instance);
                    jar.close();
                    break;
                }
            }
        }
        return container;
    }

    /**
     * 获得插件运行容器
     *
     * @return
     */
    public PluginContainer getRuningPluginContainer() {
        return runingPluginContainer;
    }

    private void unloadPlugin(String pluginName) throws IOException {
        this.runingPluginContainer.get(pluginName).getLoader().unloadJarFiles();
        this.runingPluginContainer.remove(pluginName);
    }

    private PluginInstance getInstance(String pluginName) {
        return this.runingPluginContainer.get(pluginName);
    }

    private Plugin getPlugin(String pluginName) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        PluginInstance instance = pluginContainer.get(pluginName);
        Class<?> entry = Class.forName(instance.getEntryClass(), true, instance.getLoader());
        return (Plugin) entry.newInstance();
    }
}
