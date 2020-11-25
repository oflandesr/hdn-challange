package br.com.hdn.challange.services;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.hdn.challange.models.ExchangeViabilityModel;
import br.com.hdn.challange.services.http.CustomHttpClientService;
import br.com.hdn.challange.services.http.models.CustomHttpEntityModel;

/*
*
* Serviço responsável por realizar a comunicação http com o endpoint definido na
* especificação e checar a viabilidade das vendas das moedas.
* 
*/
@Service
public class ExchangerService {
	@Autowired
	private CustomHttpClientService httpService;

	private String exchangeEndpoint = "https://api.exchangeratesapi.io";
	private int periodToCheck = 7;

	public ExchangeViabilityModel checkViability() throws Throwable {

		Map<String, String> headers = new HashMap<>();
		headers.put("Content-Type", "application/json");

		ExchangeViabilityModel viability = new ExchangeViabilityModel();
		try {
			
			// Consulta o endpoint e verifica o histórico em dias definidos na 
			// variável periodToCheck
			CustomHttpEntityModel response = this.httpService
					.sendGet(this.exchangeEndpoint + this.prepareResourceFilters(true), headers);
			if (response.getHttpStatusLine().getStatusCode() != 200) {
				viability.setMessage(response.getResponseInBody().getString("error"));
				viability.setCode(response.getHttpStatusLine().getStatusCode());
				return viability;
			}
			// Variáveis com a média calculada de acordo com a consulta efetuada
			double avgHistoryEUR = this.prepareAVG(response.getResponseInBody().getJSONObject("rates"), "EUR");
			double avgHistoryUSD = this.prepareAVG(response.getResponseInBody().getJSONObject("rates"), "USD");
			System.out.println("History EUR: " + avgHistoryEUR + " History USD: " + avgHistoryUSD);
			
			
			// Consulta o endpoint e verifica os valores atuais das moedas
			response = this.httpService.sendGet(this.exchangeEndpoint + this.prepareResourceFilters(false), headers);
			if (response.getHttpStatusLine().getStatusCode() != 200) {
				viability.setMessage(response.getResponseInBody().getString("error"));
				viability.setCode(response.getHttpStatusLine().getStatusCode());
				return viability;
			}
			
			// Variável contendo o valor atual da moeda EUR seguida da
			// checagem se é ou não viável vendê-la
			double currentEUR = response.getResponseInBody().getJSONObject("rates").getDouble("EUR");
			if (currentEUR > avgHistoryEUR) {
				viability.setMessage("According to last week's historical data on EUR and USD currencies, we advise: "
						+ "EUR: sale!");
			} else {
				viability.setMessage("According to last week's historical data on EUR and USD currencies, we advise: "
						+ "EUR: don't sell!");
			}

			// Variável contendo o valor atual da moeda USD seguida da
			// checagem se é ou não viável vendê-la
			double currentUSD = response.getResponseInBody().getJSONObject("rates").getDouble("USD");
			if (currentUSD > avgHistoryUSD) {
				viability.setMessage(viability.getMessage() + " - USD: sale!");
			} else {
				viability.setMessage(viability.getMessage() + " - USD: don't sell!");
			}
			viability.setCode(response.getHttpStatusLine().getStatusCode());
			
			System.out.println("Current EUR: " + currentEUR + " Current USD: " + currentUSD);

			return viability;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("An error occurs while proccess your data");
		} finally {
			this.httpService.close();
		}
	}
	
	/*
	*
	* Método criado para preparar os filtros que serão utilizados
	* na consulta da api para trazer os valores históricos ou atuais 
	* das moedas
	* 
	*/
	private String prepareResourceFilters(boolean hasPeriod) {
		if (hasPeriod) {
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

			Date date = new Date();
			String todate = dateFormat.format(date);

			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DATE, -this.periodToCheck);
			Date todate1 = cal.getTime();
			String fromdate = dateFormat.format(todate1);
			return "/history?base=BRL&symbols=EUR,USD&start_at=" + fromdate + "&end_at=" + todate;
		}
		return "/latest?base=BRL&symbols=EUR,USD";
	}

	/*
	*
	* Método criado para calcular a média do histórico retornado
	* pela consulta do endpoint
	* 
	*/
	private double prepareAVG(JSONObject obj, String key) {
		double result = 0;
		for (String keyStr : obj.keySet()) {
			JSONObject keyvalue = obj.getJSONObject(keyStr);
			System.out.println("type: " + key + " key: " + keyStr + " value: " + keyvalue.getDouble(key));
			result = result + keyvalue.getDouble(key);
		}
		return result / this.periodToCheck;
	}
}
