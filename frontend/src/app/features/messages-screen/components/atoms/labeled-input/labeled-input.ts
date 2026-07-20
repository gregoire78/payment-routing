import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-labeled-input',
  imports: [],
  templateUrl: './labeled-input.html',
  styleUrl: './labeled-input.css',
})
export class LabeledInput {
  @Input({ required: true }) public id = '';
  @Input({ required: true }) public label = '';
  @Input() public value = '';
  @Input() public placeholder = '';
  @Output() public valueChange = new EventEmitter<string>();

  protected onValueChange(event: Event): void {
    this.valueChange.emit((event.target as HTMLInputElement).value);
  }
}
