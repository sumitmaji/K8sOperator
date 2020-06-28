package com.sum.operator.tomcat.controller;

import com.sum.operator.Context;
import com.sum.operator.api.Controller;
import com.sum.operator.api.ResourceController;
import com.sum.operator.tomcat.crd.DoneableTomcat;
import com.sum.operator.tomcat.crd.Tomcat;
import com.sum.operator.tomcat.crd.TomcatList;
import com.sum.operator.tomcat.crd.TomcatStatus;
import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import io.fabric8.kubernetes.api.model.extensions.HTTPIngressRuleValueBuilder;
import io.fabric8.kubernetes.api.model.extensions.Ingress;
import io.fabric8.kubernetes.api.model.extensions.IngressBackendBuilder;
import io.fabric8.kubernetes.api.model.extensions.IngressBuilder;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Optional;

@Controller(customResourceClass = Tomcat.class,
        kind = TomcatController.KIND,
        group = TomcatController.GROUP,
        customResourceListClass = TomcatList.class,
        customResourceDonebaleClass = DoneableTomcat.class,
        version = TomcatController.VERSION,
        crdName = "tomcat.paas.k8s.io",
        scope = "Namespaced",
        plural = "tomcats",
        defaultFinalizer = "tomcat.finalizer.k8s.io"

)
public class TomcatController implements ResourceController<Tomcat> {
    static final String KIND = "tomcat";
    static final String GROUP = "paas.k8s.io";
    static final String VERSION = "v1alpha1";
    @Override
    public boolean deleteResource(Tomcat resource, Context<Tomcat> context) {
        Boolean delDep = context.getK8sClient().apps().deployments()
                .inNamespace(resource.getMetadata().getNamespace())
                .withName(resource.getMetadata().getName())
                .delete();

        Boolean delService = context.getK8sClient().services()
                .inNamespace(resource.getMetadata().getNamespace())
                .withName(resource.getMetadata().getName())
                .delete();

        Boolean delIng = context.getK8sClient().extensions().ingresses()
                .inNamespace(resource.getMetadata().getNamespace())
                .withName(resource.getMetadata().getName())
                .delete();
        Boolean delConfigMap = context.getK8sClient().configMaps()
                .inNamespace(resource.getMetadata().getNamespace())
                .withName(resource.getMetadata().getNamespace())
                .delete();

        return true;
    }

    @Override
    public Optional<Tomcat> createOrUpdateResource(Tomcat resource, Context<Tomcat> context) {
        String namespace = resource.getMetadata().getNamespace();
        try{

            int servicePort = 82;
            int containerPort = 8080;
            String content = new String(
                    Files.readAllBytes((Paths.get(
                            TomcatController.class.getResource("catalina.properties").toURI())
                    ))
            );
            ConfigMap configMap = new ConfigMapBuilder().withNewMetadata()
                    .withName(resource.getMetadata().getName())
                    .endMetadata().addToData("catalina.properties", content).build();

            configMap = context.getK8sClient().configMaps().inNamespace(namespace)
                    .createOrReplace(configMap);

            Deployment deployment = new DeploymentBuilder().withNewMetadata()
                    .withName(resource.getMetadata().getNamespace())
                    .withLabels(resource.getMetadata().getLabels()).endMetadata()
                    .withNewSpec().withReplicas(1)
                    .withNewSelector().addToMatchLabels(resource.getMetadata().getLabels()).endSelector()
                    .withNewTemplate().withNewMetadata().addToLabels(resource.getMetadata().getLabels())
                    .endMetadata()
                    .withNewSpec().addNewContainer().withName("tomcat")
                    .withImage(resource.getSpec().getDockerImage())
                    .addNewPort().withContainerPort(containerPort).endPort()
                    .withImagePullPolicy("Never")
                    .addNewVolumeMount().withName("properties")
                    .withMountPath("/usr/local/tomcat/conf/catalina.properties")
                    .withSubPath("catalina.properties")
                    .endVolumeMount().endContainer().addNewVolume()
                    .withName("properties")
                    .withConfigMap(new ConfigMapVolumeSourceBuilder()
                            .withName(resource.getMetadata().getName()).build())
                    .endVolume().endSpec().endTemplate()
                    .endSpec().build();


            deployment = context.getK8sClient().apps().deployments()
                    .inNamespace(namespace).createOrReplace(deployment);

            Service service = new ServiceBuilder().withNewMetadata()
                    .withName(resource.getMetadata().getName())
                    .endMetadata().withNewSpec()
                    .withSelector(resource.getMetadata().getLabels())
                    .addNewPort().withName("ui").withPort(servicePort)
                    .withTargetPort(new IntOrString(containerPort)).endPort()
                    .endSpec().build();

            service = context.getK8sClient().services().inNamespace(namespace).createOrReplace(service);

            Ingress ing = new IngressBuilder().withNewMetadata()
                    .withName(resource.getMetadata().getName())
                    .withAnnotations(Collections.singletonMap("nginx.org/rewrites",
                            String.format("serviceName=%s rewrite=/%s/", resource.getMetadata().getName(),
                                    resource.getSpec().getContext())))
                    .endMetadata().withNewSpec().addNewRule()
                    .withHttp(new HTTPIngressRuleValueBuilder().addNewPath()
                    .withBackend(new IngressBackendBuilder()
                            .withServiceName(resource.getMetadata().getName())
                    .withServicePort(new IntOrString(servicePort)).build()).
                            withPath(String.format("/%s/", resource.getSpec()
                                    .getContext())).endPath().build())
                    .endRule().endSpec().build();

            ing = context.getK8sClient().extensions().ingresses()
                    .inNamespace(namespace).createOrReplace(ing);

            TomcatStatus status = new TomcatStatus();
            status.setProvisioningStatus("Running");
            resource.setStatus(status);

            return Optional.of(resource);

        }catch (Exception e){
            e.printStackTrace();
        }

        return Optional.of(resource);
    }
}
