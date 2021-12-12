import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminpageComponent } from './adminpage.component';
import { AppModule } from 'src/app/app.module';

describe('AdminpageComponent', () => {
  let component: AdminpageComponent;
  let fixture: ComponentFixture<AdminpageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [ AppModule ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AdminpageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it(`should have as title 'Shop Display: Admin'`, () => {
    const fixture = TestBed.createComponent(AdminpageComponent);
    const app = fixture.debugElement.componentInstance;
    expect(app.titleService.getTitle()).toEqual('Shop Display: Admin');
  });
});
