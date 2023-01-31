package com.sum.operator.tomcat.crd;

import io.fabric8.kubernetes.client.CustomResource;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Tomcat extends CustomResource {

    private TomcatSpec spec;
    private TomcatStatus status;

}
