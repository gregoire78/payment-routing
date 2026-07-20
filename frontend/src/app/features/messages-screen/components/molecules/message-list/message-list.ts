import { Component, EventEmitter, Input, Output } from '@angular/core';
import { MessageView } from '../../../../../services/message-api';

@Component({
  selector: 'app-message-list',
  imports: [],
  templateUrl: './message-list.html',
  styleUrl: './message-list.css',
})
export class MessageList {
  @Input() public messages: MessageView[] = [];
  @Output() public messageSelected = new EventEmitter<number>();
}
