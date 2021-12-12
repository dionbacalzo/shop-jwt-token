export class InventoryItem<T> {
	id: string;
	title: string;	
	manufacturer: string;
	price: number;
	releaseDate: string;
	type: string;
	
	constructor(options: {
			id?: string,
			title?: string,
			manufacturer?: string,
			price?: number,
			releaseDate?: any,
			type?: string
    	} = {}) {
	    this.id = options.id;
	    this.title = options.title;
	    this.manufacturer = options.manufacturer;
	    this.price = options.price === undefined ? 0.00 : options.price;
	    if(options.releaseDate instanceof Date){
	    	this.releaseDate = options.releaseDate.toISOString()
	    } else {
	    	this.releaseDate = options.releaseDate;
		}
	    this.type = options.type;
	}
}
