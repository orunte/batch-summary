package com.example.batchsummary;

import freemarker.template.Configuration;
import freemarker.template.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

@Component
public class BatchSummaryListener extends JobExecutionListenerSupport {

    private final Logger logger = LoggerFactory.getLogger(BatchSummaryListener.class);

    @Override
    public void afterJob(JobExecution jobExecution) {
        logger.info("Creating job summary");
        try {
            Configuration freemarkerConfig = new Configuration(Configuration.VERSION_2_3_31);
            freemarkerConfig.setDirectoryForTemplateLoading(ResourceUtils.getFile("classpath:templates"));
            freemarkerConfig.setDefaultEncoding("UTF-8");
            Template template = freemarkerConfig.getTemplate("summary.ftl");
            Map<String, Object> templateData = new HashMap<>();
            templateData.put("jobExecution", jobExecution);
            try (StringWriter out = new StringWriter()) {
                template.process(templateData, out);
                System.out.println(out.getBuffer().toString());
                out.flush();
            }
        } catch (Exception ex) {
            logger.error("Couldn't create summary", ex);
        }
    }
}
