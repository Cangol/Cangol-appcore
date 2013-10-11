package mobi.cangol.mobile.service.download;

public class DownloadTask {
	private DownloadResource resource;
	private DownloadThread thread;
	public DownloadTask(DownloadResource resource, DownloadThread thread) {
		this.resource = resource;
		this.thread = thread;
	}
	
}
