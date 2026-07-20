import { HttpClient } from '@angular/common/http';
import { Service, inject } from '@angular/core';
import { Observable } from 'rxjs';

export type MessageStatus = 'RECEIVED' | 'ROUTED' | 'FAILED';

export interface MessageView {
	id: number;
	externalMessageId: string;
	payload: string;
	status: MessageStatus;
	receivedAt: string;
}

export interface PublishMessageRequest {
	externalMessageId: string;
	payload: string;
}

export interface PublishMessageResponse {
	status: string;
	externalMessageId: string;
}

@Service()
export class MessageApi {
	private readonly http = inject(HttpClient);
	private readonly apiBase = '/api/messages';

	listMessages(page = 0, size = 20): Observable<MessageView[]> {
		return this.http.get<MessageView[]>(`${this.apiBase}?page=${page}&size=${size}`);
	}

	getMessage(id: number): Observable<MessageView> {
		return this.http.get<MessageView>(`${this.apiBase}/${id}`);
	}

	publishMessage(request: PublishMessageRequest): Observable<PublishMessageResponse> {
		return this.http.post<PublishMessageResponse>(`${this.apiBase}/publish`, request);
	}
}
