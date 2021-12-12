import { Component, OnInit } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { MessageService } from '../../service/message.service';
import { AuthenticationService } from '../../service/authentication.service';
import { Constant } from 'src/app/object/constant';

@Component({
	selector: 'app-homepage',
	templateUrl: './homepage.component.html',
	styleUrls: ['./homepage.component.css']
})
export class HomepageComponent implements OnInit {

	pictureSrc: String | ArrayBuffer = 'assets/images/default-pic.png';

	carouselSrc = Constant.carouselSrc;

	constructor(
		private authService: AuthenticationService,
		private titleService: Title,
		private messageService: MessageService
	) { }

	ngOnInit() {
		this.titleService.setTitle(Constant.pagetitle.home);
		this.messageService.clear();		
	}

	get isAuthenticated(): boolean {
		return this.authService.authenticated;
	}

	get user() {
		return this.authService.user;
	}
}
