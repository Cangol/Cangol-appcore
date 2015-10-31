package mobi.cangol.mobile.api;

public class ApiPage {
	
	private int pageSize;
    private int curPage;
    private long totalCount;
    private long totalPageCount;
    
    public ApiPage() {
		super();
	}
	public ApiPage(int pageSize, int curPage, long totalCount,
			long totalPageCount) {
		super();
		this.pageSize = pageSize;
		this.curPage = curPage;
		this.totalCount = totalCount;
		this.totalPageCount = totalPageCount;
	}
	public long getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
	}
	public long getTotalPageCount() {
		return totalPageCount;
	}
	public void setTotalPageCount(long totalPageCount) {
		this.totalPageCount = totalPageCount;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public int getCurPage() {
		return curPage;
	}
	public void setCurPage(int curPage) {
		this.curPage = curPage;
	}
}
