import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { FormsModule } from '@angular/forms';
import { ChatService } from '../services/chat.service';
import { MatTableModule } from '@angular/material/table';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSelectModule } from '@angular/material/select';
import { ResponseData } from '../types';

@Component({
  selector: 'lib-chat-plugin',
  standalone: true,
  imports: [
    CommonModule,
    MatInputModule,
    MatButtonModule,
    MatFormFieldModule,
    FormsModule,
    MatIconModule,
    MatTableModule,
    MatProgressSpinnerModule,
    MatSelectModule
  ],
  templateUrl: './chat-plugin.component.html',
  styleUrls: ['./chat-plugin.component.css']
})
export class ChatPluginComponent {
  private _results!: ResponseData;
  loading = false;
  header = '';

  promptString = '';

  onPrompt() {
    this.loading = true;
    this.searchService.answerPrompt(this.promptString).subscribe({
      next: (res) => {
        this._results = res;
        this.loading = false;
        this.header = this.promptString;
      }
    });
  }

  answerResults(): ResponseData {
    return this._results;
  }

  constructor(private searchService: ChatService) {}
}
