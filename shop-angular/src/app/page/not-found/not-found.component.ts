import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { take } from 'rxjs/operators';
import { Title } from '@angular/platform-browser';
import { MessageService } from '../../service/message.service';
import { Util } from 'src/app/object/util';
import { paths } from 'src/app/object/paths';
import { Constant } from 'src/app/object/constant';

@Component({
  selector: 'not-found',
  templateUrl: './not-found.component.html',
  styleUrls: ['./not-found.component.css']
})
export class NotFoundComponent implements OnInit {

  path: string;
  
  constructor(private route: ActivatedRoute, private titleService: Title, private messageService: MessageService) { }

  ngOnInit() {
    this.titleService.setTitle(Constant.pagetitle.notFound);
    this.messageService.clear();

    this.route.data.pipe(take(1))
      .subscribe((data: { path: string }) => {
        this.path = data.path;
      });
  }

  displayPath(): string {
    let pathDisplay: string = '';
    if (this.path && this.path !== '' ) {
      if(this.path.toLowerCase() === '/'+paths.shop.toLowerCase()) {
        pathDisplay = 'Shop';
      } else if (this.path.toLowerCase().startsWith('/'+paths.shop.toLowerCase())) {
        pathDisplay = this.path.slice(5);
      } else {
        pathDisplay = this.path;
      }      
      pathDisplay = Util.capitalize(pathDisplay.replace('/',''));      
    }
    return pathDisplay;
  }

}