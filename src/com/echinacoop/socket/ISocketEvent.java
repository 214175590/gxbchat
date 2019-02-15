package com.echinacoop.socket;

import com.echinacoop.modal.WSClient;
import com.echinacoop.modal.WSData;

public interface ISocketEvent {

	public boolean filter(WSClient client, WSData wsData);

	public void onMessage(WSClient client, WSData wsData);

	public void onClose(WSClient client);

	public void onError(Throwable e);

	public void onOpen(WSClient client);

	interface _ISocketEvent {

	}

}
