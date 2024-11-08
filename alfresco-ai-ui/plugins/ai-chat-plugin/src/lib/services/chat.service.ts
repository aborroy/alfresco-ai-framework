import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ResponseData } from '../types';
import { ConfigService } from '../../app.config.service';

@Injectable({
  providedIn: 'root'
})
export class ChatService {
  constructor(private http: HttpClient, private configService: ConfigService) {}

  answerPrompt(prompt: string): Observable<ResponseData> {
    const body = prompt;
    return this.http.post<ResponseData>(this.configService.chatServer + '/chat', body, {
      headers: {
        'Content-Type': 'text/plain; charset=utf-8'
      }
    });
  }
}
