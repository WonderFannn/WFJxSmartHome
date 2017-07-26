package com.jinxin.db;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

/**
 * @author xiaohui
 *
 */
public class MainDbLocalAdapter
{
  private Context context;

  public MainDbLocalAdapter(Context paramContext)
  {
    this.context = paramContext;
  }


  /**获取本地联系人的数量
 * @return
 */
public int getLocalNumberCount()
  {
    Cursor localCursor = this.context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
    int i = localCursor.getCount();
    System.out.println("lcoalcorsor.size---->"+i);
    localCursor.close();
    return i;
  }

  public Cursor getLocalPhoneInfo(String paramString)
  {
    String str = paramString.toString().trim();
    Uri localUri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_FILTER_URI, Uri.encode(str));
    if (paramString.equals(""))
      return this.context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, new String[] { "_id", "display_name", "has_phone_number", "photo_id" }, null, null, "display_name COLLATE LOCALIZED ASC");
    return this.context.getContentResolver().query(localUri, new String[] { "_id", "display_name", "has_phone_number", "photo_id" }, null, null, "display_name COLLATE LOCALIZED ASC");
  }

}