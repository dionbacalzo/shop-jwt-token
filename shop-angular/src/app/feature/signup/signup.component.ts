import { Component, OnInit } from '@angular/core';
import { AuthenticationService } from '../../service/authentication.service';
import { Router } from '@angular/router';

import { MessageService, ErrorMessage, Message } from '../../service/message.service';
import { FormGroup, FormControl, Validators, ValidationErrors } from '@angular/forms';

@Component({
	selector: 'app-signup',
	templateUrl: './signup.component.html',
	styleUrls: ['./signup.component.css']
})
export class SignupComponent implements OnInit {
	credentials = { username: '', password: '', retypePassword: '', firstname: '', lastname: '', role: 'USER' };
	signupForm: FormGroup;
	hideSignupForm: boolean;

	constructor(
		private authService: AuthenticationService,
		private messageService: MessageService
	) { }

	ngOnInit() {
		//redirect to homepage if a user is logged in
		this.authService.redirectToHome(true);
		this.hideSignupForm = false;
		this.signupForm = new FormGroup({
			'username': new FormControl(this.credentials.username, Validators.required),
			'password': new FormControl(this.credentials.password, Validators.required),
			'retypePassword': new FormControl(this.credentials.retypePassword, Validators.required),
			'firstname': new FormControl(this.credentials.firstname, Validators.required),
			'lastname': new FormControl(this.credentials.lastname, Validators.required),
			'role': new FormControl(this.credentials.role, Validators.required)
		}, {
				validators: this.MustMatch('password', 'retypePassword')
			});
	}

	get username() { return this.signupForm.get('username'); }
	get firstname() { return this.signupForm.get('firstname'); }
	get lastname() { return this.signupForm.get('lastname'); }
	get password() { return this.signupForm.get('password'); }
	get retypePassword() { return this.signupForm.get('retypePassword'); }

	signup() {
		this.hideSignupForm = true;
		this.credentials = this.signupForm.value;
		this.authService.signup(this.credentials, data => {
			if (data) {
				this.messageService.clear();
				if (data.status === 'FAIL') {
					this.messageService.add(
						new ErrorMessage({
							messageDisplay: data.message
						})
					);
				} else {
					this.messageService.add(
						new Message({
							type: 'info',
							messageDisplay: data.message
						})
					);
				}
			}
			this.hideSignupForm = false;
		});
	}

	MustMatch(controlName: string, matchingControlName: string) {
		return (formGroup: FormGroup): ValidationErrors | null => {
			const control = formGroup.controls[controlName];
			const matchingControl = formGroup.controls[matchingControlName];

			if (matchingControl.errors && !matchingControl.errors.mustMatch) {
				// return if another validator has already found an error on the matchingControl
				return;
			}

			// set error on matchingControl if validation fails
			if (control.value !== matchingControl.value) {
				matchingControl.setErrors({ mustMatch: true });
			} else {
				matchingControl.setErrors(null);
			}
		};
	}
}
