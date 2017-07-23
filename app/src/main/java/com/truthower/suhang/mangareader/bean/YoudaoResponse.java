package com.truthower.suhang.mangareader.bean;

import java.util.List;

/**
 * Created by Administrator on 2016/4/10.
 */
public class YoudaoResponse {

    /**
     * us-phonetic : træns'let
     * phonetic : træns'leɪt; trɑːns-; -nz-
     * uk-phonetic : træns'leɪt; trɑːns-; -nz-
     * explains : ["vt. 翻译；转化；解释；转变为；调动","vi. 翻译"]
     */

    private BasicBean basic;
    /**
     * translation : ["翻译"]
     * query : translate
     * errorCode : 0
     * web : [{"value":["翻译","转化","平移"],"key":"translate"},{"value":["翻译"],"key":"translate verb"},{"value":["气泡翻译","泡泡翻译","实时翻译"],"key":"Bubble Translate"}]
     */

    private String query;
    private int errorCode;
    private List<String> translation;
    /**
     * value : ["翻译","转化","平移"]
     * key : translate
     */

    private List<WebBean> web;

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public List<String> getTranslation() {
        return translation;
    }

    public void setTranslation(List<String> translation) {
        this.translation = translation;
    }

    public List<WebBean> getWeb() {
        return web;
    }

    public void setWeb(List<WebBean> web) {
        this.web = web;
    }

    public BasicBean getBasic() {
        return basic;
    }

    public void setBasic(BasicBean basic) {
        this.basic = basic;
    }

    public class BasicBean {

        /**
         * us_phonetic : træns'let
         * phonetic : træns'leɪt; trɑːns-; -nz-
         * uk_phonetic : træns'leɪt; trɑːns-; -nz-
         * explains : ["vt. 翻译；转化；解释；转变为；调动","vi. 翻译"]
         */

        private String us_phonetic;
        private String phonetic;
        private String uk_phonetic;
        private List<String> explains;

        public String getUs_phonetic() {
            return us_phonetic;
        }

        public void setUs_phonetic(String us_phonetic) {
            this.us_phonetic = us_phonetic;
        }

        public String getPhonetic() {
            return phonetic;
        }

        public void setPhonetic(String phonetic) {
            this.phonetic = phonetic;
        }

        public String getUk_phonetic() {
            return uk_phonetic;
        }

        public void setUk_phonetic(String uk_phonetic) {
            this.uk_phonetic = uk_phonetic;
        }

        public List<String> getExplains() {
            return explains;
        }

        public void setExplains(List<String> explains) {
            this.explains = explains;
        }
    }

    public static class WebBean {
        private String key;
        private List<String> value;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public List<String> getValue() {
            return value;
        }

        public void setValue(List<String> value) {
            this.value = value;
        }
    }
}
