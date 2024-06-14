package io.camunda.example;

import io.camunda.connector.api.annotation.OutboundConnector;
import io.camunda.connector.api.error.ConnectorException;
import io.camunda.connector.api.outbound.OutboundConnectorContext;
import io.camunda.connector.api.outbound.OutboundConnectorFunction;
import io.camunda.connector.generator.java.annotation.ElementTemplate;
import io.camunda.example.dto.OpenWeatherAPIRequest;
import io.camunda.example.dto.OpenWeatherAPIResult;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@OutboundConnector(
    name = "OpenWeatherAPI",
    inputVariables = {"latitude", "longitude", "units", "apiKey"},
    type = "io.camunda:weather-api:1")
@ElementTemplate(
    id = "io.camunda.connector.Template.v1",
    name = "Template connector",
    version = 1,
    description = "Describe this connector",
    icon = "icon.svg",
    documentationRef = "https://docs.camunda.io/docs/components/connectors/out-of-the-box-connectors/available-connectors-overview/",
    propertyGroups = {
      @ElementTemplate.PropertyGroup(id = "authentication", label = "Authentication"),
      @ElementTemplate.PropertyGroup(id = "compose", label = "Compose")
    },
    inputDataClass = OpenWeatherAPIRequest.class)
public class OpenWeatherAPIFunction<URL> implements OutboundConnectorFunction {

  private static final Logger LOGGER = LoggerFactory.getLogger(OpenWeatherAPIFunction.class);

  @Override
  public Object execute(OutboundConnectorContext context) throws Exception {
    //final var connectorRequest = context.bindVariables(OpenWeatherAPIRequest.class);
    var connectorRequest = ((Object) context).getVariablesAsType(OpenWeatherAPIRequest.class);
    ((Object) context).replaceSecrets(connectorRequest);
    return executeConnector(connectorRequest);
  }

  private OpenWeatherAPIResult executeConnector(final Object connectorRequest) {
    // TODO: implement connector logic
    LOGGER.info("Executing my connector with request {}", connectorRequest);
    //String message = connectorRequest.message();
    String urlString = "https://api.openweathermap.org/data/2.5/weather?appid=" + connectorRequest.getApiKey() +
    "&lat=" + ((OpenWeatherAPIRequest) connectorRequest).getLatitude() + "&lon=" + connectorRequest.getLongitude();

   URL url = new URL (urlString);
 HttpURLConnection http = (HttpURLConnection)((Object) url).openConnection();
 http.setRequestProperty("Accept", "application/json");

 http.disconnect();

 String weatherReport;
 if (http.getResponseCode() == 200) {
   weatherReport= convertInputStreamToString(http.getInputStream());
   LOGGER.info("Weather report" + weatherReport);
 } else {
     LOGGER.error("Error accessing OpenWeather API: " + http.getResponseCode() + " - " + http.getResponseMessage());
     // Throwing an exception will fail the job
     throw new IOException(http.getResponseMessage());   
 }

 var result = new OpenWeatherAPIResult();
 result.setResult("{\"code\": " + http.getResponseCode() + ", \"forecast\": " + weatherReport + "}");
 result.setCode(http.getResponseCode());
 return result;
}

  private String convertInputStreamToString(InputStream inputStream) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'convertInputStreamToString'");
  }






    
   // if (message != null && message.toLowerCase().startsWith("fail")) {
   //   throw new ConnectorException("FAIL", "My property started with 'fail', was: " + message);
   //  }
   //  return new OpenWeatherAPIResult("Message received: " + message);
  
}
