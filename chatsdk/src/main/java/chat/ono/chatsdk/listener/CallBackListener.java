package chat.ono.chatsdk.listener;


public interface CallBackListener {
	void onCallback(Object o);
	void onCallbackFail(int status);
}
