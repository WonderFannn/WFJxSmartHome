package com.jinxin.jxsmarthome.ui.widget.lrcview;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

public class Lyric implements Serializable {
	private static final long serialVersionUID = 20071125L;
	private static Logger log = Logger.getLogger(Lyric.class.getName());
	private int width;
	private int height;
	private long time;
	private long tempTime;
	public List<Sentence> list = new ArrayList();
	private boolean isMoving;
	private int currentIndex;
	private boolean initDone;
	private transient PlayListItems info;
	private transient File file;
	private boolean enabled = true;
	private long during = 2147483647L;
	private int offset;
	private static final Pattern pattern = Pattern
			.compile("(?<=\\[).*?(?=\\])");

	public Lyric(final PlayListItems info) {
		this.offset = info.getOffset();
		this.info = info;
		log.info("传进来的歌名是:" + info.toString());
		if (info.isFile) {
			log.log(Level.INFO, "不用找了，直接关联到的歌词是：" + this.file);
			this.file = info.getLyricFile();
			init(this.file);
			this.initDone = true;
			this.file = info.getLyricFile();
			return;
		}

		new Thread() {
			public void run() {
				Lyric.this.init(info);
				Lyric.this.initDone = false;
			}
		}.start();
	}

	public void setEnabled(boolean b) {
		this.enabled = b;
	}

	public File getLyricFile() {
		return this.file;
	}

	public void adjustTime(int time) {
		if (this.list.size() == 1) {
			return;
		}
		this.offset += time;
		this.info.setOffset(this.offset);
	}

	private void init(PlayListItems info) {
		File file = new File(info.getLrcpath() + info.artist + "-" + info.name
				+ ".lrc");
		if (!file.exists()) {
			file.getParentFile().mkdirs();
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			HttpClient client = new DefaultHttpClient();
			HttpGet get = new HttpGet(info.getNetpath());
			HttpResponse httpResponse = client.execute(get);

			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				InputStream is = httpResponse.getEntity().getContent();
				FileOutputStream fos = new FileOutputStream(file);
				BufferedInputStream bis = new BufferedInputStream(is);
				int count = 0;
				byte[] byest = new byte[1024];
				while ((count = bis.read(byest)) != -1) {
					fos.write(byest, 0, count);
				}
				is.close();
				fos.close();
				bis.close();
				if (file.length() < 10L) {
					file.delete();
				} else {
					System.out.println("歌词保存完毕" + file.getAbsolutePath());
					this.file = file;
					init(file);
					this.initDone = true;
				}
			} else {
				file.delete();
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void init(File file) {
		BufferedReader br = null;
		String fileCode = "";
		try {
			fileCode = codeString(file);
			Log.d("Yang", "String Code "+ fileCode);
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					file.getAbsolutePath()), fileCode));
			StringBuilder sb = new StringBuilder();
			String temp = null;
			while ((temp = br.readLine()) != null) {
				sb.append(temp).append("\n");
			}
			init(sb.toString());
		} catch (Exception ex) {
			Logger.getLogger(Lyric.class.getName()).log(Level.SEVERE, null, ex);
			try {
				br.close();
			} catch (Exception ex1) {
				Logger.getLogger(Lyric.class.getName()).log(Level.SEVERE, null,
						ex1);
			}
		} finally {
			try {
				br.close();
			} catch (Exception ex) {
				Logger.getLogger(Lyric.class.getName()).log(Level.SEVERE, null,
						ex);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void init(String content) {
		if ((content == null) || (content.trim().equals(""))) {
			this.list.add(new Sentence(this.info.getName(), -2147483648L,
					2147483647L));
			return;
		}
		try {
			BufferedReader br = new BufferedReader(new StringReader(content));
			String temp = null;
			while ((temp = br.readLine()) != null) {
				parseLine(temp.trim());
			}
			br.close();

			Collections.sort(this.list, new Comparator() {

				@Override
				public int compare(Object lhs, Object rhs) {
					// TODO Auto-generated method stub
					return (int) (((Sentence) lhs).getFromTime() - ((Sentence) rhs)
							.getFromTime());
				}
			});
			if (this.list.size() == 0) {
				this.list
						.add(new Sentence(this.info.getName(), 0L, 2147483647L));
				return;
			}
			Sentence first = (Sentence) this.list.get(0);
			this.list.add(0,
					new Sentence(this.info.getName(), 0L, first.getFromTime()));

			int size = this.list.size();
			for (int i = 0; i < size; i++) {
				Sentence next = null;
				if (i + 1 < size) {
					next = (Sentence) this.list.get(i + 1);
				}
				Sentence now = (Sentence) this.list.get(i);
				if (next != null) {
					now.setToTime(next.getFromTime() - 1L);
				}
			}

			if (this.list.size() == 1) {
				((Sentence) this.list.get(0)).setToTime(2147483647L);
			} else {
				Sentence last = (Sentence) this.list.get(this.list.size() - 1);

				last.setToTime(0L);
			}
		} catch (Exception ex) {
			Logger.getLogger(Lyric.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	private int parseOffset(String str) {
		String[] ss = str.split("\\:");
		if (ss.length == 2) {
			if (ss[0].equalsIgnoreCase("offset")) {
				int os = Integer.parseInt(ss[1]);

				return os;
			}
			return 2147483647;
		}

		return 2147483647;
	}

	private void parseLine(String line) {
		if (line.equals("")) {
			return;
		}
		Matcher matcher = pattern.matcher(line);
		List<String> temp = new ArrayList<String>();
		int lastIndex = -1;
		int lastLength = -1;
		String str;
		while (matcher.find()) {
			String s = matcher.group();
			int index = line.indexOf("[" + s + "]");
			if ((lastIndex != -1) && (index - lastIndex > lastLength + 2)) {
				String content = line.substring(lastIndex + lastLength + 2,
						index);
				for (Iterator localIterator = temp.iterator(); localIterator
						.hasNext();) {
					str = (String) localIterator.next();
					long t = parseTime(str);
					if (t != -1L) {
						this.list.add(new Sentence(content, t));
					}
				}
				temp.clear();
			}
			temp.add(s);
			lastIndex = index;
			lastLength = s.length();
		}

		if (temp.isEmpty())
			return;
		try {
			int length = lastLength + 2 + lastIndex;
			String content = line.substring(length > line.length() ? line
					.length() : length);

			if ((content.equals("")) && (this.offset == 0)) {
				for (String s : temp) {
					int of = parseOffset(s);
					if (of != 2147483647) {
						this.offset = of;
						this.info.setOffset(this.offset);
						break;
					}
				}
				return;
			}
			for (String s : temp) {
				long t = parseTime(s);
				if (t != -1L)
					this.list.add(new Sentence(content, t));
			}
		} catch (Exception localException) {
		}
	}
	
	/** 
	 * 判断文件的编码格式 
	 * @param fileName :file 
	 * @return 文件编码格式 
	 * @throws Exception 
	 */  
	public static String codeString(File fileName) throws Exception{
	    BufferedInputStream bin = new BufferedInputStream(new FileInputStream(fileName));
	    byte[] head = new byte[2048];  
	    bin.read(head);
	    org.mozilla.universalchardet.UniversalDetector detector =  
                new org.mozilla.universalchardet.UniversalDetector(null);  
            detector.handleData(head, 0, head.length);

            detector.dataEnd();  
            String encoding = detector.getDetectedCharset();  
            detector.reset();  
            if (encoding == null) {
                encoding = "GBK";
            }
	    return encoding;
	}

	private long parseTime(String time) {
		String[] ss = time.split("\\:|\\.");

		if (ss.length < 2)
			return -1L;
		if (ss.length == 2)
			try {
				if ((this.offset == 0) && (ss[0].equalsIgnoreCase("offset"))) {
					this.offset = Integer.parseInt(ss[1]);
					this.info.setOffset(this.offset);
					System.err.println("整体的偏移量：" + this.offset);
					return -1L;
				}
				int min = Integer.parseInt(ss[0]);
				int sec = Integer.parseInt(ss[1]);
				if ((min < 0) || (sec < 0) || (sec >= 60)) {
					throw new RuntimeException("数字不合法!");
				}

				return (min * 60 + sec) * 1000L;
			} catch (Exception exe) {
				return -1L;
			}
		if (ss.length == 3) {
			try {
				int min = Integer.parseInt(ss[0]);
				int sec = Integer.parseInt(ss[1]);
				int mm = Integer.parseInt(ss[2]);
				if ((min < 0) || (sec < 0) || (sec >= 60) || (mm < 0)
						|| (mm > 99)) {
					throw new RuntimeException("数字不合法!");
				}

				return (min * 60 + sec) * 1000L + mm * 10;
			} catch (Exception exe) {
				return -1L;
			}
		}
		return -1L;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public void setTime(long time) {
		if (!this.isMoving)
			this.tempTime = (this.time = time + this.offset);
	}

	public boolean isInitDone() {
		return this.initDone;
	}

	int getNowSentenceIndex(long t) {
		for (int i = 0; i < this.list.size(); i++) {
			if (((Sentence) this.list.get(i)).isInTime(t)) {
				return i;
			}
		}

		return -1;
	}

	public boolean canMove() {
		return (this.list.size() > 1) && (this.enabled);
	}

	public long getTime() {
		return this.tempTime;
	}

	private void checkTempTime() {
		if (this.tempTime < 0L)
			this.tempTime = 0L;
		else if (this.tempTime > this.during)
			this.tempTime = this.during;
	}

	public void startMove() {
		this.isMoving = true;
	}

	public void stopMove() {
		this.isMoving = false;
	}
}