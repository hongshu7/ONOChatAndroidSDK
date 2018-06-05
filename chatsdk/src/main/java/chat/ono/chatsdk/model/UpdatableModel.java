package chat.ono.chatsdk.model;

import android.content.ContentValues;

/**
 * Created by kevin on 2018/6/5.
 */

public class UpdatableModel {
    protected ContentValues updates = new ContentValues();
    protected boolean isInserted;

    public ContentValues getUpdateValues() {
        ContentValues _updates = updates;
        if (updates.size() > 0) {
            _updates = new ContentValues(updates);
            updates.clear();
        }
        return _updates;
    }

    public boolean isInserted() {
        return isInserted;
    }

    public void setInserted(boolean inserted) {
        isInserted = inserted;
    }
}
