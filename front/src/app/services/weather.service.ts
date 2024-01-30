import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {map, Observable} from 'rxjs';
import {environment} from "../../environment/environment";
@Injectable({
  providedIn: 'root'
})
export class WeatherService {
  private unsplashApiUrl = environment.UNSPLASH_API_URL;
  private unsplashApiKey = environment.UNSPLASH_API_KEY
  private weatherApiUrl = environment.WEATHER_API_URL
  private backendApiUrl = environment.BACKEND_API_URL+"/activities"
  private appid = environment.APP_ID

  constructor(private http: HttpClient) {}

  getWeather(lat: number, lon: number): Observable<any> {
    return this.http.get(`${this.weatherApiUrl}?lat=${lat}&lon=${lon}&appid=${this.appid}`);
  }
  getImageUrl(searchQuery: string): Observable<string> {
    const params = new HttpParams()
      .set('query', searchQuery)
      .set('client_id', this.unsplashApiKey)
      .set('per_page', '1');

    return this.http.get<any>(`${this.unsplashApiUrl}`, { params }).pipe(
      map(response => response.results[0]?.urls?.regular || '/assets/placeholder.png')
    );
  }

  getWeatherByCityName(cityName: string): Observable<any> {
    const url = `${this.weatherApiUrl}?q=${cityName}&appid=${this.appid}`;
    return this.http.get(url);
  }

  sendWeatherDataToBackend(location: string, weatherDescription: string, time: string, explorerId: number): Observable<any> {
    const dataToSend = { location, weatherDescription, time , explorerId};
    return this.http.post(this.backendApiUrl, dataToSend);
  }

}
