# M-Health Service Setup Guide

## Project Overview
This project is regarding the apis needed for admin portal

## Prerequisites

- **Java 17**
- **Spring Boot 3.1.7**
- **Maven >= 3.9.2**
- **Spring Cloud 2022.0.5**
- **MySQL**
- **Eureka Cloud Server**

---

## Service Configuration

### 1. Update _Service Properties_ File

```properties

# Database
spring.datasource.url=jdbc:mysql://localhost:3306/mhealth_devtest
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.username=root
spring.datasource.password=root

#Eureka
eureka.client.register-with-eureka=true
eureka.client.fetch-registry=true
eureka.client.service-url.defaultZone=http://localhost:6761/eureka/
eureka.instance.hostname=localhost
eureka.instance.prefer-ip-address=true
```

### 2. Update _API Gateway_ Properties File
```properties
spring.cloud.gateway.routes[{NEXT-INDEX}].id={CAPITAL-NAME}
spring.cloud.gateway.routes[{NEXT-INDEX}].uri=http://localhost:{next-available-port}
spring.cloud.gateway.routes[{NEXT-INDEX}].predicates[0]=Path=/{service-initial-path}/**
```

### 3. Deployment Script Configuration
- Add new service configuration in the deployment script.

### 4. Create Folder on Production Server
- **Path:** `/home/core/mpatient/admin`

### 5. Update _pom.xml_ for Production Profile
```xml
<profile>
  <id>prod</id>
  <properties>
    <application.properties.location>/home/core/mpatient/admin/application.properties</application.properties.location>
  </properties>
</profile>
```

---

## Additional Notes
- Ensure the correct values are substituted for placeholders like `{next-available-port}`, `{CAPITAL-NAME}`, `{service-initial-path}`, and `{name}` before deployment.
- Validate configuration files before running the service.

**Initial Release Version:** 0.0.0.0

