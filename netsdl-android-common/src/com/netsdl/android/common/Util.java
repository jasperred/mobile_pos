package com.netsdl.android.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.netsdl.android.common.db.SkuMaster;

public class Util {
	public static final String DEFAULT_LOCAL_DEVICE_ID = "000000000000000";

	public static String getSchemeFromURI(String strURI) {
		int end = strURI.indexOf(':');
		if (end < 0)
			return null;
		String strSub = strURI.substring(end + 1, strURI.length());
		if (strSub.length() < 2)
			return null;
		return strURI.substring(0, end);
	}

	public static BufferedReader getBufferedReaderFromURI(String strURI,
			String strEncode) throws URISyntaxException,
			ClientProtocolException, IOException {
		String scheme = getSchemeFromURI(strURI);
		if ("file".equalsIgnoreCase(scheme)) {
			return new BufferedReader(new InputStreamReader(
					new FileInputStream(strURI.substring(scheme.length() + 3,
							strURI.length())), strEncode));
		} else {
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet();
			request.setURI(new URI(strURI));
			HttpResponse response = client.execute(request);
			return new BufferedReader(new InputStreamReader(response
					.getEntity().getContent(), strEncode));
		}

	}

	public static BufferedReader getBufferedReaderFromURI(String strURI)
			throws URISyntaxException, ClientProtocolException, IOException {
		String scheme = getSchemeFromURI(strURI);
		if ("file".equalsIgnoreCase(scheme)) {
			return new BufferedReader(new InputStreamReader(
					new FileInputStream(strURI.substring(scheme.length() + 3,
							strURI.length()))));
		} else {
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet();
			request.setURI(new URI(strURI));
			HttpResponse response = client.execute(request);
			return new BufferedReader(new InputStreamReader(response
					.getEntity().getContent()));
		}
	}

	public static Map<String, String> getInitInfo() throws URISyntaxException,
			ClientProtocolException, IOException {
		BufferedReader in = null;
		try {
			in = null;
			in = getBufferedReaderFromURI(Constant.INIT_URI);
			String line = in.readLine();
			Map<String, String> map = new HashMap<String, String>();
			while (line != null) {
				if (line.trim().length() == 0)
					continue;
				String[] strs = line.split(",");
				map.put(strs[0], strs[1]);
				line = in.readLine();
			}

			return map;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static Map<String, String> getInfo(String url)
			throws URISyntaxException, ClientProtocolException, IOException {
		BufferedReader in = null;
		try {
			in = null;
			in = getBufferedReaderFromURI(url);
			Map<String, String> map = new HashMap<String, String>();
			String line = in.readLine();
			map.put(Constant.URL, line);
			line = in.readLine();
			map.put(Constant.VERSION, line);
			line = in.readLine();
			map.put(Constant.ROWS, line);
			return map;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void insertSqliteFromCSV(SkuMaster skuMaster, String url) {
		if (url == null)
			return;
		BufferedReader in = null;
		try {
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet();
			request.setURI(new URI(url));
			HttpResponse response = client.execute(request);
			in = new BufferedReader(new InputStreamReader(response.getEntity()
					.getContent(), "UTF-8"));

			String line = in.readLine();
			while (line != null) {
				if (line.trim().length() == 0)
					continue;

				skuMaster.deleteByKey(line);
				skuMaster.insert(line);

				line = in.readLine();
			}
			Log.d("initSqlite", "End");

		} catch (ClientProtocolException e) {
		} catch (URISyntaxException e) {
		} catch (IOException e) {
		} catch (IllegalArgumentException e) {
		} catch (SecurityException e) {
		} catch (IllegalAccessException e) {
		} catch (NoSuchFieldException e) {
		} finally {

			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
	}

	boolean isTableExists(SQLiteDatabase db, String tableName) {
		if (tableName == null || db == null || !db.isOpen()) {
			return false;
		}
		Cursor cursor = null;
		try {
			cursor = db
					.rawQuery(
							"SELECT COUNT(*) FROM sqlite_master WHERE type = ? AND name = ?",
							new String[] { "table", tableName });
			if (!cursor.moveToFirst()) {
				return false;
			}
			int count = cursor.getInt(0);
			cursor.close();
			return count > 0;
		} finally {
			if (cursor != null)
				cursor.close();
		}

	}

	public static String getMD5(String str) {
		MessageDigest messageDigest = null;

		try {
			messageDigest = MessageDigest.getInstance("MD5");

			messageDigest.reset();

			messageDigest.update(str.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			System.out.println("NoSuchAlgorithmException caught!");
			System.exit(-1);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		byte[] byteArray = messageDigest.digest();

		StringBuffer md5StrBuff = new StringBuffer();

		for (int i = 0; i < byteArray.length; i++) {
			if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
				md5StrBuff.append("0").append(
						Integer.toHexString(0xFF & byteArray[i]));
			else
				md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
		}

		return md5StrBuff.toString();
	}

	public static String getUUID() {
		String s = UUID.randomUUID().toString();
		return s.substring(0, 8) + s.substring(9, 13) + s.substring(14, 18)
				+ s.substring(19, 23) + s.substring(24);
	}

	public static String getLocalMacAddress(Context context) {
		WifiManager wifi = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		return info.getMacAddress();
	}

	public static String ExternalStorageDirectory() {
		return Environment.getExternalStorageDirectory().toString();
	}

	public static String getIpAddress(Context context) {
		WifiManager wifi = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		return intToIp(info.getIpAddress());
	}

	public static String intToIp(int i) {
		return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF)
				+ "." + ((i >> 24) & 0xFF);
	}

	public static String getLocalDeviceId(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return telephonyManager.getDeviceId();
	}

	public static boolean ftpUpload(String filepath, String filename,
			String ftpUrl, String ftpPath, String ftpUser, String ftpPassword) {
		FileInputStream input = null;
		FTPClient ftpClient = null;

		try {
			ftpClient = new FTPClient();
			String[] fs = ftpUrl.split(":");
			if(fs!=null&&fs.length==2)
			{
				ftpClient.connect(fs[0], new Integer(fs[1]));
			}
			else
				ftpClient.connect(ftpUrl);
			ftpClient.login(ftpUser, ftpPassword);

			int reply = ftpClient.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				ftpClient.disconnect();
				ftpClient = null;
				return false;
			}

			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
			ftpClient.enterLocalPassiveMode();

			input = new FileInputStream(filepath + File.separatorChar
					+ filename);
			if (ftpPath != null && ftpPath.trim().length() > 0) {
				ftpClient.changeWorkingDirectory(ftpPath);
			}
			ftpClient.storeFile(filename, input);

			ftpClient.logout();
			return true;
		} catch (SocketException e) {
			return false;
		} catch (IOException e) {
			return false;
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
				}
				input = null;
			}
			if (ftpClient != null) {
				if (ftpClient.isConnected()) {
					try {
						ftpClient.disconnect();
					} catch (IOException e) {
					}
					ftpClient = null;
				}
			}

		}

	}

	public static int CopyFile(String fromFile, String toFile) {

		try {
			InputStream fosfrom = new FileInputStream(fromFile);
			OutputStream fosto = new FileOutputStream(toFile);
			byte bt[] = new byte[1024];
			int c;
			while ((c = fosfrom.read(bt)) > 0) {
				fosto.write(bt, 0, c);
			}
			fosfrom.close();
			fosto.close();
			return 0;

		} catch (Exception ex) {
			return -1;
		}
	}

	/**
	 * 四舍五入
	 * 
	 * @param num
	 * @param digit
	 * @return
	 */
	public static BigDecimal round(BigDecimal num) {
		int digit = 2;
		if (num == null)
			return new BigDecimal(0);
		BigDecimal a = num.setScale(digit, BigDecimal.ROUND_HALF_UP);
		if(a.intValue()==a.doubleValue())
		{
			a = new BigDecimal(a.intValue());
		}
		return a;
	}
	/**
	 * 日期转换
	 * @param date
	 * @param format
	 * @return
	 */
	public static String dateToString(Date date,String format)
	{
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(date);
	}

}
