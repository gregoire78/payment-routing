import { Component, computed, inject, signal } from '@angular/core';
import { MessageApi, MessageView, PublishMessageRequest } from '../../services/message-api';
import { MessageDetailCard } from './components/organisms/message-detail-card/message-detail-card';
import { MessagesListCard } from './components/organisms/messages-list-card/messages-list-card';
import { PublishMessageCard } from './components/organisms/publish-message-card/publish-message-card';

@Component({
  selector: 'app-messages-screen',
  imports: [PublishMessageCard, MessagesListCard, MessageDetailCard],
  templateUrl: './messages-screen.html',
  styleUrl: './messages-screen.css',
})
export class MessagesScreen {
  private readonly messageApi = inject(MessageApi);

  protected readonly messages = signal<MessageView[]>([]);
  protected readonly selectedMessage = signal<MessageView | null>(null);
  protected readonly loading = signal(false);
  protected readonly error = signal<string | null>(null);
  protected readonly publishResponse = signal<string | null>(null);

  protected readonly externalMessageId = signal('');
  protected readonly payload = signal('');

  protected readonly canPublish = computed(
    () => this.externalMessageId().trim().length > 0 && this.payload().trim().length > 0,
  );

  constructor() {
    this.loadMessages();
  }

  protected loadMessages(): void {
    this.loading.set(true);
    this.error.set(null);

    this.messageApi.listMessages().subscribe({
      next: (items) => {
        this.messages.set(items);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Impossible de charger les messages.');
        this.loading.set(false);
      },
    });
  }

  protected viewMessage(id: number): void {
    this.error.set(null);

    this.messageApi.getMessage(id).subscribe({
      next: (item) => this.selectedMessage.set(item),
      error: () => this.error.set('Impossible de charger le detail du message.'),
    });
  }

  protected publishMessage(): void {
    if (!this.canPublish()) {
      return;
    }

    this.error.set(null);
    this.publishResponse.set(null);

    const body: PublishMessageRequest = {
      externalMessageId: this.externalMessageId().trim(),
      payload: this.payload().trim(),
    };

    this.messageApi.publishMessage(body).subscribe({
      next: (response) => {
        this.publishResponse.set(`Message accepte: ${response.externalMessageId}`);
        this.externalMessageId.set('');
        this.payload.set('');
        this.loadMessages();
      },
      error: () => this.error.set('Publication impossible.'),
    });
  }

  protected onExternalIdChange(value: string): void {
    this.externalMessageId.set(value);
  }

  protected onPayloadChange(value: string): void {
    this.payload.set(value);
  }
}
