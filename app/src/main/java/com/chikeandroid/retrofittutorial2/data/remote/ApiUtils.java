package com.chikeandroid.retrofittutorial2.data.remote;

/**
 * Created by Chike on 12/4/2016.
 */

public class ApiUtils {

    private ApiUtils() {}

    public static final String BASE_URL = "http://jsonplaceholder.typicode.com/";

    public static APIService getAPIService() {

        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }
}
