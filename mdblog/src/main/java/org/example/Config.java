package org.example;

public class Config {
    private String host;
    private String web;
    private String blog;
    private int port;
    private String theme;

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getWeb() {
        return web;
    }

    public void setWeb(String web) {
        this.web = web;
    }

    public String getBlog() {
        return blog;
    }

    public void setBlog(String blog) {
        this.blog = blog;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return "Config{" +
                "host='" + host + '\'' +
                ", web='" + web + '\'' +
                ", blog='" + blog + '\'' +
                ", port=" + port +
                ", theme='" + theme + '\'' +
                '}';
    }
}
