import { Component, EventEmitter, Input, Output } from '@angular/core';
import { PublishForm } from '../../molecules/publish-form/publish-form';

@Component({
  selector: 'app-publish-message-card',
  imports: [PublishForm],
  templateUrl: './publish-message-card.html',
  styleUrl: './publish-message-card.css',
})
export class PublishMessageCard {
  @Input() public externalMessageId = '';
  @Input() public payload = '';
  @Input() public canPublish = false;
  @Input() public publishResponse: string | null = null;

  @Output() public externalMessageIdChange = new EventEmitter<string>();
  @Output() public payloadChange = new EventEmitter<string>();
  @Output() public publish = new EventEmitter<void>();
}
