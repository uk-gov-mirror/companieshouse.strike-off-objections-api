package uk.gov.companieshouse.api.strikeoffobjections.file;

import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.util.StringUtils;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.companieshouse.api.strikeoffobjections.common.ApiLogger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FileTransferApiClientUnitTest {

    private static final String REQUEST_ID = "abc";
    private static final String DUMMY_URL = "http://test";
    private static final String FILE_ID = "12345";
    private static final String EXCEPTION_MESSAGE = "BAD THINGS";
    private static final String DELETE_URL = DUMMY_URL + "/" + FILE_ID;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ApiLogger apiLogger;

    @InjectMocks
    private FileTransferApiClient fileTransferApiClient;

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    private MultipartFile file;

    @BeforeEach
    public void setup() {
        ReflectionTestUtils.setField(fileTransferApiClient, "fileTransferApiURL", DUMMY_URL);
        file = new MockMultipartFile("testFile", new byte[10]);
    }

    @Test
    public void testUploadSuccess() {
        final ResponseEntity<FileTransferApiResponse> apiResponse = apiSuccessResponse();

        when(restTemplate.postForEntity(eq(DUMMY_URL), any(), eq(FileTransferApiResponse.class)))
                .thenReturn(apiResponse);

        FileTransferApiClientResponse fileTransferApiClientResponse = fileTransferApiClient.upload(REQUEST_ID, file);

        assertEquals(FILE_ID, fileTransferApiClientResponse.getFileId());
        assertEquals(HttpStatus.OK, fileTransferApiClientResponse.getHttpStatus());
    }

    @Test
    public void testUploadApiReturnsError() {
        final ResponseEntity<FileTransferApiResponse> apiErrorResponse = apiErrorResponse();

        when(restTemplate.postForEntity(eq(DUMMY_URL), any(), eq(FileTransferApiResponse.class))).thenReturn(apiErrorResponse);

        FileTransferApiClientResponse fileTransferApiClientResponse = fileTransferApiClient.upload(REQUEST_ID, file);

        assertTrue(fileTransferApiClientResponse.getHttpStatus().isError());
        assertEquals(apiErrorResponse.getStatusCode(), fileTransferApiClientResponse.getHttpStatus());
        assertTrue(StringUtils.isBlank(fileTransferApiClientResponse.getFileId()));
    }

    @Test
    public void testUploadGenericExceptionResponse() {
        final RestClientException exception = new RestClientException(EXCEPTION_MESSAGE);

        when(restTemplate.postForEntity(eq(DUMMY_URL), any(), eq(FileTransferApiResponse.class))).thenThrow(exception);

        expectedException.expect(RestClientException.class);
        expectedException.expectMessage(exception.getMessage());

        assertThrows(RestClientException.class, () -> fileTransferApiClient.upload(REQUEST_ID, file));
    }

    @Test
    public void testDeleteSuccess() {
        final ResponseEntity<String> apiResponse = new ResponseEntity<>("", HttpStatus.NO_CONTENT);
        when(restTemplate.exchange(eq(DELETE_URL), eq(HttpMethod.DELETE), any(), eq(String.class)))
                .thenReturn(apiResponse);
        FileTransferApiClientResponse fileTransferApiClientResponse = fileTransferApiClient.delete(REQUEST_ID, FILE_ID);
        assertEquals(HttpStatus.NO_CONTENT, fileTransferApiClientResponse.getHttpStatus());
    }

    @Test
    public void testDeleteApiReturnsError() {
        final ResponseEntity<String> apiResponse = new ResponseEntity<>("", HttpStatus.INTERNAL_SERVER_ERROR);

        when(restTemplate.exchange(eq(DELETE_URL), eq(HttpMethod.DELETE), any(), eq(String.class)))
                .thenReturn(apiResponse);

        FileTransferApiClientResponse fileTransferApiClientResponse = fileTransferApiClient.delete(REQUEST_ID, FILE_ID);

        assertTrue(fileTransferApiClientResponse.getHttpStatus().isError());
        assertEquals(apiResponse.getStatusCode(), fileTransferApiClientResponse.getHttpStatus());
    }

    @Test
    public void testDeleteGenericExceptionResponse() {
        final RestClientException exception = new RestClientException(EXCEPTION_MESSAGE);

        when(restTemplate.exchange(eq(DELETE_URL), eq(HttpMethod.DELETE), any(), eq(String.class))).thenThrow(exception);

        expectedException.expect(RestClientException.class);
        expectedException.expectMessage(exception.getMessage());

        assertThrows(RestClientException.class, () -> fileTransferApiClient.delete(REQUEST_ID, FILE_ID));
    }

    private ResponseEntity<FileTransferApiResponse> apiSuccessResponse() {
        FileTransferApiResponse response = new FileTransferApiResponse();
        response.setId(FILE_ID);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private ResponseEntity<FileTransferApiResponse> apiErrorResponse() {
        FileTransferApiResponse response = new FileTransferApiResponse();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}