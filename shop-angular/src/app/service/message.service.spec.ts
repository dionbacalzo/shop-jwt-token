import { TestBed } from '@angular/core/testing';

import { MessageService } from './message.service';
import { AppModule } from '../app.module';

describe('MessageService', () => {
  beforeEach(() => TestBed.configureTestingModule({imports: [ AppModule ]}));

  it('should be created', () => {
    const service: MessageService = TestBed.get(MessageService);
    expect(service).toBeTruthy();
  });
});
