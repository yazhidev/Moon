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

    @Property(nameInDb = "haveLover")
    private boolean haveLover;

    @Property(nameInDb = "loverId")
    private String loverId;

    @Property(nameInDb = "loverHeadUrl")
    private String loverHeadUrl;

    @Property(nameInDb = "loverName")
    private String loverName;

    @Property(nameInDb = "gender")
    private int gender;

    @Property(nameInDb = "email")
    private String email;

    @Property(nameInDb = "emailVerified")
    private boolean emailVerified;

    @Generated(hash = 889710475)
    public User(Long id, String name, String objectId, String headUrl,
            String inviteNumber, boolean haveLover, String loverId,
            String loverHeadUrl, String loverName, int gender, String email,
            boolean emailVerified) {
        this.id = id;
        this.name = name;
        this.objectId = objectId;
        this.headUrl = headUrl;
        this.inviteNumber = inviteNumber;
        this.haveLover = haveLover;
        this.loverId = loverId;
        this.loverHeadUrl = loverHeadUrl;
        this.loverName = loverName;
        this.gender = gender;
        this.email = email;
        this.emailVerified = emailVerified;
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

    public boolean getHaveLover() {
        return this.haveLover;
    }

    public void setHaveLover(boolean haveLover) {
        this.haveLover = haveLover;
    }

    public String getLoverId() {
        return this.loverId;
    }

    public void setLoverId(String loverId) {
        this.loverId = loverId;
    }

    public String getLoverHeadUrl() {
        return this.loverHeadUrl;
    }

    public void setLoverHeadUrl(String loverHeadUrl) {
        this.loverHeadUrl = loverHeadUrl;
    }

    public String getLoverName() {
        return this.loverName;
    }

    public void setLoverName(String loverName) {
        this.loverName = loverName;
    }

    public int getGender() {
        return this.gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean getEmailVerified() {
        return this.emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }
}
