package slack.umbrella_post_msg;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import tk.plogitech.darksky.api.jackson.DarkSkyJacksonClient;
import tk.plogitech.darksky.forecast.APIKey;
import tk.plogitech.darksky.forecast.ForecastException;
import tk.plogitech.darksky.forecast.ForecastRequest;
import tk.plogitech.darksky.forecast.ForecastRequestBuilder;
import tk.plogitech.darksky.forecast.GeoCoordinates;
import tk.plogitech.darksky.forecast.model.Forecast;
import tk.plogitech.darksky.forecast.model.Latitude;
import tk.plogitech.darksky.forecast.model.Longitude;

/**
 * Umbrella Post Message
 *
 */
public class App {
	public static void main(String[] args) throws ForecastException, ClientProtocolException, IOException {

		// Get Daily weather - rainforcast from darksky
		ForecastRequest requestForecast = new ForecastRequestBuilder()
				.key(new APIKey("95390164c6d5d092a538ead432d92c68"))
				// .time(Instant.now().minus(5, ChronoUnit.DAYS))
				.language(ForecastRequestBuilder.Language.en).units(ForecastRequestBuilder.Units.us)
				.exclude(ForecastRequestBuilder.Block.minutely).exclude(ForecastRequestBuilder.Block.daily)
				.exclude(ForecastRequestBuilder.Block.currently)
				.location(new GeoCoordinates(new Longitude(91.726486), new Latitude(25.277685))).build();
		// System.out.println(requestForecast.url());

		DarkSkyJacksonClient client = new DarkSkyJacksonClient();
		Forecast forecast = client.forecast(requestForecast);
		forecast.setTimezone("Asia/Kolkata");

		StringBuffer sb = new StringBuffer();
		sb.append("City: Cherrapunji");
		sb.append("\n Location (lat, lon): " + forecast.getLatitude().value() + ", " + forecast.getLongitude().value());
		sb.append("\n Timezone: " + forecast.getTimezone());
		sb.append("\n Summary: " + forecast.getHourly().getSummary());
		sb.append(
				"\n Rain Precipitation Probability : " + forecast.getHourly().getData().get(0).getPrecipProbability());
		sb.append("\n Bring your Umbrella - It is going to Rain.....");
		System.out.println(sb.toString());

		// Post Message in Slack Channel if Rain precipitation is > 0.0

		if (forecast.getHourly().getData().get(0).getPrecipProbability() > 0.0) {
			String payload = "{\"text\": \"" + sb.toString() + "\"}";

			StringEntity entity = new StringEntity(payload, ContentType.APPLICATION_JSON);

			HttpClient httpClient = HttpClientBuilder.create().build();
			HttpPost requestSlack = new HttpPost(
					"https://hooks.slack.com/services/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");
			requestSlack.setEntity(entity);

			HttpResponse response = httpClient.execute(requestSlack);
			System.out.println(response.getStatusLine().getStatusCode());
		} // end if
		else {
			System.out.println("Do not POST Slack Message : Its a Dry Day..");
		}
	} // end of main
} // end of class
