package com.njit.buddy.application.network.task;

/**
 * @author toyknight 11/9/2015.
 */
public interface ResponseHandler<T> {

    void onSuccess(T result);

    void onFail(int error_code);

}
