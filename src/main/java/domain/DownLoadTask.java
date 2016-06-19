package domain;

/**
 * Created by Administrator on 2016/5/23.
 */
public class DownLoadTask implements Runnable {
	private String url;
	private String savePath;

	public DownLoadTask(String url, String savePath) {
		this.url = url;
		this.savePath = savePath;
	}

	public void run() {
		HttpDownload.download(url, savePath);
	}
}
