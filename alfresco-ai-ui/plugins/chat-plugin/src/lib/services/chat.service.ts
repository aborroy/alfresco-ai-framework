import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ResponseData } from '../types';

const CHAT_URL = 'http://localhost:9999/chat';

@Injectable({
  providedIn: 'root'
})
export class ChatService {
  constructor(private http: HttpClient) {}

  answerPrompt(prompt: string): Observable<ResponseData> {
    const body = prompt;
    return this.http.post<ResponseData>(CHAT_URL, body, {
      headers: {
        'Content-Type': 'text/plain; charset=utf-8'
      }
    });
  }
}
