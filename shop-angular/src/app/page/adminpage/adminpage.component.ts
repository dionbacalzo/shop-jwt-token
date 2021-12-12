import { Component, OnInit } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { MessageService } from '../../service/message.service';
import { Constant } from 'src/app/object/constant';

@Component({
	selector: 'app-adminpage',
	templateUrl: './adminpage.component.html',
	styleUrls: ['./adminpage.component.css']
})
export class AdminpageComponent implements OnInit {

	constructor(private titleService: Title, private messageService: MessageService) { }

	ngOnInit() {
		this.titleService.setTitle(Constant.pagetitle.admin);
		this.messageService.clear();
	}

}
