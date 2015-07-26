package com.vegaasen.http.rest.handler;

import com.vegaasen.http.rest.model.http.ContentType;

/**
 * All the default headers that can be added explicitly will exist here. Please note that if the one you're adding
 * already exists in the header-scheme, this one will actually take precedence.
 *
 * @author <a href="vegaasen@gmail.com">vegardaasen</a>
 */
public interface DefaultHeaders<T> {

    /**
     * When adding multiple contentType-headers, only the last one added will be cared for.
     *
     * @param contentType the type of content
     * @return the object that you're working with
     */
    T addContentType(ContentType contentType);

    /**
     * When adding multiple accept headers, only the last one added will be cared for.
     *
     * @param contentType the type of content
     * @return the object that you're working with
     */
    T addAccepts(ContentType contentType);

}
