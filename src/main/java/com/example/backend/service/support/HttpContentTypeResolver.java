package com.example.backend.service.support;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
public class HttpContentTypeResolver implements ContentTypeResolver {

    @Override
    public String normalize(String rawContentType) {
        MediaType mediaType = MediaType.parseMediaType(rawContentType);
        return mediaType.getType() + "/" + mediaType.getSubtype();
    }
}
