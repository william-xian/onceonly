package cn.mx.app.repository;


import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import cn.mx.app.entity.UserFriend;



@RepositoryRestResource(collectionResourceRel = "user_friend", path = "user_friend")
public interface UserFriendRepository extends PagingAndSortingRepository<UserFriend,Long> {
}

