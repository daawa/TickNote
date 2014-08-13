package zzw.mochuan.ticknote;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class JsonData {
	public static final String queryUrl = "http://10.68.190.30:8080/test/json_note";
	public static final String updateUrl = "http://10.68.190.30:8080/test/update_note";

	public static String connServerForResult(String strUrl, int limit_start, String recId) {
		if (limit_start >= 0) {
			String url = queryUrl + "?limit_start=" + limit_start;
			return connServerForResult(url, recId);
		}

		return connServerForResult(queryUrl, recId);

	}

	public static String connServerForResult(String strUrl, String recId) {

		String strResult = "";

		try {

			HttpClient httpClient = new DefaultHttpClient();

			HttpResponse httpResponse = null;

			if (recId != null) {
				HttpPost hp = new HttpPost(strUrl);
				// HttpEntity entity = hp.getEntity();
				Log.w("Requesting data:", "httpPost:" + strUrl);

				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("recordId", recId));

				hp.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
				// params.setParameter("recordId", recId);

				httpResponse = httpClient.execute(hp);

			} else {
				HttpGet hg = new HttpGet(strUrl);
				Log.w("Requesting data:", "httget:" + strUrl);
				httpResponse = httpClient.execute(hg);
			}

			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {

				strResult = EntityUtils.toString(httpResponse.getEntity());
			}
		} catch (ClientProtocolException e) {
			// tvJson.setText("protocol error");
			e.printStackTrace();
		} catch (IOException e) {
			// tvJson.setText("IO error");
			e.printStackTrace();
		}
		return strResult;
	}

	public static JSONObject parseJson(String strResult) {
		try {
			JSONObject jsonObj = new JSONObject(strResult);

			// int id = jsonObj.getInt("id");
			// String name = jsonObj.getString("name");
			// String gender = jsonObj.getString("gender");
			// // tvJson.setText("ID号"+id + ", 姓名：" + name + ",性别：" + gender);
			return jsonObj;
		} catch (JSONException e) {
			System.out.println("Json parse error");
			e.printStackTrace();
		}
		return null;
	}

	// 解析多个数据的Json
	public static JSONArray parseJsonMulti(String strResult) {

		// Log.w("parseJsonMulti:", "strResutl:" + strResult);
		JSONArray jsonObjs = null;
		try {
			jsonObjs = new JSONObject(strResult).getJSONArray("records");
		} catch (JSONException e) {
			System.out.println("Jsons parse error !");
			e.printStackTrace();
		}
		return jsonObjs;
	}

	public static ArrayList<Map<String, String>> getDataResource() {

		ArrayList<Map<String, String>> arraylist = new ArrayList<Map<String, String>>();

		String res = connServerForResult(queryUrl, null);
		JSONArray ja = parseJsonMulti(res);
		JSONObject jo = null;

		for (int i = 0; i < ja.length(); ++i) {
			try {
				jo = ja.getJSONObject(i);
				Map<String, String> item = new HashMap<String, String>(5);
				item.put("_id", jo.getString("_id"));
				item.put("digest", jo.getString("digest"));
				item.put("create_date_time", jo.getString("create_date_time"));

				arraylist.add(item);

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return arraylist;

	}

	public String getDataResourceStr() {

		String res = connServerForResult(queryUrl, null);

		return res;

	}

	public String getDataResourceStr(int limit_start) {

		String res = connServerForResult(queryUrl, limit_start, null);

		return res;

	}

	public static int update(Map<String, String> map, String recId, int flag) {
		int res = 0;
		try {

			HttpParams httpparms = new BasicHttpParams();

			httpparms.setParameter("charset", HTTP.UTF_8);

			HttpConnectionParams.setConnectionTimeout(httpparms, 8 * 1000);

			HttpConnectionParams.setSoTimeout(httpparms, 8 * 1000);

			HttpClient httpClient = new DefaultHttpClient(httpparms);

			HttpResponse httpResponse = null;

			HttpPost hp = new HttpPost(updateUrl);
			List<NameValuePair> arguments = new ArrayList<NameValuePair>();
			hp.setHeader("charset", HTTP.UTF_8);
			hp.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

			if (recId != null && flag == ali.com.Constants.UPDATE_MODE.UPDATE) {

				// HttpEntity entity = hp.getEntity();
				Log.w("Updating data:", "httpPost:" + updateUrl);

				Log.w("params length:", "size:" + arguments.size());
				arguments.add(new BasicNameValuePair("recordId", recId));
				for (String key : map.keySet()) {
					arguments.add(new BasicNameValuePair(key, map.get(key)));
				}

				arguments.add(new BasicNameValuePair(ali.com.Constants.UPDATE_MODE.name, ""
						+ ali.com.Constants.UPDATE_MODE.UPDATE));

				Log.w("params length:", "size:" + arguments.size());

			} else if (recId != null && flag == ali.com.Constants.UPDATE_MODE.DELETE) {

				arguments.add(new BasicNameValuePair("recordId", recId));
				arguments.add(new BasicNameValuePair(ali.com.Constants.UPDATE_MODE.name, ""
						+ ali.com.Constants.UPDATE_MODE.DELETE));

			} else {
				arguments.add(new BasicNameValuePair(ali.com.Constants.UPDATE_MODE.name, ""
						+ ali.com.Constants.UPDATE_MODE.CREATE));

				Log.w("Creating data:", "httpPost:" + updateUrl);

				for (String key : map.keySet()) {
					arguments.add(new BasicNameValuePair(key, map.get(key)));
					Log.w(key, map.get(key));
				}

				Log.w("params length:", "size:" + arguments.size());

			}
			hp.setEntity(new UrlEncodedFormEntity(arguments, HTTP.UTF_8));

			httpResponse = httpClient.execute(hp);

			httpClient.getConnectionManager().shutdown();

			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {

				String ret = EntityUtils.toString(httpResponse.getEntity());
				Log.w("ret_from_http", "" + ret);
				try{
					res = Integer.valueOf(ret);
				}catch(NumberFormatException e){
					e.printStackTrace();
				}								
			} 
			else{
				Log.w("ret_from_http", "" + httpResponse.getStatusLine().getStatusCode());
			}
		} catch (ClientProtocolException e) {
			// httpClient.getConnectionManager().shutdown();
			e.printStackTrace();
		} catch (IOException e) {
			// httpClient.getConnectionManager().shutdown();
			e.printStackTrace();
		}
		return res;

	}

	public static void delete(String id) {

	}

	public JSONObject getRecord(String id) {

		return null;
	}
}
