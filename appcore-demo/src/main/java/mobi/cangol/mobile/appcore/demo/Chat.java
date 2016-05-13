/*
 *
 *  Copyright (c) 2013 Cangol
 *   <p/>
 *   Licensed under the Apache License, Version 2.0 (the "License")
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *  <p/>
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  <p/>
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package mobi.cangol.mobile.appcore.demo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by xuewu.wei on 2016/5/11.
 */
public class Chat implements Parcelable {
    private String uid;
    private String nickname;
    private String avatar;
    private String content;
    private String time;
    private String num;

    public Chat() {
    }

    protected Chat(Parcel in) {
        uid = in.readString();
        nickname = in.readString();
        avatar = in.readString();
        content = in.readString();
        time = in.readString();
        num = in.readString();
    }

    public static final Creator<Chat> CREATOR = new Creator<Chat>() {
        @Override
        public Chat createFromParcel(Parcel in) {
            return new Chat(in);
        }

        @Override
        public Chat[] newArray(int size) {
            return new Chat[size];
        }
    };

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uid);
        dest.writeString(nickname);
        dest.writeString(avatar);
        dest.writeString(content);
        dest.writeString(time);
        dest.writeString(num);
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Chat{");
        sb.append("uid='").append(uid).append('\'');
        sb.append(", nickname='").append(nickname).append('\'');
        sb.append(", avatar='").append(avatar).append('\'');
        sb.append(", content='").append(content).append('\'');
        sb.append(", time='").append(time).append('\'');
        sb.append(", num='").append(num).append('\'');
        sb.append(", CREATOR='").append(CREATOR).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
