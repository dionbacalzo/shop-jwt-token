import { TestBed } from '@angular/core/testing';

import { AdminService } from './admin.service';
import { AppModule } from '../app.module';

describe('AdminService', () => {
  beforeEach(() => TestBed.configureTestingModule({ imports: [AppModule] }));

  it('should be created', () => {
    const service: AdminService = TestBed.get(AdminService);
    expect(service).toBeTruthy();
  });
});
