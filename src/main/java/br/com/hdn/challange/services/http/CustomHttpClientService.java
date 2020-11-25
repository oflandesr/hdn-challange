package br.com.hdn.challange.services.http;

import java.io.IOException;
import java.util.Map;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import br.com.hdn.challange.services.http.models.CustomHttpEntityModel;

/*
*
* Serviço responsável por realizar uma chamada http
* de acordo com os parâmetros informados
* 
*/
@Service
public class CustomHttpClientService {
	private final CloseableHttpClient httpClient = HttpClients.createDefault();

	public CustomHttpEntityModel sendGet(String endpoint, Map<String, String> headers) throws Exception {
		HttpGet request = new HttpGet(endpoint);

		for (Map.Entry<String, String> header : headers.entrySet())
			request.addHeader(header.getKey(), header.getValue());

		try {
			CloseableHttpResponse response = httpClient.execute(request);

			CustomHttpEntityModel customHttpEM = new CustomHttpEntityModel();
			customHttpEM.setResponseInBody(new JSONObject(EntityUtils.toString(response.getEntity())));
			customHttpEM.setHttpHeaders(response.getAllHeaders());
			customHttpEM.setHttpStatusLine(response.getStatusLine());
			
			return customHttpEM;

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Internal Server Error");
		}
	}
	
	public void close() throws IOException {
        httpClient.close();
    }

}