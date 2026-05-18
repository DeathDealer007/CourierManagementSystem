import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AdminService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/admin`;

  constructor() {}

  getDashboardStats(): Observable<any> {
    return this.http.get(`${this.apiUrl}/dashboard`);
  }

  getAllDeliveries(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/deliveries`);
  }

  getAllUsers(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/users`);
  }

  getReports(): Observable<any> {
    return this.http.get(`${this.apiUrl}/reports`);
  }

  getAllHubs(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/hubs`);
  }

  resolveDelivery(id: number, status: string): Observable<any> {
    return this.http.put(`${this.apiUrl}/deliveries/${id}/resolve`, { status });
  }

  updateDeliveryStatus(id: number, status: string): Observable<any> {
    return this.http.put(`${this.apiUrl}/delivery/${id}`, { status });
  }
}
