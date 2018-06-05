package chat.ono.chatsdk.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import chat.ono.chatsdk.IMClient;
import chat.ono.chatsdk.core.IMCore;
import chat.ono.chatsdk.model.Conversation;
import chat.ono.chatsdk.model.User;
import chat.ono.chatsdk.utils.DBHelper;


public class DB {
	private static final String TAG = "DB";

	private static DB instance;

	//private HashMap<String, User> Users;
	
	public static DB getInstance() {
		if (instance == null) {
			instance = new DB();
		}
		return instance;
	}
	
	private DBHelper dbHelper;
	
	private DB() {
		
		dbHelper = new DBHelper(IMClient.getContext());

		//Users = fetchUsers();
	}

	public List<Conversation> fetchConversations() {
		List<Conversation> records = new ArrayList<Conversation>();
		if (IMCore.getInstance().getUserId() == null) {
			return records;
		}
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = null;
		try {
			cursor = db.rawQuery("SELECT * FROM conversation WHERE belong_id=? ORDER BY contact_time DESC", new String[]{ IMCore.getInstance().getUserId() });
			if (cursor.getCount() == 0) return records;
			while (cursor.moveToNext()) {
				Conversation record = new Conversation();
				record.setUnreadCount(cursor.getInt(cursor.getColumnIndex("unread_count")));
				record.setContactTime(cursor.getLong(cursor.getColumnIndex("contact_time")));
				record.setConversationType(cursor.getInt(cursor.getColumnIndex("conversation_type")));

				record.setUserId(cursor.getString(cursor.getColumnIndex("user_id")));
				record.setLastMessageId(cursor.getString(cursor.getColumnIndex("last_message_id")));
				//todo: get user and message

				record.setInserted(true);
				records.add(record);
			}
		}catch (Exception e){
			Log.e(TAG, e.getMessage());
		}finally {
			if (cursor != null){
				cursor.close();
			}
			if (dbHelper != null){
				dbHelper.close();
			}
		}
        return records;
	}

	
	public void addConversation(Conversation conversation) {
		
		SQLiteDatabase db =  dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
        values.put("belong_id", IMCore.getInstance().getUserId());
        values.put("user_id", conversation.getUserId());
		values.put("contact_time", conversation.getContactTime());
		values.put("unread_count", conversation.getUnreadCount());
		values.put("conversation_type", conversation.getConversationType());
		values.put("last_message_id", conversation.getLastMessageId());

        db.insert("conversation", null, values);
        dbHelper.close();
        

	}

	public void deleteConversation(String userId) {
		SQLiteDatabase db =  dbHelper.getWritableDatabase();
		db.delete("record", "belong_id=? AND user_id=?", new String[]{ IMCore.getInstance().getUserId(), userId});
		dbHelper.close();
	}

	public void updateRecord(Conversation conversation) {
		ContentValues values = conversation.getUpdateValues();
		if (values.size() > 0) {
			SQLiteDatabase db =  dbHelper.getWritableDatabase();
			db.update("record", values, "belong_id=? AND user_id=?", new String[] { IMCore.getInstance().getUserId(), conversation.getUserId() });
			dbHelper.close();
		}
	}

	
	public List<Message> fetchMessages(String userId, String minMsgId, int limit) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		List<Message> messages = new ArrayList<Message>();
		String sql;
		String[] params;
		if (TextUtils.isEmpty(minMsgId)) {
			sql = "SELECT * FROM message WHERE belong_id=? AND user_id=? ORDER BY message_id DESC LIMIT " + limit;
			params = new String[]{ IMCore.getInstance().getUserId(), userId };
		} else {
			sql = "SELECT * FROM message WHERE belong_id=? AND user_id=? AND message_id<? ORDER BY message_id DESC LIMIT " + limit;
			params = new String[]{ IMCore.getInstance().getUserId(), userId, minMsgId };
		}
		Log.e("Sql", "belong_id:"+IMCore.getInstance().getUserId()+"  user_id:"+userId);
		Cursor cursor = null;
		try {
			cursor = db.rawQuery(sql, params);
			Log.v("lm", "records count:"+cursor.getCount());
			if (cursor.getCount() == 0) return messages;
			cursor.moveToLast();
			cursor.moveToNext();
			while (cursor.moveToPrevious()) {
				int type = cursor.getInt(cursor.getColumnIndex("type"));
				Message msg = IMClient.createMessageFromType(type);
				msg.setMessageId(cursor.getString(cursor.getColumnIndex("message_id")));
				msg.setUserId(cursor.getString(cursor.getColumnIndex("user_id")));
				msg.setTimestamp(cursor.getLong(cursor.getColumnIndex("timestamp")));
				msg.setSelf(cursor.getInt(cursor.getColumnIndex("is_self")) == 1);
				msg.setSend(cursor.getInt(cursor.getColumnIndex("is_send")) == 1);
				msg.setError(cursor.getInt(cursor.getColumnIndex("is_error")) == 1);
				String data = cursor.getString(cursor.getColumnIndex("data"));
				msg.decode(data);
				msg.setInserted(true);

				messages.add(msg);


			}
		}catch (Exception e){
			Log.e(TAG, e.getMessage());
		}finally {
			if (cursor != null){
				cursor.close();
			}
			if (dbHelper != null){
				dbHelper.close();
			}
		}

        return messages;
	}


	
	public void addMessage(Message msg) {
		SQLiteDatabase db =  dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();

        
        dbHelper.close();
	}

	public void deleteMessage(Message message) {
		SQLiteDatabase db =  dbHelper.getWritableDatabase();
		db.delete("message", "message_id=?", new String[] { message.getMessageId() });
		dbHelper.close();
	}

	public void updateMessage(Message message) {
		updateMessage(message, message.getMessageId());
	}

	public void updateMessage(Message message, String oldId) {
		ContentValues values = message.getUpdateValues();
		Log.i("IM", "update msgs values:" + values.toString() + ", oldId:" + oldId);
		if (values.size() > 0) {
			SQLiteDatabase db =  dbHelper.getWritableDatabase();
			db.update("message", values, "message_id=?", new String[] { oldId });
			dbHelper.close();
		}
	}

	public void addUser(User user) {
		SQLiteDatabase db =  dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("user_id", user.getUserId());
		values.put("nickname", user.getNickname());
		values.put("avatar", user.getAvatar());
		values.put("gender", user.getGender());
		values.put("remark", user.getRemark());
		db.insert("user", null, values);
		dbHelper.close();
	}


	public User getUser(String userId) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = null;
		User user = null;
		try {
			cursor = db.rawQuery("SELECT * FROM user WHERE uid=?", new String[]{userId});
			if (cursor.getCount() == 0) return null;
			user = new User();
			if (cursor.moveToNext()) {
				user.setUserId(cursor.getString(cursor.getColumnIndex("userId")));
				user.setNickname(cursor.getString(cursor.getColumnIndex("nickname")));
				user.setAvatar(cursor.getString(cursor.getColumnIndex("avatar")));
				user.setGender(cursor.getInt(cursor.getColumnIndex("gender")));
			}
		}catch (Exception e){
			Log.e(TAG, e.getMessage());
		}finally {
			if (cursor != null){
				cursor.close();
			}
			if (dbHelper != null){
				dbHelper.close();
			}
		}

		return user;
	}


}
