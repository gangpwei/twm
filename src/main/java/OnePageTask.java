import domain.DownLoadTask;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;

/**
 * Desc:
 * User: weigangpeng
 * Date: 2016/4/9
 * Time: 11:10
 */
public class OnePageTask implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(OnePageTask.class);

	private ExecutorService threadPool;
	private String url;
	private boolean isFirstPage;
	private String savePath;


	public OnePageTask(ExecutorService threadPool, String url, boolean isFirstPage, String savePath) {
		this.threadPool = threadPool;
		this.url = url;
		this.isFirstPage = isFirstPage;
		this.savePath = savePath;
	}

	public void run() {
		logger.info("开始处理一页：url=" + url);
		// 创建HttpClient实例
		CloseableHttpClient httpclient = HttpClientBuilder.create().build();
		// 创建Get方法实例
		HttpGet httpGet = new HttpGet(url);
		RequestConfig config = RequestConfig.custom().setSocketTimeout(5000).setConnectTimeout(5000).setConnectionRequestTimeout(5000).build();//设置请求和传输超时时间;
		httpGet.setConfig(config);
		HttpResponse response;
		try {
			response = httpclient.execute(httpGet);
		} catch (ConnectTimeoutException e) {
			logger.error("ConnectTimeoutException,连接超时，url="+ url);
			return;
		}  catch (SocketTimeoutException e) {
			logger.error("SocketTimeoutException,连接超时，url="+ url);
			return;
		}  catch (Exception e) {
			logger.error("Exception,url="+ url, e);
			return;
		}

		HttpEntity entity = response.getEntity();
		StringBuilder sb = new StringBuilder();
		if (entity != null) {
			InputStream instream = null;
			try {
				instream = entity.getContent();
			} catch (IOException e) {
				logger.error("IOException,url="+ url, e);
			}
			BufferedReader reader = new BufferedReader(new InputStreamReader(instream));

			String line;
			try {
				while ((line = reader.readLine()) != null) {
                            System.out.println(line);
					//取图片URL
					if (line.contains("<br /><br /><img")) {
						Document doc = Jsoup.parse(line);
						Elements elements = doc.getElementsByTag("img");
						for (Element element : elements) {
							String imgPath = element.attr("src");
							System.out.println(imgPath);
							String fileName = imgPath.substring(imgPath.lastIndexOf("/"), imgPath.length());
//							domain.HttpDownload.download(imgPath, "d://twm_imgs/" + fileName);
							threadPool.execute(new DownLoadTask(imgPath, savePath + fileName));
						}
					}
					//判断是否是第一页
					else if (isFirstPage && line.contains("<div class=\"bpages\">")) {
//                            System.out.println(line);
						Document doc = Jsoup.parse(line);
						Elements elements = doc.getElementsByTag("a");
						for (Element element : elements) {
							String elementVal = element.html();
//                                System.out.println(elementVal);
							if (!StringUtil.isBlank(elementVal) && !elementVal.equals("1") && Character.isDigit(elementVal.charAt(0))) {
								String pagePath = element.attr("href");
//                                    System.out.println(pagePath);
								String baseUrl = url.substring(0, url.lastIndexOf("/") + 1);
								String pageUrl = baseUrl + pagePath;
								threadPool.execute(new OnePageTask(threadPool,pageUrl,false,savePath));
							}
						}
					}
					sb.append(line).append("\n");
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					instream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		logger.info("URL={}, 执行结束", url);
	}
}
