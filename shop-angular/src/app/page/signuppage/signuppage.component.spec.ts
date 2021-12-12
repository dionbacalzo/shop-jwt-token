import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SignuppageComponent } from './signuppage.component';
import { AppModule } from 'src/app/app.module';

describe('SignuppageComponent', () => {
  let component: SignuppageComponent;
  let fixture: ComponentFixture<SignuppageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [ AppModule ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SignuppageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it(`should have as title 'Shop Display: Signup'`, () => {
    const fixture = TestBed.createComponent(SignuppageComponent);
    const app = fixture.debugElement.componentInstance;
    expect(app.titleService.getTitle()).toEqual('Shop Display: Signup');
  });
});
