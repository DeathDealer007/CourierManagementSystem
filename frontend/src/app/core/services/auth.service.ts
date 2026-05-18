import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { User } from '../models/user.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/auth`;
  
  private currentUserSubject = new BehaviorSubject<User | null>(this.getUserFromStorage());
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor() {}

  register(user: User): Observable<string> {
    return this.http.post(`${this.apiUrl}/register`, user, { responseType: 'text' });
  }

  login(user: User): Observable<string> {
    // Add dummy email to satisfy backend @NotBlank validation on User entity
    const loginPayload = { ...user, email: 'dummy@example.com' };
    return this.http.post(`${this.apiUrl}/login`, loginPayload, { responseType: 'text' }).pipe(
      tap(token => {
        const cleanToken = token.startsWith('"') && token.endsWith('"') ? token.slice(1, -1) : token;
        localStorage.setItem('token', cleanToken);
        const decoded = this.decodeToken(cleanToken);
        if (decoded) {
          const loggedInUser: User = { 
            id: decoded.userId,
            username: decoded.sub, 
            email: '',
            roles: decoded.roles ? decoded.roles.map((r: string) => ({ name: r })) : []
          };
          this.currentUserSubject.next(loggedInUser);
        }
      })
    );
  }

  logout() {
    localStorage.removeItem('token');
    this.currentUserSubject.next(null);
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  private getUserFromStorage(): User | null {
    const token = this.getToken();
    if (!token) return null;
    const decoded = this.decodeToken(token);
    if (!decoded) return null;
    return { 
      id: decoded.userId,
      username: decoded.sub, 
      email: '',
      roles: decoded.roles ? decoded.roles.map((r: string) => ({ name: r })) : []
    };
  }

  private decodeToken(token: string) {
    try {
      const base64Url = token.split('.')[1];
      const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
      const jsonPayload = decodeURIComponent(window.atob(base64).split('').map(function(c) {
        return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
      }).join(''));
      return JSON.parse(jsonPayload);
    } catch (e) {
      return null;
    }
  }
}
