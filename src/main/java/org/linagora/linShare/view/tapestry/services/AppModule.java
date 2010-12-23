/*
 *    This file is part of Linshare.
 *
 *   Linshare is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as
 *   published by the Free Software Foundation, either version 3 of
 *   the License, or (at your option) any later version.
 *
 *   Linshare is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public
 *   License along with Foobar.  If not, see
 *                                    <http://www.gnu.org/licenses/>.
 *
 *   (c) 2008 Groupe Linagora - http://linagora.org
 *
*/
package org.linagora.linShare.view.tapestry.services;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.apache.commons.fileupload.FileItemFactory;
import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.ScopeConstants;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.InjectService;
import org.apache.tapestry5.ioc.annotations.Scope;
import org.apache.tapestry5.ioc.annotations.SubModule;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.ioc.services.PerthreadManager;
import org.apache.tapestry5.ioc.services.SymbolProvider;
import org.apache.tapestry5.ioc.services.SymbolSource;
import org.apache.tapestry5.ioc.services.ThreadLocale;
import org.apache.tapestry5.services.AliasContribution;
import org.apache.tapestry5.services.ApplicationStateContribution;
import org.apache.tapestry5.services.ApplicationStateManager;
import org.apache.tapestry5.services.Dispatcher;
import org.apache.tapestry5.services.PersistentLocale;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.RequestFilter;
import org.apache.tapestry5.services.RequestGlobals;
import org.apache.tapestry5.services.RequestHandler;
import org.apache.tapestry5.services.Response;
import org.apache.tapestry5.services.ValidationMessagesSource;
import org.apache.tapestry5.upload.services.MultipartDecoder;
import org.apache.tapestry5.upload.services.UploadSymbols;
import org.chenillekit.image.ChenilleKitImageConstants;
import org.linagora.linShare.core.Facade.DocumentFacade;
import org.linagora.linShare.core.Facade.ParameterFacade;
import org.linagora.linShare.core.Facade.SearchDocumentFacade;
import org.linagora.linShare.core.Facade.ShareFacade;
import org.linagora.linShare.core.Facade.UserFacade;
import org.linagora.linShare.core.domain.vo.UserVo;
import org.linagora.linShare.core.repository.ParameterRepository;
import org.linagora.linShare.core.utils.PropertyPlaceholderConfigurer;
import org.linagora.linShare.view.tapestry.beans.ShareSessionObjects;
import org.linagora.linShare.view.tapestry.objects.BusinessInformativeContentBundle;
import org.linagora.linShare.view.tapestry.objects.HelpsASO;
import org.linagora.linShare.view.tapestry.rest.impl.DocumentRestServiceImpl;
import org.linagora.linShare.view.tapestry.rest.impl.ShareRestServiceImpl;
import org.linagora.linShare.view.tapestry.rest.impl.UserRestServiceImpl;
import org.linagora.linShare.view.tapestry.services.impl.AssetProtectionDispatcher;
import org.linagora.linShare.view.tapestry.services.impl.BusinessMessagesManagementServiceImpl;
import org.linagora.linShare.view.tapestry.services.impl.ConfigureMarshallerModule;
import org.linagora.linShare.view.tapestry.services.impl.MailContainerBuilder;
import org.linagora.linShare.view.tapestry.services.impl.MyMultipartDecoderImpl;
import org.linagora.linShare.view.tapestry.services.impl.PropertiesSymbolProvider;
import org.linagora.linShare.view.tapestry.services.impl.UserAccessAuthentity;
import org.linagora.linShare.view.tapestry.services.impl.UserLocaleDispatcher;
import org.linagora.linkit.flexRenderer.services.FlexRendererConfigService;
import org.linagora.linkit.flexUpload.services.FlexUploadConfigService;
import org.linagora.restmarshaller.ConfigureAliasMarshaller;
import org.linagora.restmarshaller.ConfigureOmitFieldMarshaller;
import org.linagora.restmarshaller.Marshaller;
import org.linagora.restmarshaller.StringClasse;
import org.linagora.restmarshaller.xstream.impl.XstreamMarshaller;
import org.slf4j.Logger;


/**
 * This module is automatically included as part of the Tapestry IoC Registry, it's a good place to
 * configure and extend Tapestry, or to place your own service definitions.
 */
@SubModule(ConfigureMarshallerModule.class)
public class AppModule
{
    public static void bind(ServiceBinder binder)
    {
  	 binder.bind(Dispatcher.class,AssetProtectionDispatcher.class).withId("AssetProtectionDispatcher");
     
  	 //binder.bind(BusinessMessagesManagementService.class, BusinessMessagesManagementServiceImpl.class);

 	 //binder.bind(Marshaller.class,DocumentMarshaller.class).withId("DocumentMarshaller");
 	
     //binder.bind(MyServiceInterface.class, MyServiceImpl.class);
        
        // Make bind() calls on the binder object to define most IoC services.
        // Use service builder methods (example below) when the implementation
        // is provided inline, or requires more initialization than simply
        // invoking the constructor.
    }
    
    
    public static BusinessMessagesManagementService buildBusinessMessagesManagementService(ApplicationStateManager applicationStateManager)
    {
    	return new BusinessMessagesManagementServiceImpl(applicationStateManager);
	}
    
    
    @Scope(ScopeConstants.PERTHREAD)
    public static MyMultipartDecoder buildMyMultipartDecoder(
    		
            FileItemFactory fileItemFactory,
            
            @Symbol(UploadSymbols.REPOSITORY_THRESHOLD)
            int repositoryThreshold,

            @Symbol(UploadSymbols.REQUESTSIZE_MAX)
            long maxRequestSize,

            @Symbol(UploadSymbols.FILESIZE_MAX)
            long maxFileSize,

            @Inject @Symbol(SymbolConstants.CHARSET)
            String requestEncoding,
            
            @InjectService("parameterRepository")
            ParameterRepository parameterRepository,      		
            PerthreadManager perthreadManager
    	) {

	    	MyMultipartDecoder multipartDecoder = new MyMultipartDecoderImpl(fileItemFactory,maxRequestSize,maxFileSize,requestEncoding,parameterRepository);
			perthreadManager.addThreadCleanupListener(multipartDecoder);
	    	return multipartDecoder;	
    }
    
    public static void contributeApplicationDefaults(
            MappedConfiguration<String, String> configuration, 
            @InjectService("PropertiesSymbolProvider")
			PropertiesSymbolProvider propertiesSymbolProvider)
    {
        // Contributions to ApplicationDefaults will override any contributions to
        // FactoryDefaults (with the same key). Here we're restricting the supported
        // locales to just "en" (English). As you add localised message catalogs and other assets,
        // you can extend this list of locales (it's a comma separated series of locale names;
        // the first locale name is the default when there's no reasonable match).
        
        configuration.add(SymbolConstants.SUPPORTED_LOCALES, propertiesSymbolProvider.valueForSymbol("linshare.availableLocales"));

        // The factory default is true but during the early stages of an application
        // overriding to false is a good idea. In addition, this is often overridden
        // on the command line as -Dtapestry.production-mode=false
        configuration.add(SymbolConstants.PRODUCTION_MODE, propertiesSymbolProvider.valueForSymbol("linshare.productionMode"));
        
        
        configuration.add(UploadSymbols.FILESIZE_MAX , "0");
        configuration.add(UploadSymbols.REQUESTSIZE_MAX , propertiesSymbolProvider.valueForSymbol("linshare.default.maxUploadSize"));
        configuration.add(SymbolConstants.APPLICATION_VERSION, propertiesSymbolProvider.valueForSymbol("Implementation-Version"));
    }

    /** Configure flex renderer component */
    public static void contributeFlexRendererConfigService(MappedConfiguration<String, String> configuration,
            @InjectService("PropertiesSymbolProvider") 
            PropertiesSymbolProvider provider) {
        configuration.add(FlexRendererConfigService.FLASH_PLAYER_VERSION,
                provider.valueForSymbol("linshare.flash.version.minimal"));
    }

    /** Configure flex upload component */
    public static void contributeFlexUploadConfigService(MappedConfiguration<String, String> configuration,
            @InjectService("PropertiesSymbolProvider")
            PropertiesSymbolProvider provider) {
        configuration.add(FlexUploadConfigService.ALLOWED_AGENT_FOR_FLEX_UPLOAD,
                provider.valueForSymbol("linshare.flash.allowed.agents"));
    }

//    /** Configure flex upload component */
//    public static void contributeFlexUploadConfigService(MappedConfiguration<String, String> configuration,
//            @InjectService("PropertiesSymbolProvider")
//            PropertiesSymbolProvider propertiesSymbolProvider) {
//    }

	/**
	 * Override the default MarkupWritterFactory with one that always output XHTML tags. 
	 * This is because we want to use "text/html" content type (to be understood by IE)
	 * _and_ output valid XHTML. 
	 */
//	public static void contributeAlias(Configuration<AliasContribution<MarkupWriterFactory>> configuration,
//								@Inject @Symbol(SymbolConstants.CHARSET) final String applicationCharset) {
//		configuration.add(AliasContribution.create(MarkupWriterFactory.class,
//				new XhtmlMarkupWriterFactoryImpl(applicationCharset)));
//	}
    
	public static void contributeAssetProtectionDispatcher(OrderedConfiguration<String> conf) {
		conf.add("png","^.*\\.png$");
		conf.add("jpg","^.*\\.jpg$");
		conf.add("jpeg","^.*\\.jpeg$");
		conf.add("javascript","^.*\\.js$");
		conf.add("css","^.*\\.css$");
		conf.add("gif","^.*\\.gif$");
		conf.add("swf","^.*\\.swf$");
		conf.add("ico","^.*\\.ico$");
		conf.add("applet","^.*applet/.*\\.jar$");
	}

    /**
     * This is a service definition, the service will be named "TimingFilter". The interface,
     * RequestFilter, is used within the RequestHandler service pipeline, which is built from the
     * RequestHandler service configuration. Tapestry IoC is responsible for passing in an
     * appropriate Logger instance. Requests for static resources are handled at a higher level, so
     * this filter will only be invoked for Tapestry related requests.
     * 
     * <p>
     * Service builder methods are useful when the implementation is inline as an inner class
     * (as here) or require some other kind of special initialization. In most cases,
     * use the static bind() method instead. 
     * 
     * <p>
     * If this method was named "build", then the service id would be taken from the 
     * service interface and would be "RequestFilter".  Since Tapestry already defines
     * a service named "RequestFilter" we use an explicit service id that we can reference
     * inside the contribution method.
     */    
    public static RequestFilter buildTimingFilter(final Logger log)
    {
        return new RequestFilter()
        {
            public boolean service(Request request, Response response, RequestHandler handler)
                    throws IOException
            {
                long startTime = System.currentTimeMillis();
				log.info(String.format("%s (XHR:%s) : %s", request.getMethod(), request.isXHR(), request.getPath()));
                try
                {
                    // The responsibility of a filter is to invoke the corresponding method
                    // in the handler. When you chain multiple filters together, each filter
                    // received a handler that is a bridge to the next filter.
                    
                    return handler.service(request, response);
                }
                finally
                {
                    long elapsed = System.currentTimeMillis() - startTime;

                    log.info(String.format("Request time: %d ms", elapsed));
                }
            }
        };
    }

    /** This service that loads user informations in session. */
    public static UserAccessAuthentity buildUserAccessAuthentity(
        @InjectService("UserFacade") UserFacade userFacade,
        ApplicationStateManager applicationStateManager) {
        return new UserAccessAuthentity(userFacade, applicationStateManager);
    }

    public static RequestFilter buildUserAccessAuthentityFilter(
    		@InjectService("UserAccessAuthentity") final UserAccessAuthentity userAccessAuthentity) {

		return new RequestFilter(){
			public boolean service(Request request, Response response, RequestHandler handler) throws IOException {
				userAccessAuthentity.processAuth();
				return handler.service(request, response);	
			}
		};
	}
    
	/**
	 * this dispatcher check the role for attribute an url
	 */
    
	////////////// Symbol Provider //////////////

	/*
	 * Use properties file as symbol provider
	 */
	public static PropertiesSymbolProvider buildPropertiesSymbolProvider(
			@InjectService("propertyPlaceholder") PropertyPlaceholderConfigurer placeholderConfigurer) {
		return new PropertiesSymbolProvider(placeholderConfigurer);
	}

	/*
	 * Contribute the properties provider to symbol provider
	 */
	public static void contributeSymbolSource(OrderedConfiguration<SymbolProvider> providers,
			@InjectService("PropertiesSymbolProvider")
			PropertiesSymbolProvider propertiesSymbolProvider) {
		providers.add("properties", propertiesSymbolProvider);
	}

	
	/**
	 * UserDetailsVo for session object
	 * @param configuration
	 */
	@SuppressWarnings("unchecked")
	public void contributeApplicationStateManager(MappedConfiguration<Class, ApplicationStateContribution> configuration)
	{
		configuration.add(UserVo.class, new ApplicationStateContribution("session"));
		configuration.add(ShareSessionObjects.class, new ApplicationStateContribution("session"));
//        configuration.add(UserSignature.class, new ApplicationStateContribution("session"));
        configuration.add(BusinessInformativeContentBundle.class, new ApplicationStateContribution("session"));
        configuration.add(HelpsASO.class, new ApplicationStateContribution("session"));
    	
	}
	
	public static void contributeMasterDispatcher(OrderedConfiguration<Dispatcher> configuration,
	        @InjectService("AssetProtectionDispatcher") Dispatcher assetProtectionDispatcher,
	        @InjectService("UserLocaleDispatcher") Dispatcher userLocaleDispatcher) {
			configuration.add("AssetProtectionDispatcher", assetProtectionDispatcher, "before:Asset");
			configuration.add("UserLocaleDispatcher", userLocaleDispatcher, "before:PageRender");        
	}
	
    /**
     * This is a contribution to the RequestHandler service configuration. This is how we extend
     * Tapestry using the timing filter. A common use for this kind of filter is transaction
     * management or security. The @Local annotation selects the desired service by type, but only
     * from the same module.  Without @Local, there would be an error due to the other service(s)
     * that implement RequestFilter (defined in other modules).
     */
    public void contributeRequestHandler(OrderedConfiguration<RequestFilter> configuration,
    		@InjectService("TimingFilter") RequestFilter timingFilter,
    		@InjectService("UserAccessAuthentityFilter") RequestFilter userAccessAuthentityFilter)
    {
        // Each contribution to an ordered configuration has a name, When necessary, you may
        // set constraints to precisely control the invocation order of the contributed filter
        // within the pipeline.
    	configuration.add("userAccessAuthentity", userAccessAuthentityFilter, "before:*");
        configuration.add("Timing", timingFilter);
        
    }
  
    public static void contributeAliasOverrides(
    		  @InjectService("MyMultipartDecoder") MyMultipartDecoder myDecoder,
    		  Configuration<AliasContribution> configuration) {
    	configuration.add(AliasContribution.create(MultipartDecoder.class, myDecoder));
    }
    
    /** Dispatcher to set the user locale */
    public static Dispatcher buildUserLocaleDispatcher(
			ApplicationStateManager stateManager,
			@InjectService("PersistentLocale") PersistentLocale persistentLocale,
			@InjectService("SymbolSource") SymbolSource symbolSource) {

		return new UserLocaleDispatcher(persistentLocale, stateManager, symbolSource, "fr");
	}
    
    /****************************
     * REST part
     ****************************/

    // Adding the properties for mail subject and content
    public static void contributeValidationMessagesSource(OrderedConfiguration<String> configuration) {
    	configuration.add("app", "org/linagora/linShare/view/tapestry/rest/mail");
    	configuration.add("app2", "org/linagora/linShare/view/tapestry/services/keys");
    }
    
    // These two methods are to be contributed by a submodule
    public static ConfigureAliasMarshaller buildConfigureAliasMarshaller(List<StringClasse> configuration) {
    	return new ConfigureAliasMarshaller(configuration);
    }

    public static ConfigureOmitFieldMarshaller buildConfigureOmitFieldMarshaller(List<StringClasse> configuration) {
    	return new ConfigureOmitFieldMarshaller(configuration);
    }

    /**
     * Construct the marshaller service
     * @param configureAliasMarshaller : all the aliases
     * @param configureOmitMarshaller : all the field to omit
     * @return
     */
    public static Marshaller buildMarshaller(
    		@InjectService("ConfigureAliasMarshaller") ConfigureAliasMarshaller configureAliasMarshaller,
    		 @InjectService("ConfigureOmitFieldMarshaller") ConfigureOmitFieldMarshaller configureOmitMarshaller) {
    	return new XstreamMarshaller(configureAliasMarshaller, configureOmitMarshaller);
    }
    
    
    /**
     * Create the Rest dispatcher. Holds nearly all the know universe as constructor
     */
    public void contributeRestfulWSDispatcher (MappedConfiguration<String, Object> config,
    		 ApplicationStateManager applicationStateManager,
    		 @InjectService("MyMultipartDecoder") MyMultipartDecoder myDecoder,
    		 @InjectService("RequestGlobals") RequestGlobals requestGlobal,
    		 @InjectService("DocumentFacade") DocumentFacade documentFacade,
    		 @InjectService("Marshaller") Marshaller xstreamMarshaller,
    		 @InjectService("SearchDocumentFacade") SearchDocumentFacade searchDocumentFacade,
    		 @InjectService("ShareFacade") ShareFacade shareFacade,
    		 @InjectService("UserFacade") UserFacade userFacade,
             @InjectService("ParameterFacade") ParameterFacade parameterFacade,
    		 @InjectService("PropertiesSymbolProvider")	PropertiesSymbolProvider propertiesSymbolProvider,
    		 @InjectService("ValidationMessagesSource") ValidationMessagesSource validationMessagesSource,
    		 @InjectService("ThreadLocale")  ThreadLocale threadLocale,
    		 @InjectService("MailContainerBuilder")  MailContainerBuilder mailContainerBuilder

    		 )
    {

        config.add("documentrestservice", new DocumentRestServiceImpl(applicationStateManager, searchDocumentFacade, documentFacade, parameterFacade, myDecoder, propertiesSymbolProvider, xstreamMarshaller));
        config.add("sharerestservice", new ShareRestServiceImpl(applicationStateManager, shareFacade, documentFacade, mailContainerBuilder));
        config.add("userrestservice", new UserRestServiceImpl(applicationStateManager, userFacade, propertiesSymbolProvider, xstreamMarshaller,mailContainerBuilder));
    }
     
    public static MailContainerBuilder buildMailContainerBuilder(
		 @InjectService("PersistentLocale") PersistentLocale persistentLocale,
		 @InjectService("Request") Request request,
		 @InjectService("ValidationMessagesSource") ValidationMessagesSource validationMessagesSource) {
    	return new MailContainerBuilder(persistentLocale, request, validationMessagesSource);
    }
    
    /**
     * ChenilleKit Kaptcha configuration
     * @param configuration
     */
    public static void contributeKaptchaProducer(MappedConfiguration<String, Properties> configuration) {
    	Properties properties = new Properties();
    	properties.setProperty("kaptcha.border", "no");
    	configuration.add(ChenilleKitImageConstants.KAPATCHA_CONFIG_KEY, properties);
    }

}
