package com.truthower.suhang.mangareader.sort;

/**
 * 文件名数字排序工具
 * <p>
 * 排序规则：取出文件名（去掉路径和扩展名），把文件名里所有非数字的字符都去掉，
 * 剩下的数字拼成一个数，然后按这个数比较大小。
 * 例如 60story48(112).jpg -> 6048112，001.jpg -> 1。
 * 文件名里一个数字都没有时视为相等，也就是不排序。
 */
public class FileNameNumberUtil {
    private FileNameNumberUtil() {
    }

    /**
     * 从路径/文件名里取出文件名，并去掉扩展名
     */
    public static String getFileName(String pathOrName) {
        if (null == pathOrName) {
            return "";
        }
        String s = pathOrName.replace("file://", "").replace('\\', '/');
        int slash = s.lastIndexOf('/');
        if (slash >= 0 && slash < s.length() - 1) {
            s = s.substring(slash + 1);
        }
        int dot = s.lastIndexOf('.');
        if (dot > 0 && dot < s.length() - 1 && isExtension(s.substring(dot + 1))) {
            s = s.substring(0, dot);
        }
        return s;
    }

    /**
     * 文件名里所有的数字，按原来的先后顺序拼在一起
     */
    public static String getNumbers(String pathOrName) {
        String name = getFileName(pathOrName);
        StringBuilder numbers = new StringBuilder();
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (c >= '0' && c <= '9') {
                numbers.append(c);
            }
        }
        return numbers.toString();
    }

    /**
     * 文件名里有没有数字
     */
    public static boolean hasNumber(String pathOrName) {
        return getNumbers(pathOrName).length() > 0;
    }

    /**
     * 比较两个文件名里的数字。任一文件名里没有数字时返回0，即不调整它们的顺序。
     */
    public static int compareNumbers(String lhs, String rhs) {
        String l = getNumbers(lhs);
        String r = getNumbers(rhs);
        if (l.length() == 0 || r.length() == 0) {
            return 0;
        }
        return compareDigits(l, r);
    }

    /**
     * 比较两个纯数字串（不受int/long范围限制）：先去掉前导0，位数多的更大，
     * 位数一样时逐位比较。
     */
    public static int compareDigits(String l, String r) {
        int lStart = 0;
        while (lStart < l.length() - 1 && l.charAt(lStart) == '0') {
            lStart++;
        }
        int rStart = 0;
        while (rStart < r.length() - 1 && r.charAt(rStart) == '0') {
            rStart++;
        }
        int lLength = l.length() - lStart;
        int rLength = r.length() - rStart;
        if (lLength != rLength) {
            return lLength > rLength ? 1 : -1;
        }
        for (int i = 0; i < lLength; i++) {
            char a = l.charAt(lStart + i);
            char b = r.charAt(rStart + i);
            if (a != b) {
                return a > b ? 1 : -1;
            }
        }
        return 0;
    }

    private static boolean isExtension(String extension) {
        if (extension.length() == 0) {
            return false;
        }
        for (int i = 0; i < extension.length(); i++) {
            char c = extension.charAt(i);
            boolean isLetterOrDigit = (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')
                    || (c >= '0' && c <= '9');
            if (!isLetterOrDigit) {
                return false;
            }
        }
        return true;
    }
}
