import { Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { MessageService } from '../../service/message.service';
import { ShopRestService } from '../../service/shop-rest.service';
import { InventoryItem } from '../../object/inventory-item';


@Component({
	selector: 'app-shop',
	templateUrl: './shop.component.html',
	styleUrls: ['./shop.component.css']
})
export class ShopComponent implements OnInit {
	hideItemList = true;
	errorItemList = false;
	emptyItemList = false;
	products: any[] = [];

	displayedColumns: string[] = ['title', 'price', 'type', 'manufacturer', 'releaseDate'];
	dataSource: MatTableDataSource<InventoryItem<{}>>;
	@ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
	@ViewChild(MatSort, { static: true }) sort: MatSort;

	constructor(
		private rest: ShopRestService,
		private messageService: MessageService
	) { }

	ngOnInit() {
		// this.messageService.clear();
		this.getContent();
	}

	getContent() {
		this.products = [];
		this.rest.getContent().subscribe((data: { itemList: [] }) => {
			if (!data) {
				this.hideItemList = true;
				this.errorItemList = true;
			} else if (!data['itemList']) {
				const message = {
					error: 'Error',
					type: 'warn',
					messageDisplay: 'No Items found'
				};
				this.messageService.add(message);
				this.hideItemList = true;
			} else {
				if (data['itemList'].length === 0) {
					this.emptyItemList = true;
					this.hideItemList = true;
				} else {
					data['itemList'].forEach((item: {}) => {
						this.products.push(new InventoryItem(item));
					});

					this.dataSource = new MatTableDataSource(this.products);
					this.dataSource.paginator = this.paginator;
					this.dataSource.sort = this.sort;
					this.hideItemList = false;
				}
			}
		});
	}

	applyFilter(filterValue: string) {
		this.dataSource.filter = filterValue.trim().toLowerCase();

		if (this.dataSource.paginator) {
			this.dataSource.paginator.firstPage();
		}
	}
}
