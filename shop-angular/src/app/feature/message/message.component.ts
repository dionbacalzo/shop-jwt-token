import { Component, OnInit } from "@angular/core";

import { MessageService, Message } from "../../service/message.service";

@Component({
	selector: "app-message",
	templateUrl: "./message.component.html",
	styleUrls: ["./message.component.css"]
})
export class MessageComponent implements OnInit {
	messageContainerList: {}[] = [];

	constructor(public messageService: MessageService) {}

	ngOnInit() {
		//run function everytime the message content is added
		this.messageService.messageSubject.subscribe(data => {			
			this.setContainerColor();
		});
	}

	get messagesValues(): any[] {
		return this.messageService.messages;
	}

	setContainerColor() {
		let newMessageContainerList: {}[] = [];
		this.messagesValues.forEach(data => {
			let backgroundColor: string = "#fff";
				if (data) {
					if (data.type == "error") {
						backgroundColor = "#ff0000"; //red
					} else if (data.type == "info") { 
						backgroundColor = "#1d730d"; //green
					} else if (data.type == "warn") {
						backgroundColor = "#dacc27"; //yellow
					}
				}
				let newMessageContainer = {
					message: data,
					backgroundColor: backgroundColor
				}
				newMessageContainerList.push(newMessageContainer);
		});
		this.messageContainerList = newMessageContainerList;
	}

	remove(index) {
		this.messageContainerList.splice(index, 1)
	}
}
