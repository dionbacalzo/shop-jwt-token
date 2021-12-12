import { TestBed } from '@angular/core/testing';

import { FileUploadService } from './file-upload.service';
import { AppModule } from '../app.module';

describe('FileUploadService', () => {
  beforeEach(() => TestBed.configureTestingModule({imports: [ AppModule ]}));

  it('should be created', () => {
    const service: FileUploadService = TestBed.get(FileUploadService);
    expect(service).toBeTruthy();
  });
});
