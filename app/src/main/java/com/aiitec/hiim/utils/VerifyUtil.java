package com.aiitec.hiim.utils;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;
import static java.util.regex.Pattern.matches;

/**
 * 校验工具
 */
public class VerifyUtil {

    static Pattern numberPattern = compile("[0-9]*");
//    static Pattern mobilePattern = compile("^((13[0,1-9])|(15[^4,\\D])|(18[0,1-9])|(14[0,3-9])|(17[0,1-9]))\\d{8}$");

    /**
     * 166开头的腾讯王卡也是合格的手机号码
     */
//    static Pattern mobilePattern = compile("^((13[0,1-9])|(15[^4,\\D])|(18[0,1-9])|(14[0,3-9])|(17[0,1-9])|(166))\\d{8}$");
    /**
     * 只要限制用户是1开头就行了
     */
    static Pattern mobilePattern = compile("^(1)\\d{10}$");

    private VerifyUtil() {
    }

    /**
     * 判断手机格式是否正确
     */
    public static boolean isMobileNO(String mobiles) {
        Matcher m = mobilePattern.matcher(mobiles);
        return m.matches();
    }

    /**
     * 判断email格式是否正确
     */
    public static boolean isEmail(String email) {
        String str = "^([a-zA-Z0-9_\\-.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern p = compile(str);
        Matcher m = p.matcher(email);
        return m.matches();
    }

    /**
     * 判定输入汉字
     *
     * @param c
     * @return
     */
    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
            return true;
        }
        return false;
    }

    /**
     * 密码强度
     */
    public static String checkPassword(String passwordStr) {
        // 不超过20位的数字组合
        String str = "^(?:\\d+|[a-zA-Z]+|[!@#$%^&*]+)$";
        // 由字母、数字组成，不超过20位
        String str1 = "^(?!\\d+$)(?![a-zA-Z]+$)[a-zA-Z\\d]+$";
        // 由字母不超过20位
        String str2 = "^(?![a-zA-z]+$)(?!\\d+$)(?![!@#$%^&*]+$)(?![a-zA-z\\d]+$)(?![a-zA-z!@#$%^&*]+$)(?![\\d!@#$%^&*]+$)[a-zA-Z\\d!@#$%^&*]+$";
        if (passwordStr.matches(str)) {
            return "弱";
        }
        if (passwordStr.matches(str1)) {
            return "中";
        }
        if (passwordStr.matches(str2)) {
            return "强";
        }
        return "弱";
    }

    /**
     * 包含字母
     */
    public static boolean containLetter(String string) {
        String regex = ".*[a-zA-Z]+.*";
        Matcher m = compile(regex).matcher(string);
        return m.matches();
    }

    /**
     * 区号+座机号码+分机号码
     */
    public static boolean isFixedPhone(String fixedPhone) {
        String reg = "(?:(\\(\\+?86\\))(0[0-9]{2,3}\\-?)?([2-9][0-9]{6,7})+(\\-[0-9]{1,4})?)|" +
                "(?:(86-?)?(0[0-9]{2,3}\\-?)?([2-9][0-9]{6,7})+(\\-[0-9]{1,4})?)";
        return matches(reg, fixedPhone);
    }

    /**
     * 判断是否含有特殊字符
     */
    public static boolean isSpecialChar(String str) {
        String regEx = "[ _`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]|\n|\r|\t";
        Pattern p = compile(regEx);
        Matcher m = p.matcher(str);
        return m.find();
    }

    /**
     * 邮政编码
     */
    public static boolean isPost(String post) {
        if (post.matches("[0-9]\\d{5}(?!\\d)")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 纯数字
     */
    public static boolean isNumber(String number) {
        String reg = "^\\d+$";
        if (number.matches(reg)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 纯字母
     */
    public static boolean isLetter(String letter) {
        String reg = "[a-zA-Z]+";
        if (letter.matches(reg)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 汉字、字母和数字的组合
     */
    public static boolean isChineseLetterNumber(String letter) {
        String reg = "^[a-zA-Z0-9\\u4e00-\\u9fa5]+$";
        if (letter.matches(reg)) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * 纯中文
     */
    public static boolean isChinese(String letter) {
        String reg = "[\u4E00-\u9FA5]+";
        if (letter.matches(reg)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 验证身份证号码
     *
     * @param idCardNumber
     * @return
     */
    public static boolean isIdCardNumber(String idCardNumber) {
        String reg15 = "^[1-9]\\\\d{7}((0\\\\d)|(1[0-2]))(([0|1|2]\\\\d)|3[0-1])\\\\d{3}$";
        String reg18 = "^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9]|X)$";
        if (idCardNumber.matches(reg15) || idCardNumber.matches(reg18)) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * 验证银行卡号
     *
     * @param cardId
     * @return
     */
    public static boolean checkBankCard(String cardId) {
        if (TextUtils.isEmpty(cardId)) {
            return false;
        }
        char bit = getBankCardCheckCode(cardId
                .substring(0, cardId.length() - 1));
        if (bit == 'N') {
            return false;
        }
        return cardId.charAt(cardId.length() - 1) == bit;
    }

    /**
     * 从不含校验位的银行卡卡号采用 Luhm 校验算法获得校验位
     *
     * @param nonCheckCodeCardId
     * @return
     */
    public static char getBankCardCheckCode(String nonCheckCodeCardId) {
        if (nonCheckCodeCardId == null
                || nonCheckCodeCardId.trim().length() == 0
                || !nonCheckCodeCardId.matches("\\d+")) {
            // 如果传的不是数据返回N
            return 'N';
        }
        char[] chs = nonCheckCodeCardId.trim().toCharArray();
        int luhmSum = 0;
        for (int i = chs.length - 1, j = 0; i >= 0; i--, j++) {
            int k = chs[i] - '0';
            if (j % 2 == 0) {
                k *= 2;
                k = k / 10 + k % 10;
            }
            luhmSum += k;
        }
        return (luhmSum % 10 == 0) ? '0' : (char) ((10 - luhmSum % 10) + '0');
    }

    /**
     * 验证URL地址
     *
     * @param url
     * @return 验证成功返回true，验证失败返回false
     */
    public static boolean checkURL(String url) {
        String regex = "^([hH][tT]{2}[pP]:/*|[hH][tT]{2}[pP][sS]:/*|[fF][tT][pP]:/*)(([A-Za-z0-9-~]+).)+([A-Za-z0-9-~\\/])+(\\?{0,1}(([A-Za-z0-9-~]+\\={0,1})([A-Za-z0-9-~]*)\\&{0,1})*)$";
        return matches(regex, url);
    }

    /**
     * 验证字符串是否是数字
     *
     * @param content
     * @return 如果是数字就返回true, 否则返回false
     */
    public static boolean isDigit(String content) {

        Matcher m = numberPattern.matcher(content);
        return m.matches();
    }


    /**
     * 验证密码6~20位字母和数字组合(可以包含字符)
     *
     * @param input
     * @return
     */
    public static boolean rexCheckPassword(String input) {
        // 6-20 位，字母、数字、字符(至少两种组合,纯数字和纯字母,纯字符不行)
//        String regStr = "^(?![\\d]+$)(?![a-zA-Z]+$)(?![^\\da-zA-Z]+$).{6,20}$";
        // 6-20 位，字母、数字、字符(至少两种组合,纯数字和纯字母,纯字符不行),也不等于字符和字母组合,字符和数字组合.
//        String regStr = "^(?![\\d]+$)(?![a-zA-Z]+$)(?![^\\da-zA-Z]+$)(?![^\\d]+$)(?![^a-zA-Z]+$).{6,20}$";
        String regStr = "^(?![\\d]+$)(?![a-zA-Z]+$)(?![^\\da-zA-Z]+$)(?![^\\d]+$)(?![^a-zA-Z]+$).{6,16}$";
        return input.matches(regStr);
    }

}
