import { Component, OnInit, ViewEncapsulation, inject } from '@angular/core';
import { ActivatedRoute, PRIMARY_OUTLET, Router } from '@angular/router';
import {
  AlfrescoViewerModule,
  ContentMetadataModule,
  FileUploadErrorEvent,
  NodeCommentsModule,
  NodesApiService,
  VersionManagerModule
} from '@alfresco/adf-content-services';
import { InfoDrawerModule, NotificationService, ViewerModule } from '@alfresco/adf-core';
import { CommonModule } from '@angular/common';
import { TranslateModule } from '@ngx-translate/core';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';

@Component({
  standalone: true,
  imports: [
    CommonModule,
    InfoDrawerModule,
    TranslateModule,
    VersionManagerModule,
    NodeCommentsModule,
    AlfrescoViewerModule,
    ViewerModule,
    ContentMetadataModule,
    MatIconModule,
    MatButtonModule
  ],
  selector: 'lib-preview-component',
  templateUrl: 'preview.component.html',
  styleUrls: ['preview.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class PreviewComponent implements OnInit {
  nodeId: string | null = null;

  private notificationService = inject(NotificationService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private nodeApiService = inject(NodesApiService);

  ngOnInit() {
    this.route.params.subscribe((params) => {
      const id = params['nodeId'];
      if (id) {
        this.nodeApiService.getNode(id).subscribe(
          (node) => {
            if (node && node.isFile) {
              this.nodeId = id;
              return;
            }
            this.router.navigate(['/files', id]);
          },
          () => this.router.navigate(['/files', id])
        );
      }
    });
  }

  onUploadError(event: FileUploadErrorEvent) {
    const errorMessage = event.error;
    this.notificationService.showError(errorMessage);
  }

  onViewerVisibilityChanged() {
    const primaryUrl = this.router.parseUrl(this.router.url).root.children[PRIMARY_OUTLET].toString();
    void this.router.navigateByUrl(primaryUrl);
  }
}
