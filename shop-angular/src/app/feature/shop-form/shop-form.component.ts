import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

import {
	FormBuilder,
	FormControl,
	FormGroup,
	FormArray} from '@angular/forms';

import { InventoryItem } from '../../object/inventory-item';

import { ShopRestService } from '../../service/shop-rest.service';
import { MessageService, Message } from '../../service/message.service';
import { AuthenticationService } from '../../service/authentication.service';

@Component({
	selector: 'app-shop-form',
	templateUrl: './shop-form.component.html',
	styleUrls: ['./shop-form.component.css']
})
export class ShopFormComponent implements OnInit {
	hideItemList = true;
	saveResult = '';
	productForm = this.formBuilder.group({
		itemList: new FormArray([])
	});

	constructor(
		private authService: AuthenticationService,
		private rest: ShopRestService,
		private formBuilder: FormBuilder,
		private messageService: MessageService
	) { }

	ngOnInit() {
		//redirect to homepage if a user is not logged in
		this.authService.redirectToHome(false);
		this.getContent();
	}	

	createItem(): FormGroup {
		return this.formBuilder.group(new InventoryItem());
	}

	addProduct(): void {
		this.itemList.insert(0, this.createItem());
	}

	deleteProduct(item: number): void {
		this.productForm.markAsTouched();
		this.itemList.removeAt(item);
	}

	resetProducts(): void {
		this.messageService.clear();
		this.itemList.reset();
		this.productForm = this.formBuilder.group({
			itemList: new FormArray([])
		});
		this.getContent();
	}

	get itemList() {
		return this.productForm.get('itemList') as FormArray;
	}

	getContent() {
		this.rest.getContent().subscribe((data: { itemList: [] }) => {
			if (data) {
				this.setItemForm(data);
			} else {
				this.hideItemList = true;
			}
		});
	}

	onSubmit() {
		// TODO: Use EventEmitter with form value
		const saveData = [];
		this.productForm.value['itemList'].forEach(item => {
			saveData.push(new InventoryItem(item));
		});

		this.rest.saveContent(saveData).subscribe((data: { itemList: [] }) => {
			if (data) {
				this.itemList.reset();
				this.productForm = this.formBuilder.group({
					itemList: new FormArray([])
				});

				this.setItemForm(data);
				const message = new Message({
					type: 'info',
					messageDisplay: 'Successfully Saved Items'
				});
				this.messageService.add(message);
			}
		});
	}

	setItemForm(data: { itemList: [] }) {
		if (data && data.itemList && data.itemList.length) {
			data.itemList.forEach(dataContent => {
				const item = new InventoryItem(dataContent);

				const itemFormGroup = new FormGroup({
					id: new FormControl(item.id),
					title: new FormControl(item.title),
					manufacturer: new FormControl(item.manufacturer),
					price: new FormControl(item.price),
					releaseDate: new FormControl(new Date(item.releaseDate)),
					type: new FormControl(item.type)
				});

				this.itemList.push(itemFormGroup);
			});
			this.hideItemList = false;
		} else {
			const message = new Message({
				type: 'warn',
				messageDisplay: 'No Items found'
			});
			this.messageService.add(message);
			this.hideItemList = true;
		}
	}
}
