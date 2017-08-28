package com.jyh.byzb.bean;

import java.util.List;

public class NavIndextEntity {


    /**
     * data : {"slideshow":[{"image":"http://cdn0.108tec.com/gxsp/Uploads/Picture/2017-08-07/5987fee1e3b12.png","ban_access_role":[],"title":"","ban_access_msg":"","url":"http://www.baidu.com"}],"button":[{"image":"http://cdn0.108tec.com/gxsp/Uploads/Picture/2017-08-07/5988005af153d.png","ban_access_role":["1"],"title":"操作建议","ban_access_msg":"想获得您青睐老师更鑫个人参考建议，请联系客服","url":"http://gxsp.108tec.com/article/lists/id/jianyi.html"},{"image":"http://cdn0.108tec.com/gxsp/Uploads/Picture/2017-08-07/598801f1a584d.png","ban_access_role":[],"title":"一对一指导","ban_access_msg":"","url":"http://www.baidu.com"}]}
     * msg : ok
     * code : 200
     */

    private DataBean data;
    private String msg;
    private int code;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public static class DataBean {
        private List<SlideshowBean> slideshow;
        private List<ButtonBean> button;

        public List<SlideshowBean> getSlideshow() {
            return slideshow;
        }

        public void setSlideshow(List<SlideshowBean> slideshow) {
            this.slideshow = slideshow;
        }

        public List<ButtonBean> getButton() {
            return button;
        }

        public void setButton(List<ButtonBean> button) {
            this.button = button;
        }

        public static class SlideshowBean {
            /**
             * image : http://cdn0.108tec.com/gxsp/Uploads/Picture/2017-08-07/5987fee1e3b12.png
             * ban_access_role : []
             * title :
             * ban_access_msg :
             * url : http://www.baidu.com
             */

            private String image;
            private String title;
            private String ban_access_msg;
            private String url;
            private List<String> ban_access_role;

            public String getImage() {
                return image;
            }

            public void setImage(String image) {
                this.image = image;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getBan_access_msg() {
                return ban_access_msg;
            }

            public void setBan_access_msg(String ban_access_msg) {
                this.ban_access_msg = ban_access_msg;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }

            public List<String> getBan_access_role() {
                return ban_access_role;
            }

            public void setBan_access_role(List<String> ban_access_role) {
                this.ban_access_role = ban_access_role;
            }
        }

        public static class ButtonBean {
            /**
             * image : http://cdn0.108tec.com/gxsp/Uploads/Picture/2017-08-07/5988005af153d.png
             * ban_access_role : ["1"]
             * title : 操作建议
             * ban_access_msg : 想获得您青睐老师更鑫个人参考建议，请联系客服
             * url : http://gxsp.108tec.com/article/lists/id/jianyi.html
             */

            private String image;
            private String title;
            private String ban_access_msg;
            private String url;
            private List<String> ban_access_role;

            public String getImage() {
                return image;
            }

            public void setImage(String image) {
                this.image = image;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getBan_access_msg() {
                return ban_access_msg;
            }

            public void setBan_access_msg(String ban_access_msg) {
                this.ban_access_msg = ban_access_msg;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }

            public List<String> getBan_access_role() {
                return ban_access_role;
            }

            public void setBan_access_role(List<String> ban_access_role) {
                this.ban_access_role = ban_access_role;
            }
        }
    }
}
