package com.brokilone.utils;

public class MavenRepoResponseDto {
    private ResponseDto response;

    public void setResponse(ResponseDto response) {
        this.response = response;
    }

    public ResponseDto getResponse() {
        return response;
    }

    @Override
    public String toString() {
        return "MavenRepoResponseDto{" +
                "response=" + response +
                '}';
    }
}
