package io.onceonly.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.context.junit4.SpringRunner;

import cn.mx.app.Launcher;
import cn.mx.app.entity.Goods;
import cn.mx.app.entity.GoodsDesc;
import cn.mx.app.entity.GoodsOrder;
import cn.mx.app.entity.GoodsShipping;
import cn.mx.app.entity.UserChief;
import cn.mx.app.entity.UserFriend;
import cn.mx.app.entity.UserProfile;
import cn.mx.app.entity.Wallet;
import cn.mx.app.repository.GoodsDescRepository;
import cn.mx.app.repository.GoodsOrderRepository;
import cn.mx.app.repository.GoodsRepository;
import cn.mx.app.repository.GoodsShippingRepository;
import cn.mx.app.repository.UserChiefRepository;
import cn.mx.app.repository.UserFriendRepository;
import cn.mx.app.repository.UserProfileRepository;
import cn.mx.app.repository.WalletRepository;


/**
 * 构造 10000个用户 {UserChief,UserProfile,Wallet} id <- [1,10000] 
 * 构造 1000个商品{Goods,GoodsDesc} 
 * 构造10000*10个订单
 * 构造10000*10个好友关系每人有10个
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Launcher.class)
public class PerformanceCndOnJoinOrWhereTest {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private GoodsDescRepository goodsDescRepository;
	@Autowired
	private GoodsRepository goodsRepository;
	@Autowired
	private GoodsOrderRepository goodsOrderRepository;
	@Autowired
	private GoodsShippingRepository goodsShippingRepository;
	@Autowired
	private UserChiefRepository userChiefRepository;
	@Autowired
	private UserFriendRepository userFriendRepository;
	@Autowired
	private UserProfileRepository userProfileRepository;
	@Autowired
	private WalletRepository walletRepository;
	
	@Before
	public void addUser() {
		if(userChiefRepository.count() != 0) {
			return;
		}
		List<UserChief> ucs = new ArrayList<>(10000);
		List<UserProfile> ups = new ArrayList<>(10000);
		List<Wallet> ws = new ArrayList<>(10000);
		for(Long id=1L; id <= 10000; id++) {
			String uniq = String.format("%07d", id);
			UserChief uc = new UserChief();
			uc.setId(id);
			uc.setAvatar("avatar"+uniq);
			uc.setGenre((int)(id%2));
			uc.setName("name"+uniq);
			uc.setPasswd("passwd"+uniq);
			ucs.add(uc);
			
			UserProfile up =new UserProfile();
			up.setId(id);
			up.setNickname("nickname"+uniq);
			up.setGender(id%2==0);
			up.setPhone("phone"+uniq);
			ups.add(up);
			
			Wallet w = new Wallet();
			w.setId(id);
			w.setExpenditure((int)(id%50));
			w.setIncome((int)(id%100));
			w.setBalance(w.getIncome()-w.getExpenditure());
			ws.add(w);
		}
		userChiefRepository.save(ucs);
		userProfileRepository.save(ups);
		walletRepository.save(ws);
	}
	
	@Before
	public void addGoods() {
		if(goodsRepository.count() != 0) {
			return;
		}
		List<Goods> gs = new ArrayList<>(1000);
		List<GoodsDesc> gds = new ArrayList<>(1000);
		for(Long id=1L; id <= 1000; id++) {
			String uniq = String.format("%07d", id);
			Goods g = new Goods();
			g.setId(id);
			g.setGenre((int)(id%2));
			g.setName("name"+uniq);
			gs.add(g);
			
			GoodsDesc up =new GoodsDesc();
			up.setId(id);
			up.setSaled((int)(id%100));
			up.setContent("content"+uniq);
			gds.add(up);
		}
		goodsRepository.save(gs);
		goodsDescRepository.save(gds);
	}

	@Before
	public void addFriend() {
		if(userFriendRepository.count() != 0) {
			return;
		}
		List<UserFriend> ufs = new ArrayList<>(100000);
		for(Long id=1L; id <= 60000; id++) {
			UserFriend go = new UserFriend();
			go.setId(id);
			long uid = id%10000+1;
			go.setUserId(uid);
			go.setFriendId((uid+(id/10000)+1)%10000+1);
			ufs.add(go);
		}
		for(Long id=60001L; id <= 100000; id++) {
			UserFriend go = new UserFriend();
			go.setId(id);
			long uid = id%10000+1;
			go.setUserId(uid);
			go.setFriendId((uid+10000-(id/10000)+1)%10000+1);
			ufs.add(go);
		}
		userFriendRepository.save(ufs);
	}
	
	@Before
	public void addGoodsShipping() {
		if(goodsOrderRepository.count() != 0) {
			return;
		}
		List<GoodsOrder> gos = new ArrayList<>(100000);
		List<GoodsShipping> gss = new ArrayList<>(100000);
		for(Long id=1L; id <= 100000; id++) {
			String uniq = String.format("%07d", id);
			GoodsOrder go = new GoodsOrder();
			go.setId(id);
			go.setGoodsId(id%1000+1);
			go.setUserId(id%10000+1);
			go.setAmount((int)(id%10)+1);
			go.setMoney((int)(id%100));
			go.setCtime(System.currentTimeMillis()-id*10000000);
			gos.add(go);
			
			GoodsShipping g = new GoodsShipping();
			g.setId(id);
			g.setGoodsOrderId(id);
			g.setBuyerId(id%1000+1);
			g.setReceiverId((id+100)%1000+1);
			g.setAddr("addr"+uniq);
			gss.add(g);
			
		}
		goodsOrderRepository.save(gos);
		goodsShippingRepository.save(gss);
	}
	
	
	@Test
	public void cndOnJoin() {
		String sql = "";
		sql += "select uf.user_id uid, ufp.nickname uname,uf.friend_id fid,mfp.nickname fname,mf.friend_id mffid";
		sql += "from user_friend uf";
		sql += "right join user_friend mf on uf.friend_id = mf.user_id and mf.user_id > 9000";
		sql += "left join user_profile ufp on ufp.id=uf.user_id";
		sql += "left join user_profile mfp on mfp.id=mf.user_id";
		sql += "limit 300";
		jdbcTemplate.query(sql,new RowMapper<Object[]>() {
			@Override
			public Object[] mapRow(ResultSet rs, int rowNum) throws SQLException {
				// TODO Auto-generated method stub
				return null;
			}
			
		});
	}
	@Test
	public void cndOnWhere() {
		String sql = "";
		sql += "select uf.user_id uid, ufp.nickname uname,uf.friend_id fid,mfp.nickname fname,mf.friend_id mffid";
		sql += "from user_friend uf";
		sql += "right join user_friend mf on uf.friend_id = mf.user_id";
		sql += "left join user_profile ufp on ufp.id=uf.user_id";
		sql += "left join user_profile mfp on mfp.id=mf.user_id";
		sql += "where uf.user_id = mf.friend_id and mf.user_id > 9000";
		sql += "limit 300";
		jdbcTemplate.query(sql,new RowMapper<Object[]>() {
			@Override
			public Object[] mapRow(ResultSet rs, int rowNum) throws SQLException {
				// TODO Auto-generated method stub
				return null;
			}
			
		});
	}
}
