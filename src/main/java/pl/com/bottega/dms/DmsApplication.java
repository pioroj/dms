package pl.com.bottega.dms;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;


@SpringBootApplication
public class DmsApplication {

    public static void main(String[] args) {
        /*ApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
        DocumentFlowProcess documentFlowProcess = applicationContext.getBean(DocumentFlowProcess.class);
        documentFlowProcess.create(new CreateDocumentCommand());*/

        ApplicationContext ctx = SpringApplication.run(DmsApplication.class, args);
    }

}
