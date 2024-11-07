import { Component, ViewEncapsulation } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PaginationModule } from '@alfresco/adf-core';
import { DocumentListModule } from '@alfresco/adf-content-services';

@Component({
  selector: 'lib-trashcan-component',
  standalone: true,
  imports: [CommonModule, DocumentListModule, PaginationModule],
  templateUrl: './trashcan.component.html',
  styleUrls: ['./trashcan.component.css'],
  encapsulation: ViewEncapsulation.None
})
export class TrashcanComponent {}
