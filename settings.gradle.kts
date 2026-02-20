rootProject.name = "webbysky-tracker-microservices"

include("eureka-server",
    "user-service",
    "config-server")
include("api-gateway")