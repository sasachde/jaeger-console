There are 2 services in all to be deployed: service-a and service-b

* The container images for both the apps already built and uploaded to quay.io/sasachde. Dockerfiles are included to built images and upload them to any repo.
* service-a and service-b both have external endpoints.
* Internally service-a talks to service-b and returns a response containing responses from both service-a as well as service-b.
* After deploying the apps to OpenShift we can send traffic to the service-a REST endpoint and be able to see traces inside the Jaeger console.
* Also, we should be able to visualize the traffic inside the kiali console. 
