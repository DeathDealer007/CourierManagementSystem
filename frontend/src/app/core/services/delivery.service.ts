import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Delivery } from '../models/delivery.model';

@Injectable({
  providedIn: 'root'
})
export class DeliveryService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/deliveries`;

  constructor() {}

  createDelivery(delivery: Delivery): Observable<Delivery> {
    return this.http.post<Delivery>(this.apiUrl, delivery);
  }

  getAllDeliveries(): Observable<Delivery[]> {
    return this.http.get<Delivery[]>(this.apiUrl);
  }

  getDeliveryById(id: number): Observable<Delivery> {
    return this.http.get<Delivery>(`${this.apiUrl}/${id}`);
  }

  getMyDeliveries(): Observable<Delivery[]> {
    return this.http.get<Delivery[]>(`${this.apiUrl}/my`);
  }

  getDeliveryByTracking(trackingNumber: string): Observable<Delivery> {
    return this.http.get<Delivery>(`${this.apiUrl}/tracking/${trackingNumber}`);
  }

  updateDeliveryStatus(id: number, status: string): Observable<Delivery> {
    return this.http.put<Delivery>(`${this.apiUrl}/${id}`, { status });
  }
}
