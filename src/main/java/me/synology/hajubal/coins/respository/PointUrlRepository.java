package me.synology.hajubal.coins.respository;

import me.synology.hajubal.coins.entity.PointUrl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PointUrlRepository extends JpaRepository<PointUrl, Long> {
    List<PointUrl> findByUrl(String url);

    List<PointUrl> findByName(String name);

    @Query("select pu " +
            " from PointUrl pu " +
            " where pu.name = :siteName " +
            " and pu.id not in (" +
            " select pu.id " +
            " from PointUrl pu join PointUrlUserCookie puuc on pu.id = puuc.pointUrl.id " +
            " and pu.permanent = false " +
            " join UserCookie uc on puuc.userCookie.id = uc.id " +
            " and uc.isValid = true " +
            " and uc.userName = :userName " +
            " where pu.name = :siteName )"
            )
    List<PointUrl> findByNotCalledUrl(@Param("siteName") String siteName, @Param("userName") String userName);
}
