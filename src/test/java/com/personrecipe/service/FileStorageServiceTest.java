package com.personrecipe.service;

import com.personrecipe.config.QiniuProperties;
import com.qiniu.http.Response;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FileStorageServiceTest {

	@Mock
	private QiniuProperties qiniuProperties;

	@Mock
	private Auth qiniuAuth;

	@Mock
	private UploadManager uploadManager;

	@Mock
	private Response response;

	@Test
	void shouldUploadImageToQiniuAndReturnFullUrl() throws Exception {
		byte[] content = new byte[]{1, 2, 3};
		MockMultipartFile file = new MockMultipartFile("file", "dish.png", "image/png", content);
		DefaultPutRet putRet = new DefaultPutRet();
		putRet.key = "uploads/2026/07/27/test.png";

		when(qiniuProperties.getBucket()).thenReturn("personrecipe2");
		when(qiniuProperties.normalizedDomain()).thenReturn("http://example.com");
		when(qiniuAuth.uploadToken(eq("personrecipe2"), anyString())).thenReturn("upload-token");
		when(uploadManager.put(
				any(InputStream.class),
				eq((long) content.length),
				anyString(),
				eq("upload-token"),
				isNull(),
				eq("image/png"),
				eq(false)
		)).thenReturn(response);
		when(response.isOK()).thenReturn(true);
		when(response.jsonToObject(DefaultPutRet.class)).thenReturn(putRet);

		FileStorageService service = new FileStorageService(qiniuProperties, qiniuAuth, uploadManager);
		String url = service.storeImage(file);

		assertEquals("http://example.com/uploads/2026/07/27/test.png", url);
		ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
		verify(qiniuAuth).uploadToken(eq("personrecipe2"), keyCaptor.capture());
		assertTrue(keyCaptor.getValue().matches("uploads/\\d{4}/\\d{2}/\\d{2}/[a-f0-9]{32}\\.png"));
		verify(response).close();
	}
}