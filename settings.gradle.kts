pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name = "webbysky-tracker-microservices"

/*include("eureka-server")
include("user-service")
include("metrics-service")
include("sender-to-email-service")
include("ai-service")
include("config-server")
include("api-gateway")*/



include("eureka-server",
    "user-service",
    "config-server",
    "ai-service")
include("api-gateway")


