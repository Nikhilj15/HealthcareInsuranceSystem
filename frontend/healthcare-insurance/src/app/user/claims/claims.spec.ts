import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { Claims } from './claims';

describe('Claims', () => {
  let component: Claims;
  let fixture: ComponentFixture<Claims>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Claims],
      providers: [provideRouter([])],
    }).compileComponents();

    fixture = TestBed.createComponent(Claims);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
