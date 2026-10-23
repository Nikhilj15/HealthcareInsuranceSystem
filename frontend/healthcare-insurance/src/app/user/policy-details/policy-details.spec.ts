import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { PolicyDetails } from './policy-details';

describe('PolicyDetails', () => {
  let component: PolicyDetails;
  let fixture: ComponentFixture<PolicyDetails>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PolicyDetails],
      providers: [provideRouter([])],
    }).compileComponents();

    fixture = TestBed.createComponent(PolicyDetails);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
