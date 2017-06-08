package com.yukiww233.mapper;

import org.apache.ibatis.annotations.*;

/**
 * Created by disagree on 2017/5/10.
 */
@Mapper
public interface WalletMapper {
    @Insert("insert into wallet (uid,balance) values (#{uid},#{balance})")
    void insert(@Param("uid") String uid, @Param("balance") Double balance);

    @Update("update wallet set balance=balance+#{rewards} where uid=#{uid}")
    void addBalance(@Param("uid") String uid,
                    @Param("rewards") int rewards);

    @Update("update wallet set balance=balance-#{rewards} where uid=#{uid}")
    void minusBalance(@Param("uid") String uid,
                      @Param("rewards") int rewards);

    @Select("select balance from wallet where uid=#{uid}")
    Double getBalance(@Param("uid") String uid);
}
