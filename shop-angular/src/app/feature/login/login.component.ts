import { Component, OnInit } from '@angular/core';
import { AuthenticationService } from '../../service/authentication.service';
import { Router } from '@angular/router';

import { MessageService, Message, ErrorMessage } from '../../service/message.service';
import { FormGroup, FormControl, Validators } from '@angular/forms';

@Component({
	selector: 'app-login',
	templateUrl: './login.component.html',
	styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
	credentials = { username: '', password: '' };
	loginForm: FormGroup;
	hideLoginForm: boolean;
	errorLoginForm: boolean;

	constructor(
		private authService: AuthenticationService,
		private router: Router,
		private messageService: MessageService
	) { }

	ngOnInit() {
		this.loginForm = new FormGroup({
			'username': new FormControl(this.credentials.username, Validators.required),
			'password': new FormControl(this.credentials.password, Validators.required)
		});
		this.hideLoginForm = false;
		this.errorLoginForm = false;
		// no need to redirect now that login component is at homepage
		// this.authService.redirectToHome(true);
		// this.messageService.clear();
	}

	get username() { return this.loginForm.get('username'); }

	get password() { return this.loginForm.get('password'); }

	login() {
		this.hideLoginForm = true;
		this.errorLoginForm = false;
		this.credentials = this.loginForm.value;
		this.messageService.clear();
		this.authService.authenticate(this.credentials, data => {			
			if (data) {				
				if (data.status === 'FAIL') {
					this.messageService.add(new ErrorMessage({
						messageDisplay: data.message
					}));
				} else {
					this.messageService.add(
						new Message({
							type: 'info',
							messageDisplay: data.message,
							persist: 0
						})
					);
					this.router.navigateByUrl('/shop');
				}				
			} else {
				this.errorLoginForm = true;
			}
			this.hideLoginForm = false;
		});
	}
	
}
