package com.brokilone.utils;

public class DocDto {
    private String g;
    private String a;
    private String v;

    public String getG() {
        return g;
    }

    public String getA() {
        return a;
    }

    public void setA(String a) {
        this.a = a;
    }

    public String getV() {
        return v;
    }

    public void setV(String v) {
        this.v = v;
    }

    @Override
    public String toString() {
        return "DocDto{" +
                "groupId='" + g + '\'' +
                ", artifactId='" + a + '\'' +
                ", lastVersion='" + v + '\'' +
                '}';
    }
}
