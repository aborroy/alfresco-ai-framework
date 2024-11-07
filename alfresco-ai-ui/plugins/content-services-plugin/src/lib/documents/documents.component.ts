import { Component, ViewChild, Input, ViewEncapsulation, inject } from '@angular/core';
import { NotificationService, PaginationModule, ToolbarModule } from '@alfresco/adf-core';
import { BreadcrumbModule, ContentDirectiveModule, DocumentListComponent, DocumentListModule, UploadModule } from '@alfresco/adf-content-services';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'lib-documents-component',
  standalone: true,
  imports: [ToolbarModule, BreadcrumbModule, UploadModule, DocumentListModule, PaginationModule, ContentDirectiveModule],
  templateUrl: './documents.component.html',
  styleUrls: ['./documents.component.css'],
  encapsulation: ViewEncapsulation.None
})
export class DocumentsComponent {
  private notificationService = inject(NotificationService);
  private router = inject(Router);
  private activeRoute = inject(ActivatedRoute);

  @Input()
  showViewer = false;

  @ViewChild('documentList')
  documentList!: DocumentListComponent;

  uploadSuccess() {
    this.notificationService.openSnackMessage('File uploaded');
    this.documentList.reload();
  }

  showPreview(event: any) {
    const entry = event.value.entry;

    if (entry && entry.isFile) {
      void this.router.navigate(['.', { outlets: { viewer: ['viewer', entry.id] } }], { relativeTo: this.activeRoute });
    }
  }
}
