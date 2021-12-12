import { Component, OnInit } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { MessageService } from '../../service/message.service';
import { Constant } from 'src/app/object/constant';

@Component({
  selector: 'app-profilepage',
  templateUrl: './profilepage.component.html',
  styleUrls: ['./profilepage.component.css']
})
export class ProfilepageComponent implements OnInit {

    constructor(private titleService: Title, private messageService: MessageService) { }

    ngOnInit() {
        this.titleService.setTitle(Constant.pagetitle.updateProfile);
        this.messageService.clear();
    }

}
