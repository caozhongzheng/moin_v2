package com.keyboard.bean;

public class EmoticonBean {

    public final static int FACE_TYPE_NOMAL = 0;
    public final static int FACE_TYPE_DEL = 1;
    public final static int FACE_TYPE_USERDEF = 2;

    /**
     * 表情ID
     */
    private String id;

    /**
     * 所属表情专辑ID
     */
    private String parentId;
    /**
     * 表情缩略图标
     */
    private String iconUri;
    /**
     * 表情缩略图标URL
     */
    private String iconUrl;
    /**
     * 表情大图
     */
    private String gifUri;
    /**
     * 表情大图URL
     */
    private String gifUrl;
    /**
     * 描述标签，如大笑、OK、打脸等
     */
    private String tags;
    /**
     * 使用次数
     */
    private int useStat;
    /**
     * 点击处理事件类型 (类型 0 系统表情、、对应这里的2; 1 用户，对应这里的3，还要添加)
     */
    private long eventType;
    /**
     * 内容
     */
    private String content;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getGifUri() {
        return gifUri;
    }

    public void setGifUri(String gifUri) {
        this.gifUri = gifUri;
    }

    public String getGifUrl() {
        return gifUrl;
    }

    public void setGifUrl(String gifUrl) {
        this.gifUrl = gifUrl;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public int getUseStat() {
        return useStat;
    }

    public void setUseStat(int useStat) {
        this.useStat = useStat;
    }

    public long getEventType() { return eventType; }

    public void setEventType(long eventType) { this.eventType = eventType; }

    public String getIconUri() {
        return iconUri;
    }

    public void setIconUri(String iconUri) {
        this.iconUri = iconUri;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public static String fromChars(String chars) { return chars; }

    public static String fromChar(char ch) { return Character.toString(ch); }

    public static String fromCodePoint(int codePoint) { return newString(codePoint); }

    public static final String newString(int codePoint) {
        if (Character.charCount(codePoint) == 1) {
            return String.valueOf(codePoint);
        } else {
            return new String(Character.toChars(codePoint));
        }
    }

    public EmoticonBean(String id, String parentId, String iconUri, String iconUrl, String gifUri, String gifUrl, String tags, int useStat, long eventType, String content) {
        this.id = id;
        this.parentId = parentId;
        this.iconUri = iconUri;
        this.iconUrl = iconUrl;
        this.gifUri = gifUri;
        this.gifUrl = gifUrl;
        this.tags = tags;
        this.useStat = useStat;
        this.eventType = eventType;
        this.content = content;
    }

    public EmoticonBean(){ }

    @Override
    public String toString() {
        return "EmoticonBean{" +
                "id=" + id +
                ", parentId='" + parentId + '\'' +
                ", iconUri='" + iconUri + '\'' +
                ", iconUrl='" + iconUrl + '\'' +
                ", gifUri='" + gifUri + '\'' +
                ", gifUrl='" + gifUrl + '\'' +
                ", tags='" + tags + '\'' +
                ", useStat=" + useStat +
                ", eventType=" + eventType +
                ", content='" + content + '\'' +
                '}';
    }
}
