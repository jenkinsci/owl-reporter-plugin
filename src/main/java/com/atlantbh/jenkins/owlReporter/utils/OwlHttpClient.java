package com.atlantbh.jenkins.owlReporter.utils;

import okhttp3.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;

public class OwlHttpClient {

    private MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private String owlUrl;
    private OkHttpClient client;

    private final static String CREATE_RUN = "/api/v1/test-runs";
    private final static String UPLOAD_XML = "/api/v1/test-runs/%d/test-cases/junit-xml-report";

    public OwlHttpClient(String owlUrl) {
        this.owlUrl = owlUrl;
        this.client = new OkHttpClient();
    }

    public Long createTestRun(String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(owlUrl + CREATE_RUN)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            JSONParser parser = new JSONParser();
            try {
                JSONObject testRun = (JSONObject) parser.parse(response.body().string());
                return (Long) testRun.get("id");
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public Integer uploadXml(File file, Long testRunId) throws IOException {
        RequestBody requestBody = new MultipartBody.Builder()
                .addFormDataPart("file", file.getName(),
                        RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), file))
                .build();

        Request request = new Request.Builder()
                .url(owlUrl + String.format(UPLOAD_XML, testRunId))
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.code();
        }
    }
}
