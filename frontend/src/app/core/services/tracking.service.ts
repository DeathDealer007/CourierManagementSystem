import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Tracking, DeliveryProof, Document } from '../models/tracking.model';

@Injectable({
  providedIn: 'root'
})
export class TrackingService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/tracking`;

  constructor() {}

  addTracking(tracking: Tracking): Observable<Tracking> {
    return this.http.post<Tracking>(this.apiUrl, tracking);
  }

  getTrackingHistory(trackingNumber: string): Observable<Tracking[]> {
    return this.http.get<Tracking[]>(`${this.apiUrl}/${trackingNumber}`);
  }

  uploadDocument(file: File, deliveryId: number, userId: number): Observable<Document> {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('deliveryId', deliveryId.toString());
    formData.append('userId', userId.toString());
    return this.http.post<Document>(`${this.apiUrl}/documents/upload`, formData);
  }

  getProof(deliveryId: number): Observable<DeliveryProof[]> {
    return this.http.get<DeliveryProof[]>(`${this.apiUrl}/${deliveryId}/proof`);
  }

  addProof(deliveryId: number, proof: DeliveryProof): Observable<DeliveryProof> {
    return this.http.post<DeliveryProof>(`${this.apiUrl}/${deliveryId}/proof`, proof);
  }
}
