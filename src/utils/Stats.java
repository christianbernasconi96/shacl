package utils;

/** Classe che contiene le statistiche di un pattern */
public class Stats {
	// TODO: aggiungere statistiche previsione Max
	private int minSubjsObj;
	private int maxSubjsObj;
	private int avgSubjsObj; 
	private int minSubjObjs;
	private int maxSubjObjs;
	private int avgSubjObjs;
	
	public Stats(int minSubjsObj, int maxSubjsObj, int avgSubjsObj, int minSubjObjs, int maxSubjObjs, int avgSubjObjs) {
		this.minSubjsObj = minSubjsObj;
		this.maxSubjsObj = maxSubjsObj;
		this.avgSubjsObj = avgSubjsObj;
		this.minSubjObjs = minSubjObjs;
		this.maxSubjObjs = maxSubjObjs;
		this.avgSubjObjs = avgSubjObjs;
	}

	public int getMinSubjsObj() {
		return minSubjsObj;
	}

	public int getMaxSubjsObj() {
		return maxSubjsObj;
	}

	public int getAvgSubjsObj() {
		return avgSubjsObj;
	}

	public int getMinSubjObjs() {
		return minSubjObjs;
	}

	public int getMaxSubjObjs() {
		return maxSubjObjs;
	}

	public int getAvgSubjObjs() {
		return avgSubjObjs;
	}
	
	
}
