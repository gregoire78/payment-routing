import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-labeled-textarea',
  imports: [],
  templateUrl: './labeled-textarea.html',
  styleUrl: './labeled-textarea.css',
})
export class LabeledTextarea {
  @Input({ required: true }) public id = '';
  @Input({ required: true }) public label = '';
  @Input() public rows = 4;
  @Input() public value = '';
  @Output() public valueChange = new EventEmitter<string>();

  protected onValueChange(event: Event): void {
    this.valueChange.emit((event.target as HTMLTextAreaElement).value);
  }
}
