package net.loyin.kit;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.jfinal.core.Const;

/**
 * I18N support.
 * 
 * 1: Config parameters in JFinalConfig
 * 2: Init I18N in JFinal 
 * 3: I18N support text with Locale
 * 4: Controller use I18N.getText(...) with Local setting in I18nInterceptor
 * 5: The resource file in WEB-INF/classes
 * 
 * important: Locale can create with language like new Locale("xxx");
 * 
 * need test
 * Using String get Locale was learned from Strus2
 */
public class I18N {
	private static String dir="i18n";
	private static Locale defaultLocale = Locale.getDefault();
	private static int i18nMaxAgeOfCookie = Const.DEFAULT_I18N_MAX_AGE_OF_COOKIE;
	private static final ConcurrentMap<String, ResourceBundle> bundlesMap = new ConcurrentHashMap<String, ResourceBundle>();
	
	private static volatile I18N me;
	
	private I18N() {
	}
	
	public static I18N me() {
		if (me == null)
			synchronized (I18N.class) {
				if (me == null)
					me = new I18N();
			}
		return me;
	}
	
	public static void init(String dir_, String baseName, Integer i18nMaxAgeOfCookie) {
		if(dir_!=null)
			dir=dir_;
		if (i18nMaxAgeOfCookie != null)
			I18N.i18nMaxAgeOfCookie = i18nMaxAgeOfCookie;
		String resourceBundleKey = getresourceBundleKey(baseName,defaultLocale);
		System.out.println("resourceBundleKey:\t"+resourceBundleKey);
		ResourceBundle resourceBundle = bundlesMap.get(resourceBundleKey);
		if (resourceBundle == null) {
				resourceBundle = ResourceBundle.getBundle(dir+"/"+baseName, defaultLocale);
				bundlesMap.put(resourceBundleKey, resourceBundle);
				System.out.println("add "+resourceBundleKey);
		}
	}
	
	public static Locale getDefaultLocale() {
		return defaultLocale;
	}
	
	final static public int getI18nMaxAgeOfCookie() {
		return i18nMaxAgeOfCookie;
	}
	
	/**
	 * 将来只改这里就可以了: resourceBundleKey的生成规则
	 */
	private static String getresourceBundleKey(String baseName,Locale locale) {
		return baseName +"_"+  locale.toString();
	}
	
	public static String getText(String key) {
		String[] k=key.split("\\.");
		String baseName=k[0];
		return getResourceBundle(baseName,defaultLocale).getString(k[1]);
	}
	
	public static String getText(String baseName,String key, String defaultValue) {
		String result = getResourceBundle(baseName,defaultLocale).getString(key);
		return result != null ? result : defaultValue;
	}
	
	public static String getText(String key, Locale locale) {
		String[] k=key.split("\\.");
		return getResourceBundle(k[0],locale).getString(k[1]);
	}
	public static String getText(String key,String defaultValue, Locale locale) {
		String[] k=key.split("\\.");
		String result=getResourceBundle(k[0],locale).getString(k[1]);
		return result != null ? result : defaultValue;
	}
	public static String getText(String baseName,String key, String defaultValue, Locale locale) {
		String result = getResourceBundle(baseName,locale).getString(key);
		return result != null ? result : defaultValue;
	}
	private static ResourceBundle getResourceBundle(String baseName,Locale locale) {
		String resourceBundleKey = getresourceBundleKey(baseName,locale);
		ResourceBundle resourceBundle = bundlesMap.get(resourceBundleKey);
		if (resourceBundle == null) {
			resourceBundle = ResourceBundle.getBundle(dir+"/"+baseName, locale);
			bundlesMap.put(resourceBundleKey, resourceBundle);
		}
		return resourceBundle;
	}
	public static Locale localeFromString(String localeStr) {
        if ((localeStr == null) || (localeStr.trim().length() == 0) || ("_".equals(localeStr))) {
            // return (defaultLocale != null) ? defaultLocale : Locale.getDefault();	// 原实现被注掉
        	return defaultLocale;
        }
        
        int index = localeStr.indexOf('_');
        if (index < 0) {
            return new Locale(localeStr);
        }
        
        String language = localeStr.substring(0, index);
        if (index == localeStr.length()) {
            return new Locale(language);
        }
        
        localeStr = localeStr.substring(index + 1);
        index = localeStr.indexOf('_');
        if (index < 0) {
            return new Locale(language, localeStr);
        }
        
        String country = localeStr.substring(0, index);
        if (index == localeStr.length()) {
            return new Locale(language, country);
        }
        
        localeStr = localeStr.substring(index + 1);
        return new Locale(language, country, localeStr);
    }

	public  static void main(String[] args){
		I18N.init("i18n","dic", null);//字典
		System.out.println(I18N.getText("dic.userno"));
		I18N.init("i18n","msg", null);//信息
		System.out.println(I18N.getText("msg.save_fail"));
		I18N.init("i18n","err", null);//异常
		System.out.println(I18N.getText("err.login_err_userno"));
	}
}
