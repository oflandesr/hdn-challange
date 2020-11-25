package br.com.hdn.challange.models;

import org.json.JSONObject;

/*
*
* Classe modelo que conterá as informações que serão
* devolvidas para o cliente que realizou a consulta
* para saber se é o momento ou não de vender suas moedas
* 
*/
public class ExchangeViabilityModel {
	private String message;
	private int code;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public JSONObject toJSONObject() {
		return new JSONObject("{\"message\": \"" + this.message + "\"}");
	}
}
