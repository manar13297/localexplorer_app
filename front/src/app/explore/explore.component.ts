import {Component, CUSTOM_ELEMENTS_SCHEMA, OnInit} from '@angular/core';
import {NgFor, NgIf} from "@angular/common";
import {HttpClientModule} from "@angular/common/http";
import {WeatherService} from "../services/weather.service";
import {catchError, forkJoin, map, of, switchMap} from "rxjs";
import {MapsService} from "../services/maps.service";
import * as mapboxgl from 'mapbox-gl';


@Component({
  selector: 'app-explore',
  standalone: true,
  imports: [NgFor,HttpClientModule, NgIf],
  templateUrl: './explore.component.html',
  styleUrl: './explore.component.css',
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class ExploreComponent implements OnInit{

  activities: any[] = [];
  isLoading: boolean = false;
  toggleFavourite(activity: any) {
    activity.isFavourite = !activity.isFavourite;
  }

  constructor(private weatherService: WeatherService, private mapsService: MapsService) {

  }

  ngOnInit() {
    this.getUserLocation();
  }

  getUserLocation() {
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(position => {
        this.getWeatherData(position.coords.latitude, position.coords.longitude);
        console.log(position.coords.latitude , position.coords.longitude);
      });
    } else {
      console.error('Geolocation is not supported by this browser.');
    }
  }

  getWeatherData(lat: number, lon: number) {
    this.isLoading = true;
    this.weatherService.getWeather(lat, lon).subscribe((weatherData) => {
      const location = weatherData.name;
      const weatherDescription = weatherData.weather[0].main;
      const time = new Date(weatherData.dt * 1000).toLocaleTimeString();
      const explorerId: number = 2;
      this.sendDataToBackend(location, weatherDescription, time, explorerId);
      console.log("weather data:", weatherData)
    });
  }

  sendDataToBackend(location: string,  weatherDescription: string, time: string, explorerId: number) {
    this.isLoading = true;

    this.weatherService.sendWeatherDataToBackend(location, weatherDescription, time, explorerId).subscribe(response => {
      const activityObservables = response.activities.map((activity: any) =>
        this.weatherService.getImageUrl(activity.name + ","+activity.location).pipe(
          switchMap(imageUrl =>
            this.mapsService.geocodeLocation(activity.locationHint+ ","+ activity.location).pipe(
              map(coords => ({
                name: activity.name,
                description: activity.description,
                locationHint: activity.locationHint ,
                isFavourite: false,
                location: location,
                imageUrl: imageUrl,
                lat: coords.lat,
                lng: coords.lon,
              })),
              catchError(() => of({
                name: activity.name,
                description: activity.description,
                locationHint: activity.locationHint,
                location: location,
                imageUrl: '/assets/placeholder.png',
                isFavourite: activity.isFavourite,
                lat: 0,
                lng: 0,
              }))
            )
          ),
          catchError(() => of({
            name: activity.name,
            description: activity.description,
            locationHint: activity.locationHint,
            location: location,
            imageUrl: '/assets/placeholder.png',
            isFavourite: activity.isFavourite,
            lat: 0,
            lng:0 ,
          }))
        )
      );

      forkJoin(activityObservables).subscribe(completedActivities => {
        this.activities = completedActivities as any [];
        this.activities.forEach((activity, index) => {
          setTimeout(() => this.initializeMap(activity, index), 0);
        });
        console.log("activities", this.activities);
        this.isLoading = false;
      }, error => {
        console.error('Error processing activities:', error);
        this.isLoading = false;
      });
    }, error => {
      console.error('Error receiving data from backend:', error);
      this.isLoading = false;
    });


  }


  initializeMap(activity: { lng: number; lat: number; }, index: number) {
    const mapElementId = `map-${index}`;
    const map = new mapboxgl.Map({
      accessToken:
        'pk.eyJ1IjoibWFuYXItIiwiYSI6ImNscnphODFmcTFoYnkyam14c2QyYWlvNHcifQ.vzl9ErXbpHp99flkqgQi2g',
      container: mapElementId,
      style: 'mapbox://styles/mapbox/streets-v11',
      center: [activity.lng, activity.lat],
      zoom: 9
    });

    new mapboxgl.Marker()
      .setLngLat([activity.lng, activity.lat])
      .addTo(map);
  }
}
