package com.vegaasen.http.rest.utils.apache;

import com.vegaasen.http.rest.model.Scheme;
import com.vegaasen.http.rest.model.http.Header;
import com.vegaasen.http.rest.model.http.Response;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * @author <a href="vegaasen@gmail.com">vegardaasen</a>
 */
public class HttpUtilsIntTest {

    private Scheme validScheme;

    @Before
    public void setUp() {
        validScheme = new Scheme();
        validScheme.setTo("http://www.vegaasen.com/blog/");
        validScheme.addHeader(new Header("User-Agent", "some-cool-browser"));
    }

    @Test
    public void httpGet_normalProcedure() {
        Response response = HttpUtils.httpGet(validScheme);
        assertNotNull(response);
    }

}
