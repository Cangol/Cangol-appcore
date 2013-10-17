package mobi.cangol.mobile.service.download;

import java.lang.ref.WeakReference;

public class BaseViewHolder {
	public WeakReference<DownloadResource> tag;

	public DownloadResource getTag() {
		DownloadResource resInfo=tag.get();
		return resInfo;
	}

	public void setTag(DownloadResource resouce) {
		this.tag = new WeakReference<DownloadResource>(resouce);
	}
	
	public void updateView(DownloadResource resouce) {
		
	}
}
