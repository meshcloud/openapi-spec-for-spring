package io.meshcloud.openapispecforspring.test.app57;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.config.HypermediaMappingInformation;
import org.springframework.hateoas.mediatype.MessageResolver;
import org.springframework.hateoas.mediatype.hal.CurieProvider;
import org.springframework.hateoas.mediatype.hal.Jackson2HalModule;
import org.springframework.hateoas.server.LinkRelationProvider;
import org.springframework.http.MediaType;

@Configuration
class App57HalMediaTypeConfiguration implements HypermediaMappingInformation {

    private final LinkRelationProvider linkRelationProvider;

    App57HalMediaTypeConfiguration(LinkRelationProvider linkRelationProvider) {
        this.linkRelationProvider = linkRelationProvider;
    }

    @Override
    public List<MediaType> getMediaTypes() {
        return App57MediaTypes.ALL;
    }

    @Override
    public ObjectMapper configureObjectMapper(ObjectMapper mapper) {
        mapper.registerModule(getJacksonModule());
        mapper.setHandlerInstantiator(new Jackson2HalModule.HalHandlerInstantiator(
                linkRelationProvider, CurieProvider.NONE, MessageResolver.DEFAULTS_ONLY));
        return mapper;
    }

    @Override
    public Module getJacksonModule() {
        return new Jackson2HalModule();
    }
}
