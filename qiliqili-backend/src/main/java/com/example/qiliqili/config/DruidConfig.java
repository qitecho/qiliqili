//package com.example.qiliqili.config;
//import com.alibaba.druid.spring.boot3.autoconfigure.DruidDataSourceBuilder;
//import com.alibaba.druid.support.http.StatViewServlet;
//import com.alibaba.druid.support.http.WebStatFilter;
//import org.springframework.boot.web.servlet.FilterRegistrationBean;
//import org.springframework.boot.web.servlet.ServletRegistrationBean;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import javax.sql.DataSource;
//
//@Configuration
//public class DruidConfig {
//
//    // 自定义数据源（可选，通常由 application.yml 自动配置）
//    @Bean
//    public DataSource druidDataSource() {
//        return DruidDataSourceBuilder.create().build();
//    }
//
//    // 配置 Druid 监控 Servlet
//    @Bean
//    public ServletRegistrationBean<StatViewServlet> statViewServlet() {
//        ServletRegistrationBean<StatViewServlet> bean =
//                new ServletRegistrationBean<>(new StatViewServlet(), "/druid/*");
//
//        bean.addInitParameter("loginUsername", "admin");
//        bean.addInitParameter("loginPassword", "admin123");
//        bean.addInitParameter("allow", ""); // 留空允许所有 IP
//        bean.addInitParameter("resetEnable", "false");
//
//        return bean;
//    }
//
//    // 配置 Web 统计 Filter
//    @Bean
//    public FilterRegistrationBean<WebStatFilter> webStatFilter() {
//        FilterRegistrationBean<WebStatFilter> bean =
//                new FilterRegistrationBean<>(new WebStatFilter());
//
//        bean.addUrlPatterns("/*");
//        bean.addInitParameter("exclusions", "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");
//
//        return bean;
//    }
//}