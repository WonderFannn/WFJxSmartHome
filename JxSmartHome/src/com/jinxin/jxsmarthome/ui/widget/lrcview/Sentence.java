package com.jinxin.jxsmarthome.ui.widget.lrcview;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;

public class Sentence
  implements Serializable
{
  private static final long serialVersionUID = 20071125L;
  private long fromTime;
  private long toTime;
  private String content;

  public Sentence()
  {
  }

  public Sentence(String content, long fromTime, long toTime)
  {
    this.content = content;
    this.fromTime = fromTime;
    this.toTime = toTime;
  }

  public String changeCharset(String str)
    throws UnsupportedEncodingException
  {
    if (str != null) {
      byte[] first3bytes = str.getBytes("utf-16le");
      return new String(first3bytes, "GBK");
    }
    return null;
  }

  public Sentence(String content, long fromTime) {
    this(content, fromTime, 0L);
  }

  public Sentence(String content) {
    this(content, 0L, 0L);
  }

  public long getFromTime() {
    return this.fromTime;
  }

  public void setFromTime(long fromTime) {
    this.fromTime = fromTime;
  }

  public long getToTime() {
    return this.toTime;
  }

  public void setToTime(long toTime) {
    this.toTime = toTime;
  }

  public boolean isInTime(long time)
  {
    return (time >= this.fromTime) && (time <= this.toTime);
  }

  public String getContent()
  {
    return this.content;
  }

  public long getDuring()
  {
    return this.toTime - this.fromTime;
  }

  public String toString() {
    return "{" + this.fromTime + "(" + this.content + ")" + this.toTime + "}";
  }
}
