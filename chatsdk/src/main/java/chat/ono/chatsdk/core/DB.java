package chat.ono.chatsdk.core;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import chat.ono.chatsdk.IMClient;
import chat.ono.chatsdk.model.Conversation;
import chat.ono.chatsdk.model.User;
import chat.ono.chatsdk.utils.DBHelper;


public class DB {
	private static final String TAG = "DB";
	private static DBHelper dbHelper = new DBHelper(IMClient.getContext());
	private static int openTimes = 0;
	private static SQLiteDatabase db = null;

	private static SQLiteDatabase getDB() {
		if (db == null) {
			openTimes = 0;
		}
		if (openTimes == 0) {
			db = dbHelper.getReadableDatabase();
		}
		openTimes++;
		return db;
	}

	private static void closeDB() {
		openTimes--;
		if (openTimes == 0) {
			db.close();
			db = null;
		}
	}


	public static List<Conversation> fetchConversations() {
		List<Conversation> records = new ArrayList<Conversation>();
		if (IMCore.getInstance().getUserId() == null) {
			return records;
		}
		SQLiteDatabase db = getDB();
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
				record.setUser(fetchUser(record.getUserId()));
				record.setLastMessage(fetchMessage(record.getLastMessageId()));

				record.setInserted(true);
				records.add(record);
			}
		}catch (Exception e){
			Log.e(TAG, e.getMessage());
		}finally {
			if (cursor != null){
				cursor.close();
			}
			closeDB();
		}
        return records;
	}

	public static Conversation fetchConversation(String targetId) {
		Conversation record = null;
		SQLiteDatabase db = getDB();
		Cursor cursor = null;
		try {
			cursor = db.rawQuery("SELECT * FROM conversation WHERE belong_id=? AND target_id=?", new String[]{ IMCore.getInstance().getUserId(), targetId });
			if (cursor.moveToNext()) {
				record.setUnreadCount(cursor.getInt(cursor.getColumnIndex("unread_count")));
				record.setContactTime(cursor.getLong(cursor.getColumnIndex("contact_time")));
				record.setConversationType(cursor.getInt(cursor.getColumnIndex("conversation_type")));
				record.setTargetId(cursor.getString(cursor.getColumnIndex("target_id")));
				record.setUserId(cursor.getString(cursor.getColumnIndex("user_id")));
				record.setLastMessageId(cursor.getString(cursor.getColumnIndex("last_message_id")));
				record.setUser(fetchUser(record.getUserId()));
				record.setLastMessage(fetchMessage(record.getLastMessageId()));

				record.setInserted(true);
			}
		}catch (Exception e){
			Log.e(TAG, e.getMessage());
		}finally {
			if (cursor != null){
				cursor.close();
			}
			closeDB();
		}
		return record;
	}

	
	public static void addConversation(Conversation conversation) {
		
		SQLiteDatabase db =  getDB();
		ContentValues values = new ContentValues();
        values.put("belong_id", IMCore.getInstance().getUserId());
        values.put("user_id", conversation.getUserId());
		values.put("contact_time", conversation.getContactTime());
		values.put("unread_count", conversation.getUnreadCount());
		values.put("conversation_type", conversation.getConversationType());
		values.put("last_message_id", conversation.getLastMessageId());

        db.insert("conversation", null, values);
		conversation.setInserted(true);

		closeDB();

	}

	public static void deleteConversation(String userId) {
		SQLiteDatabase db =  getDB();
		db.delete("conversation", "belong_id=? AND user_id=?", new String[]{ IMCore.getInstance().getUserId(), userId});
		closeDB();
	}

	public static void updateConversation(Conversation conversation) {
		ContentValues values = conversation.getUpdateValues();
		if (values.size() > 0) {
			SQLiteDatabase db =  getDB();
			db.update("conversation", values, "belong_id=? AND user_id=?", new String[] { IMCore.getInstance().getUserId(), conversation.getUserId() });
			closeDB();
		}
	}

	public static chat.ono.chatsdk.model.Message fetchMessage(String messageId) {
		SQLiteDatabase db = getDB();
		Cursor cursor = null;
		chat.ono.chatsdk.model.Message msg = null;
		try {
			cursor = db.rawQuery("SELECT * FROM user WHERE message_id=?", new String[]{ messageId });
			if (cursor.getCount() == 0) return null;
			if (cursor.moveToNext()) {
				msg.setMessageId(cursor.getString(cursor.getColumnIndex("message_id")));
				msg.setUserId(cursor.getString(cursor.getColumnIndex("user_id")));
				msg.setTimestamp(cursor.getLong(cursor.getColumnIndex("timestamp")));
				msg.setSelf(cursor.getInt(cursor.getColumnIndex("is_self")) == 1);
				msg.setSend(cursor.getInt(cursor.getColumnIndex("is_send")) == 1);
				msg.setError(cursor.getInt(cursor.getColumnIndex("is_error")) == 1);
				String data = cursor.getString(cursor.getColumnIndex("data"));
				msg.decode(data);
			}
			msg.setInserted(true);
		}catch (Exception e){
			Log.e(TAG, e.getMessage());
		}finally {
			if (cursor != null){
				cursor.close();
			}
			closeDB();
		}

		return msg;
	}

	
	public static List<chat.ono.chatsdk.model.Message> fetchMessages(String userId, String minMsgId, int limit) {
		SQLiteDatabase db = getDB();
		List<chat.ono.chatsdk.model.Message> messages = new ArrayList<chat.ono.chatsdk.model.Message>();
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
				chat.ono.chatsdk.model.Message msg = IMClient.createMessageFromType(type);
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
			closeDB();
		}

        return messages;
	}


	
	public static void addMessage(chat.ono.chatsdk.model.Message msg) {
		SQLiteDatabase db =  getDB();
		ContentValues values = new ContentValues();
		values.put("belong_id", IMCore.getInstance().getUserId());
		values.put("user_id", msg.getUserId());
		values.put("timestamp", msg.getTimestamp());
		values.put("is_send", msg.isSend());
		values.put("is_self", msg.isSelf());
		values.put("is_error", msg.isError());
		values.put("type", msg.getType());
		values.put("data", msg.encode());

		db.insert("conversation", null, values);

		msg.setInserted(true);

		closeDB();
	}

	public static void deleteMessage(chat.ono.chatsdk.model.Message message) {
		SQLiteDatabase db =  getDB();
		db.delete("message", "message_id=?", new String[] { message.getMessageId() });
		closeDB();
	}

	public static void updateMessage(chat.ono.chatsdk.model.Message message) {
		updateMessage(message, message.getMessageId());
	}

	public static void updateMessage(chat.ono.chatsdk.model.Message message, String oldId) {
		ContentValues values = message.getUpdateValues();
		Log.i("IM", "update msgs values:" + values.toString() + ", oldId:" + oldId);
		if (values.size() > 0) {
			SQLiteDatabase db =  getDB();
			db.update("message", values, "message_id=?", new String[] { oldId });
			closeDB();
		}
	}

	public static void addUser(User user) {
		SQLiteDatabase db =  getDB();
		ContentValues values = new ContentValues();
		values.put("user_id", user.getUserId());
		values.put("nickname", user.getNickname());
		values.put("avatar", user.getAvatar());
		values.put("gender", user.getGender());
		values.put("remark", user.getRemark());
		db.insert("user", null, values);
		user.setInserted(true);

		closeDB();
	}

	public static void updateUser(User user) {
		ContentValues values = user.getUpdateValues();
		Log.i("IM", "update user values:" + values.toString());
		if (values.size() > 0) {
			SQLiteDatabase db =  getDB();
			db.update("user", values, "user_id=?", new String[] { user.getUserId() });
			closeDB();
		}
	}


	public static User fetchUser(String userId) {
		SQLiteDatabase db = getDB();
		Cursor cursor = null;
		User user = null;
		try {
			cursor = db.rawQuery("SELECT * FROM user WHERE user_id=?", new String[]{ userId });
			if (cursor.getCount() == 0) return null;
			user = new User();
			if (cursor.moveToNext()) {
				user.setUserId(cursor.getString(cursor.getColumnIndex("userId")));
				user.setNickname(cursor.getString(cursor.getColumnIndex("nickname")));
				user.setAvatar(cursor.getString(cursor.getColumnIndex("avatar")));
				user.setGender(cursor.getInt(cursor.getColumnIndex("gender")));
			}
			user.setInserted(true);
		}catch (Exception e){
			Log.e(TAG, e.getMessage());
		}finally {
			if (cursor != null){
				cursor.close();
			}
			closeDB();
		}

		return user;
	}

	public static List<User> fetchFriends() {
		List<User> records = new ArrayList<User>();

		SQLiteDatabase db = getDB();
		Cursor cursor = null;
		try {
			cursor = db.rawQuery("SELECT * FROM friends WHERE user_id=?", new String[]{ IMCore.getInstance().getUserId() });
			if (cursor.getCount() == 0) return records;
			while (cursor.moveToNext()) {
				String userId = cursor.getString(cursor.getColumnIndex("friend_id"));
				User user = fetchUser(userId);
				if (user != null) {
					records.add(user);
				}
			}
		}catch (Exception e){
			Log.e(TAG, e.getMessage());
		}finally {
			if (cursor != null){
				cursor.close();
			}
			closeDB();
		}
		return records;
	}


}