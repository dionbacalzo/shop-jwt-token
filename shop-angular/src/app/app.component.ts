import { Component, OnInit } from '@angular/core';
import { AuthenticationService } from './service/authentication.service';
import { VersionUpdateService } from './service/version-update.service';

@Component({
	selector: 'app-root',
	templateUrl: './app.component.html',
	styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {

	constructor(private authService: AuthenticationService,
		private sw: VersionUpdateService) { }

	ngOnInit() {
		this.sw.checkForUpdates();
		if (!this.isAuthenticated) {
			//this.authService.getUser().subscribe();
		}
	}

	logout() {		
		this.authService.logout()/*.subscribe()*/;
	}

	get isAuthenticated(): boolean {		
		return this.authService.authenticated;
	}

	get isAdmin(): boolean {
		let isAdmin = false;		
		if (this.authService.user.role === 'ADMIN') {
			isAdmin = true;
		}
		return isAdmin;
	}
}
