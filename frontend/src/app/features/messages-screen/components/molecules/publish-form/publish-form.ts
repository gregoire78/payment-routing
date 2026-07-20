import { Component, EventEmitter, Input, Output } from '@angular/core';
import { LabeledInput } from '../../atoms/labeled-input/labeled-input';
import { LabeledTextarea } from '../../atoms/labeled-textarea/labeled-textarea';
import { PrimaryButton } from '../../atoms/primary-button/primary-button';

@Component({
  selector: 'app-publish-form',
  imports: [LabeledInput, LabeledTextarea, PrimaryButton],
  templateUrl: './publish-form.html',
  styleUrl: './publish-form.css',
})
export class PublishForm {
  @Input() public externalMessageId = '';
  @Input() public payload = '';
  @Input() public canPublish = false;

  @Output() public externalMessageIdChange = new EventEmitter<string>();
  @Output() public payloadChange = new EventEmitter<string>();
  @Output() public publish = new EventEmitter<void>();
}
