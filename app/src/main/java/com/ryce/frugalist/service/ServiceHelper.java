package com.ryce.frugalist.service;

/**
 * Created by Tony on 2016-02-07.
 *
 * Http requests & JSON marshalling should go here
 */
public class ServiceHelper {
    private static ServiceHelper ourInstance = new ServiceHelper();

    public static ServiceHelper getInstance() {
        return ourInstance;
    }

    private ServiceHelper() {
    }

}
