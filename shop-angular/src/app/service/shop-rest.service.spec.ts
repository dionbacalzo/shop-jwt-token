import { TestBed } from '@angular/core/testing';

import { ShopRestService } from './shop-rest.service';
import { AppModule } from '../app.module';

describe('ShopRestService', () => {
	beforeEach(() => TestBed.configureTestingModule({imports: [ AppModule ]}));

	it('should be created', () => {
		const service: ShopRestService = TestBed.get(ShopRestService);
		expect(service).toBeTruthy();
	});
});
