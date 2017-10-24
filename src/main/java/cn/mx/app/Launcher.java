package cn.mx.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan(basePackages = {"io.onceonly.beans","cn.mx.app"})
@EntityScan({"io.onceonly.beans","cn.mx.app"})
@ServletComponentScan
public class Launcher {

	public static ConfigurableApplicationContext CAC = null;
	
	public static void main(String[] args)
	{
		CAC = SpringApplication.run(Launcher.class, args);
		System.out.println(String.join(", ", CAC.getBeanDefinitionNames()));
	}
}
