import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { UpdatepageComponent } from './updatepage.component';
import { AppModule } from 'src/app/app.module';

describe('UpdatepageComponent', () => {
  let component: UpdatepageComponent;
  let fixture: ComponentFixture<UpdatepageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [ AppModule ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UpdatepageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it(`should have as title 'Shop Display: Update'`, () => {
    const fixture = TestBed.createComponent(UpdatepageComponent);
    const app = fixture.debugElement.componentInstance;
    expect(app.titleService.getTitle()).toEqual('Shop Display: Update');
  });
});
