package ru.myapp.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Greeting {

	private final long requestId;
	private final String content;
	private final TokenInfo token;

	public Greeting(long requestId, String content, TokenInfo token) {
		this.requestId = requestId;
		this.content = content;
		this.token = token;
	}

}
