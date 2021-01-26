package com.ulwx.tool;

public class PageBean {

	protected int start;// 开始行号，从0开始
	protected int end;// 结束行号，开区间，end不包含在内 ；
	protected int perPage;// 每页多少行
	protected int total;// 总记录行
	protected int page;// 当前页
	protected int maxPage;// 最大的页数

	public static int USE_AUTO_CREATE_SQL_COUNT_STATEMENT;
	public static int USE_EXTERN_INT_COUNT;

	private int useFigureCountPolicy = PageBean.USE_AUTO_CREATE_SQL_COUNT_STATEMENT;
	private String sqlCount = "";

	public PageBean() {

	}

	public PageBean(int curPageNum, int pageSize) {
		this.page = curPageNum;
		this.perPage = pageSize;
	}

	public PageBean(int curPageNum, int pageSize, int total) {
		this.page = curPageNum;
		this.perPage = pageSize;
		this.total = total;
		this.doPage(total);
	}

	public String getSqlCount() {
		return sqlCount;
	}

	public void setSqlCount(String sqlCount) {
		this.sqlCount = sqlCount;
	}

	public int getUseFigureCountPolicy() {
		return useFigureCountPolicy;
	}

	public void setUseFigureCountPolicy(int useFigureCountPolicy) {
		this.useFigureCountPolicy = useFigureCountPolicy;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public void setPerPage(int perPage) {
		this.perPage = perPage;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public void setPage(int page) {
		if (page < 1)
			page = 1;
		this.page = page;
	}

	public void setMaxPage(int maxPage) {
		this.maxPage = maxPage;
	}

	public int getPerPage() {
		return perPage;
	}

	public int getNextPage() {

		int nextPage = page + 1;
		if (nextPage > this.maxPage) {
			nextPage = this.maxPage;
		}
		return nextPage;
	}

	public int getPrevPage() {

		int prevPage = page - 1;

		if (prevPage < 1)
			prevPage = 1;

		return prevPage;

	}

	public int getTotal() {
		return total;
	}

	public int getPage() {
		return page;
	}

	public int getMaxPage() {
		return maxPage;
	}

	/**
	 * 从第0行开始
	 * 
	 * @return
	 */
	public int getStart() {
		return start;
	}

	public int getEnd() {
		return end;
	}

	/**
	 * 
	 * @param total
	 *            总共多少行
	 * @param page
	 *            当前页号，从1开始
	 * @param perPage
	 *            每页多少行，如果为0，表明只显示一页
	 */
	public void doPage(int total, int page, int perPage) {

		if (total == 0) {
			start = 0;
			end = 0;
			return;
		}
//		if (total < perPage && page==1) {
//			perPage = total;
//			page = 1;
//		}
		if (page <= 0)
			page = 1;
		if (perPage == 0) {
			perPage = total;
		}
		if (total % perPage == 0) {
			maxPage = total / perPage;
		} else {
			maxPage = total / perPage + 1;
		}

	
		this.page = page;
		this.perPage = perPage;
		this.total = total;
		
		if (page > maxPage){
			start = 0;
			end = 0;
			return;
		}
		start = perPage * (page - 1);// 从0开始
		end = start + perPage; // 开区间

		if (end > total) {
			end = total;
		}

	}

	public boolean isEmpty(){
		if(this.start==0 && this.end==0){
			return true;
		}else{
			return false;
		}
	}
	public void doPage(int total) {
		doPage(total, page, perPage);
	}

	public void doPage() {
		doPage(total, page, perPage);
	}

	@Override
	public String toString() {
		return "PageBean [start=" + start + ", end=" + end + ", perPage="
				+ perPage + ", total=" + total + ", page=" + page
				+ ", maxPage=" + maxPage + ", useFigureCountPolicy="
				+ useFigureCountPolicy + ", sqlCount=" + sqlCount + "]";
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		PageBean pu = new PageBean();
		pu.doPage(10, 66, 2);
		System.out.println(pu.getStart() + " " + pu.getEnd() + " maxPage="
				+ pu.getMaxPage());
	}

}
