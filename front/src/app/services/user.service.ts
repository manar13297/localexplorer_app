import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {environment} from "../../environment/environment";

@Injectable({
  providedIn: 'root'
})
export class UserService {
  constructor(private http: HttpClient) {}

  private baseUrl = environment.BACKEND_API_URL+"/explorer";

  getExplorerById(id: number): Observable<any> {
    return this.http.get(`${this.baseUrl}/${id}`);
  }
}
