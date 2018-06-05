package chat.ono.chatsdk.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import chat.ono.chatsdk.IMClient;
import chat.ono.chatsdk.constants.DBConstants;


public class DBHelper extends SQLiteOpenHelper {
	
	private static final String TAG = "DBHelper";

	public DBHelper(Context context) {

		super(context, DBConstants.DATABASE_NAME, null, DBConstants.DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = FileHelper.readAssetsFile(IMClient.getContext(), "message_db.sql");
		String[] sqls = sql.split("\n");
		for (String line : sqls) {
			if (line != null && !line.isEmpty()) {
				db.execSQL(line);
			}
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (newVersion > oldVersion) {
			try {
				switch (oldVersion) {
					case 1:
						doUpdateWork(db);
						break;
					default:
						break;
				}
			} catch (Exception e) {
				// TODO: handle exception
			}finally{
				onUpgrade(db, ++oldVersion, newVersion);
			}
		}
	}

	//第一次数据库升级操作
	private void doUpdateWork(SQLiteDatabase db){
		try {

		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
	}

}
