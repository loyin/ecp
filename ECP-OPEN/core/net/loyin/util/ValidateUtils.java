package net.loyin.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

public class ValidateUtils {

  private static ValidateUtils validateUtils = new ValidateUtils();

  public static ValidateUtils me() {
    return validateUtils;
  }

  private ValidateUtils() {
  }

  /**
   * 匹配正则表达式
   *
   * @param regex regex
   * @param value value
   * @return boolean
   */
  public boolean match(String regex, String value) {
    Pattern pattern = Pattern.compile(regex);

    if (pattern.matcher(value).find()) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * 区分大小写
   *
   * @param regex regex
   * @param flags flags
   * @param value value
   * @return boolean
   */
  public boolean match(String regex, int flags, String value) {
    Pattern pattern = Pattern.compile(regex, flags);

    if (pattern.matcher(value).find()) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * 邮箱验证工具
   *
   * @param value value
   * @return boolean
   */
  public boolean isEmail(String value) {
    String check = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";

    return match(check, Pattern.CASE_INSENSITIVE, value);
  }

  /**
   * 手机号码验证
   *
   * @param value value
   * @return boolean
   */
  public boolean isMobile(String value) {
    String check = "^(((13[0-9]{1})|(15[0-9]{1})|(18[0-9]{1}))+\\d{8})$";
    return match(check, Pattern.CASE_INSENSITIVE, value);
  }

  /**
   * 座机验证
   *
   * @param value value
   * @return boolean
   */
  public boolean isTel(String value) {
    String check = "^\\d{3,4}-?\\d{7,9}$";
    return match(check, Pattern.CASE_INSENSITIVE, value);
  }

  /**
   * 电话号码 包括移动电话和座机
   *
   * @param value value
   * @return boolean
   */
  public boolean isPhone(String value) {
    String telcheck = "^\\d{3,4}-?\\d{7,9}$";
    String mobilecheck = "^(((13[0-9]{1})|(15[0-9]{1})|(18[0-9]{1}))+\\d{8})$";

    if (match(telcheck, Pattern.CASE_INSENSITIVE, value)
        || match(mobilecheck, Pattern.CASE_INSENSITIVE, value)) {
      return true;
    } else
      return false;
  }

  /**
   * @param value 输入内容限制为英文字母 、数字和下划线
   * @return boolean
   */
  public boolean isGeneral(String value) {
    String check = "^\\w+$";
    return match(check, Pattern.CASE_INSENSITIVE, value);
  }

  public boolean isGeneral(String value, int min, int max) {
    String check = "^\\w{" + min + "," + max + "}$";
    return match(check, Pattern.CASE_INSENSITIVE, value);
  }

  /**
   * 判断是否是生日
   *
   * @param value value
   * @return boolean
   */
  public boolean isBirthDay(String value) {
    String check = "(\\d{4})(/|-|\\.)(\\d{1,2})(/|-|\\.)(\\d{1,2})$";

    if (match(check, Pattern.CASE_INSENSITIVE, value)) {
      int year = Integer.parseInt(value.substring(0, 4));
      int month = Integer.parseInt(value.substring(5, 7));
      int day = Integer.parseInt(value.substring(8, 10));

      if (month < 1 || month > 12)
        return false;

      if (day < 1 || day > 31)
        return false;

      if ((month == 4 || month == 6 || month == 9 || month == 11)
          && day == 31)
        return false;

      if (month == 2) {
        boolean isleap = (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0));
        if (day > 29 || (day == 29 && !isleap))
          return false;
      }
      return true;
    } else {
      return false;
    }
  }

  /**
   * 身份证
   *
   * @param value value
   * @return boolean
   */
  public boolean isIdentityCard(String value) {
    String check = "(^\\d{15}$)|(^\\d{17}([0-9]|X)$)";
    return match(check, Pattern.CASE_INSENSITIVE, value);
  }

  /**
   * 邮政编码
   *
   * @param value value
   * @return boolean
   */
  public boolean isZipCode(String value) {
    String check = "^[0-9]{6}$";
    return match(check, Pattern.CASE_INSENSITIVE, value);
  }

  /**
   * 货币
   *
   * @param value value
   * @return boolean
   */
  public boolean isMoney(String value) {
    String check = "^(\\d+(?:\\.\\d{1,2})?)$";
    return match(check, Pattern.CASE_INSENSITIVE, value);
  }

  /**
   * 大于0的数字
   *
   * @param value value
   * @return boolean
   */
  public boolean isNumber(String value) {
    String check = "^(\\+|\\-)?\\d+$";
    return match(check, Pattern.CASE_INSENSITIVE, value);
  }

  public boolean isNumber(String value, int min, int max) {
    String check = "^(\\+|\\-)?\\d{" + min + "," + max + "}$";
    return match(check, Pattern.CASE_INSENSITIVE, value);
  }

  /**
   * 正整数
   *
   * @param value value
   * @return boolean
   */
  public boolean isPositiveNumber(String value) {
    String check = "^\\d+$";
    return match(check, Pattern.CASE_INSENSITIVE, value);
  }

  public boolean isPositiveNumber(String value, int min, int max) {
    String check = "^\\d{" + min + "," + max + "}$";
    return match(check, Pattern.CASE_INSENSITIVE, value);
  }

  /**
   * 中文
   *
   * @param value value
   * @return boolean
   */
  public boolean isChinese(String value) {
    String check = "^[\\u2E80-\\u9FFF]+$";
    return match(check, Pattern.CASE_INSENSITIVE, value);
  }

  public boolean isChinese(String value, int min, int max) {
    String check = "^[\\u2E80-\\u9FFF]{" + min + "," + max + "}$";
    return match(check, Pattern.CASE_INSENSITIVE, value);
  }

  /**
   * 中文字、英文字母、数字和下划线
   *
   * @param value value
   * @return boolean
   */
  public boolean isString(String value) {
    String check = "^[\\u0391-\\uFFE5\\w]+$";
    return match(check, Pattern.CASE_INSENSITIVE, value);
  }

  public boolean isString(String value, int min, int max) {
    String check = "^[\\u0391-\\uFFE5\\w]{" + min + "," + max + "}$";
    return match(check, Pattern.CASE_INSENSITIVE, value);
  }

  /**
   * UUID
   *
   * @param value value
   * @return boolean
   */
  public boolean isUUID(String value) {
    String check = "^[0-9a-z]{8}-[0-9a-z]{4}-[0-9a-z]{4}-[0-9a-z]{4}-[0-9a-z]{12}$";
    return match(check, Pattern.CASE_INSENSITIVE, value);
  }

  /**
   * 礼品卡前缀
   *
   * @param value value
   * @return boolean
   */
  public boolean isCardPrefix(String value) {
    String check = "^[A-Za-z]\\w{0,3}\\d{0,4}$";
    return match(check, Pattern.CASE_INSENSITIVE, value);
  }

  /**
   * 礼品卡格式
   *
   * @param value value
   * @return boolean
   */
  public boolean isCard(String value) {
    String check = "^[A-Za-z]\\w{3}\\d{4}$";
    return match(check, Pattern.CASE_INSENSITIVE, value);
  }

  public boolean isUsername(String value) {
    return isGeneral(value, 5, 18);
  }

  public boolean isPassword(String value) {
    return isGeneral(value, 5, 18);
  }

  /**
   * 匹配是否是链接
   *
   * @param value value
   * @return boolean
   */
  public boolean isUrl(String value) {
//
//    // 子域名
//    final String SUB_DOMAIN = "(?i:[a-z0-9]|[a-z0-9][-a-z0-9]*[a-z0-9])";
//
//    // 顶级域名
//    final String TOP_DOMAINS = "(?x-i:com\\b        \n"
//        + "     |edu\\b        \n" + "     |biz\\b        \n"
//        + "     |in(?:t|fo)\\b \n" + "     |mil\\b        \n"
//        + "     |net\\b        \n" + "     |org\\b        \n"
//        + "     |[a-z][a-z]\\b \n" + // 国家代码
//        ")                   \n";
//    // 主机名
//    final String HOSTNAME = "(?:" + SUB_DOMAIN + "\\.)+" + TOP_DOMAINS;
//
//    // URL 地址中不允许包括的字符，以及其他的条件
//    final String NOT_IN = ";:\"'<>()\\[\\]{}\\s\\x7F-\\xFF";
//    final String NOT_END = "!.,?";
//    final String ANYWHERE = "[^" + NOT_IN + NOT_END + "]";
//    final String EMBEDDED = "[" + NOT_END + "]";
//    final String URL_PATH = "/" + ANYWHERE + "*(" + EMBEDDED + "+"
//        + ANYWHERE + "+)*";
//
//    // 端口号 0～65535
//    final String PORT = "(?:                          \n"
//        + "  [0-5]?[0-9]{1,4}           \n"
//        + "  |                          \n"
//        + "  6(?:                       \n"
//        + "     [0-4][0-9]{3}           \n"
//        + "     |                       \n"
//        + "     5(?:                    \n"
//        + "        [0-4][0-9]{2}        \n"
//        + "        |                    \n"
//        + "        5(?:                 \n"
//        + "           [0-2][0-9]        \n"
//        + "           |                 \n"
//        + "           3[0-5]            \n"
//        + "         )                   \n"
//        + "      )                      \n"
//        + "   )                         \n" + ")";
//
//    // URL 表达式
//    final String URL = "(?x:                                                \n"
//        + "  \\b                                               \n"
//        + "  (?:                                               \n"
//        + "    (?: ftp | http s? ): // [-\\w]+(\\.\\w[-\\w]*)+ \n"
//        + "   |                                                \n"
//        + "    "
//        + HOSTNAME
//        + "                                \n"
//        + "  )                                                 \n"
//        + "  (?: : "
//        + PORT
//        + " )?                             \n"
//        + "  (?: "
//        + URL_PATH
//        + ")?                            \n"
//        + ")";
    String check = "^((https?|ftp):\\/\\/)?(((([a-z]|\\d|-|\\.|_|~|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(%[\\da-f]{2})|[!\\$&'\\(\\)\\*\\+,;=]|:)*@)?(((\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5]))|((([a-z]|\\d|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(([a-z]|\\d|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])([a-z]|\\d|-|\\.|_|~|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])*([a-z]|\\d|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])))\\.)+(([a-z]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(([a-z]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])([a-z]|\\d|-|\\.|_|~|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])*([a-z]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])))\\.?)(:\\d*)?)(\\/((([a-z]|\\d|-|\\.|_|~|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(%[\\da-f]{2})|[!\\$&'\\(\\)\\*\\+,;=]|:|@)+(\\/(([a-z]|\\d|-|\\.|_|~|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(%[\\da-f]{2})|[!\\$&'\\(\\)\\*\\+,;=]|:|@)*)*)?)?(\\?((([a-z]|\\d|-|\\.|_|~|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(%[\\da-f]{2})|[!\\$&'\\(\\)\\*\\+,;=]|:|@)|[\\uE000-\\uF8FF]|\\/|\\?)*)?(\\#((([a-z]|\\d|-|\\.|_|~|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(%[\\da-f]{2})|[!\\$&'\\(\\)\\*\\+,;=]|:|@)|\\/|\\?)*)?$";
    return match(check, Pattern.CASE_INSENSITIVE, value);
  }

  /**
   * 判断
   *
   * @param value value
   * @return boolean
   */
  public boolean isDateTime(String value) {
    String check = "^(\\d{4})(/|-|\\.|年)(\\d{1,2})(/|-|\\.|月)(\\d{1,2})(日)?(\\s+\\d{1,2}(:|时)\\d{1,2}(:|分)?(\\d{1,2}(秒)?)?)?$";// check = "^(\\d{4})(/|-|\\.)(\\d{1,2})(/|-|\\.)(\\d{1,2})$";

    return match(check, Pattern.CASE_INSENSITIVE, value);
  }

  public boolean isNullOrEmpty(Object value) {
    if (value instanceof Collection) {
      if (value == null || ((Collection) value).isEmpty()) {
        return true;
      }
    } else if (value instanceof String) {
      if (StringUtils.isEmpty(value.toString())) {
        return true;
      }
    } else {
      if (value == null)
        return true;
    }
    return false;
  }

  public boolean isLength(String value, int min, int max) {
    int length = isNullOrEmpty(value) ? 0 : value.length();
    if (length >= min && length <= max) return true;
    else return false;
  }

  public boolean compareDate(String date1, String date2) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    try {
      Date d1 = sdf.parse(date1);
      Date d2 = sdf.parse(date2);
      return d1.compareTo(d2) > 0;
    } catch (ParseException e) {
      return false;
    }
  }

  public static void main(String[] args) {
    String email = "sss@gmail.com";
    String phone = "18611351122";
    String general = "aa";

    // System.out.println(ValidateUtils.me().isEmail(email) + "--"
    // + ValidateUtils.me().isPhone(phone) + "--"
    // + ValidateUtils.me().isGeneral(general) + "--"
    // + isBirthDay("1990.02.29")+"--"+isCard("va234567"));
    // System.out.println(isNumber("000001"));
    // System.out.println(isCard("sz000016"));

    // 测试代码
    String[] strs = {"http://www.13.com", "https://www.asdf.com.cn/asdf",
        "www.23.com", "http://www.123.com/tsf",
        "http://www.abc.com/ab:2525/a.asp",
        "http://www.123.com:2563/tsf/a.html?a=2&&b=abc",
        "http://www.aa.com/servlet",
        "http://ee.com/abcSerlvet?a=8&&b=asdf",
        "ftp://192.168.0.2:8080", "http://ww.ss.com/////", "http://ww.gg.cn/"};
    for (int i = 0; i < strs.length; i++) {
      System.out.printf("%s --> %s%n", strs[i], ValidateUtils.me().isUrl(strs[i]));
    }
    System.out.println("--------------------------------");
//    System.out.println(isDateTime("2013年11月26日 11时10分11秒"));

    String check = "^((https?|ftp):\\/\\/)?(((([a-z]|\\d|-|\\.|_|~|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(%[\\da-f]{2})|[!\\$&'\\(\\)\\*\\+,;=]|:)*@)?(((\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5]))|((([a-z]|\\d|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(([a-z]|\\d|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])([a-z]|\\d|-|\\.|_|~|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])*([a-z]|\\d|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])))\\.)+(([a-z]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(([a-z]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])([a-z]|\\d|-|\\.|_|~|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])*([a-z]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])))\\.?)(:\\d*)?)(\\/((([a-z]|\\d|-|\\.|_|~|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(%[\\da-f]{2})|[!\\$&'\\(\\)\\*\\+,;=]|:|@)+(\\/(([a-z]|\\d|-|\\.|_|~|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(%[\\da-f]{2})|[!\\$&'\\(\\)\\*\\+,;=]|:|@)*)*)?)?(\\?((([a-z]|\\d|-|\\.|_|~|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(%[\\da-f]{2})|[!\\$&'\\(\\)\\*\\+,;=]|:|@)|[\\uE000-\\uF8FF]|\\/|\\?)*)?(\\#((([a-z]|\\d|-|\\.|_|~|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(%[\\da-f]{2})|[!\\$&'\\(\\)\\*\\+,;=]|:|@)|\\/|\\?)*)?$";
    Pattern pattern = Pattern.compile(check, Pattern.CASE_INSENSITIVE);
    for (int i = 0; i < strs.length; i++) {
      System.out.printf("%s --> %s%n", strs[i], pattern.matcher(strs[i]).find());
    }
//    if (pattern.matcher("/channel/save").find()) {
//      System.out.print("true");
//    } else {
//      System.out.print("false");
//    }
    System.out.println("UUID:" + ValidateUtils.me().isUUID("ee706ee9-1658-43d0-bc1f-2db23cab4ab2"));
  }
}
