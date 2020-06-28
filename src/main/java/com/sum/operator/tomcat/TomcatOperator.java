package com.sum.operator.tomcat;

import com.sum.operator.Operator;
import com.sum.operator.tomcat.controller.TomcatController;
import io.fabric8.kubernetes.client.*;
import org.slf4j.Logger;

public class TomcatOperator {
    //    private static transient Logger log = Logger
    public static void main(String[] args) throws InterruptedException {
        ConfigBuilder builder = new ConfigBuilder();
        builder.withUsername("admin");
        builder.withPassword("admin");
        builder.withMasterUrl("https://localhost:6443/");
        builder.withTrustCerts(true);
        builder.withNamespace("default");
        builder.withDisableHostnameVerification(true);
        Config config = builder.build();
        try (KubernetesClient client = new DefaultKubernetesClient(config)) {
            Operator operator = new Operator(client);
            operator.registerController(new TomcatController(), "default");
            while (true) {
                Thread.sleep(99999999);
            }
        }
    }
}
