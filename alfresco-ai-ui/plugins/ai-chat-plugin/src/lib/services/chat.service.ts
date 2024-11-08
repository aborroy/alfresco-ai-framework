import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { ResponseData, DocumentMetadata } from '../types';
import { ConfigService } from '../../app.config.service';

@Injectable({
  providedIn: 'root'
})
export class ChatService {
  constructor(private http: HttpClient, private configService: ConfigService) {}

  answerPrompt(prompt: string): Observable<ResponseData> {
    const body = prompt;
    return this.http
      .post<ResponseData>(this.configService.chatServer + '/chat', body, {
        headers: {
          'Content-Type': 'text/plain; charset=utf-8'
        }
      })
      .pipe(map((response) => this.filterUniqueDocuments(response)));
  }

  private filterUniqueDocuments(response: ResponseData): ResponseData {
    const uniqueDocs = new Map<string, DocumentMetadata>();
    response.documentMetadata.forEach((doc) => {
      if (!uniqueDocs.has(doc.documentId) || uniqueDocs.get(doc.documentId)!.distance < doc.distance) {
        uniqueDocs.set(doc.documentId, doc);
      }
    });

    return {
      ...response,
      documentMetadata: Array.from(uniqueDocs.values())
    };
  }
}
