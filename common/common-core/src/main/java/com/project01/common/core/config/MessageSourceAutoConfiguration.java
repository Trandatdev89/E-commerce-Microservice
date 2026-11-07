//package com.project01.common.core.config;
//
//import org.springframework.boot.autoconfigure.AutoConfiguration;
//import org.springframework.boot.autoconfigure.AutoConfigureOrder;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
//import org.springframework.boot.autoconfigure.condition.SearchStrategy;
//import org.springframework.boot.autoconfigure.context.MessageSourceProperties;
//import org.springframework.boot.context.properties.EnableConfigurationProperties;
//import org.springframework.context.annotation.Conditional;
//import org.springframework.context.annotation.ImportRuntimeHints;
//
//@AutoConfiguration
//@ConditionalOnMissingBean(
//        name = {"messageSource"},
//        search = SearchStrategy.CURRENT
//)
//@AutoConfigureOrder(Integer.MIN_VALUE)
//@Conditional({ResourceBundleCondition.class})
//@EnableConfigurationProperties({MessageSourceProperties.class})
//@ImportRuntimeHints({MessageSourceAutoConfiguration.MessageSourceRuntimeHints.class})
//public class MessageSourceAutoConfiguration {
//    private static final Resource[] NO_RESOURCES = new Resource[0];
//
//    public MessageSourceAutoConfiguration() {
//    }
//
//    @Bean
//    public MessageSource messageSource(MessageSourceProperties properties) {
//        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
//        if (!CollectionUtils.isEmpty(properties.getBasename())) {
//            messageSource.setBasenames((String[])properties.getBasename().toArray(new String[0]));
//        }
//
//        if (properties.getEncoding() != null) {
//            messageSource.setDefaultEncoding(properties.getEncoding().name());
//        }
//
//        messageSource.setFallbackToSystemLocale(properties.isFallbackToSystemLocale());
//        Duration cacheDuration = properties.getCacheDuration();
//        if (cacheDuration != null) {
//            messageSource.setCacheMillis(cacheDuration.toMillis());
//        }
//
//        messageSource.setAlwaysUseMessageFormat(properties.isAlwaysUseMessageFormat());
//        messageSource.setUseCodeAsDefaultMessage(properties.isUseCodeAsDefaultMessage());
//        messageSource.setCommonMessages(this.loadCommonMessages(properties.getCommonMessages()));
//        return messageSource;
//    }
//
//    private Properties loadCommonMessages(List<Resource> resources) {
//        if (CollectionUtils.isEmpty(resources)) {
//            return null;
//        } else {
//            Properties properties = CollectionFactory.createSortedProperties(false);
//
//            for(Resource resource : resources) {
//                try {
//                    PropertiesLoaderUtils.fillProperties(properties, resource);
//                } catch (IOException ex) {
//                    throw new UncheckedIOException("Failed to load common messages from '%s'".formatted(resource), ex);
//                }
//            }
//
//            return properties;
//        }
//    }
//
//    protected static class ResourceBundleCondition extends SpringBootCondition {
//        private static final ConcurrentReferenceHashMap<String, ConditionOutcome> cache = new ConcurrentReferenceHashMap();
//
//        protected ResourceBundleCondition() {
//        }
//
//        public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
//            String basename = context.getEnvironment().getProperty("spring.messages.basename", "messages");
//            ConditionOutcome outcome = (ConditionOutcome)cache.get(basename);
//            if (outcome == null) {
//                outcome = this.getMatchOutcomeForBasename(context, basename);
//                cache.put(basename, outcome);
//            }
//
//            return outcome;
//        }
//
//        private ConditionOutcome getMatchOutcomeForBasename(ConditionContext context, String basename) {
//            ConditionMessage.Builder message = ConditionMessage.forCondition("ResourceBundle", new Object[0]);
//
//            for(String name : StringUtils.commaDelimitedListToStringArray(StringUtils.trimAllWhitespace(basename))) {
//                for(Resource resource : this.getResources(context.getClassLoader(), name)) {
//                    if (resource.exists()) {
//                        return ConditionOutcome.match(message.found("bundle").items(new Object[]{resource}));
//                    }
//                }
//            }
//
//            return ConditionOutcome.noMatch(message.didNotFind("bundle with basename " + basename).atAll());
//        }
//
//        private Resource[] getResources(ClassLoader classLoader, String name) {
//            String target = name.replace('.', '/');
//
//            try {
//                return (new PathMatchingResourcePatternResolver(classLoader)).getResources("classpath*:" + target + ".properties");
//            } catch (Exception var5) {
//                return MessageSourceAutoConfiguration.NO_RESOURCES;
//            }
//        }
//    }
//
//    static class MessageSourceRuntimeHints implements RuntimeHintsRegistrar {
//        MessageSourceRuntimeHints() {
//        }
//
//        public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
//            hints.resources().registerPattern("messages.properties").registerPattern("messages_*.properties");
//        }
//    }
//}