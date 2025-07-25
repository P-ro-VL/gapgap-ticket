package vn.hoangshitposting.gapgapticket.api;

@FunctionalInterface
public interface ApiCallExecutor<T> {
   ApiCallResult<T> call() throws ApiCallException;
}