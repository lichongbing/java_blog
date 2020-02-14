package com.mtons.mblog;


import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

/**
 * SprintBootApplication
 */

@SpringBootApplication
@EnableCaching
public class BootApplication {

    private static final Logger log = LoggerFactory.getLogger(BootApplication.class);
    @Value("${http.port}")
    private Integer port;
    @Value("${server.port}")
    private Integer httpsPort;

    public BootApplication() {
    }

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(BootApplication.class, args);
        String serverPort = context.getEnvironment().getProperty("server.port");
        log.info("mblog started at http://localhost:" + serverPort);
    }

    @Bean
    public ServletWebServerFactory servletContainer() {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory() {
            @Override
            protected void postProcessContext(Context context) {
                // 如果要强制使用https，请松开以下注释
                SecurityConstraint constraint = new SecurityConstraint();
                constraint.setUserConstraint("CONFIDENTIAL");
                SecurityCollection collection = new SecurityCollection();
                collection.addPattern("/*");
                constraint.addCollection(collection);
                context.addConstraint(constraint);
            }
        };
        tomcat.addAdditionalTomcatConnectors(createStandardConnector()); // 添加http
        return tomcat;
    }

    private Connector createStandardConnector() {
        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        connector.setSecure(false);
        connector.setScheme("http");
        connector.setPort(this.port);
        connector.setRedirectPort(this.httpsPort);
        return connector;
    }

}