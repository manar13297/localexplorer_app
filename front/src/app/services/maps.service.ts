import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {catchError, map, Observable, of} from "rxjs";
@Injectable({
  providedIn: 'root'
})
export class MapsService {
  private geocodeApiUrl = 'https://api.mapbox.com/geocoding/v5/mapbox.places';
  private accessToken = 'pk.eyJ1IjoibWFuYXItIiwiYSI6ImNscnphODFmcTFoYnkyam14c2QyYWlvNHcifQ.vzl9ErXbpHp99flkqgQi2g';

  constructor(private http: HttpClient) {}
  geocodeLocation(locationHint: string): Observable<{ lat: number; lon: number }> {
    const apiUrl = `${this.geocodeApiUrl}/${encodeURIComponent(locationHint)}.json?access_token=${this.accessToken}`;

    return this.http.get<any>(apiUrl).pipe(
      map((response) => {
        if (response.features && response.features.length > 0) {
          const [lonStr, latStr] = response.features[0].center;

          const lon = parseFloat(lonStr);
          const lat = parseFloat(latStr);

          console.log('Parsed coordinates:', { lat, lon });
          if (isFinite(lat) && isFinite(lon)) {
            return { lat, lon };
          } else {
            throw new Error('Invalid coordinates: lat or lon is not a finite number');
          }
        } else {
          throw new Error('Geocoding failed');
        }
      }),
      catchError((error) => {
        console.error('Geocoding error:', error);
        return of({ lat: 0, lon: 0 });
      })
    );
  }


}
