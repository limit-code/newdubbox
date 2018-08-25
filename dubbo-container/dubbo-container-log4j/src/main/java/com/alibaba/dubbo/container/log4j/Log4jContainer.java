/*
 * Copyright 1999-2011 Alibaba Group.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.dubbo.container.log4j;

import java.util.Enumeration;
import java.util.Properties;

import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.LogManager;
import org.apache.log4j.PropertyConfigurator;

import com.alibaba.dubbo.common.utils.ConfigUtils;
import com.alibaba.dubbo.container.Container;

/**
 * Log4jContainer. (SPI, Singleton, ThreadSafe)
 * 
 * @author william.liangf
 */
public class Log4jContainer implements Container {

    public static final String LOG4J_FILE = "dubbo.log4j.file";
    public static final String LOG4J_FILE_ONOFF = "dubbo.log4j.file.on-off";

    public static final String LOG4J_LEVEL = "dubbo.log4j.level";
    
    public static final String LOG4J_JMS_ONOFF = "dubbo.log4j.jms.on-off";
    public static final String LOG4J_JMS_PROVIDERURL = "dubbo.log4j.appender.jms.ProviderURL";
    public static final String LOG4J_JMS_TOPICBINDINGNAME = "dubbo.log4j.appender.jms.TopicBindingName";

    public static final String LOG4J_SUBDIRECTORY = "dubbo.log4j.subdirectory";

    public static final String DEFAULT_LOG4J_LEVEL = "ERROR";

    @SuppressWarnings("unchecked")
    public void start() {
        String level = ConfigUtils.getProperty(LOG4J_LEVEL);
        if (level == null || level.length() == 0) {
            level = DEFAULT_LOG4J_LEVEL;
        }
        String jmsOnOff = ConfigUtils.getProperty(LOG4J_JMS_ONOFF);
        String fileOnOff = ConfigUtils.getProperty(LOG4J_FILE_ONOFF);
        boolean isJms = jmsOnOff != null && jmsOnOff.equalsIgnoreCase("ON");
        boolean isFile = true;
        if (fileOnOff != null && fileOnOff.equalsIgnoreCase("OFF")) {
        	isFile = false;
		}
        Properties properties = new Properties();
        if (isFile && isJms) {
        	properties.setProperty("log4j.rootLogger", level + ",CONSOLE, application, jms");
		} else if (isFile) {
			properties.setProperty("log4j.rootLogger", level + ",CONSOLE, application");
		} else if (isJms) {
			properties.setProperty("log4j.rootLogger", level + ",CONSOLE, jms");
		} else {
			properties.setProperty("log4j.rootLogger", level + ",CONSOLE");
		}
        if (isJms) {
        	String jmsProviderURL = ConfigUtils.getProperty(LOG4J_JMS_PROVIDERURL);
        	String jmsTopicBindingName = ConfigUtils.getProperty(LOG4J_JMS_TOPICBINDINGNAME);
			properties.setProperty("log4j.appender.jms", "org.apache.log4j.net.JMSAppender");
            properties.setProperty("log4j.appender.jms.InitialContextFactoryName", "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
            properties.setProperty("log4j.appender.jms.TopicConnectionFactoryBindingName", "ConnectionFactory");
            properties.setProperty("log4j.appender.jms.ProviderURL", jmsProviderURL);
            properties.setProperty("log4j.appender.jms.TopicBindingName", jmsTopicBindingName);
        }
        
        if (isFile) {
        	String file = ConfigUtils.getProperty(LOG4J_FILE);
        	properties.setProperty("log4j.appender.application", "org.apache.log4j.DailyRollingFileAppender");
        	properties.setProperty("log4j.appender.application.File", file);
        	properties.setProperty("log4j.appender.application.Append", "true");
        	properties.setProperty("log4j.appender.application.DatePattern", "'.'yyyy-MM-dd");
        	properties.setProperty("log4j.appender.application.layout", "org.apache.log4j.PatternLayout");
        	properties.setProperty("log4j.appender.application.layout.ConversionPattern", "%d [%t] %-5p %C{6} (%F:%L) - %m%n");
		}
       
        properties.setProperty("log4j.appender.CONSOLE", "org.apache.log4j.ConsoleAppender");
        properties.setProperty("log4j.appender.CONSOLE.Threshold", "INFO");
        properties.setProperty("log4j.appender.CONSOLE.Target", "System.out");
//            properties.setProperty("log4j.appender.CONSOLE.DatePattern", "'.'yyyy-MM-dd");
        properties.setProperty("log4j.appender.CONSOLE.layout", "org.apache.log4j.PatternLayout");
        properties.setProperty("log4j.appender.CONSOLE.layout.ConversionPattern", "%d [%t] %-5p %C{6} (%F:%L) - %m%n");
        
        PropertyConfigurator.configure(properties);
            
        String subdirectory = ConfigUtils.getProperty(LOG4J_SUBDIRECTORY);
        if (subdirectory != null && subdirectory.length() > 0) {
            Enumeration<org.apache.log4j.Logger> ls = LogManager.getCurrentLoggers();
            while (ls.hasMoreElements()) {
                org.apache.log4j.Logger l = ls.nextElement();
                if (l != null) {
                    Enumeration<Appender> as = l.getAllAppenders();
                    while (as.hasMoreElements()) {
                        Appender a = as.nextElement();
                        if (a instanceof FileAppender) {
                            FileAppender fa = (FileAppender)a;
                            String f = fa.getFile();
                            if (f != null && f.length() > 0) {
                                int i = f.replace('\\', '/').lastIndexOf('/');
                                String path;
                                if (i == -1) {
                                    path = subdirectory;
                                } else {
                                    path = f.substring(0, i);
                                    if (! path.endsWith(subdirectory)) {
                                        path = path + "/" + subdirectory;
                                    }
                                    f = f.substring(i + 1);
                                }
                                fa.setFile(path + "/" + f);
                                fa.activateOptions();
                            }
                        }
                    }
                }
            }
        }
    }

    public void stop() {
    }

}