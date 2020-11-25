package br.com.hdn.challange.services.http.models;

import org.apache.http.Header;
import org.apache.http.StatusLine;
import org.json.JSONObject;

/*
*
* Modelo customizado de retorno das requisições HTTP.
* Foi criado com o objetivo de simplificar respostas distintas
* vindas do backend idependentemente do recurso utilizado na chamada.
* 
*/
public class CustomHttpEntityModel {
	private JSONObject responseInBody;
	private Header[] httpHeaders;
	private StatusLine httpStatusLine;

	public JSONObject getResponseInBody() {
		return responseInBody;
	}

	public void setResponseInBody(JSONObject responseInBody) {
		this.responseInBody = responseInBody;
	}

	public Header[] getHttpHeaders() {
		return httpHeaders;
	}

	public void setHttpHeaders(Header[] httpHeaders) {
		this.httpHeaders = httpHeaders;
	}

	public StatusLine getHttpStatusLine() {
		return httpStatusLine;
	}

	public void setHttpStatusLine(StatusLine httpStatusLine) {
		this.httpStatusLine = httpStatusLine;
	}

}
