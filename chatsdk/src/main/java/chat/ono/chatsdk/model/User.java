package chat.ono.chatsdk.model;

/**
 * Created by kevin on 2018/5/27.
 */

public class User extends UpdatableModel {
    private String userId;
    private String nickname;
    private String avatar;
    private int gender;
    private String remark;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        if (nickname == null) {
            nickname = "";
        }
        if (!nickname.equals(this.nickname)) {
            this.nickname = nickname;
            if (isInserted) {
                updates.put("nickname", nickname);
            }
        }
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        if (avatar == null) {
            avatar = "";
        }
        if (!avatar.equals(this.avatar)) {
            this.avatar = avatar;
            if (isInserted) {
                updates.put("avatar", avatar);
            }
        }
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        if (gender != this.gender) {
            this.gender = gender;
            if (isInserted) {
                updates.put("gender", gender);
            }
        }
    }

    public String getRemark() {
        return remark == null ? "" : remark;
    }

    public void setRemark(String remark) {
        if (remark == null) {
            remark = "";
        }
        if (!remark.equals(this.remark)) {
            this.remark = remark;
            if (isInserted) {
                updates.put("remark", remark);
            }
        }
    }
}
