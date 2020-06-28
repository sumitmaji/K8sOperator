package com.sum.operator.tomcat.crd;

import io.fabric8.kubernetes.api.builder.Function;
import io.fabric8.kubernetes.client.CustomResourceDoneable;

public class DoneableTomcat extends CustomResourceDoneable<Tomcat> {
    public DoneableTomcat(Tomcat resource, Function<Tomcat, Tomcat> function) {
        super(resource, function);
    }
}
