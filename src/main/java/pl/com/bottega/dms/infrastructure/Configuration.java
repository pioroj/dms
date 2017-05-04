package pl.com.bottega.dms.infrastructure;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import pl.com.bottega.dms.application.DocumentCatalog;
import pl.com.bottega.dms.application.DocumentFlowProcess;
import pl.com.bottega.dms.application.ReadingConfirmator;
import pl.com.bottega.dms.application.impl.StandardDocumentFlowProcess;
import pl.com.bottega.dms.application.impl.StandardReadingConfirmator;
import pl.com.bottega.dms.application.user.AuthProcess;
import pl.com.bottega.dms.application.user.CurrentUser;
import pl.com.bottega.dms.application.user.UserRepository;
import pl.com.bottega.dms.application.user.impl.StandardAuthProcess;
import pl.com.bottega.dms.application.user.impl.StandardCurrentUser;
import pl.com.bottega.dms.model.DocumentFactory;
import pl.com.bottega.dms.model.DocumentRepository;
import pl.com.bottega.dms.model.DocumentStatus;
import pl.com.bottega.dms.model.numbers.*;
import pl.com.bottega.dms.model.printing.*;
import pl.com.bottega.dms.model.validation.*;

import java.util.concurrent.Executor;

@org.springframework.context.annotation.Configuration
@EnableAsync
public class Configuration extends AsyncConfigurerSupport {

    @Bean
    public DocumentFlowProcess documentFlowProcess(NumberGenerator numberGenerator,
                                                   PrintCostCalculator printCostCalculator,
                                                   DocumentRepository documentRepository,
                                                   CurrentUser currentUser,
                                                   ApplicationEventPublisher publisher,
                                                   DocumentFactory documentFactory,
                                                   DocumentValidator validator
    ) {
        return new StandardDocumentFlowProcess(numberGenerator, printCostCalculator,
                documentRepository, currentUser, publisher, documentFactory, validator);
    }

    @Bean
    public NumberGenerator numberGenerator(
            @Value("${sandbox.qualitySystem}") String qualitySystem,
            Environment env
    ) {
        NumberGenerator base;
        if (qualitySystem.equals("ISO")) {
            base = new ISONumberGenerator();
        } else if (qualitySystem.equals("QEP")) {
            base = new QEPNumberGenerator();
        } else {
            throw new IllegalArgumentException("Unknown quality system");
        }
        if (hasProfile("demo", env))
            base = new DemoNumberGenerator(base);
        if (hasProfile("audit", env))
            base = new AuditNumberGenerator(base);
        return base;
    }

    private boolean hasProfile(String profile, Environment enviroment) {
        for (String activeProfile : enviroment.getActiveProfiles()) {
            if (activeProfile.equals(profile))
                return true;
        }
        return false;
    }

    @Bean
    public PrintCostCalculator printCostCalculator(@Value("${sandbox.printSystem}") String printSystem) {
        PrintCostCalculator calculator;
        if (printSystem == null || printSystem.equals("BW")) {
            calculator = new BWPrintCostCalculator();
        } else if (printSystem.equals("RGB")) {
            calculator = new RGBPrintCostCalculator();
        } else {
            throw new IllegalArgumentException("Unknown print system");
        }
        calculator = new ManualPrintCostCalculator(new PagesCountPrintCostCalculator(calculator));
        return calculator;
    }

    @Bean
    public DocumentCatalog documentCatalog() {
        return new JPADocumentCatalog();
    }

    @Bean
    public DocumentRepository documentRepository() {
        return new JPADocumentRepository();
    }

    @Bean
    public ReadingConfirmator readingConfirmator(DocumentRepository repo) {
        return new StandardReadingConfirmator(repo);
    }

    @Bean
    public AuthProcess authProcess(UserRepository userRepository, CurrentUser currentUser) {
        return new StandardAuthProcess(userRepository, currentUser);
    }

    @Bean
    @Scope(value = "session", proxyMode = ScopedProxyMode.INTERFACES)
    public CurrentUser currentUser() {
        return new StandardCurrentUser();
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/*");
            }
        };
    }

    @Bean
    public DocumentFactory documentFactory(NumberGenerator numberGenerator) {
        return new DocumentFactory(numberGenerator);
    }

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(2);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("DMS-Async-Executor");
        executor.initialize();
        return executor;
    }

    @Bean
    public DocumentValidator documentValidator(@Value("${sandbox.qualitySystem}") String qualitySystem) {
        if (qualitySystem.equals("ISO")) {
            return isoDocumentValidator();
        } else if (qualitySystem.equals("QEP")) {
            return qepDocumentValidator();
        } else {
            throw new IllegalArgumentException("Invalid quality system");
        }
    }

    private DocumentValidator qepDocumentValidator() {
        DocumentValidator v1 = new VerifiedAuthorValidator();
        DocumentValidator v2 = new ExpiresAtValidator(DocumentStatus.VERIFIED);
        DocumentValidator v3 = new PublishedContentValidator();
        v1.setNext(v2);
        v2.setNext(v3);
        v3.setNext(new AgreeableDocumentValidator());
        return v1;
    }

    private DocumentValidator isoDocumentValidator() {
        DocumentValidator v1 = new VerifiedNumberValidator();
        DocumentValidator v2 = new ExpiresAtValidator(DocumentStatus.PUBLISHED);
        v1.setNext(v2);
        v2.setNext(new AgreeableDocumentValidator());
        return v1;
    }
}
