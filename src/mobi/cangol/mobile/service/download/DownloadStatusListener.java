package mobi.cangol.mobile.service.download;


public interface DownloadStatusListener {
	
	void onStatusChange(DownloadResource resource,int status);
}
