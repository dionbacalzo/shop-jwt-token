package com.shop.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ReadPreference;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;

@Configuration 
@ComponentScan(basePackages = {"com.shop.controller", "com.shop.service"})
@EnableWebMvc
@EnableSpringDataWebSupport
@PropertySource(value= "classpath:/mongo.properties")
@EnableMongoRepositories(basePackages = "com.shop.dao")
@Import({ SecurityConfig.class })
public class AppConfig extends AbstractMongoConfiguration implements WebMvcConfigurer {

	@Autowired
	Environment env;
	
	@Override
	public MongoClient mongoClient() {
		MongoClientOptions.Builder builder =  new MongoClientOptions.Builder();
		builder.connectionsPerHost(50);
		builder.writeConcern(WriteConcern.JOURNALED);
		builder.readPreference(ReadPreference.secondaryPreferred());
		MongoClientOptions options = builder.build();
		//if you want your db to be accessed remotely then at bin/mongod.cfg, change bindIp to 0.0.0.0
		MongoClient mongoClient = new MongoClient(new ServerAddress(env.getProperty("mongo.server"), Integer.parseInt( env.getProperty("mongo.port"))), options);
		return mongoClient;
		//return new MongoClient("localhost", 27017);
	}
	/*
	@Bean
	public ViewResolver getViewResolver() {
		InternalResourceViewResolver resolver = new InternalResourceViewResolver();
		resolver.setPrefix("/WEB-INF/views/");
		resolver.setSuffix(".jsp");
		return resolver;
	}
	
	
	@Bean
    public TilesConfigurer tilesConfigurer() {
        TilesConfigurer tilesConfigurer = new TilesConfigurer();
        tilesConfigurer.setDefinitions(
          new String[] { "/WEB-INF/tiles.xml" });
        tilesConfigurer.setCheckRefresh(true);
         
        return tilesConfigurer;
    }
     
    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        TilesViewResolver viewResolver = new TilesViewResolver();
        registry.viewResolver(viewResolver);
    }
    */
    @Override
	public void addResourceHandlers(final ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/resources/**").addResourceLocations("/resources/");
	}

	/*
	@Override
	public MongoDbFactory mongoDbFactory() {
		MongoDbFactory mongoDbFactory = new SimpleMongoDbFactory(mongoClient(),
		env.getProperty("mongo.databaseName"));

		return mongoDbFactory;
	}
	
	@Override
	public MongoTemplate mongoTemplate() {
        MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory());
        return mongoTemplate;
    }

	*/

	@Override
	protected String getDatabaseName() {
		return env.getProperty("mongo.databaseName");
	}  
} 