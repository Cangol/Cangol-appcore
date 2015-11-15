package com.example.appcore.api;

import mobi.cangol.mobile.api.Result;

/**
 * Created by weixuewu on 15/11/15.
 */
public class BinaryResult<T> extends Result {

    @Override
    public Result<T> parserResult(Class c, Object response, String root) {
        byte[] aa= (byte[]) response;

        return null;
    }
}
