package info.nukoneko.android.ho_n.sys.eventbus.event;import info.nukoneko.android.ho_n.sys.eventbus.NKEventTwitter;import twitter4j.User;import twitter4j.UserList;/** * Created by atsumi on 2016/10/22. */public class OnStreamUserListMemberAddition extends NKEventTwitter {    private final User addedMember;    private final User listOwner;    private final UserList list;    public OnStreamUserListMemberAddition(long managingUserId, User addedMember, User listOwner, UserList list) {        super(managingUserId);        this.addedMember = addedMember;        this.listOwner = listOwner;        this.list = list;    }    public User getAddedMember() {        return addedMember;    }    public User getListOwner() {        return listOwner;    }    public UserList getList() {        return list;    }}