package com.brokilone.utils;

import java.util.List;

public class ResponseDto {
    List<DocDto> docs;

    public void setDocs(List<DocDto> docs) {
        this.docs = docs;
    }

    public List<DocDto> getDocs() {
        return docs;
    }

    @Override
    public String toString() {
        return "ResponseDto{" +
                "docs=" + docs +
                '}';
    }
}
