var five = require("johnny-five");
var Firebase = require("firebase");
var CronJob = require('cron').CronJob;
var Forecast = require('forecast');

var forecastCelsius = new Forecast({
  service: 'forecast.io',
  key: '8b74674f7d5ec7fc69ad39d856dd6a2b',
  units: 'celcius',
  cache: true,      
  ttl: {             
    minutes: 27,
    seconds: 45
    }
});

var forecastFahrenheit = new Forecast({
  service: 'forecast.io',
  key: '8b74674f7d5ec7fc69ad39d856dd6a2b',
  units: 'fahrenheit',
  cache: true,      
  ttl: {             
    minutes: 27,
    seconds: 45
    }
});

var board = new five.Board();
board.on("ready", function() {
  //LED on pin 11
  var led = new five.Led(5);
  var fan = new five.Motor({
    pins: {
      pwm: 3,
      dir: 12
    },
    invertPWM: true
  });
  var photoresistor = new five.Sensor({
    pin: "A2",
    freq: 250
  });
  var temperature = new five.Thermometer({
    controller: "LM35",
    pin: "A0"
  });

  var myLEDRef = new Firebase("https://miniprojecthello.firebaseio.com/led");
  var myFanRef = new Firebase("https://miniprojecthello.firebaseio.com/fan");
  var myStatusRef = new Firebase("https://miniprojecthello.firebaseio.com/homeStatus/1");
  var mySetingsRef = new Firebase("https://miniprojecthello.firebaseio.com/settings");

  var ls = false;
  var fs = false;
  var isAutoMode = false;
  var isCelcius = true;

  myLEDRef.on("child_added", function(snapshot) {
	var ledState = snapshot.val();
	console.log(ledState);
  ls = ledState.status;
	if(ledState.status == true){
		led.on();
		led.brightness(ledState.brightness);
	}
	else if(ledState.status == false){
		led.off();
	}
  });

  myLEDRef.on("child_changed", function(snapshot) {
	var ledState = snapshot.val();
	console.log(ledState);
  ls = ledState.status;
	if(ledState.status == true){
		led.on();
		led.brightness(ledState.brightness);
	}
	else if(ledState.status == false){
		led.off();
	}
  });

  myFanRef.on("child_added", function(snapshot) {
  var fanState = snapshot.val();
  console.log(fanState);
  fs=fanState.status;
  if(fs){
    fan.forward(fanState.speed);
  }
  else{
    fan.stop();
  }
  });

  myFanRef.on("child_changed", function(snapshot) {
  var fanState = snapshot.val();
  console.log(fanState);
  fs=fanState.status;
  if(fs){
    fan.forward(fanState.speed);
  }
  else{
    fan.stop();
  }
  });

  var jobStatus = new CronJob({
    cronTime: '*/5 * * * * *',
    onTick: function() {
      var p_percent = (100-photoresistor.scale([0, 100]).value).toFixed(2);
      console.log('p-val: '+photoresistor.value.toFixed(2));
      console.log(p_percent + " %");
      console.log(temperature.celsius.toFixed(2) + "°C", temperature.fahrenheit.toFixed(2) + "°F");      
      console.log(new Date()+' ');
      var b = Math.round(((100-p_percent)*255)/100);
      var temp =  Math.round(temperature.celsius);
      console.log(temp+' ')
      var s;
      if(temp<20){
        s=50;
      }
      else if(temp>=20 && temp<25){
        s=100;
      }
      else if(temp>=25 && temp<35){
        s=150;
      }
      else if(temp>=35 && temp<40){
        s=200;
      }
      else{
        s=255;
      }

      if(isCelcius){
        temp = Math.round(temperature.celsius)+' °C';
      }else{
        temp = Math.round(temperature.fahrenheit)+' °F';
      }

      myLEDRef.child(1).update({
        status: ls,
        brightness: b
      });
      myFanRef.child(1).update({
        status: fs,
        speed: s
      });

      myStatusRef.update({
        lastUpdate: new Date()+' ',
        lightIntensity: (100-photoresistor.scale([0, 100]).value).toFixed(2) + " %",
        temperature: temp
      });
    }
  });
  var jobWeather = new CronJob({
    cronTime: '*/10 * * * * *',
    onTick: function() {
      if(isCelcius){
        forecastCelsius.get([12.9726, 79.1626], function(err, weather) {
        if(err)
          return console.dir(err);
        console.log(weather.currently.icon);
        myStatusRef.update({
          lastUpdate: new Date()+' ',
          weather: weather.currently.summary+'. Feels like '+Math.round(weather.currently.apparentTemperature)+' °C'
        });
      });
      console.log(new Date()+' ');
      }else{
        forecastFahrenheit.get([12.9726, 79.1626], function(err, weather) {
        if(err)
          return console.dir(err);
        console.log(weather.currently.icon);
        myStatusRef.update({
          lastUpdate: new Date()+' ',
          weather: weather.currently.summary+'. Feels like '+Math.round(weather.currently.apparentTemperature)+' °F'
        });
      });
      console.log(new Date()+' ');

      }
      
    }
  });

  mySetingsRef.on("child_added", function(snapshot) {
  var settings = snapshot.val();
  console.log(settings);
  isAutoMode = settings.autoMode;
  isCelcius = settings.temperatureUnit;

  if(isAutoMode){
    jobStatus.start();
    jobWeather.start();
  }
  else{
    jobStatus.stop();
    jobWeather.stop();
  }
  });

  mySetingsRef.on("child_changed", function(snapshot) {
  var settings = snapshot.val();
  console.log(settings);
  isAutoMode = settings.autoMode;
  isCelcius = settings.temperatureUnit;
  
  if(isAutoMode){
    jobStatus.start();
    jobWeather.start();
  }
  else{
    jobStatus.stop();
    jobWeather.stop();
  }
  });
});