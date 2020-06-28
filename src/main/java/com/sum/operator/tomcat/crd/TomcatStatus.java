package com.sum.operator.tomcat.crd;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.fabric8.kubernetes.api.model.KubernetesResource;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonDeserialize(using = JsonDeserializer.None.class)
public class TomcatStatus implements KubernetesResource {
    private int availableReplicas;
    private String provisioningStatus;
}
