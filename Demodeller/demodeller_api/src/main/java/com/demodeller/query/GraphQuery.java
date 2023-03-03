package com.demodeller.query;

public class GraphQuery {

	private int contextId;
	private String contextName;
	private String nodeName;
	private String[] relation;
	private int matchType ;
    private int pageSize = 10;
    private int pageIndex = 1;
	/**
	 * @return the pageSize
	 */
	public int getPageSize() {
		return pageSize;
	}
	/**
	 * @param pageSize the pageSize to set
	 */
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	/**
	 * @return the pageIndex
	 */
	public int getPageIndex() {
		return pageIndex;
	}
	/**
	 * @param pageIndex the pageIndex to set
	 */
	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}
	/**
	 * @return the context
	 */
	public String getContextName() {
		return contextName;
	}
	/**
	 * @param contextName the context to set
	 */
	public void setContextName(String contextName) {
		this.contextName = contextName;
	}
	/**
	 * @return the matchType
	 */
	public int getMatchType() {
		return matchType;
	}
	/**
	 * @param matchType the matchType to set
	 */
	public void setMatchType(int matchType) {
		this.matchType = matchType;
	}
	/**
	 * @return the nodeName
	 */
	public String getNodeName() {
		return nodeName;
	}
	/**
	 * @param nodeName the nodeName to set
	 */
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}
	/**
	 * @return the relation
	 */
	public String[] getRelation() {
		return relation;
	}
	/**
	 * @param relation the relation to set
	 */
	public void setRelation(String[] relation) {
		this.relation = relation;
	}
	/**
	 * @return the contextId
	 */
	public int getContextId() {
		return contextId;
	}
	/**
	 * @param contextId the contextId to set
	 */
	public void setContextId(int contextId) {
		this.contextId = contextId;
	}
	

}
