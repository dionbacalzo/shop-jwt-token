
import { Injectable, OnInit } from '@angular/core';
import { SwUpdate } from '@angular/service-worker';
import { interval } from 'rxjs/internal/observable/interval';

/**
 * Detects backend changes and automatically reloads the page when deployed to production server
 */
@Injectable()
export class VersionUpdateService {

    constructor(public updates: SwUpdate) {
        if (updates.isEnabled) {
          // check every 15 minutes for updates
          interval((1000 * 60 * 15)*1).subscribe(() => updates.checkForUpdate()
            .then(() => console.log('checking backend updates')));
        }
      }

    public checkForUpdates(): void {
        this.updates.available.subscribe(event => this.promptUser());
      }
    
      private promptUser(): void {
        console.log('updating to new version');
          this.updates.activateUpdate().then(() => {
            document.location.reload()        
        }); 
      }
}
