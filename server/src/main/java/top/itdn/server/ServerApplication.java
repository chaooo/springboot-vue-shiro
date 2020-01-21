package top.itdn.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.ConfigurableEnvironment;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Description
 *
 * @author : Charles
 * @date : 2020/1/17
 */
@Slf4j
@SpringBootApplication
/*@ComponentScan(basePackages = "top.itdn.server.dao")*/
public class ServerApplication {

    public static void main(String[] args) throws UnknownHostException {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(ServerApplication.class, args);
        ConfigurableEnvironment environment = applicationContext.getEnvironment();
        String host = InetAddress.getLocalHost().getHostAddress();
        String port = environment.getProperty("server.port");
        log.info("\n----------------------------------------------------------\n\t" +
                "Application is running! Access URLs:\n\t" +
                "Local: \t\thttp://localhost:" + port + "/\n\t" +
                "External: \thttp://" + host + ":" + port + "/\n\t" +
                "----------------------------------------------------------");
    }
}
