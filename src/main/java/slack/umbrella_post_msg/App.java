/** 
 * @author apeksha mehta
 * https://github.com/ADMehta/slack
 */
package slack.umbrella_post_msg;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
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
	private static final String SLACK_WEBHOOK = "https://hooks.slack.com/services/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";
	private static final String FORECAST_REQUEST_APIKEY = "95390164c6d5d092a538ead432d92c68";
	private static final String TIMEZONE = "Asia/Kolkata";
	private static final double LAT = 25.277685;
	private static final double LON = 91.726486;
	private static final int HTTP_STATUS_CODE = 200;
	private static final String CITY = "Cherapunji";

	/**
	 * Slack App... To Post in #bring-an-umbrella channel whenever it’s supposed to rain
	 * 
	 * @param args
	 *            String args
	 * @throws ForecastException
	 *             ForecastException
	 * @throws ClientProtocolException
	 *             ClientProtocolException
	 * @throws IOException
	 *             IOException
	 */
	public static void main(String[] args) throws ForecastException, ClientProtocolException, IOException {
		String msg = getDailyRain();
		if(!msg.equals(null) && !msg.equals("")) {
			String respCode = postSlackMsg(msg);
		}
	}

	/**
	 * Get Daily weather rain forecast from darksky
	 * 
	 * @return Rain forecast message
	 * @throws ForecastException
	 */
	static String getDailyRain() throws ForecastException {
		StringBuffer sb = new StringBuffer();
		ForecastRequest requestForecast = new ForecastRequestBuilder().key(new APIKey(FORECAST_REQUEST_APIKEY))
				.language(ForecastRequestBuilder.Language.en).units(ForecastRequestBuilder.Units.us)
				.exclude(ForecastRequestBuilder.Block.minutely).exclude(ForecastRequestBuilder.Block.daily)
				.exclude(ForecastRequestBuilder.Block.currently)
				.location(new GeoCoordinates(new Longitude(LON), new Latitude(LAT))).build();

		DarkSkyJacksonClient client = new DarkSkyJacksonClient();
		Forecast forecast = client.forecast(requestForecast);

		if (forecast.getHourly().getData().get(0).getPrecipProbability() == 0.0) {
			forecast.setTimezone(TIMEZONE);
			sb.append(CITY);
			sb.append("\n Location (lat, lon): " + forecast.getLatitude().value() + ", "
					+ forecast.getLongitude().value());
			sb.append("\n Timezone: " + forecast.getTimezone());
			sb.append("\n Summary: " + forecast.getHourly().getSummary());
			sb.append("\n Rain Precipitation Probability : "
					+ forecast.getHourly().getData().get(0).getPrecipProbability());
			sb.append("\n Bring your Umbrella - It is going to Rain.....");
		} 
		
		return (sb.toString());
	}

	/**
	 * Post Message in Slack Channel
	 * 
	 * @param msg
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	static String postSlackMsg(String msg) throws IOException {
		String payload = "{\"text\": \"" + msg + "\"}";
		StringEntity entity = new StringEntity(payload, ContentType.APPLICATION_JSON);

		HttpClient httpClient = HttpClientBuilder.create().build();
		HttpPost requestSlack = new HttpPost(SLACK_WEBHOOK);
		requestSlack.setEntity(entity);
		HttpResponse response = null;
		try {
			response = httpClient.execute(requestSlack);
			if (response.getStatusLine().getStatusCode() != HTTP_STATUS_CODE) {
				throw new HttpResponseException(response.getStatusLine().getStatusCode(),
						"Failed to post message on slack channel");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return String.valueOf(response.getStatusLine().getStatusCode());
	}

}
