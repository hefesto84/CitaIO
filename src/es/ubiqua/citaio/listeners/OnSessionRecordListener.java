package es.ubiqua.citaio.listeners;

public interface OnSessionRecordListener {
	public void onStartRecord();
	public void onStopRecord();
	public void onSessionClosed();
}
