package com.yazhi1992.moon.sql;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**\
 * Created by zengyazhi on 2018/1/25.
 */

@Entity(nameInDb = "user")
public class User {

    @Id(autoincrement = true)
    private Long id;

    @Property(nameInDb = "name")
    private String name;

    @Property(nameInDb = "objectId")
    private String objectId; //leancloud 中的 objectId

    @Property(nameInDb = "headUrl")
    private String headUrl;

    @Property(nameInDb = "inviteNumber")
    private String inviteNumber; //用于绑定另一半的号码

    @Generated(hash = 831244146)
    public User(Long id, String name, String objectId, String headUrl,
            String inviteNumber) {
        this.id = id;
        this.name = name;
        this.objectId = objectId;
        this.headUrl = headUrl;
        this.inviteNumber = inviteNumber;
    }

    @Generated(hash = 586692638)
    public User() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getObjectId() {
        return this.objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getHeadUrl() {
        return this.headUrl;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }

    public String getInviteNumber() {
        return this.inviteNumber;
    }

    public void setInviteNumber(String inviteNumber) {
        this.inviteNumber = inviteNumber;
    }
}
