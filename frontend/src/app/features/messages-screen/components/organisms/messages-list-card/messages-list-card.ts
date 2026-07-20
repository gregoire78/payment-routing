import { Component, EventEmitter, Input, Output } from '@angular/core';
import { MessageView } from '../../../../../services/message-api';
import { PrimaryButton } from '../../atoms/primary-button/primary-button';
import { MessageList } from '../../molecules/message-list/message-list';

@Component({
  selector: 'app-messages-list-card',
  imports: [PrimaryButton, MessageList],
  templateUrl: './messages-list-card.html',
  styleUrl: './messages-list-card.css',
})
export class MessagesListCard {
  @Input() public loading = false;
  @Input() public error: string | null = null;
  @Input() public messages: MessageView[] = [];

  @Output() public refresh = new EventEmitter<void>();
  @Output() public viewMessage = new EventEmitter<number>();
}
