import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FilePromptComponent } from './file-prompt.component';

describe('FilePromptComponent', () => {
  let component: FilePromptComponent;
  let fixture: ComponentFixture<FilePromptComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FilePromptComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FilePromptComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
