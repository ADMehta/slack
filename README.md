# Slack App

## Insight into the App, what it does!
Purpose : To get real time weather data for your city and posts a message in #bring-an-umbrella slack channel if the rain Precipitation is > 0.0

* Researched online for available weather API to get rain - precipitation data. - Used darksky.forecast.api -(https://github.com/200Puls/darksky-forecast-api) Included in dependencies of java project.
* Get rain precipitation for a city by providing longitude and latitude. I used Lat, Lon for City  - Cherrapunji ( The wettest place on earth)  :)
* POST Call : generated URL to drysky.net forecast AP - obtained hourly weather  `https://api.darksky.net/forecast/95390164c6d5d092a538ead432d92c68/37.8272,-122.2913?lang=en&units=us&exclude=minutely,currently,daily`
* Derived Rain precipitation probability value
* if Rain precipitation probability is  > 0.0 Then
* Make a POST call to the  Incoming Webhooks - channel:  \#bring-an-umbrella.

## Technical Details, how it works!

**Set up Incoming Web-hook** 
1. Created Slack Workspace - https://inventing-group.slack.com/
2. Created Channel - #bring-an-umbrella.
3. Created Slack App -   Bring Umbrella in inventing  workspace using - https://api.slack.com/incoming-webhooks 
4. Activate Incoming Webhooks -  to send realtime messages to #bring-an-umbrella Channel
5. Added New Webhook to the workspace -> channel #bring-an-umbrella.
6. Verified web hook connection by posting curl command / postman - POST method..Success.!!

**Created Maven Java Project**
Tools used: eclipse, crontab 
Java Documentation: can be found under .../doc (in project root)

**Build and Run Package**
`$ mvn clean package`
generated jar at location : .../target/slackmsg-0.0.1-SNAPSHOT-jar-with-dependencies.jar

**Scheduler**
In order to post slack message every 3 hours, set up a cron Job to run at 0th minute of every 3 hour of the day
`$ crontab -e 
0 */3 * * * java -jar <.../target/slackmsg-0.0.1-SNAPSHOT-jar-with-dependencies.jar>`



