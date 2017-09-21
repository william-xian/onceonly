package cn.mx.app.repository;


import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import cn.mx.app.entity.UserProfile;



@RepositoryRestResource(collectionResourceRel = "user_profile", path = "user_profile")
public interface UserProfileRepository extends PagingAndSortingRepository<UserProfile,Long> {
}

