import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-primary-button',
  imports: [],
  templateUrl: './primary-button.html',
  styleUrl: './primary-button.css',
})
export class PrimaryButton {
  @Input() public disabled = false;
  @Input() public type: 'button' | 'submit' = 'button';
  @Output() public pressed = new EventEmitter<void>();
}
