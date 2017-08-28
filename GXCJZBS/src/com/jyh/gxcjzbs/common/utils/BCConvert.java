package com.jyh.gxcjzbs.common.utils;

/**
 * @author Administrator
 *	符号转换
 */
public class BCConvert { 

	/** 
	 * ASCII表中可见字符从!开始，偏移位值为33(Decimal) 
	 */  
	static final char DBC_CHAR_START = 33; // 半角!  
	
	/** 
	 * ASCII表中可见字符到~结束，偏移位值为126(Decimal) 
	 */  
	static final char DBC_CHAR_END = 126; // 半角~  
    
    static final char SBC_CHAR_START = 65281; // 全角！

   
    static final char SBC_CHAR_END   = 65374; // 全角～

   
    static final int  CONVERT_STEP   = 65248; // 全角半角转换间隔

   
    static final char SBC_SPACE      = 12288; // 全角空格 12288

   
    static final char DBC_SPACE      = ' ';  // 半角空格

    /**
     * 半角转全角
     * @param src
     * @return
     */
    public static String DBC2SBC(String src) {
        if (src == null) {
            return src;
        }
        StringBuilder buf = new StringBuilder(src.length());
        char[] ca = src.toCharArray();
        for (int i = 0; i < ca.length; i++) {
            if (ca[i] == DBC_SPACE) { // 如果是半角空格，直接用全角空格替代
                buf.append(SBC_SPACE);
            } else if ((ca[i] >= DBC_CHAR_START) && (ca[i] <= DBC_CHAR_END)) { // 字符是!到~之间的可见字符
                buf.append((char) (ca[i] + CONVERT_STEP));
            } else { // 不对空格以及ascii表中其他可见字符之外的字符做任何处理
                buf.append(ca[i]);
            }
        }
        return buf.toString();
    }

   
    /**
     * 全角转半角
     * @param src
     * @return
     */
    public static String SBC2DBC(String src) {
        if (src == null) {
            return src;
        }
        StringBuilder buf = new StringBuilder(src.length());
        char[] ca = src.toCharArray();
        for (int i = 0; i < src.length(); i++) {
            if (ca[i] >= SBC_CHAR_START && ca[i] <= SBC_CHAR_END) { // 如果位于全角！到全角～区间内
                buf.append((char) (ca[i] - CONVERT_STEP));
            } else if (ca[i] == SBC_SPACE) { // 如果是全角空格
                buf.append(DBC_SPACE);
            } else { // 不处理全角空格，全角！到全角～区间外的字符
                buf.append(ca[i]);
            }
        }
        return buf.toString();
    }

}