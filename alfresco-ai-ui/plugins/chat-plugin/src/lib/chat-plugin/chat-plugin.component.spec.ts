import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ChatPluginComponent } from './chat-plugin.component';

describe('ChatPluginComponent', () => {
  let component: ChatPluginComponent;
  let fixture: ComponentFixture<ChatPluginComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ChatPluginComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(ChatPluginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
