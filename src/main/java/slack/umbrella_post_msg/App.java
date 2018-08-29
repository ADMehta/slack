/** @author apeksha mehta
 * 
 */
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

public class App {
	public static final String FORECAST_REQUEST_APIKEY = "95390164c6d5d092a538ead432d92c68";
	public static final String TIMEZONE = "Asia/Kolkata";
	public static final double LAT = 25.277685;
	public static final double LON = 91.726486;
	public static final String CITY = "Cherapunji";
	public static StringBuffer sb = new StringBuffer();
/**
 * Slack App...
 * To Post in #bring-an-umbrella channel whenever it’s supposed to rain
 * @param args String args
 * @throws ForecastException ForecastException
 * @throws ClientProtocolException ClientProtocolException
 * @throws IOException IOException
 */

	public static void main(String[] args) throws ForecastException, ClientProtocolException, IOException {
		double rainPrec = getDailyRain();
		if (rainPrec > 0.0) {
			postSlackMsg();
		} else {
			System.out.println("Do not POST Slack Message : Its a Dry Day..");
		}
	}

	/**
	 * Get Daily weather rain forecast from darksky
	 * 
	 * @return rain PrecipProbability
	 * @throws ForecastException
	 */
	static double getDailyRain() throws ForecastException {
		ForecastRequest requestForecast = new ForecastRequestBuilder().key(new APIKey(FORECAST_REQUEST_APIKEY))
				.language(ForecastRequestBuilder.Language.en).units(ForecastRequestBuilder.Units.us)
				.exclude(ForecastRequestBuilder.Block.minutely).exclude(ForecastRequestBuilder.Block.daily)
				.exclude(ForecastRequestBuilder.Block.currently)
				.location(new GeoCoordinates(new Longitude(LON), new Latitude(LAT))).build();
		// System.out.println(requestForecast.url());

		DarkSkyJacksonClient client = new DarkSkyJacksonClient();
		Forecast forecast = client.forecast(requestForecast);

		forecast.setTimezone(TIMEZONE);
		sb.append(CITY);
		sb.append("\n Location (lat, lon): " + forecast.getLatitude().value() + ", " + forecast.getLongitude().value());
		sb.append("\n Timezone: " + forecast.getTimezone());
		sb.append("\n Summary: " + forecast.getHourly().getSummary());
		sb.append(
				"\n Rain Precipitation Probability : " + forecast.getHourly().getData().get(0).getPrecipProbability());
		sb.append("\n Bring your Umbrella - It is going to Rain.....");
		System.out.println(sb.toString());

		return (forecast.getHourly().getData().get(0).getPrecipProbability());
	}

	/**
	 * Post Message in Slack Channel
	 * 
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	static void postSlackMsg() throws ClientProtocolException, IOException {
		String payload = "{\"text\": \"" + sb.toString() + "\"}";
		StringEntity entity = new StringEntity(payload, ContentType.APPLICATION_JSON);

		HttpClient httpClient = HttpClientBuilder.create().build();
		HttpPost requestSlack = new HttpPost(
				"https://hooks.slack.com/services/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");
		requestSlack.setEntity(entity);

		HttpResponse response = httpClient.execute(requestSlack);
		System.out.println(response.getStatusLine().getStatusCode());
	}

}
