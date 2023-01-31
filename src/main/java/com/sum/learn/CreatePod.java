package com.sum.learn;

import io.fabric8.kubernetes.api.model.DoneablePod;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.client.*;
import io.fabric8.kubernetes.client.dsl.NonNamespaceOperation;
import io.fabric8.kubernetes.client.dsl.PodResource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

public class CreatePod {

  private static final Logger logger = LogManager.getLogger(CreatePod.class);

  public static void main(String[] args) {
    if (args.length == 0) {
      logger.error("Usage: podJsonFileName <token> <namespace>");
      return;
    }
    String fileName = args[0];
    String namespace = null;
    if (args.length > 2) {
      namespace = args[2];
    }

    File file = new File("/home/sumit/Documents/IdeaProjects/K8sOperator/src/main/resources/pod.yaml");
    if (!file.exists() || !file.isFile()) {
      logger.error("File does not exist: " + fileName);
      return;
    }

    ConfigBuilder builder = new ConfigBuilder();
    if (args.length > 1) {
      builder.withOauthToken(args[1]);
    }
    Config config = builder.build();
    config.setCaCertData("LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tCk1JSUMvakNDQWVhZ0F3SUJBZ0lCQURBTkJna3Foa2lHOXcwQkFRc0ZBREFWTVJNd0VRWURWUVFERXdwcmRXSmwKY201bGRHVnpNQjRYRFRJeU1Ea3lNakF6TXprME5Gb1hEVE15TURreE9UQXpNemswTkZvd0ZURVRNQkVHQTFVRQpBeE1LYTNWaVpYSnVaWFJsY3pDQ0FTSXdEUVlKS29aSWh2Y05BUUVCQlFBRGdnRVBBRENDQVFvQ2dnRUJBTVRUCitqenlPNzg2MmhNbG5Nc3Y3Y0VpM1J1WDhzVGgzU2IwcEpBOGVwSzYrR3lSOVBCN3lMUjAyRllQUkoxRjh2dEIKcUt2dGJQYnR3VkZKL3c0N3B4ZGU4ZzU4VnFSemloTWl5V2lpcHNwSUJKeEZjWG5Pb3Q1cnJVQ1lISU5hNlFKeApOMEc1eWpHTFBiNG1PTlpJVk9JVjlVR0JMM04yemJJY1BzMnBzbVRib1pkblJMWlVCQm9DWGtQMDRJazk5ODgvCjVlZDJlT2hPMFk0UktwV2xRaVVqWmRybEdhTW1TZDBrSHAzV3ZaSXphYmdxQVIrSWpyN1JCWFc0TU9abytIdjEKU0t6UDJIWFZmdTlla1FvNTF0RHpJdm1Mck9Bb0tLN3l5czNST1Zhckl1MGxSdStKTmRVN3A5L0dxRFJib2wzbApMS1BFQnorR24zYVVWRkdsNmk4Q0F3RUFBYU5aTUZjd0RnWURWUjBQQVFIL0JBUURBZ0trTUE4R0ExVWRFd0VCCi93UUZNQU1CQWY4d0hRWURWUjBPQkJZRUZFb3hDZUs4M1hYNkdWMU1xSXpLSzV1UE42aXlNQlVHQTFVZEVRUU8KTUF5Q0NtdDFZbVZ5Ym1WMFpYTXdEUVlKS29aSWh2Y05BUUVMQlFBRGdnRUJBRTd0ZS81NjZvWE5RK203Yi9IRgpCbTcvNm9ZVTRQUXdCVm9wM25EZHFtWHkvS3JTdmhaay92V3laY2hTK1MyeUJrdWw2bFNxV3dkNTcxZit1ZGVuCnRyeTQ0Qytvemovd09MaXJBZmdEd2ZmRDN4c1N5ZUhGV1JYb3F0Smx1emViVHNMcUVDODlXZlFxSzFNdkFPUWgKOXF5ZUthRzZhK2gxWnZOUTE0UGoyOHlhOU1zc1ZRZ1VYcDJXZkJyNWloOFRJT2Z6R2lrTHlkcW1QK0djKzdFMQpML3ZsTjZJN2NscTBlcHNFZGhXSEUvSjhXUXdtcWR0U2NiNTZiL0RWWDQzZTlDTzVOcmdPdHNpK1hMVFVmdE53ClNhU29hcEcxclhCcmM3bUZsM2h2b2NaTEFVRWpsNU4wL1RpSUhhQ3g5dlR2TEdtQnRLMURSWWQ0QnIyTlFJMUIKUlZjPQotLS0tLUVORCBDRVJUSUZJQ0FURS0tLS0tCg==");
    config.setClientCertData("LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tCk1JSURJVENDQWdtZ0F3SUJBZ0lJV2tBS05vRmRMYUV3RFFZSktvWklodmNOQVFFTEJRQXdGVEVUTUJFR0ExVUUKQXhNS2EzVmlaWEp1WlhSbGN6QWVGdzB5TWpBNU1qSXdNek01TkRSYUZ3MHlNekE1TWpJd016TTVORGxhTURReApGekFWQmdOVkJBb1REbk41YzNSbGJUcHRZWE4wWlhKek1Sa3dGd1lEVlFRREV4QnJkV0psY201bGRHVnpMV0ZrCmJXbHVNSUlCSWpBTkJna3Foa2lHOXcwQkFRRUZBQU9DQVE4QU1JSUJDZ0tDQVFFQXlEdXZzQlY0R3hkM3pvamYKbXlRdHRpZ3RHa1JqOWxwQUh3dVY5Um85blNidUIxdnI5cnlza2NRR3hUaWNvblRGVFZZZzNhTW9kR2Uyc3I3eApIM3c3KzIwVHZyVVAwRzQvelpKYkxsaTJwU2NJUUUzcTVGa0E5YUM2VnQrQjhzdFBGaFMrRVdabWJtMU5MQ0JXCnZEZ0pCR3hZc2xIV3NSdmE1Z1lLYm5YSE9aa1F1a0RzTkc3dWxpZGpzTENIK3MvcUdPVS9rN2VKdHMvNzZmbHAKSTU3MjJWWENuci9CT1l4U0VESjBXaHJTME44dkVhRzE3OVlpUzB0WmNVV0taU3ZVbjRiSmRjQ3l3VE9IS0FyQwprTTUrazhlS1F0cGRHVi9uaTdiU0ZWdk9halo1V1kxRFFuMVJKM0taWXNQWi9CNThqbkRVYVc1NGFDOE9LNUYyCmhRaWtFd0lEQVFBQm8xWXdWREFPQmdOVkhROEJBZjhFQkFNQ0JhQXdFd1lEVlIwbEJBd3dDZ1lJS3dZQkJRVUgKQXdJd0RBWURWUjBUQVFIL0JBSXdBREFmQmdOVkhTTUVHREFXZ0JSS01Rbml2TjExK2hsZFRLaU15aXVianplbwpzakFOQmdrcWhraUc5dzBCQVFzRkFBT0NBUUVBaW1JZU15YThCUHFMbm0vekp2Y2Q5SklIbjR5OVF1WVBkMGRpCmtaanNKTnQxcmJEWW41aDEySFVZZENXSTVEVlhjalB5WXZ2elpOKysxaGRWUmxteC9ueG1kcW01dXhlb3VNWjkKMEtCcmRvV3lQdDdYK0ZqdjgyYWtLWTA5azlGQlZQNHNjQ3dCaTAzQjZ0YmxQbkl2b2pNYUhURktidWFhc2EyMwprOEszaDhUdGVlSVhESTNDZFd2cTRVZk9tUVphcHpQa3dHOTZkSTlRenYyR1BQb0dPZFBhRHhVZEZZUzhWYUt1ClRIUVdUdTJYOE0yMDkwMk5SWHE4YnNxWXJvZ0dnMlZ6SnhUY0tyWFJFeXcwckJVRnNkd1ExRisvL3Z2L0JVaHcKK2UvNWFiOTBpYUF2ek5nc04yMHpoa3NONUVZbFhtOW01M3dFc1B0MkxkQ3JadGVQZFE9PQotLS0tLUVORCBDRVJUSUZJQ0FURS0tLS0tCg==");
    config.setClientKeyData("LS0tLS1CRUdJTiBSU0EgUFJJVkFURSBLRVktLS0tLQpNSUlFb3dJQkFBS0NBUUVBeUR1dnNCVjRHeGQzem9qZm15UXR0aWd0R2tSajlscEFId3VWOVJvOW5TYnVCMXZyCjlyeXNrY1FHeFRpY29uVEZUVllnM2FNb2RHZTJzcjd4SDN3NysyMFR2clVQMEc0L3paSmJMbGkycFNjSVFFM3EKNUZrQTlhQzZWdCtCOHN0UEZoUytFV1ptYm0xTkxDQld2RGdKQkd4WXNsSFdzUnZhNWdZS2JuWEhPWmtRdWtEcwpORzd1bGlkanNMQ0grcy9xR09VL2s3ZUp0cy83NmZscEk1NzIyVlhDbnIvQk9ZeFNFREowV2hyUzBOOHZFYUcxCjc5WWlTMHRaY1VXS1pTdlVuNGJKZGNDeXdUT0hLQXJDa001K2s4ZUtRdHBkR1Yvbmk3YlNGVnZPYWpaNVdZMUQKUW4xUkozS1pZc1BaL0I1OGpuRFVhVzU0YUM4T0s1RjJoUWlrRXdJREFRQUJBb0lCQUV4aWdKWXl5bnZEU3lBTwpGUWRSR09NWmxvTWp2ZUtYOFVnbW1sRkRibWZLRnhuVmxrR3RIa1FKUE8vMXRjTS91cGpURjN5VnBuUXBzSWVsCjBjUktGM1BGMjdkdnlSV2hTVk81MW5EcXhOcGg5b0xaZHBjZjRQN1REdUxmSnVxaFpvMHM0b2t1d0RxMkZhK1UKc3FIZzJvZkdjVnV0WHJKM3h0empSdzEyZnNiWWtJRlFPeGRFQUF5dzZlTTBEcHIyTHpGL0hyNitoZjZhZEtVQQo3bWkrWjNWV0FmQWdML0tSM0o1M21TQ3FJUGp4MnYzbzhzNmthU3R2SWtPS29ybFJBdjVncDJHR2NNZVpzUXI0CnBFVWNCOEdab296UHUzNEJ5Z2pxdytoT2xRMjJqNnUvYkd3dFhRS0hpKzd5N1I4ZG0wWmFpZ3h5VnZ0VDhrc2MKRDZ0bnB3a0NnWUVBeUZpWFJhY3NWVzJjUUovSWdDdzRvdG9MRDFsZ2hWbzBvbzliS3FicStFZGptWmc3WW13QwpRamtSNUJYVE51TmtqbE03U1J2d1ZYcmtPY083aEdLN3B1U3djUXAyKzFianBQeFVmYTRXb3NuYWpLWkJyVDdOClVONENXa2tndkhNMzBmQlRYOEJpN2V0R2QvamZWczZVR2s1dEhQRkRPeUJHamFYZEpnZUxqbjBDZ1lFQS85c1EKNUdvUTFLU3dUUDczbUx6clo3cVFWb1o5THNNNU1KWGhCY0M4b25KNWszYnlUVVBVT2EwS1RvdmNzT1hIMWNibwo2VDR4VHdoVUQ0c3Fkd09lVTBaQXYxaDZ5cXp2clFwODNReHBKZS83ZDhreWU0TDlra2ROQk9pcE8vYnY3NHU0CjYxaDRtYVk2ZGhISzZhZkF1MFlCcjJQNDhzZngzV3BQVURnY3NjOENnWUEreE9yWUt0Rk1EcVIyNXRqVnQ0VWoKR0xlQVhwdE1ESUFUTW1ENDB3VXBOb3l4RWE0Qlh4K3lDZGxlSWF4a05RaExReTUrOEY3ekdTYXFKblg0dTBwWgp0cXJGTCtjcFJnYmFxU2Y3bkhDQkhrYjM3QnNGb1hpSVRwWmtIU2RCZzhQSVpjaHBab2hZVEc5dXBTMnNMOXJkCkJ4ZE9peFJxVU9scDliNTg3TDQvclFLQmdRRDlaVzQrZXBtK3NIajNXTkhKa05tb2V3cDIxMjNsd1czRG83MGcKSUhueWZXUFJXODJ3SGt4eTd1RTFWNm16L1c5RVYyTFhsa1BwblRyN3hmM0JyM0MvMHVWUFhRY0g4ZXpTT1gvZQp3V3JtS3Q2b0RUa1ZRRzk0ejJzRlFKUlRra2FodllUZ2VWZS8vUFVMZUtXNWNlNGN5OEhoZmliUjlKcWppcVdRCnNuSWcyd0tCZ0Z5TzViZitwWkk0UGQ0b09DcmNOelVDMXIvbUordUtpbEduL3NVVTNlY2d2WWpvbTk1TGUxbnIKSTBVRFdITlV3TDM3ZWRwWmZSVnoxQWphTEt5RFgzVjk3NC80M0dmNlVkYWRsRDRWSnNNRHJNYWFScWRoc2QvcgptQk5xb2xqVXNRUi9sVUFzZWR3M0d5Ky93c2xIYWMwSEhoK0t3eUdrUHV0MXpyK3ZEcEUyCi0tLS0tRU5EIFJTQSBQUklWQVRFIEtFWS0tLS0tCg==");
    config.setMasterUrl("https://192.168.0.123:6643");
    config.setNamespace("default");
    try (final KubernetesClient client = new DefaultKubernetesClient(config)) {
      if (namespace == null) {
        namespace = client.getNamespace();
      }

      List<HasMetadata> resources = client.load(new FileInputStream(file)).get();
      if (resources.isEmpty()) {
        logger.error("No resources loaded from file: " +fileName);
        return;
      }
      HasMetadata resource = resources.get(0);
      if (resource instanceof Pod){
        Pod pod = (Pod) resource;
        logger.info("Creating pod in namespace " + namespace);
        logger.info("Creating");
        NonNamespaceOperation<Pod, PodList, DoneablePod, PodResource<Pod, DoneablePod>> pods = client.pods().inNamespace(namespace);
        Pod result = pods.create(pod);
        logger.info("Created pod " + result.getMetadata().getName());
      } else {
        logger.error("Loaded resource is not a Pod! " + resource);
      }
    } catch (KubernetesClientException e) {
      logger.error(e.getMessage(), e);
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
    }
  }

}
