import { TestBed } from '@angular/core/testing';

import { PathResolveService } from './path-resolve.service';
import { AppModule } from '../app.module';

describe('PathResolveService', () => {
  beforeEach(() => TestBed.configureTestingModule({imports: [ AppModule ]}));

  it('should be created', () => {
    const service: PathResolveService = TestBed.get(PathResolveService);
    expect(service).toBeTruthy();
  });
});
