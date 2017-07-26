package com.jinxin.jxsmarthome.ui.widget.lrcview;

import android.os.Environment;
import java.io.File;
import java.io.Serializable;

public class PlayListItems
  implements Serializable
{
  private static final long serialVersionUID = 20071213L;
  public String name = "";
  public String location = "";
  public boolean isFile = true;
  public String artist;
  public String netpath;
  public File lyricFile;
  public String lrcpath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/lrc/";
  public int offset;

  public PlayListItems(String lrcpath, String name, String artist, String neturl)
  {
    this.name = name;
    this.lrcpath = lrcpath;
    this.artist = artist;
    File file = new File(lrcpath + artist + "-" + name + ".lrc");
    if (file.exists()) {
      this.lyricFile = file;
      this.isFile = true;
      this.location = file.getAbsolutePath();
    } else {
      this.isFile = false;
      this.netpath = neturl;
    }
  }

  public String getLrcpath()
  {
    return this.lrcpath;
  }

  public void setLrcpath(String lrcpath)
  {
    this.lrcpath = lrcpath;
  }

  public String getNetpath()
  {
    return this.netpath;
  }

  public void setNetpath(String netpath)
  {
    this.netpath = netpath;
  }

  public String getName()
  {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getLocation() {
    return this.location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public boolean isFile() {
    return this.isFile;
  }

  public void setFile(boolean isFile) {
    this.isFile = isFile;
  }

  public String getArtist() {
    return this.artist;
  }

  public void setArtist(String artist) {
    this.artist = artist;
  }

  public File getLyricFile() {
    return this.lyricFile;
  }

  public void setLyricFile(File lyricFile) {
    this.lyricFile = lyricFile;
  }

  public int getOffset() {
    return this.offset;
  }

  public void setOffset(int offset) {
    this.offset = offset;
  }
}
