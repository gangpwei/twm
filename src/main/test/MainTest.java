import org.apache.http.client.ClientProtocolException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainTest {
	private static final Logger logger = LoggerFactory.getLogger(MainTest.class);

	private static ExecutorService threadPool = Executors.newFixedThreadPool(50);

	/**
	 * @param args
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public static void main(String[] args) throws IOException {
		String url = "http://www.315ff.com/tttppp/629538.html";
//		dealOnePage(url, true);
		threadPool.execute(new OnePageTask(threadPool,url,true,"d://twm_imgs/"));
		while (true){
			int activeCount =Thread.activeCount();

			logger.info("activeCount="+activeCount);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private static void dealOnePage(String url, boolean isFirstPage) throws IOException {

//            System.out.println(sb.toString());
	}


//        public static String convertStreamToString(InputStream is) {
//            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
//            StringBuilder sb = new StringBuilder();
//
//            String line = null;
//            try {
//                while ((line = reader.readLine()) != null) {
//                    sb.append(line + "\n");
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            } finally {
//                try {
//                    is.close();
//                } catch (IOException e) {
//                   e.printStackTrace();
//                }
//            }
//            return sb.toString();
//        }

}