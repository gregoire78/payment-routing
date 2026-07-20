import { Component, Input } from '@angular/core';
import { MessageView } from '../../../../../services/message-api';

@Component({
  selector: 'app-message-detail-card',
  imports: [],
  templateUrl: './message-detail-card.html',
  styleUrl: './message-detail-card.css',
})
export class MessageDetailCard {
  @Input() public message: MessageView | null = null;
}
