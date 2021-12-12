import { Component, OnInit } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { MessageService } from '../../service/message.service';
import { Constant } from 'src/app/object/constant';

@Component({
	selector: 'app-signuppage',
	templateUrl: './signuppage.component.html',
	styleUrls: ['./signuppage.component.css']
})
export class SignuppageComponent implements OnInit {

	constructor(private titleService: Title, private messageService: MessageService) { }

	ngOnInit() {
		this.titleService.setTitle(Constant.pagetitle.signup);
		this.messageService.clear();
	}

}
