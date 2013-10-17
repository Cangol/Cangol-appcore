package mobi.cangol.mobile.service.download;



public class DownloadResource {
	//下载的URL
	protected String url;
	//下载的文件名
	protected String fileName;
	//文件长度
	protected long fileLength;
	//已完成长度
	protected long completeSize;
	//进度
	protected int progress;
	//速度 b/s
	protected long speed;
	//开始,暂停,安装,运行
	protected int status;
	//本地文件更目录
	protected String localPath;
	//异常说明
	protected String exception;
	//源文件地址
	protected String sourceFile;
	//配置地址
	protected String confFile;
	//下载对象 json 字符串
	protected String  object;
	//唯一标示
	protected String  key;
	protected transient DownloadTask downloadTask;

	
	public DownloadResource(String url, String fileName) {
		this.url = url;
		this.fileName = fileName;
	}
	public long getCompleteSize() {
		return completeSize;
	}
	public void setCompleteSize(long completeSize) {
		this.completeSize = completeSize;
	}
	public long getFileLength() {
		return fileLength;
	}
	public void setFileLength(long fileLength) {
		this.fileLength = fileLength;
	}
	public int getProgress() {
		return progress;
	}
	public void setProgress(int progress) {
		this.progress = progress;
	}
	public long getSpeed() {
		return speed;
	}
	public void setSpeed(long speed) {
		this.speed = speed;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getLocalPath() {
		return localPath;
	}
	public void setLocalPath(String localPath) {
		this.localPath = localPath;
	}
	public String getException() {
		return exception;
	}
	public void setException(String exception) {
		this.exception = exception;
	}
	public String getSourceFile() {
		return sourceFile;
	}
	public void setSourceFile(String sourceFile) {
		this.sourceFile = sourceFile;
	}
	public String getConfFile() {
		return confFile;
	}
	public void setConfFile(String confFile) {
		this.confFile = confFile;
	}
	public String getObject() {
		return object;
	}
	public void setObject(String object) {
		this.object = object;
	}
	public DownloadTask getDownloadTask() {
		return downloadTask;
	}
	public void setDownloadTask(DownloadTask downloadTask) {
		this.downloadTask = downloadTask;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key=key;
	}
	@Override
	public boolean equals(Object o) {
		return key!=null&&key.equals(((DownloadResource)o).getKey());
	}
	
	public void reset() {
		completeSize=0;
		progress=0;
		speed=0;
		status=0;
	}
	
	public static DownloadResource initFromFile(String absolutePath) {
		
		return null;
	}
	
	public void save() {
		
	}
	
	
	
}
