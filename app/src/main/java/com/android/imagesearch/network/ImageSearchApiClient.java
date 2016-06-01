package com.android.imagesearch.network;

import retrofit.RestAdapter;

public final class ImageSearchApiClient {

    private final static String API_URL = "https://en.wikipedia.org/w";
    private static ImageSearchApiInterface imageSearchApiInterface;

    private ImageSearchApiClient() {
        //Empty Constructor
    }

    /**
     * Create a Rest adapter for api.
     *
     * @return
     */
    private static RestAdapter getRestAdapter() {

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(API_URL)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();

        return restAdapter;
    }

    /**
     * Reteruns rest interface of image search api.
     * @return
     */
    public static ImageSearchApiInterface getImageSearchApi() {
        if (imageSearchApiInterface == null) {
			imageSearchApiInterface = getRestAdapter().create(ImageSearchApiInterface.class);
		}            

        return imageSearchApiInterface;
    }
}
