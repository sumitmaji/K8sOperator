package com.sum.operator.api;

import com.sum.operator.Context;
import io.fabric8.kubernetes.client.CustomResource;

import java.util.Optional;

public interface ResourceController<R extends CustomResource> {

    /**
     * The implementation should delete the associated component(s). Note that this is method is called when an object
     * is marked for deletion. After its executed the default finalizer is automatically removed by the framework;
     * unless the return value is false - note that this is almost never the case.
     *
     * @param resource
     * @param context
     * @return true - so the finalizer is automatically removed after the call.
     * false if you don't want to remove the finalizer. Note that this is ALMOST NEVER the case.
     */
    boolean deleteResource(R resource, Context<R> context);

    /**
     * The implementation of this operation is required to be idempotent.
     *
     * @return The resource is updated in api server if the return value is present
     *  within Optional. This the common use cases. However in cases, for example the operator is restarted,
     *  and we don't want to have an update call to k8s api to be made unnecessarily, by returning an empty Optional
     *  this update can be skipped.
     *  <b>However we will always call an update if there is no finalizer on object and its not marked for deletion.</b>
     */
    Optional<R> createOrUpdateResource(R resource, Context<R> context);

}
