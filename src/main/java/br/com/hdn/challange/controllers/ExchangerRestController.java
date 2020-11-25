package br.com.hdn.challange.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import br.com.hdn.challange.models.ExchangeViabilityModel;
import br.com.hdn.challange.services.ExchangerService;
/*
*
* Este controller será responsável por escutar as requisições http
* e resolvê-las de acordo com as definições dos requisitos descritos
* no README.md desse projeto
* 
*/
@RestController
public class ExchangerRestController {
	/*
	*
	* Servico que será utilizado para verificar se é ou não o
	* momento de vender as moedas
	* 
	*/
	@Autowired
	private ExchangerService eService;

	@RequestMapping(value = "/exchange", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<String> checkViability() {

		ResponseEntity<String> response;
		ExchangeViabilityModel viabilityResponse = new ExchangeViabilityModel();

		try {
			
			/*
			*
			* Após a consulta no serviço de viabilidade a resposta será
			* interpretada e devolvida
			* 
			*/
			viabilityResponse = eService.checkViability();
			switch (viabilityResponse.getCode()) {
			case 200:
				response = new ResponseEntity<String>(viabilityResponse.toJSONObject().toString(), HttpStatus.OK);
				break;
			case 400:
				response = new ResponseEntity<String>(viabilityResponse.toJSONObject().toString(),
						HttpStatus.BAD_REQUEST);
				break;
			default:
				response = new ResponseEntity<String>(viabilityResponse.toJSONObject().toString(),
						HttpStatus.INTERNAL_SERVER_ERROR);
				break;
			}

		} catch (Throwable e) {
			e.printStackTrace();
			viabilityResponse.setMessage("Internal Server Error. Please contact your system admin");
			response = new ResponseEntity<String>(viabilityResponse.toJSONObject().toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return response;
	}
}
