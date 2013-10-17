package mobi.cangol.mobile.service.download;


public interface DownloadEvent {
	
	void onStart(DownloadResource resource);
	
	void onFinish(DownloadResource resource);
	
	void onFailure(DownloadResource resource);
}
