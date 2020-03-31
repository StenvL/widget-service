package com.widget.service.models;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WidgetsRepository extends CrudRepository<Widget, Long> {
    @Query("select count(*) from #{#entityName} where z = ?1")
    int getWidgetsCountByZIndex(int zIndex);

    @Query("select max(z) from #{#entityName}")
    Integer getMaxZIndex();

    @Query("select min(z) from #{#entityName}")
    Integer getMinZIndex();

    @Modifying
    @Query("update #{#entityName} set z = z + 1 where z >= ?1")
    void incZIndices(int startIndex);
}
