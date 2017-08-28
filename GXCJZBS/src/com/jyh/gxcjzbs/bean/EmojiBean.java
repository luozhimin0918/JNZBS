package com.jyh.gxcjzbs.bean;

/**
 * 项目名称：com.jyh.jyh007.live.bean
 * 类描述：
 * 创建人：Administrator
 * 创建时间：2016/9/917:35
 * 修改人：Administrator
 * 修改时间：2016/9/917:35
 * 修改备注：
 */
public class EmojiBean {
//    name,type,image,path
    private String name;
    private String type;
    private String image;
    private String path;

    @Override
    public String toString() {
        return "EmojiBean{" +
                "image='" + image + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", path='" + path + '\'' +
                '}';
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public EmojiBean() {

    }

    public EmojiBean(String image, String name, String path, String type) {

        this.image = image;
        this.name = name;
        this.path = path;
        this.type = type;
    }
}
