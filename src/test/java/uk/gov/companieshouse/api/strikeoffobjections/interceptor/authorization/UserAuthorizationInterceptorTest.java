package uk.gov.companieshouse.api.strikeoffobjections.interceptor.authorization;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.strikeoffobjections.common.ApiLogger;
import uk.gov.companieshouse.api.strikeoffobjections.groups.Unit;
import uk.gov.companieshouse.api.strikeoffobjections.model.entity.CreatedBy;
import uk.gov.companieshouse.api.strikeoffobjections.model.entity.Objection;
import uk.gov.companieshouse.api.strikeoffobjections.service.IObjectionService;
import uk.gov.companieshouse.api.strikeoffobjections.service.impl.ERICHeaderParser;
import uk.gov.companieshouse.api.strikeoffobjections.utils.Utils;
import uk.gov.companieshouse.service.ServiceException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Unit
@ExtendWith(MockitoExtension.class)
class UserAuthorizationInterceptorTest {

    private static final String USER_EMAIL = "demo@ch.gov.uk";
    private static final String DIFFERENT_USER_EMAIL = "different@ch.gov.uk";

    @Mock
    private ApiLogger apiLogger;

    @Mock
    private ERICHeaderParser ericHeaderParser;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private UserAuthorizationInterceptor userAuthorizationInterceptor;

    @Test
    void testUserAuthorised() throws Exception {
        Objection objection = new Objection();
        CreatedBy createdBy = new CreatedBy("id", USER_EMAIL,
               "client", "Joe Bloggs", false);
        objection.setCreatedBy(createdBy);
        when(ericHeaderParser.getEmailAddress(any())).thenReturn(USER_EMAIL);
        when(request.getAttribute("objection")).thenReturn(objection);

        boolean result = userAuthorizationInterceptor.preHandle(request, response, null);

        assertTrue(result);
    }

    @Test
    void testUserNotAuthorised() throws Exception {
        Objection objection = new Objection();
        CreatedBy createdBy = new CreatedBy("id", USER_EMAIL,
                "client", "Joe Bloggs", false);
        objection.setCreatedBy(createdBy);
        when(ericHeaderParser.getEmailAddress(any())).thenReturn(DIFFERENT_USER_EMAIL);
        when(request.getAttribute("objection")).thenReturn(objection);

        boolean result = userAuthorizationInterceptor.preHandle(request, response, null);

        assertFalse(result);
        verify(response, times(1)).setStatus(401);
    }
}