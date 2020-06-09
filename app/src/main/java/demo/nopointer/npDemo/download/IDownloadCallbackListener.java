package demo.nopointer.npDemo.download;

public interface IDownloadCallbackListener {
	/**
	 * 开始下载
	 */
	public static final int DOWNLOAD_START = 0x0000;
	/**
	 * 下载中
	 */
	public static final int DOWNLOAD_ING = 0x0005;
	/**
	 * 下载完成
	 */
	public static final int DOWNLOAD_SUCCESS = 0x0001;
	/**
	 * 暂停下载
	 */
	public static final int DOWNLOAD_PAUSE = 0x0002;
	/**
	 * 继续下载
	 */
	public static final int DOWNLOAD_RESUME = 0x0003;
	/**
	 * 重新下载
	 */
	public static final int DOWNLOAD_RESTART = 0x0004;
	/**
	 * 非空闲状态
	 */
	public static final int NOT_FREE = -0x0003;
	/**
	 * 下载失败
	 */
	public static final int DOWNLOAD_FAILURE = -0x0001;
	/**
	 * 取消下载
	 */
	public static final int DOWNLOAD_CANCEL = -0x0004;
	/**
	 * 文件删除成功
	 */
	public static final int DELETE_SUCCESS = 0x0001;
	/**
	 * 文件删除失败
	 */
	public static final int DELETE_FAILURE = -0x0001;
	
    /**
     *  下载状态发生改变
     * @param argDownloadId
     * @param argStatusCode
     */
	public abstract void onStatusChange(String argUrl, long argDownloadId, int argStatusCode);
	

	/**
	 * 下载进度发生改变
	 * @param argDownloadID
	 * @param argTotleSize      文件总字节数
	 * @param argFarSize        当前下载字节数
	 * @param argNotiPercent    当前下载百分比
	 */
	public abstract void onDownloadSizeChange(String argUrl, long argDownloadID, int argFarSize, int argTotleSize, int argNotiPercent);
	
	
	/**
	 * 下载完成
	 * @param argDownloadId
	 * @param argFilePath      下载成功后保存的文件路径
	 */
	public abstract void onDownloadSuccess(String argUrl, long argDownloadId, String argFilePath);
	
	
	/**
	 * 下载失败
	 * @param argDownloadId
	 * @param argErrorCode
	 * @param argErrorMessage
	 */
	public abstract void onDownloadFailure(String argUrl, long argDownloadId, int argErrorCode, String argErrorMessage);
}
